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
import com.google.wallet.online.jwt.JwtRequest;
import com.google.wallet.online.jwt.Pay;
import com.google.wallet.online.jwt.Ship;
import com.google.wallet.online.jwt.WalletBody;
import com.google.wallet.online.jwt.util.WalletOnlineService;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.SignatureException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet generates the MaskedWalletRequest based on parameters passed in
 * the HTTP request
 */
public class MaskedWalletRequestServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) {
    doPost(req, resp);
  }
 
  /**
   * The following parameters are parsed:
   *
   * total - order total
   * currency - order currency
   * gid - google order id for any subsequent maskedWalletRequest after the
   *   initial request
   * pnr - phone number required boolean
   */
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) {
    // Get request domain
    String origin = Config.getDomain(req);
   
    String googleId = req.getParameter("gid");
    Boolean phoneNumberRequired = new Boolean(req.getParameter("pnr"));
    
    // Create MaskedWalletRequest JWT
    WalletBody mwb = new WalletBody(Config.OAUTH_CLIENT_ID, Config.MERCHANT_NAME, origin,
        new Pay(req.getParameter("total"), req.getParameter("currency")), new Ship());
    if (googleId != null && googleId.length() > 0) {
      mwb.setGoogleTransactionId(req.getParameter("gid"));
    }
    if (phoneNumberRequired) {
      mwb.setPhoneNumberRequired(true);
    }
    JwtRequest mwr = new JwtRequest(JwtRequest.Type.MASKED_WALLET, mwb);

    WalletOnlineService ows =
        new WalletOnlineService(Config.getMerchantId(), Config.getMerchantSecret());

    // Respond to request with JWT
    PrintWriter pw;
    try {
      pw = resp.getWriter();
      pw.write(ows.javaToJwt(mwr));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (SignatureException e) {
      e.printStackTrace();
    }
  }
}
