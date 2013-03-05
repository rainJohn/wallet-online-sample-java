/**
 * The Xyz.Wallet namespace handles all aspects of the application related to
 * Google Wallet functionality. It requires cookies.js as many of the parameters
 * are stored in cookies to try to be as server agnostic as possible.
 *
 */

// Create Xyz namespace if it's hasn't already been created
var Xyz = Xyz || {};

(function(wallet) {

  /**
   * Static path to the JWT validation URL
   * @type {string}
   */
  wallet.VALIDATE_URL = 'validate';

  /**
   * Static path to Masked Wallet JWT generation URL
   * @type {string}
   */
  wallet.MWR_URL = 'mwr';

  /**
   * Google Transaction Id
   * @type {string}
   */
  wallet.transactionId = Xyz.Cookie.getTransactionId();

  /**
   * Edit cart JWT
   * @type {string}
   */
  wallet.changeJwt = Xyz.Cookie.getChangeJwt();

  /**
   * Get previously stored accessToken from cookies
   * The accessToken is stored in a cookie so we can allow users to persist
   * their Wallet authorization.
   */
  $(function() {
    accessToken = Xyz.Cookie.getAccessToken();
    google.wallet.online.setAccessToken(accessToken);
    wallet.checkAuth();
  });

  /**
   * Helper function to generate the post body that's posted to the server to
   * generate the JWT.  It uses the price in currentItem.
   *
   * @return {String} post body key value pair.
   */
  wallet.itemToPostBody = function(param) {
    var postBody = 'total=' + Xyz.App.currentItem.get('item').get('price') +
      '&currency=USD';
    if (param) {
      postBody += '&gid=' + param;
    }
    return postBody;
  };

  /**
   * Creates the Full Wallet post parameters
   * @return {String} post key value pairs to define the item being purchased.
   */
  wallet.fwrPostBody = function() {
    //get the current item
    var item = Xyz.App.currentItem.get('item');

    //generate post body from current item
    var cart = 'description=' + item.get('desc') + '&' + 'quantity=' +
      '1' + '&' + 'unitprice=' + item.get('price') + '&' + 'currency=' +
      'USD' + '&' + 'gid=' + wallet.transactionId;
    return cart;
  };

  /**
   * Check if Wallet is a preauthed method of payment. If Wallet is preauthed
   * store the access token associated for future use. <code>authorize()</code>
   * automatically sets the Wallet access token so we don't need to do that here
   */
  wallet.checkAuth = function() {
    google.wallet.online.authorize({
        // using the global OAuth2 client id
        'clientId' : clientId,
        'callback' : function(param) {
          // If the user hasn't pre-authorized Wallet null will be passed as the
          // parameter
          // If it's not null the access token will be passed
          if (param) {
            // Persist our access token setting in a cookie
            Xyz.Cookie.setAccessToken(param.access_token, param.expires_in);
          }else {
            Xyz.Cookie.setAccessToken('', 1);
          }
        }
    });
  };

  /**
   * Button creation callback. When Wallet finishes creating the button it calls
   * the specified callback. This function then removes previously appended
   * buttons and appends the latest button.
   *
   * @param {DOM
   *          Node} params the created Wallet button.
   */
  wallet.buttonReady = function(params) {
    // Remove any previously appended buttons
    $('#gWalletDiv').remove();
    // Append Wallet button to page
    var buttonDiv = document.createElement('div');
    buttonDiv.id = 'gWalletDiv';
    buttonDiv.appendChild(params.walletButtonElement);
    document.getElementById('buybutton').appendChild(buttonDiv);
    // Show JQuery mobile loading spinners.
    buttonDiv.childNodes[0].addEventListener('click', function() {
      $.mobile.showPageLoadingMsg('a', 'loading', false);
    });
  };

  /**
   * Creates new Google Wallet button with the JWT provided
   *
   * @param {String}
   *          jwt Masked Wallet Request JWT.
   */
  wallet.createButton = function() {
    if (Xyz.App.currentItem.get('defined')) {
      $.post(wallet.MWR_URL, wallet.itemToPostBody(), function(jwt) {
        console.log('Masked Wallet Request:' + JSON.stringify(jwt));
        google.wallet.online.createWalletButton({
            'jwt' : jwt,
            'buttonStyle' : 'CUSTOM_10_DOLLARS_OFF_EXCLUDING',
            'success' : wallet.maskedWalletSuccess,
            'failure' : wallet.maskedWalletFailure,
            'ready' : wallet.buttonReady
        });
      });
    }
  };

  /**
   * Masked Wallet request success handler. This function handles success from
   * the various ways to initiate the purchase flow.
   *
   * @param {object}
   *          param The MaskedWalletResponse.
   */
  wallet.maskedWalletSuccess = function(param) {
    // Pull MaskedWalletResponse JS object from the response
    Xyz.App.user.set('maskedWallet', param.response.response);

    // The Wallet transaction id ties various the various api requests for a
    // single order
    // together. It's returned in the Masked Wallet Response. Here we're
    // setting the global Transaction Id.
    wallet.transactionId = param.response.response.googleTransactionId;

    console.log('Masked Wallet Response:' + JSON.stringify(param.response));

    // Persist Masked Wallet Response and Transaction Id for page refreshes
    Xyz.Cookie.setMaskedWallet(param.response.response);
    Xyz.Cookie.setTransactionId(wallet.transactionId);

    // Hide JQuery mobile loading spinners
    $.mobile.hidePageLoadingMsg();

    // Validate the JWT before proceeding
    $.post(wallet.VALIDATE_URL, 'jwt=' + param.jwt, function(response) {
      if (response === 'true') {
        $.mobile.changePage('#confirmation-page', {
          transition: 'slide'
        });
      } else {
        window.alert('unverified jwt');
      }
    });
    // Ajax call to server to create the change item JWT
    // This is called in the handler to reduce latency in the future
    $.post(wallet.MWR_URL, wallet.itemToPostBody(wallet.transactionId),
      function(param) {
      wallet.changeJwt = param;
      Xyz.Cookie.setChangeJwt(param);
    });
  };

  /**
   * Masked Wallet Request failure handler. This function handles failures from
   * the various ways to implement the purchase flow. You should implement your
   * error handling code here.
   *
   * @param {Object}
   *          error ErrorResponse.
   */
  wallet.maskedWalletFailure = function(error) {
    // Hide spinner
    $.mobile.hidePageLoadingMsg();

    // log error message
    console.log('There was an error getting the Masked Wallet: ' +
      JSON.stringify(error));
  };

  /**
   * Continue Checkout button logic. Request Masked Wallet should be tied to
   * your continue checkout button. This allows you to get the
   * maskedWalletRequest for pre-authorized users with out any user interaction.
   *
   */
  wallet.requestMaskedWallet = function() {
    if (Xyz.Cookie.getAccessToken()) {
      $.mobile.showPageLoadingMsg('a', 'loading', false);

      $.post(wallet.MWR_URL, wallet.itemToPostBody(), function(jwt) {
        google.wallet.online.requestMaskedWallet({
            'jwt' : jwt,
            'success' : wallet.maskedWalletSuccess,
            'failure' : wallet.maskedWalletFailure
        });
      });
    } else {
      $.mobile.changePage('#sign-on', {
        transition: 'slide'
      });
    }
  };

  /**
   * Calls ChangeMaskedWallet using the JWT which allows pops up the choose to
   * allow the user to edit their payment or shipping selection.
   *
   * @param {String}
   *          jwt maskedWalletRequest JWT with Google Transaction Id.
   */
  wallet.changeMaskedWallet = function(jwt) {
    google.wallet.online.changeMaskedWallet({
        'jwt' : wallet.changeJwt,
        'success' : wallet.maskedWalletSuccess,
        'failure' : wallet.maskedWalletFailure
    });
  };

  /**
   * Handles the Full Wallet Request success case. The parameter passed to this
   * callback contains the credit card number and Full Wallet Response object.
   * If you processed a card You would pull out the PAN from the response and
   * send it to your payment processor before transitioning to your receipt
   * page.
   *
   * @param {Object}
   *          param PAN and Full Wallet Request object.
   */
  wallet.fullWalletSuccess = function(param) {
    if (console) {
      console.log('Full Wallet Response:' + JSON.stringify(param));
    }
    //Hide spinner
    $.mobile.hidePageLoadingMsg();

    //Update the user object with the Full Wallet Response so it can be
    //accessed by views
    Xyz.App.user.set('fullWallet', param);

    //Validate JWT and process order
    //Here since we're not processing a real world transaction we only validate
    //the JWT
    $.post(wallet.VALIDATE_URL, 'jwt=' + param.jwt, function(response) {
      if (response === 'true') {
        $.mobile.changePage('#receipt', {
          transition: 'slide'
        });
        //on notify Wallet of the transaction status
        wallet.notifyTransactionStatus();
      } else {
        window.alert('unverified jwt');
      }
    });
  };

  /**
   * Full Wallet Request failure handler. You should implement your error
   * handling code here.
   *
   * @param {Object}
   *          error Defines the code and details of why the request failed.
   */
  wallet.fullWalletFailure = function(error) {
    // Hide spinner
    $.mobile.hidePageLoadingMsg();

    // log error message
    console.log('There was an error getting the Full Wallet: ' +
      JSON.stringify(error));
  };

  /**
   * Full Wallet Request requests the one time card number from Wallet.  This is
   * called when the customer confirms the purchase.  Below we're using ajax to
   * pull the full wallet request JWT but it could also be rendered in the page.
   */
  wallet.requestFullWallet = function() {
    //ajax request for JWT
    $.post('fwr', wallet.fwrPostBody(), function(jwt) {
      //log the JWT
      console.log('Full Wallet Request:' + JSON.stringify(jwt));
      //show the loading spinner
      $.mobile.showPageLoadingMsg('a', 'loading', false);
      //request full wallet
      google.wallet.online.requestFullWallet({
          'jwt' : jwt,
          'success' : wallet.fullWalletSuccess,
          'failure' : wallet.fullWalletFailure
      });
    });
  };

  /**
   * NotifyTransactionStatus is used to notify Wallet of the final transaction
   * status. You need to call this function after you've processed the one time
   * card.
   */
  wallet.notifyTransactionStatus = function() {
    //ajax request for JWT
    $.post('tsn', 'gid=' + wallet.transactionId, function(jwt) {
      //notify Google Wallet
      google.wallet.online.notifyTransactionStatus({
        'jwt' : jwt
      });
      //We're authorizing here in case the user wishes to continue shopping to
      //ensure a smoother flow for the second time they purchase
      wallet.checkAuth();
    });
  };
})(window.Xyz.Wallet = window.Xyz.Wallet || {});
