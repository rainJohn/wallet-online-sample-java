/**
 * Backbone view templates and view-model binding
 */

var Xyz = Xyz || {};

(function(views) {
  /**
   * Splash page item template
   * @type {string}
   */
  var itemSelectionSubTemplate =
    '<img class="float-right total-padding-top" src=<%=image%> >' +
    '</br>' +
    'Camera <%=name%></br>' +
    '<font color=gray>$<%=price%></font>';

  /**
   * Login template
   * @type {string}
   */
  var notLoggedInTemplate =
    '<span data-role="button" data-icon="arrow-d" data-iconpos="right">Login' +
    '</span>' +
    '<ul data-role="listview" data-inset="true">' +
    '<li data-icon="false" id="login-button"><a href="#"></a>' +
    'Login with Google</li>' +
    '</ul>';

  /**
   * Logout template
   * @type {string}
   */
  var loggedInTemplate =
    '<span data-role="button" data-icon="arrow-d" data-iconpos="right">' +
    '<% shortEmail %></span>' +
    '<ul data-role="listview" data-inset="true">' +
      '<li data-icon="false">Logout</li>' +
    '</ul>';

  /**
   * Item details top template
   * @type {string}
   */
  var cameraInfoTemplate =
    '<p class="content-indent" id="camera_name">Camera <%= name %></p>' +
    '<div><table class="fill-100 small-font">' +
    '<tr><td class="fill-60"><img class="fill-100" id="camera_img" ' +
    'src=<%= image %> ></span>' +
    '<td class="fill-40">' +
    '<label class="drop-padding-match">Quantity</label>' +
    '<select data-inline="true" data-corners="false">' +
    '<option value="standard">1</option>' +
    '</select>' +
    '</span><br>' +
    '<span class="drop-padding-match">$<%= price %></span></table></div>';

  /**
   * Item details bottom template
   * @type {string}
   */
  var cameraInfoTemplateBottom =
    '<p class="content-indent" id="camera_name">Product highlights</p>' +
    '<ul id="item_details" class="content-indent padding-10 small-font">' +
    '<%= cameraDesc %>' +
    '</ul>';

  /**
   * Total order details template
   * @type {string}
   */
  var orderInfoTemplate =
    '<p class="content-indent" id="camera_name">Shopping Cart</p>' +
    '<div>' +
    '<table class="content-indent fill-100 small-font"><tr>' +
    '<td class="fill-30"><img class="fill-100" id="camera_img" ' +
    'src=<%= image %> ></td>' +
    '<td>' +
    '<label class="content-indent">Quantity 1</label>' +
    '<br>' +
    '<br>' +
    '<span class="content-indent">Camera <%= name %></span></td>' +
    '<td class="text-right"><br><br>$<%= subtotal %></td>' +
    '</table></div>' +
    '<div class="padding-10"><label class="border-1px" id="remove">Remove' +
    '</label></div>' +
    '<table class="fill-100 small-font">' +
    '<tr>' +
    '<td class="padding-30">Estimated Shipping</td>' +
    '<td class="text-right content-indent">$9.99</td>' +
    '</tr>' +
    '<tr>' +
    '<td class="padding-30">Tax (CA)</td>' +
    '<td class="text-right content-indent">$8.00</td>' +
    '</tr>' +
    '<tr>' +
    '<td class="text-right content-indent total-padding-top"><b>Total</b>' +
    '</td>' +
    '<td class="text-right content-indent total-padding-top"><b>' +
    '<div id="total">$<%= total %></div></b></td><hr>' +
    '</tr>' +
    '</table>';

  /**
   * Receipt page template
   * @type {string}
   */
  var receiptTemplate =
    '<p class="content-indent" id="camera_name">Order Summary</p>' +
    '<table class="fill-100 small-font">' +
    '<tr>' +
    '<td class="text-right">Item Subtotal</td>' +
    '<td class="text-right content-indent"><div id="receipt-subtotal" >' +
    '$<%= receiptSubtotal %></div></td>' +
    '</tr>' +
    '<tr>' +
    '<td class="text-right">Estimated Shipping</td>' +
    '<td class="text-right content-indent"">$9.99</td>' +
    '</tr>' +
    '<tr>' +
    '<td class="text-right">Tax (CA)</td>' +
    '<td class="text-right content-indent">$8.00</td>' +
    '</tr>' +
    '<tr><td> </td><td> </td></tr>' +
    '<tr>' +
    '<td class="text-right"><b>Total</b></td>' +
    '<td class="text-right content-indent"><b><div id="receipt-total">' +
    '$<%= receiptTotal %></div></b></td>' +
    '</tr>' +
    '</table>' +
    '<hr\>' +
    '<p class="content-indent" id="camera_name">Confirmation details</p>' +
    '<table class="fill-100 small-font">' +
    '<tr><td class="content-indent">Secured by ' +
    '<img src="img/GreyLogo124_26.png"></img></td></tr>' +
    '<tr><td class="content-indent">Your order confirmation number ' +
    'is AH1234567890' +
    '.Your purchase will be shipped within two business days, your ' +
    'tracking number will be sent to you via' +
    'email at <%= receiptEmail %></td>' +
    '</tr>' +
    '</table>';

  /**
   * Purchase confirmation page template
   * @type {string}
   */
  var confirmationTemplate =
    '<p class="content-indent"><b>Order summary</b></p>' +
    '<table class="fill-100 small-font">' +
    '<tr>' +
    '<td class="text-right">Item Subtotal</td>' +
    '<td class="text-right content-indent"><div id="confirm-subtotal">' +
    '$<%= confirmSubtotal%></div></td>' +
    '</tr>' +
    '<tr>' +
    '<td class="text-right">Estimated Shipping</td>' +
    '<td class="text-right content-indent">$9.99</td>' +
    '</tr>' +
    '<tr>' +
    '<td class="text-right">Tax (CA)</td>' +
    '<td class="text-right content-indent">$8.00</td>' +
    '</tr>' +
    '<tr><td> </td><td> </td></tr>' +
    '<tr>' +
    '<td class="text-right"><b>Total</b></td>' +
    '<td class="text-right content-indent"><b><div id="confirm-total">' +
    '$<%= confirmTotal %></div></b></td>' +
    '</tr>' +
    '</table><hr />' +
    '<p class="content-indent"><b>Payment Information</b></p>';

  /**
   * Purchase confirmation page bottom half template
   * @type {string}
   */
  var confirmationBottomTemplate =
    '<p class="content-indent" id="camera_name">Camera <%=name%> </p>' +
    '<div>' +
    '<table class="small-font fill-100"><tr>' +
    '<td align="right" class="fill-30 padding-10"><span><img id="camera_img" ' +
    'src=<%= image %> ></td></span>' +
    '<td>' +
    '<label class="content-indent">Quantity 1</label>' +
    '<br>' +
    '<br>' +
    '<span class="content-indent">Camera <%= name %></span></td>' +
    '<td class="text-right content-indent"><br><br>$<%= subtotal %></td>' +
    '</table></div>';

  /**
   * Login template
   * @type {string}
   */
  var loginTemplate =
    '<span>Sign in</span>';

  /**
   * Logout template
   * @type {string}
   */
  var logoutTemplate =
    '<span><%= name %></span>' +
    '<span>Sign out</span>';

  /**
   * Login view
   * @type {Backbone.View}
   */
  views.LoginView = Backbone.View.extend({
    initalize: function() {
    },

    renderLogin: function() {
      var inTemplate = _.template(loginTemplate);
      this.$el.html(inTemplate).trigger('create');
    },

    renderLogout: function(email) {
      var variables = {
            name: ''
      };
      if (email)
        variables = {
          name: email
      };
      var outTemplate = _.template(logoutTemplate, variables);
      this.$el.html(outTemplate).trigger('create');
    }
  });

  /**
   * Camera details top half view
   * @type {Backbone.View}
   */
  views.CameraInfoView = Backbone.View.extend({
    el: '#camera-content',
    initialize: function() {
      _.bindAll(this, 'render');
      this.model.on('change', this.render);
      this.render();
    },
   render: function() {
     var item = this.model.get('item');
     var variables = {
         image: item.get('image'),
         name: item.get('name'),
         price: parseFloat(item.get('price')).toFixed(2)
       };
      var template = _.template(cameraInfoTemplate, variables);
      this.$el.html(template).trigger('create');
      return this;
    }
  });

  /**
   * Camera details bottom half view
   * @type {Backbone.View}
   */
  views.CameraInfoViewBottom = Backbone.View.extend({
    el: '#camera-content2',
    initialize: function() {
      this.model.view = this;
      this.model.on('change', this.render, this);
      this.render();
    },
    render: function() {
      var item = this.model.get('item');
      var variables = {
          cameraDesc: ''
      };
      if (item) {
            variables = {
          cameraDesc: item.get('desc')};
        }
        var template = _.template(cameraInfoTemplateBottom, variables);
        this.$el.html(template).trigger('create');
    }
  });

  /**
   * Shopping cart view that displays the items in a cart.  This view is
   * currently limited to a single item until cart functionality is implemented.
   * @type {Backbone.View}
   */
  views.CartView = Backbone.View.extend({
    el: '#order-content',
    initialize: function() {
      // Bind this as this object instead of the calling object
      _.bindAll(this, 'render');

      // We'll re-render this view on any change to the cart
      this.collection.bind('add', this.render);
      // Once full cart functionality is added we'll enable re-rendering
      // when items are removed from the cart
      // this.collection.bind('remove', this.render);
    },
    render: function() {
      // Get the first item in the cart
      var item = this.collection.at(0);
      var price = parseFloat(item.get('price'));
      // Set rendering variable
      var variables = {
        image: item.get('image'),
        name: item.get('name'),
        subtotal: price.toFixed(2),
        total: price + Xyz.App.SHIPPING + Xyz.App.TAX
      };
      // generate html from template
      var template = _.template(orderInfoTemplate, variables);
      // insert generated template and trigger jquery mobile enhance
      this.$el.html(template).trigger('create');
    }
  });

  /**
   * Order confirmation top half view
   * @type {Backbone.View}
   */
  views.ConfirmationView = Backbone.View.extend({
    el: '#confirmation-content',
    initialize: function() {
      _.bindAll(this, 'render');
      this.collection.view = this;
      this.collection.on('add', this.render);
      this.model.on('change', this.render);
    },
    render: function() {
      //change to render multiple item carts
      var item = this.collection.at(0);

      if (item && this.model.get('maskedWallet')) {
        variables = {
          confirmSubtotal: parseFloat(item.get('price')).toFixed(2),
          confirmTotal: parseFloat(item.get('price')) + 9.99 + 8.00
        };
        var maskedWallet = this.model.get('maskedWallet');

        $('#conbilling').html(maskedWallet.email + '<br />' +
          maskedWallet.pay.description[0]);
        $('#conshipping').html(maskedWallet.ship.shippingAddress.name +
          '<br />' +
          maskedWallet.ship.shippingAddress.address1 + '<br />' +
          maskedWallet.ship.shippingAddress.city + ', ' +
          maskedWallet.ship.shippingAddress.state + '  ' +
          maskedWallet.ship.shippingAddress.postalCode);
        var template = _.template(confirmationTemplate, variables);
        this.$el.html(template).trigger('create');
      }

    }
  });

  /**
   * Order confirmation bottom half view
   * @type {Backbone.View}
   */
  views.ConfirmationBottomView = Backbone.View.extend({
    el: '#confirmation-content-bottom',
    initialize: function() {
      _.bindAll(this, 'render');
      this.collection.view = this;
      this.collection.on('add', this.render);
    },
    render: function() {
      var item = this.collection.at(0);
      var variables = {
        name: item.get('name'),
        image: item.get('image'),
        subtotal: parseFloat(item.get('price')).toFixed(2)
      };
      var template = _.template(confirmationBottomTemplate, variables);

      this.$el.html(template).trigger('create');
    }
  });


  /**
   *  View to display the receipt of the order
   *  @type {Backbone.View}
   */
  views.ReceiptView = Backbone.View.extend({
    el: '#receipt-content',
    initialize: function() {
      _.bindAll(this, 'render');
      this.collection.view = this;
      this.collection.on('add', this.render);
      this.model.on('change', this.render);
    },
    render: function(email) {
      var item = this.collection.at(0);
      var variables = {
        receiptEmail: '',
        receiptSubtotal: '',
        receiptTotal: ''
      };
      if (item && this.model.get('fullWallet')) {
        var email = this.model.get('fullWallet').response.response.email;
        variables = {
          receiptEmail: email,
          receiptSubtotal: parseFloat(item.get('price')).toFixed(2),
          receiptTotal: parseFloat(item.get('price')) + 9.99 + 8.00
        };
      }
      var template = _.template(receiptTemplate, variables);
      this.$el.html(template).trigger('create');
    }
  });

  /**
   * View to display all items
   * @type {Backbone.View}
   */
  views.SelectionView = Backbone.View.extend({
    el: '#category-list',
    initialize: function() {
      _.bindAll(this, 'addItem');
      this.collection.view = this;
      this.collection.bind('add', this.addItem);
      this.render();
    },
    addItem: function(item) {
      this.inner = new views.SelectionSubView({model: item, id: 'item_' +
        item.get('id')});
      this.$el.append(this.inner.$el);
      this.inner.render();
      if (this.$el.hasClass('ui-listview')) {
          this.$el.listview('refresh');
      } else {
          this.$el.trigger('create');
      }
    },
    render: function() {
      _.each(this.collection.models, this.addItem);
    }


  });

  /**
   * Subview used to generate content of the itemlist
   * @type {Backbone.View}
   */
  views.SelectionSubView = Backbone.View.extend({
    tagName: 'li',
    events: {'click': 'onClick'},
    render: function() {
      var variables = {
        name: this.model.get('name'),
        image: this.model.get('image'),
        price: this.model.get('price')
      };
      var template = _.template(itemSelectionSubTemplate, variables);
      this.$el.html(template, variables).trigger('create');
    },
    onClick: function() {
      Xyz.App.select(this.model.get('id'));
    }
  });

})(window.Xyz.Views = window.Xyz.Views || {});



