// ==== element ====
export function isOverflowed(element) {
    return element.scrollHeight > element.clientHeight || element.scrollWidth > element.clientWidth
}

export function isOverflowedX(element) {
    return element.scrollWidth > element.clientWidth
}

// ====  scroll  ====
/**
 * 滚动到页面顶部
 */
export function scrollToTop() {
    window.scroll({ top: 0, left: 0, behavior: 'smooth' })
}

/**
 * 使dom元素完整显示在view范围内
 * @param {HTMLElement} ele 
 */
export function scrollUntilFullyVisible(ele) {
    const position = ele.getBoundingClientRect()
    const fullyVisible = position.bottom <= window.innerHeight && position.top >= 0
    if (!fullyVisible) {
        window.scroll({
            top: position.bottom,
            behavior: 'smooth'
        })
    }
}

// ==== copy =======
/**
 * 将一个对象的属性复制到另一个对象上（仅复制共同属性）（数据属性仅一层）
 * @param {*} dest 目标
 * @param {*} src  源数据
 * @returns 
 */
export function copy(dest, src) {
    for (const key in src) {
        if (Object.prototype.hasOwnProperty.call(dest, key) && Object.prototype.hasOwnProperty.call(src, key)) {
            dest[key] = src[key]
        }
    }
    return dest
}

// === propNoChange =====
/**
 * 检查是否两个数据共有属性值是否都相同（数据属性仅一层）
 * @param {*} subObj 
 * @param {*} obj 
 * @returns 
 */
export function propNoChange(subObj, obj) {
    for (const key in obj) {
        if (Object.prototype.hasOwnProperty.call(subObj, key) && subObj[key] !== obj[key]) {
            return false
        }
    }
    return true
}

export function isMobile() {
    return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
}