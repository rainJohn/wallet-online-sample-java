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

/**
 * Renders the order detail page and generates the MaskedWalletRequest.
 */
public class OrderPage {

  private VelocityHelper page;

  public void handleRequest(VelocityHelper page) {
    this.page = page;
    page.header("Complete your purchase")
        .showLogin(true)
        .showCart(true)
        .writeHeader();

    Item item = Item.getForId(page.getUpdatedValue("itemId"));
    page.getUpdatedValue("quantity");

    // Create the WalletBody for the maskedWalletRequest.
    WalletBody maskedWalletBody = new WalletBody(Config.OAUTH_CLIENT_ID, Config.MERCHANT_NAME,
        page.getDomain(), new Pay(Integer.toString(item.getPrice()), "USD"), new Ship());

    // Create the maskedWalletRequest object using the WalletBody.
    JwtRequest maskedWalletRequest = new JwtRequest(Type.MASKED_WALLET, maskedWalletBody);
    maskedWalletBody.setPhoneNumberRequired(true);

    VelocityContext context = new VelocityContext();
    configureJwt(maskedWalletBody, maskedWalletRequest, context);

    WalletOnlineService ows = page.makeWalletOnlineServices();

    page.addItemToContext(context);
    context.put("totalPrice", item.getPrice() + 9.99 + 8);

    Gson prettyPrintingGson = new GsonBuilder().setPrettyPrinting().create();
    context.put("outputString", prettyPrintingGson.toJson(maskedWalletBody));

    try {
      context.put("maskedWalletJwt", ows.javaToJwt(maskedWalletRequest));
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (SignatureException e) {
      e.printStackTrace();
    }
    if ("true".equals(page.getParameter("useCreateWalletButton"))) {
      context.put("createWalletButtonFunction", "true");
    }

    if ("true".equals(page.getParameter("inForm"))) {
      context.put("buttonInForm", "true");
    }

    page.writeTemplate("order.vm", context);

    page.footer();
  }

  /**
   * Allows testing of failure callbacks with different errors. Configure a jwt based on a jwtType
   * which can be any of valid, evil, expired, failure or no_orgin. Allows this merchant to require
   * phone number and/or shipping address.
   */
  private void configureJwt(WalletBody mwb, JwtRequest mwr, VelocityContext context) {
    String jwtType = page.getUpdatedValue("jwttype");
    if (jwtType == null) {
      jwtType = "valid";
    }
    if (jwtType.equals("evil")) {
      mwb.setOrigin("http://www.evilmerchant.com");
    } else if (jwtType.equals("expired")) {
      mwr.setIat(System.currentTimeMillis() / 1000 - 60 * 60);
      mwr.setExp(System.currentTimeMillis() / 1000 - 30 * 60);
    } else if (jwtType.equals("failure")) {
      mwb.getShip().setObjectId("invalid_object_id");
    } else if (jwtType.equals("no_origin")) {
      mwb.setOrigin(null);
    }
    if ("true".equals(page.getParameter("phoneNumberOptional"))) {
      mwb.setPhoneNumberRequired(false);
    }
    if ("true".equals(page.getParameter("shippingAddressOptional"))) {
      mwb.setShip(null);
    }
    if ("true".equals(page.getParameter("useMinimalAddresses"))) {
      mwb.setUseMinimalAddresses(true);
    }

    context.put(jwtType, " selected='selected'");
  }
}
