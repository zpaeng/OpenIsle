export function initIOSKeyboardFix() {
  if (typeof window === 'undefined' || !window.visualViewport) return;

  const ua = navigator.userAgent || '';
  const isIOS = /iP(ad|hone|od)/.test(ua);
  if (!isIOS) return;

  const viewport = window.visualViewport;
  const adjustScroll = () => {
    window.scrollTo(0, viewport.offsetTop);
  };

  viewport.addEventListener('resize', adjustScroll);
  viewport.addEventListener('scroll', adjustScroll);

  let lastScrollY = 0;
  document.addEventListener('focusin', () => {
    lastScrollY = window.scrollY;
  });
  document.addEventListener('focusout', () => {
    window.scrollTo(0, lastScrollY);
  });
}
