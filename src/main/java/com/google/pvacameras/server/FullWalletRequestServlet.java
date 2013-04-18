/**
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in
 * compliance with the License.You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.  See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.pvacameras.server;

import com.google.pvacameras.server.config.Config;
import com.google.wallet.online.jwt.Cart;
import com.google.wallet.online.jwt.FullWalletRequest;
import com.google.wallet.online.jwt.JwtRequests;
import com.google.wallet.online.jwt.JwtRequests.FullWalletContainer;

import com.google.wallet.online.jwt.LineItem;
import com.google.wallet.online.jwt.util.JwtGenerator;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet handles requests for the Full Wallet Request JWT. It parses the post parameters and
 * generates the Full Wallet Request JWT. Currently this servlet only handles a single item.
 *
 */
public class FullWalletRequestServlet extends HttpServlet {

  private static final Logger logger = 
      Logger.getLogger(FullWalletRequestServlet.class.getSimpleName());
  
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    doPost(req, resp);
  }

  /**
   * The following parameters are parsed:
   *
   *  description - description of the object
   *  quantity - number of items purchased
   *  unitprice - price per item
   *  gid - Google transaction ID
   */
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String origin = Config.getDomain(req);

    // Get post params
    String unitprice = req.getParameter("unitprice");
    int quantity = validateQuantity(req.getParameter("quantity"));
    
    if (unitprice == null) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Unit price");
      return;
    }
    
    if (quantity < 0) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid quantity");
      return;
    }
    
    FullWalletContainer container = JwtRequests.newFullWalletBuilder()
        .setIss(Config.getEnvironment().getMerchantId())
        .setRequest(FullWalletRequest.newBuilder()
            .setOrigin(origin)
            .setClientId(Config.OAUTH_CLIENT_ID)
            .setGoogleTransactionId(req.getParameter("gid"))
            .setMerchantName(Config.MERCHANT_NAME)
            .setCart(Cart.newBuilder()
                .setCurrencyCode(Config.CURRENCY)
                .addLineItem(LineItem.newBuilder()
                    .setDescription(req.getParameter("description"))
                    .setQuantity(Integer.valueOf(quantity))
                    .setUnitPrice(unitprice)
                    .build())
                .addLineItem(LineItem.newBuilder()
                    .setDescription("Sales Taxes")
                    .setRole(LineItem.Role.TAX)
                    .setTotalPrice("8.00")
                    .build())
                .addLineItem(LineItem.newBuilder()
                    .setDescription("Overnight Shipping")
                    .setRole(LineItem.Role.SHIPPING)
                    .setTotalPrice("9.99")
                    .build())
            .build())
         .build())
         .build();
    
    PrintWriter pw;
    try {
      
      pw = resp.getWriter();
      pw.write(JwtGenerator.javaToJWT(container, Config.getEnvironment().getMerchantSecret()));
      
    } catch (InvalidKeyException e) {
      
      logger.log(Level.SEVERE, "Invalid key exception ", e);
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

    } catch (SignatureException e) {

      logger.log(Level.SEVERE, "Signature Exception", e);
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      
    }
  }
  
  private int validateQuantity(String quantity) {
    if (quantity == null) {
      return -1;
    }
    try {
      return Integer.parseInt(quantity);
    } catch (NumberFormatException ignore) {
      return -1;
    }
  }
}
