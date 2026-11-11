package com.ezu.product.init;

import com.ezu.product.domain.entity.Category;
import com.ezu.product.domain.enums.CategoryStatus;
import com.ezu.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 品类种子数据初始化（幂等：t_category 为空时整树插入；失败只记日志，不阻断启动）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryInitializer implements ApplicationRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(ApplicationArguments args) {
        try {
            if (categoryRepository.count() > 0) {
                return;
            }
            Category computer = insert(0L, "电脑办公", 10);
            insert(computer.getId(), "笔记本电脑", 10);
            insert(computer.getId(), "台式机", 20);
            insert(computer.getId(), "工作站", 30);
            Category camera = insert(0L, "摄影摄像", 20);
            insert(camera.getId(), "微单相机", 10);
            insert(camera.getId(), "摄像机", 20);
            insert(camera.getId(), "镜头与配件", 30);
            Category printer = insert(0L, "打印设备", 30);
            insert(printer.getId(), "喷墨打印机", 10);
            insert(printer.getId(), "激光打印机", 20);
            insert(printer.getId(), "扫描仪", 30);
            log.info("品类种子数据初始化完成（3 顶级 / 9 二级）");
        } catch (Exception e) {
            log.error("品类种子数据初始化失败（不阻断启动）", e);
        }
    }

    /**
     * 插入单条品类并回填雪花ID
     */
    private Category insert(Long parentId, String name, int sort) {
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(name);
        category.setSort(sort);
        category.setStatus(CategoryStatus.ENABLED);
        categoryRepository.save(category);
        return category;
    }
}
