package com.google.pvacameras.server.multi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.pvacameras.server.config.Config;
import com.google.wallet.online.jwt.Cart;
import com.google.wallet.online.jwt.FullWalletRequest;
import com.google.wallet.online.jwt.JwtRequests;
import com.google.wallet.online.jwt.JwtRequests.FullWalletContainer;
import com.google.wallet.online.jwt.JwtRequests.MaskedWalletContainer;
import com.google.wallet.online.jwt.JwtResponseContainer;
import com.google.wallet.online.jwt.LineItem;
import com.google.wallet.online.jwt.MaskedWalletRequest;
import com.google.wallet.online.jwt.Pay;
import com.google.wallet.online.jwt.Ship;
import com.google.wallet.online.jwt.util.JwtGenerator;

import org.apache.velocity.VelocityContext;

import java.security.InvalidKeyException;
import java.security.SignatureException;

/**
 * Handles the Masked Wallet Response JWT, renders the order confirmation page and generates the
 * Full WalletRequest.
 */
public class ConfirmPage {

  public void handleRequest(VelocityHelper page) throws InvalidKeyException, SignatureException {
    Item item = page.getItem();
    int quantity = page.getQuantity();

    JwtResponseContainer mwrResponse = null;
    String gid = null;

    String maskedWalletJwt = page.getParameter("maskedWallet");
    
    // Parse the MaskedWallet Response into JwtResponse object.
    if (maskedWalletJwt != null) {
      
      mwrResponse = JwtGenerator.jwtToJava(JwtResponseContainer.class, maskedWalletJwt, 
          page.getEnvironment().getMerchantSecret());
      gid = mwrResponse.getResponse().getGoogleTransactionId();
    }


    FullWalletContainer container = JwtRequests.newFullWalletBuilder()
        .setIss(page.getEnvironment().getMerchantId())
        .setRequest(FullWalletRequest.newBuilder()
            .setCart(makeDefaultShoppingCart(item, quantity))
            .setClientId(Config.OAUTH_CLIENT_ID)
            .setGoogleTransactionId(gid)
            .setMerchantName(Config.MERCHANT_NAME)
            .setOrigin(page.getDomain())
            .build())
         .build();

    VelocityContext context = new VelocityContext();

    context.put("fullWalletJwt",
        JwtGenerator.javaToJWT(container, page.getEnvironment().getMerchantSecret()));
    
    page.header("Review order")
    .showLogin(true)
    .showCart(false)
    .writeHeader();

    page.addItemToContext(context);

    if (mwrResponse != null) {
      page.addJwtResponseToContext(mwrResponse, context);

      // Create and inject the ChangeWalletRequest.
      MaskedWalletContainer changeContainer = JwtRequests.newMaskedWalletBuilder()
          .setIss(page.getEnvironment().getMerchantId())
          .setRequest(MaskedWalletRequest.newBuilder()
              .setGoogleTransactionId(gid)
              .setClientId(Config.OAUTH_CLIENT_ID)
              .setMerchantName(Config.MERCHANT_NAME)
              .setOrigin(page.getDomain())
              .setPay(Pay.newBuilder().setCurrencyCode(Config.CURRENCY)
                  .setEstimatedTotalPrice(Integer.toString(item.getPrice()))
                  .build())
                  .setShip(mwrResponse.getResponse().getShip() == null ? null : new Ship())
                  .build())
                  .build();

      context.put("changeJwt", JwtGenerator.javaToJWT(changeContainer, 
          page.getEnvironment().getMerchantSecret()));

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
    return Cart.newBuilder()
      .setCurrencyCode(Config.CURRENCY)
      .addLineItem(LineItem.newBuilder()
          .setDescription(item.getDescription())
          .setQuantity(quantity)
          .setUnitPrice(Double.toString(item.getPrice()))
          .setIsDigital(false)
          .build())
      .addLineItem(LineItem.newBuilder()
          .setDescription("Sales Taxes")
          .setTotalPrice(Double.toString(Item.TAX))
          .setRole(LineItem.Role.TAX)
          .build())
      .addLineItem(LineItem.newBuilder()
          .setDescription("Overnight Shipping")
          .setTotalPrice(Double.toString(Item.SHIPPING))
          .setRole(LineItem.Role.SHIPPING)
          .build())
      .build();
  }
}
