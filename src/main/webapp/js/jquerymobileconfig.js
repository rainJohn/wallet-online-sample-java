//Allow the Google Wallet button to not be enhanced by JQuery Mobile
$(document).bind('mobileinit', function() {
  $.extend($.mobile, {
    'ignoreContentEnabled' : true
  });
});
