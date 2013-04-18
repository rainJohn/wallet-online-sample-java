package com.google.pvacameras.server.multi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.pvacameras.server.config.Config;
import com.google.wallet.online.jwt.JwtRequests;
import com.google.wallet.online.jwt.JwtRequests.MaskedWalletContainer;
import com.google.wallet.online.jwt.MaskedWalletRequest;
import com.google.wallet.online.jwt.MaskedWalletRequest.Builder;
import com.google.wallet.online.jwt.Pay;
import com.google.wallet.online.jwt.Ship;
import com.google.wallet.online.jwt.util.JwtGenerator;

import org.apache.velocity.VelocityContext;

import java.security.InvalidKeyException;
import java.security.SignatureException;

/**
 * Renders the order detail page and generates the MaskedWalletRequest.
 */
public class OrderPage {

  private VelocityHelper page;

  public void handleRequest(VelocityHelper page) throws InvalidKeyException, SignatureException {
    this.page = page;
    page.header("Complete your purchase")
        .showLogin(true)
        .showCart(true)
        .writeHeader();

    Item item = Item.getForId(page.getUpdatedValue("itemId"));
    page.getUpdatedValue("quantity");

    Builder mwrBuilder = MaskedWalletRequest.newBuilder()
        .setClientId(Config.OAUTH_CLIENT_ID)
        .setMerchantName(Config.MERCHANT_NAME)
        .setOrigin(page.getDomain())
        .setPay(Pay.newBuilder()
            .setCurrencyCode(Config.CURRENCY)
            .setEstimatedTotalPrice(Integer.toString(item.getPrice()))
            .build())
         .setPhoneNumberRequired(true)
         .setShip(new Ship());
    
    MaskedWalletContainer.Builder containerBuilder = JwtRequests.newMaskedWalletBuilder()
      .setIss(page.getEnvironment().getMerchantId());        
    
    VelocityContext context = new VelocityContext();
    configureJwt(mwrBuilder, containerBuilder, context);

    MaskedWalletContainer container = containerBuilder.setRequest(mwrBuilder.build()).build();
    
    page.addItemToContext(context);
    context.put("totalPrice", item.getPrice() + 9.99 + 8);

    Gson prettyPrintingGson = new GsonBuilder().setPrettyPrinting().create();
    context.put("outputString", prettyPrintingGson.toJson(container));

    context.put("maskedWalletJwt", JwtGenerator.javaToJWT(container, 
        page.getEnvironment().getMerchantSecret()));

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
  private void configureJwt(MaskedWalletRequest.Builder mwb, 
      MaskedWalletContainer.Builder mwr, 
      VelocityContext context) {
    String jwtType = page.getUpdatedValue("jwttype");
    if (jwtType == null) {
      jwtType = "valid";
    }
    
    if ("evil".equals(jwtType)) {
      
      mwb.setOrigin("http://www.evilmerchant.com");
      
    } else if ("expired".equals(jwtType)) {
      
      mwr.setIat(System.currentTimeMillis() / 1000 - 60 * 60);
      mwr.setExp(System.currentTimeMillis() / 1000 - 30 * 60);
      
    } else if ("failure".equals(jwtType)) {

      mwb.setShip(new Ship() {
        @SuppressWarnings("unused")
        public String objectId = "invalid_object_id";
      });
      
    } else if ("no_origin".equals(jwtType)) {
      
      mwb.setOrigin(null);
      
    }
        
    if ("true".equals(page.getParameter("phoneNumberOptional"))) {
      mwb.setPhoneNumberRequired(false);
    }
    if (Boolean.parseBoolean(page.getParameter("billingAgreement"))) {
      mwb.setPay(Pay.newBuilder()
          .setCurrencyCode(Config.CURRENCY)
          .setBillingAgreement(true)
          .build());
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
