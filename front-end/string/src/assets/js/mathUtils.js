/**
 * 计算百分比
 * @param {Numer} numerator 分子
 * @param {Number} denominator 分母
 * @param {Number} decimal 精度（小数点后几位）
 * @returns 百分比结果
 */
export function calcPercentage(numerator, denominator, decimal) {
    return Math.floor(numerator / denominator * 100).toFixed(decimal)
}