self.addEventListener('push', function(event) {
  const data = event.data ? event.data.text() : 'New notification';
  event.waitUntil(
    self.registration.showNotification('OpenIsle', {
      body: data,
      icon: '/favicon.ico'
    })
  );
});
