package com.google.pvacameras.server.multi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.pvacameras.server.config.Config;
import com.google.wallet.online.jwt.JwtRequest;
import com.google.wallet.online.jwt.JwtRequest.Type;
import com.google.wallet.online.jwt.Pay;
import com.google.wallet.online.jwt.Ship;
import com.google.wallet.online.jwt.WalletBody;
import com.google.wallet.online.jwt.util.WalletOnlineService;

import org.apache.velocity.VelocityContext;

import java.security.InvalidKeyException;
import java.security.SignatureException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Renders the order detail page and generates the MaskedWalletRequest.
 */
public class OrderServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) {
    doPost(req, resp); // Get handler forwards request to post handler.
  }

  /**
   * Post handler renders the template generates the maskedWalletRequest and injects the JWT into
   * the template.
   */
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) {
    String origin = Config.getDomain(req);
    VelocityHelper page = new VelocityHelper(req, resp);
    page.header();

    String itemId = req.getParameter("itemId");
    Item item = Item.getForId(itemId);

    String quantity = req.getParameter("itemQuantity");
    if (quantity == null || quantity.equals("")) {
      quantity = "1";
    }
    String unitprice = String.valueOf(item.getPrice());

    // Create the WalletBody for the maskedWalletRequest.
    WalletBody mwb = new WalletBody(Config.OAUTH_CLIENT_ID,
        Config.MERCHANT_NAME, origin, new Pay(unitprice, "USD"), new Ship());

    // Create the maskedWalletRequest object using the WalletBody.
    JwtRequest mwr = new JwtRequest(Type.MASKED_WALLET, mwb);
    mwb.setPhoneNumberRequired(true);

    configureJwt(req, mwb);
    configureJwt(req.getParameter("jwttype"), mwb, mwr);

    WalletOnlineService ows =
        new WalletOnlineService(Config.getMerchantId(), Config.getMerchantSecret());

    VelocityContext context = new VelocityContext();
    page.addItemToContext(item, quantity, context);
    context.put("totalPrice", item.getPrice() + 9.99 + 8);

    Gson prettyPrintingGson = new GsonBuilder().setPrettyPrinting().create();
    context.put("outputString", prettyPrintingGson.toJson(mwb));

    try {
      context.put("mwr", ows.javaToJwt(mwr));
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (SignatureException e) {
      e.printStackTrace();
    }

    page.writeTemplate("order.vm", context);

    page.footer();
  }

  /**
   * Allows this merchant to require different combinations of phone numbers, shipping address and
   * excluded ship types.
   */
  private void configureJwt(HttpServletRequest req, WalletBody mwb) {
    // TODO(mudge) check the default values for phone number and shipping address.
    if (req.getParameter("phoneNumberRequired") != null) {
      mwb.setPhoneNumberRequired(true);
    }
    if (req.getParameter("shippingAddressRequired") != null) {
      mwb.setShippingRequired(true);
    }
  }

  /**
   * Allows testing of failure callbacks with different errors. Configure a jwt based on a jwtType
   * which can be any of valid, evil, expired, failure, no_orgin or pay_only.
   */
  private void configureJwt(String jwtType, WalletBody mwb, JwtRequest mwr) {
    if (jwtType == null) {
      jwtType = "valid";
    }
    if (jwtType.equals("evil")) {
      mwb.setOrigin("http://www.evilmerchant.com");
    } else if (jwtType.equals("expired")) {
      mwr.setIat(System.currentTimeMillis() / 1000 - 60 * 60);
      mwr.setExp(System.currentTimeMillis() / 1000 - 30 * 60);
    } else if (jwtType.equals("failure")) {
      // TODO(mudge) make sure mwr an invalid jwt.
      mwb.getShip().setObjectId("invalid_object_id");
    } else if (jwtType.equals("no_origin")) {
      mwb.setOrigin(null);
    } else if (jwtType.equals("pay_only")) {
      mwb.setShip(null);
      mwb.setShippingRequired(null);
    }
  }
}
