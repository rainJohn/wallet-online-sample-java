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
import com.google.wallet.online.jwt.FullWalletBody;
import com.google.wallet.online.jwt.JwtRequest;
import com.google.wallet.online.jwt.LineItem;
import com.google.wallet.online.jwt.util.WalletOnlineService;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.SignatureException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet handles requests for the Full Wallet Request JWT. It parses the post parameters and
 * generates the Full Wallet Request JWT. Currently this servlet only handles a single item.
 *
 */
public class FullWalletRequestServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) {
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
  public void doPost(HttpServletRequest req, HttpServletResponse resp) {
    String origin = Config.getDomain(req);

    // Get post params
    String description = req.getParameter("description");
    String quantity = req.getParameter("quantity");
    String unitprice = req.getParameter("unitprice");
    String gid = req.getParameter("gid");

    // Create FullWalletRequest JWT
    WalletOnlineService ows =
        new WalletOnlineService(Config.getMerchantId(), Config.getMerchantSecret());

    LineItem item = new LineItem(description, Integer.valueOf(quantity), unitprice);
    item.setIsDigital(false);

    LineItem tax = new LineItem("Sales Taxes", "8.00", LineItem.Role.TAX);
    LineItem shipping = new LineItem("Overnight Shipping", "9.99", LineItem.Role.SHIPPING);

    Cart cart = new Cart(Config.CURRENCY);
    cart.addItem(item);
    cart.addItem(tax);
    cart.addItem(shipping);
    FullWalletBody fwb = new FullWalletBody(cart);
    fwb.setOrigin(origin);
    fwb.setGoogleTransactionId(gid);
    JwtRequest fwr = new JwtRequest(JwtRequest.Type.FULL_WALLET, fwb);

    PrintWriter pw;
    try {
      pw = resp.getWriter();
      pw.write(ows.javaToJwt(fwr));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (SignatureException e) {
      e.printStackTrace();
    }
  }
}
