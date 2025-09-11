/**
 * 滚动到指定位置
 * @param {number} element - 目标元素或位置
 * @param {number} duration - 动画持续时间
 * @param {Function} callback - 完成回调
 */
export function scrollTo(element, duration, callback) {
  const to = typeof element === 'number' ? element : element.offsetTop
  const duration_ms = duration || 500
  const element_ = document.scrollingElement || document.documentElement
  const start = element_.scrollTop
  const change = to - start
  const startDate = +new Date()

  // t = current time
  // b = start value
  // c = change in value
  // d = duration
  const easeInOutQuad = function(t, b, c, d) {
    t /= d / 2
    if (t < 1) return c / 2 * t * t + b
    t--
    return -c / 2 * (t * (t - 2) - 1) + b
  }

  const animateScroll = function() {
    const currentDate = +new Date()
    const currentTime = currentDate - startDate
    element_.scrollTop = parseInt(easeInOutQuad(currentTime, start, change, duration_ms))
    if (currentTime < duration_ms) {
      requestAnimationFrame(animateScroll)
    } else {
      element_.scrollTop = to
      if (callback) callback()
    }
  }
  animateScroll()
}
