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
import com.google.wallet.online.jwt.util.WalletOnlineService;

import org.apache.velocity.VelocityContext;

import java.security.InvalidKeyException;
import java.security.SignatureException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles the Masked Wallet Response JWT, renders the order confirmation page and generates the
 * Full WalletRequest.
 */
public class ConfirmServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) {
    doPost(req, resp); // Get handler forwards request to post handler.
  }

  /**
   * Post handler renders the template generates the FullWalletRequest and
   * deciphered MaskedWalletResponse. Then injects these JWT into the template.
   */
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) {
    String itemId = req.getParameter("itemId");
    Item item = Item.getForId(itemId);
    String quantity = req.getParameter("itemQuantity");
    if (quantity == null || quantity.isEmpty()) {
      quantity = "1";
    }

    WalletOnlineService ows =
        new WalletOnlineService(Config.getMerchantId(), Config.getMerchantSecret());

    VelocityHelper velocity = new VelocityHelper(req, resp);

    JwtResponse mwrResponse = null;
    String gid = null;
    try {
      String maskedWalletJwt = req.getParameter("maskedWallet");
      // Parse the MaskedWallet Response into JwtResponse object.
      if (maskedWalletJwt != null) {
        mwrResponse = ows.jwtToJava(maskedWalletJwt);
        gid = mwrResponse.getResponse().getGoogleTransactionId();
      }
    } catch (InvalidKeyException e) {
      e.printStackTrace();
      return;
    }

    velocity.header();

    VelocityContext context = new VelocityContext();
    velocity.addItemToContext(item, quantity, context);

    // Create the FullWalletBody using a generated cart object.
    FullWalletBody fwb = new FullWalletBody(makeDefaultShoppingCart(item, quantity));
    fwb.setOrigin(Config.getDomain(req));
    fwb.setGoogleTransactionId(gid);

    try {
      // Create the FullWalletRequest object using the generated FullWalletBody object.
      JwtRequest fwr = new JwtRequest(Type.FULL_WALLET, fwb);
      context.put("fullWalletJwt", ows.javaToJwt(fwr));
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (SignatureException e) {
      e.printStackTrace();
    }
    if (mwrResponse != null) {
      velocity.addJwtResponseToContext(mwrResponse, context);
    }

    // inject the decoded JWT into the template so it can be displayed for debugging.
    Gson prettyPrintingGson = new GsonBuilder().setPrettyPrinting().create();
    context.put("outputJwt", prettyPrintingGson.toJson(mwrResponse));
    context.put("changeJwt", req.getParameter("changeJwt"));

    context.put("totalPrice", item.getPrice() + Item.SHIPPING + Item.TAX);
    context.put("subtotal", String.format("%.2f", (double) item.getPrice()));

    velocity.writeTemplate("confirm.vm", context);

    velocity.footer();
  }

  private Cart makeDefaultShoppingCart(Item item, String quantity) {
    LineItem lineItem = new LineItem(
        item.getDescription(), Integer.parseInt(quantity), Double.toString(item.getPrice()));
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
