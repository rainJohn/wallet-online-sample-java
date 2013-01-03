/**
 * This class provides necessary functions for Single sign on feature, which
 * enables your customers to authenticate themselves to your website using their
 * Google credentials
 *
 * @author gpeng(Guang-Hong Peng)
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
  sso.login = function() {
    // Initiate the OAuth 2.0 authorization process. The user will receive a
    // popup window asking them to authenticate and authorize.
    gapi.auth.authorize({
      //client_id is your OAuth2 client id.
      'client_id' : clientId,
      'scope' : scopes,
      //immediate mode determines if we want to try to refresh a pre-existing
      //access token.  //in this case we set it to false to show the popup.
      immediate: false
      //sso.getUserProfile function handles the returned OAuth 2.0 token.
    }, sso.getUserProfile);
  };

  /**
   * Get the user profile with the access token and reload the page
   *
   * @param {object}
   *          data This object represents the OAuth 2.0 token.
   */
  sso.getUserProfile = function(data) {
    // URL used to acquire the user's basic profile information
    var userProfileUrl =
      'https://www.googleapis.com/oauth2/v1/userinfo?access_token=';
    // Obtain user profile information by using accesstoken in the OAuth object.
    $.get(userProfileUrl + encodeURIComponent(data.access_token), function(
        param) {
      // Post the obtained profile information to the server and update session
      // objects associated with it.
      $.post('store', 'accesstoken = ' + data.access_token + '&email=' +
        param.email, function() {
        location.reload();
      });
    });
  };

  /**
   * Log out the customer from the site and reload the page
   */
  sso.logout = function() {
    // Clear session objects associated with user profile information and reload
    // the page .
    $.post('store', 'logout=' + 'true', function() {
      location.reload();
    });
  };
  /**
   * Pops up a Google account sign in page and redirect them to
   * continue the buy flow after authorization
   */
  sso.loginWithGoogle = function() {
    // Initiate the OAuth 2.0 authorization process. The user will receive a
    // popup window asking them to authenticate and authorize.
    gapi.auth.authorize({
      //client_id is your OAuth2 client id
      'client_id' : clientId,
      'scope' : scopes,
      //immediate mode determines if we want to try to refresh a pre-existing
      //access token in this case we set it to false to show the popup
      immediate: false
    }, sso.handleLoginResult);
  };
  /**
   * Checks if buyers pre-authorized the app to use their Wallet information to
   * checkout
   *
   * @param {object}
   *          data This object represents the OAuth 2.0 token.
   */
  sso.handleLoginResult = function(data) {
    // Update the value of variables used to record the authorization status and
    // store the access token.
    if (data != null) {
      Xyz.Wallet.accessToken = data.access_token;
      Xyz.Cookie.setAccessToken(data.access_token);
      google.wallet.online.setAccessToken(data.access_token);
      // Redirect pre-authorized buyers to continue the buy flow.
      continueCheckout();
    }
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
