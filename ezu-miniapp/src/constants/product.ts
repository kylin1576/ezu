/**
 * 商品/租金方案常量（与后端 RentalUnit 枚举、RentalCalculator 对齐）
 */

/** 计费单位枚举值（t_rental_plan.unit） */
export const RENTAL_UNIT = {
  /** 日 */
  DAY: 1,
  /** 周 */
  WEEK: 2,
  /** 月 */
  MONTH: 3,
  /** 季 */
  QUARTER: 4,
} as const;

/** 单位 → 文案（短：/日 /周 /月 /季） */
export const RENTAL_UNIT_TEXT: Record<number, string> = {
  [RENTAL_UNIT.DAY]: "日",
  [RENTAL_UNIT.WEEK]: "周",
  [RENTAL_UNIT.MONTH]: "月",
  [RENTAL_UNIT.QUARTER]: "季",
};

/** 单位 → 计租天数（与后端 RentalCalculator 的 DAY=1/WEEK=7/MONTH=30/QUARTER=90 一致） */
export const UNIT_DAYS: Record<number, number> = {
  [RENTAL_UNIT.DAY]: 1,
  [RENTAL_UNIT.WEEK]: 7,
  [RENTAL_UNIT.MONTH]: 30,
  [RENTAL_UNIT.QUARTER]: 90,
};

/**
 * 预估总租金 = price × ceil(rentDays / unitDays)，2 位小数（HALF_UP）
 * 仅用于详情页预估展示；下单以服务端 trial 试算为准
 */
export function calcTotalRent(unit: number, price: number, rentDays: number): number {
  const unitDays = UNIT_DAYS[unit] ?? 1;
  const cycles = Math.ceil(rentDays / unitDays);
  return Math.round(price * cycles * 100) / 100;
}
