/**
 * This class provides necessary functions for Single sign on feature, which
 * enables your customers to authenticate themselves to your website using their
 * Google credentials
 *
 */
var Xyz = Xyz || {};

(function(sso) {
  /**
   * This variable defines scopes to authorize for the SingleSignOn feature.
   * @type {Array}
   */
  var scopes = [
      'https://www.googleapis.com/auth/paymentssandbox.make_payments',
      'https://www.googleapis.com/auth/userinfo.profile',
      'https://www.googleapis.com/auth/userinfo.email'];
  /**
   * Pops up a Google account sign in page and requests for
   * authorization
   */
  sso.login = function(callback) {
    // Initiate the OAuth 2.0 authorization process. The user will receive a
    // popup window asking them to authenticate and authorize.
    gapi.auth.authorize({
      //client_id is your OAuth2 client id.
      'client_id' : clientId,
      'scope' : scopes,
      //immediate mode determines if we want to try to refresh a pre-existing
      //access token.  //in this case we set it to false to show the popup.
      immediate: false
    }, function(data) {
      Xyz.Sso.handleLoginResult(data,
        callback || function() {location.reload();});
    });
  };

  /**
   * Checks if buyers pre-authorized the app to use their Wallet information to
   * checkout
   *
   * @param {object}
   *          data This object represents the OAuth 2.0 token.
   */
  sso.handleLoginResult = function(data, callback) {
    if (data != null) {
      Xyz.Cookie.setAccessToken(data.access_token, data.expires_in);
      google.wallet.online.setAccessToken(data.access_token);
    }
    // URL used to acquire the user's basic profile information
    var userProfileUrl =
      'https://www.googleapis.com/oauth2/v1/userinfo?access_token=';
    // Obtain user profile information by using accesstoken in the OAuth object.
    $.get(userProfileUrl + encodeURIComponent(data.access_token),
      function(param) {
        $.post('/store', 'accesstoken=' + data.access_token + '&email=' +
          param.email, callback);
      }
    );
  };
  /**
   * Log out the customer from the site and reload the page
   */
  sso.logout = function() {
    // Clear session objects associated with user profile information and reload
    // the page .
    Xyz.Cookie.setAccessToken('', 1);
    google.wallet.online.setAccessToken('');
    $.post('/store', 'logout=' + 'true', function() {
      location.reload();
    });
  };

  /**
   * Parses and generates necessary scopes for login
   */
  sso.generateScopes = function() {
    var encodedScopes = '';
    // Encode each scopes for the SingleSignOn feature
    for (scope in scopes) {
      encodedScopes += encodeURIComponent(scopes[scope]) + '+';
    }
    return encodedScopes.slice(0, -1);
  };
})(window.Xyz.Sso = window.Xyz.Sso || {});
