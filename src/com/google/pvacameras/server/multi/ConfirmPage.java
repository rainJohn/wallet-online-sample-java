package com.google.pvacameras.server.multi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.pvacameras.server.config.Config;
import com.google.wallet.online.jwt.Cart;
import com.google.wallet.online.jwt.FullWalletBody;
import com.google.wallet.online.jwt.JwtRequest;
import com.google.wallet.online.jwt.JwtRequest.Type;
import com.google.wallet.online.jwt.JwtResponse;
import com.google.wallet.online.jwt.LineItem;
import com.google.wallet.online.jwt.Pay;
import com.google.wallet.online.jwt.Ship;
import com.google.wallet.online.jwt.WalletBody;
import com.google.wallet.online.jwt.util.WalletOnlineService;

import org.apache.velocity.VelocityContext;

import java.security.InvalidKeyException;
import java.security.SignatureException;

/**
 * Handles the Masked Wallet Response JWT, renders the order confirmation page and generates the
 * Full WalletRequest.
 */
public class ConfirmPage {

  public void handleRequest(VelocityHelper page) {
    Item item = page.getItem();
    int quantity = page.getQuantity();

    JwtResponse mwrResponse = null;
    String gid = null;
    WalletOnlineService jwtServices = page.makeWalletOnlineServices();
    try {
      String maskedWalletJwt = page.getParameter("maskedWallet");
      // Parse the MaskedWallet Response into JwtResponse object.
      if (maskedWalletJwt != null) {
        mwrResponse = jwtServices.jwtToJava(maskedWalletJwt);
        gid = mwrResponse.getResponse().getGoogleTransactionId();
      }
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }

    page.header("Review order")
        .showLogin(true)
        .showCart(false)
        .writeHeader();

    VelocityContext context = new VelocityContext();
    page.addItemToContext(context);

    // Create the FullWalletBody using a generated cart object.
    FullWalletBody fwb = new FullWalletBody(makeDefaultShoppingCart(item, quantity));
    fwb.setOrigin(page.getDomain());
    fwb.setGoogleTransactionId(gid);
    try {
      // Create the FullWalletRequest object using the generated FullWalletBody object.
      context.put("fullWalletJwt",
          jwtServices.javaToJwt(new JwtRequest(Type.FULL_WALLET, fwb)));
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (SignatureException e) {
      e.printStackTrace();
    }
    if (mwrResponse != null) {
      page.addJwtResponseToContext(mwrResponse, context);

      try {
        // Create and inject the ChangeWalletRequest.
        WalletBody cwb = new WalletBody(Config.OAUTH_CLIENT_ID, Config.MERCHANT_NAME,
            page.getDomain(),
            new Pay(Integer.toString(item.getPrice()), "USD"),
            mwrResponse.getResponse().getShip() == null ? null : new Ship());
        cwb.setGoogleTransactionId(gid);
        context.put("changeJwt", jwtServices.javaToJwt(new JwtRequest(Type.MASKED_WALLET, cwb)));
      } catch (InvalidKeyException e) {
        e.printStackTrace();
      } catch (SignatureException e) {
        e.printStackTrace();
      }

      // inject the decoded JWT into the template so it can be displayed for debugging.
      Gson prettyPrintingGson = new GsonBuilder().setPrettyPrinting().create();
      context.put("outputJwt", prettyPrintingGson.toJson(mwrResponse));

      context.put("totalPrice", item.getPrice() + Item.SHIPPING + Item.TAX);
      context.put("subtotal", String.format("%.2f", (double) item.getPrice()));

      page.writeTemplate("confirm.vm", context);
    } else {
      page.getPrintWriter().println("<div id='confirmation-content'>No order information available "
          + "<a href='select'>Continue Shopping</a></div>");
    }

    page.footer();
  }

  private Cart makeDefaultShoppingCart(Item item, int quantity) {
    LineItem lineItem = new LineItem(
        item.getDescription(), quantity, Double.toString(item.getPrice()));
    lineItem.setIsDigital(false);

    LineItem tax = new LineItem("Sales Taxes", "" + Item.TAX, LineItem.Role.TAX);
    LineItem shipping =
        new LineItem("Overnight Shipping", "" + Item.SHIPPING, LineItem.Role.SHIPPING);

    // Create the cart object and injects the generated LineItem object.
    Cart cart = new Cart(Config.CURRENCY);
    cart.addItem(lineItem);
    cart.addItem(tax);
    cart.addItem(shipping);
    return cart;
  }
}
