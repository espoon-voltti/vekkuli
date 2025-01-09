export function scrollToPos(options: ScrollToOptions, timeout = 0): void {
  scrollWithTimeout(() => options, timeout)
}

function scrollWithTimeout(
  getOptions: () => ScrollToOptions | undefined,
  timeout = 0,
  element: HTMLElement | null = null
): void {
  withTimeout(() => {
    const opts = getOptions()
    if (opts) {
      if (element) {
        element.scrollTo({ behavior: 'smooth', ...opts })
      } else {
        window.scrollTo({ behavior: 'smooth', ...opts })
      }
    }
  }, timeout)
}

function withTimeout(callback: () => void, timeout = 0): void {
  if (timeout > 0) {
    window.setTimeout(callback, timeout)
  } else {
    requestAnimationFrame(callback)
  }
}
