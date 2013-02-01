package com.google.pvacameras.server.multi;

import com.google.pvacameras.server.config.Config;
import com.google.wallet.online.jwt.JwtRequest;
import com.google.wallet.online.jwt.JwtResponse;
import com.google.wallet.online.jwt.TransactionStatusBody;
import com.google.wallet.online.jwt.util.WalletOnlineService;

import org.apache.velocity.VelocityContext;

import java.security.InvalidKeyException;
import java.security.SignatureException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles the Full Wallet Response JWT, renders the receipt and generates the transaction status
 * notification JWT.
 */
public class ReceiptServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) {
    doPost(req, resp); // Get handler forwards request to post handler.
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) {

    VelocityHelper page = new VelocityHelper(req, resp);
    page.header();

    VelocityContext context = new VelocityContext();

    WalletOnlineService ows =
        new WalletOnlineService(Config.getMerchantId(), Config.getMerchantSecret());
    JwtResponse fwrResponse = null;
    String gid = null;
    String fullWalletJwt = req.getParameter("fullWallet");
    if (fullWalletJwt != null) {
      try {
        // parse the FullWallet Response into JwtResponse object.
        fwrResponse = ows.jwtToJava(fullWalletJwt);
        gid = fwrResponse.getResponse().getGoogleTransactionId();
      } catch (InvalidKeyException e) {
        e.printStackTrace();
        return;
      }
    }

    // Create and inject the transaction notification JWT.
    TransactionStatusBody tsb =
        new TransactionStatusBody(gid, TransactionStatusBody.Status.SUCCESS);
    JwtRequest tsn = new JwtRequest(JwtRequest.Type.TRANSACTION_STATUS, tsb);
    try {
      context.put("notificationJwt", ows.javaToJwt(tsn));
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (SignatureException e) {
      e.printStackTrace();
    }

    // Injects the receipt information.
    String subtotal = req.getParameter("subtotal");
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
