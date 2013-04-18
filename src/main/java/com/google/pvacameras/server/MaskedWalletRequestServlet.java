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
import com.google.wallet.online.jwt.JwtRequests;
import com.google.wallet.online.jwt.JwtRequests.MaskedWalletContainer;
import com.google.wallet.online.jwt.MaskedWalletRequest;
import com.google.wallet.online.jwt.Pay;
import com.google.wallet.online.jwt.Ship;
import com.google.wallet.online.jwt.util.JwtGenerator;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet generates the MaskedWalletRequest based on parameters passed in
 * the HTTP request
 */
public class MaskedWalletRequestServlet extends HttpServlet {

  private static final Logger logger = 
      Logger.getLogger(MaskedWalletRequestServlet.class.getSimpleName());
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    // Create MaskedWalletRequest JWT
    MaskedWalletContainer maskedWalletContainer = JwtRequests.newMaskedWalletBuilder()
      .setIss(Config.getEnvironment().getMerchantId())
      .setRequest(MaskedWalletRequest.newBuilder()
          .setClientId(Config.OAUTH_CLIENT_ID)
          .setOrigin(Config.getDomain(req))
          .setMerchantName(Config.MERCHANT_NAME)
          .setPhoneNumberRequired(Boolean.parseBoolean(req.getParameter("pnr")))
          .setUseMinimalAddresses(Boolean.parseBoolean(req.getParameter("min")))
          .setGoogleTransactionId(req.getParameter("gid"))
          .setShip(new Ship())
          .setPay(Pay.newBuilder()
              .setCurrencyCode(getParameterOrDefault(req, "currency", "USD"))
              .setEstimatedTotalPrice(getParameterOrDefault(req, "total", "0"))
              .build()
           )
          .build()
       )
      .build();
    
    try {
      // Sign the JWT
      String signedJwt = JwtGenerator.javaToJWT(maskedWalletContainer, 
          Config.getEnvironment().getMerchantSecret());
      // send the JWT 
      resp.getWriter().print(signedJwt);
    } catch (InvalidKeyException ex) {
      logger.log(Level.SEVERE, "Invalid key exception ", ex);
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    } catch (SignatureException ex) {
      logger.log(Level.SEVERE, "Signature exception ", ex);
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private String getParameterOrDefault(HttpServletRequest req, String key, String defaultValue) {
    String result = req.getParameter(key);
    if (result == null) {
      return defaultValue;
    }
    return result;
  }
}

