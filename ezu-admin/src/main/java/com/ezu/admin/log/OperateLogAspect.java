package com.ezu.admin.log;

import cn.dev33.satoken.session.SaSession;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ezu.admin.application.AuthApplicationService;
import com.ezu.admin.repository.OperateLogRepository;
import com.ezu.common.satoken.StpAdminUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 操作日志切面：环绕 @OperateLog 方法，业务成功后同步落库 t_operate_log；
 * 日志落库失败仅记 error，不影响业务返回值
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperateLogAspect {

    /** params 列最大长度（超出截断，防 text 列膨胀） */
    private static final int MAX_PARAMS_LENGTH = 4000;

    /** 敏感字段值脱敏占位 */
    private static final String MASKED_VALUE = "***";

    /**
     * 敏感 key 清单（小写）：序列化结果中命中（大小写不敏感）的 key，值统一替换为 ***，防止明文密码/密钥落库
     */
    private static final String[] SENSITIVE_KEYS = {"password", "oldpassword", "newpassword", "secret", "token"};

    /** 敏感 key 脱敏正则：捕获组1保留原 key（含原始大小写），值整体替换 */
    private static final Pattern SENSITIVE_KEY_PATTERN = Pattern.compile(
            "(?i)(\"(?:" + String.join("|", SENSITIVE_KEYS) + ")\")\\s*:\\s*\"[^\"]*\"");

    private final OperateLogRepository operateLogRepository;

    @Around("@annotation(operateLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperateLog operateLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        Long adminUserId = currentAdminUserId();
        String operatorName = currentOperatorName();
        String ip = currentIp();
        Object result = joinPoint.proceed();
        long costMs = System.currentTimeMillis() - startTime;
        saveLog(joinPoint, operateLog, adminUserId, operatorName, ip, (int) costMs);
        return result;
    }

    /**
     * 同步落库；插入异常只记日志，不影响业务返回值
     */
    private void saveLog(ProceedingJoinPoint joinPoint, OperateLog operateLog,
                         Long adminUserId, String operatorName, String ip, int costMs) {
        try {
            var entity = new com.ezu.admin.domain.entity.OperateLog();
            entity.setAdminUserId(adminUserId);
            entity.setOperatorName(operatorName);
            entity.setModule(operateLog.module());
            entity.setOperation(operateLog.operation());
            entity.setMethod(joinPoint.getSignature().getDeclaringTypeName() + "#" + joinPoint.getSignature().getName());
            entity.setParams(toJsonOrNull(joinPoint.getArgs()));
            entity.setIp(ip);
            entity.setCostMs(costMs);
            entity.setCreateTime(LocalDateTime.now());
            operateLogRepository.save(entity);
        } catch (Exception e) {
            log.error("操作日志记录失败：{}#{}", operateLog.module(), operateLog.operation(), e);
        }
    }

    /**
     * 参数序列化：仅序列化 com.ezu 包内的业务参数（Servlet/文件等非业务参数记简单类名占位），
     * 先做敏感 key 脱敏再截断到 MAX_PARAMS_LENGTH（先截断会把脱敏片段截半露出密文）；失败记 null
     */
    private String toJsonOrNull(Object[] args) {
        try {
            List<Object> serializableArgs = new ArrayList<>(args.length);
            for (Object arg : args) {
                serializableArgs.add(isBizParam(arg) ? arg : arg == null ? "null" : arg.getClass().getSimpleName());
            }
            String json = JSONUtil.toJsonStr(serializableArgs);
            String masked = SENSITIVE_KEY_PATTERN.matcher(json).replaceAll("$1:\"" + MASKED_VALUE + "\"");
            return StrUtil.subPre(masked, MAX_PARAMS_LENGTH);
        } catch (Exception e) {
            log.warn("操作日志参数序列化失败", e);
            return null;
        }
    }

    /**
     * 是否业务参数：null 与非 com.ezu 包对象（HttpServletRequest/Response、MultipartFile、BindingResult 等）不参与序列化
     */
    private boolean isBizParam(Object arg) {
        return arg != null && arg.getClass().getPackageName().startsWith("com.ezu");
    }

    /**
     * 当前登录 admin 账号ID，未登录/无上下文返回 null
     */
    private Long currentAdminUserId() {
        try {
            return StpAdminUtil.isLogin() ? StpAdminUtil.getAdminUserId() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 操作人姓名：优先取会话冗余的姓名，无会话取登录账号，均无返回 null
     */
    private String currentOperatorName() {
        try {
            SaSession session = StpAdminUtil.getSession();
            Object realName = session.get(AuthApplicationService.SESSION_KEY_REAL_NAME);
            if (realName != null) {
                return realName.toString();
            }
            Object username = session.get(AuthApplicationService.SESSION_KEY_USERNAME);
            return username == null ? null : username.toString();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 操作IP：X-Forwarded-For 优先（取第一个客户端IP），否则 remoteAddr；非 Web 线程返回 null
     */
    private String currentIp() {
        try {
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
                HttpServletRequest request = attributes.getRequest();
                String forwarded = request.getHeader("X-Forwarded-For");
                return StrUtil.isNotBlank(forwarded) ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
