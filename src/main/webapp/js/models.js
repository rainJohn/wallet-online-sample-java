/**
 * This file contains the Backbone models that are used to represent different
 * objects in the web app.
 *
 */

/**
 * Representation of the customer and their associated data.
 * This object stores all data associated with the user including
 * maskedWalletResponse and fullWalletResponse
 */
var Xyz = Xyz || {};

(function(models) {
  /**
   * Model representing user information
   * @type {Backbone.Model}
   */
   models.User = Backbone.Model.extend({
   defaults: {
     loggedIn: false,
     email: null,
     shortEmail: null,
     maskedWallet: null,
     fullWallet: null
   }
  });

  /**
   * Model representing purchaseable items
   * @type {Backbone.Model}
   */
   models.Item = Backbone.Model.extend({
    defaults: {
      id: '',
      name: 'Camera',
      price: 0,
      desc: 'Description.',
      image: 'img/wallet.jpg'
    },
    initialize: function() {
    }
  });

  /**
   *  Representation of the currently selected item.
   *  This is binded with CameraInfoView and CameraInfoViewBottom
   *  @type {Backbone.Model}
   */
   models.CurrentItem = Backbone.Model.extend({
    defaults: {
      defined: false,
      item: new models.Item()
    },
    initialize: function() {
      _.bindAll(this, 'setItem');
    },
    setItem: function(currItem) {
      if (currItem)
        {
        this.set({
          defined: true,
          item: currItem
        });
      Xyz.Cookie.setCurrentItem(currItem);
      }
    }
  });

  /**
   * Collection of purchaseable items.
   * @type {Backbone.Collection}
   */
   models.Items = Backbone.Collection.extend({
    model: models.Item
  });

  /**
   * Collection of items in the shopping cart.
   * Currently the views only render a single item and full shopping cart
   * functionality has not been built out.
   * @type {Backbone.Collection}
   */
   models.Cart = Backbone.Collection.extend({
    model: models.Item
  });
})(window.Xyz.Models = window.Xyz.Models || {});
