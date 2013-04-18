package com.google.pvacameras.server.multi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.wallet.online.jwt.JwtRequests;
import com.google.wallet.online.jwt.JwtRequests.TransactionStatusContainer;
import com.google.wallet.online.jwt.JwtResponseContainer;
import com.google.wallet.online.jwt.TransactionStatusNotification;
import com.google.wallet.online.jwt.util.JwtGenerator;

import org.apache.velocity.VelocityContext;

import java.security.InvalidKeyException;
import java.security.SignatureException;

/**
 * Handles the Full Wallet Response JWT, renders the receipt and generates the transaction status
 * notification JWT.
 */
public class ReceiptPage {

  public void handleRequest(VelocityHelper page) throws InvalidKeyException, SignatureException {
    page.header("Thanks for shopping at XYZ, inc")
        .showLogin(true)
        .showCart(false)
        .writeHeader();

    VelocityContext context = new VelocityContext();

    JwtResponseContainer fwrResponse = null;
    String gid = null;


    String fullWalletJwt = page.getParameter("fullWallet");

    if (fullWalletJwt != null) {
      // parse the FullWallet Response into JwtResponse object.
      fwrResponse = JwtGenerator.jwtToJava(JwtResponseContainer.class, fullWalletJwt, 
          page.getEnvironment().getMerchantSecret());
      gid = fwrResponse.getResponse().getGoogleTransactionId();
      String pan = page.getParameter("pan");
      String cvn = page.getParameter("cvn");
      // Process payment here using pan,cvn and billing address from fwrResponse

      // Publish the fullWalletJwt pan and cvn back to the reciept page.
      // You wouldn't do this normally but its nice to see it during development
      Gson prettyPrintingGson = new GsonBuilder().setPrettyPrinting().create();
      context.put("fullWalletJwt", prettyPrintingGson.toJson(fwrResponse.getResponse()));
      context.put("pan", pan);
      context.put("cvn", cvn);
    }

    TransactionStatusContainer tsb = JwtRequests.newTransactionStatusBuilder()
        .setIss(page.getEnvironment().getMerchantId())
        .setRequest(TransactionStatusNotification.newBuilder()
            .setGoogleTransactionId(gid)
            .setStatus(TransactionStatusNotification.Status.SUCCESS)
            .build()
        ).build();

    context.put("notificationJwt", 
        JwtGenerator.javaToJWT(tsb, page.getEnvironment().getMerchantSecret()));

    // Injects the receipt information.
    String subtotal = page.getParameter("subtotal");
    if (subtotal == null){
      subtotal = "0";
    }
    if (fwrResponse != null) {
      context.put("receiptEmail", fwrResponse.getResponse().getEmail());
    }
    context.put("receiptPrice", subtotal);
    context.put("totalPrice",
        String.format("%.2f", Double.parseDouble(subtotal) + Item.SHIPPING + Item.TAX));

    page.writeTemplate("receipt.vm", context);
    page.footer();
  }
}
