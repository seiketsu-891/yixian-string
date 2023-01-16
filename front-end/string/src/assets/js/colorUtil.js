/**
 * 将颜色转换为带不透明度颜色
 * @param {String} color 十六进制颜色
 * @param {Number} opacity 不透明度
 * @returns 字符串拼接
 */
function toTransparentColor(color, opacity) {
    if (opacity === 100) {
        // 在100时，直接拼接字符串不起作用
        return `${color}`
    }
    return `${color}${opacity}`
}

export { toTransparentColor }