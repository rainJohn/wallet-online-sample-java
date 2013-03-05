/**
 * The Xyz.Cookie namespace handles all aspects of the application related to
 * cookie functionality. It provides functions to restore cookie object in
 * different formats and get cookie objects in corresponding types.
 *
 */
var Xyz = Xyz || {};

(function(cookie)  {
  /**
   * Setter for MaskedWallet JWT.
   * It restores MaskedWallet JWT to persist it for page refreshes.
   * @param {object}
   *        maskedWallet The MaskedWalletResponse.
   */
  cookie.setMaskedWallet = function(maskedWallet) {
    //Store MakedWallet in JSON format
    $.cookie('maskedWallet', JSON.stringify(maskedWallet));
  };
  /**
   * Setter for Changed MaskedWallet JWT
   * It restores Changed MaskWallet JWT to persist it for page refreshes.
   * @param {String}
   *        changedJWT ChangedMaskWallet JWT with Google Transaction Id.
   */
  cookie.setChangeJwt = function(changedJWT) {
    //Store changedJWT in JSON format
    $.cookie('changedJwt', JSON.stringify(changedJWT));
  };
  /**
   * Setter for current item
   * It restores current item object to persist currently selected item object
   * for page refreshes.
   * @param {object}
   *   camera CurrentItem model which is used to represent currently selected
   *   item.
   */
  cookie.setCurrentItem = function(camera) {
    //Store current item in JSON format
    $.cookie('currentItem', JSON.stringify(camera));
  };
  /**
   * Setter for transaction ID
   * It restores transaction ID to persist it for page refreshes.
   * @param {String}
   *        transactionId ID which ties various api requests for a single order.
   */
  cookie.setTransactionId = function(transactionId) {
    $.cookie('transactionId', transactionId);
  };
  /**
   * Setter for OAuth2 access_token
   * It restores accessToekn to allow users to persist their Wallet
   * pre-authorization.
   * @param {object}
   *        accessToken Acquired accessToken after buyers complete the
   *        OAuth authorization.
   * @param {String}
   *        exp Expiration time for the accessToken in minutes from now.
   */
  cookie.setAccessToken = function(accessToken, expirationMinutes) {
    //sync the expiration time between the cookie object and the accessToken
    var expiryDate = new Date();
    expiryDate.setTime(expiryDate.getTime() + (expirationMinutes * 60 * 1000));
    $.cookie('accessToken', accessToken, {expires: expiryDate});
  };
  /**
   * Getter for OAuth2 access_token
   * @return {object} This returns the accessToken of the user's OAuth
   * authorization.
   */
  cookie.getAccessToken = function() {
    return $.cookie('accessToken');
  };
  /**
   * Getter for MaskedWallet JWT
   * @return {object} This returns the MaskedWallet JWT.
   */
  cookie.getMaskedWallet = function() {
    //Convert the obtained cookie into JSON object and return it.
    if ($.cookie('maskedWallet')) {
      return JSON.parse($.cookie('maskedWallet'));
    } else {
      return null;
    }
  };
  /**
   * Getter for changed MaskedWallet JWT
   * @return {object} This returns the Changed MaskWallet JWT.
   */
  cookie.getChangeJwt = function() {
    //Convert the obtained cookie into JSON object and return it.
    if ($.cookie('changedJwt')) {
      return JSON.parse($.cookie('changedJwt'));
    } else {
      return null;
    }
  };
  /**
   * Getter for current item object
   * @return {object} This returns currently selected item as a
   *   CurrentItem model.
   */
  cookie.getCurrentItem = function() {
    // Convert the obtained cookie from JSON format into CurrentItem
    // model and return it.
    return new Backbone.Model(JSON.parse($.cookie('currentItem')));
  };
  /**
   * Getter for transaction ID
   * @return {String} This returns the Transaction ID.
   */
  cookie.getTransactionId = function() {
    return $.cookie('transactionId');
  };
})(window.Xyz.Cookie = window.Xyz.Cookie || {});
