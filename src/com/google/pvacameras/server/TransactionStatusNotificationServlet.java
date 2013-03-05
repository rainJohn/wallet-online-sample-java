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
import com.google.wallet.online.jwt.TransactionStatusBody;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.SignatureException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet generates the TransactionStatusNotification. It takes a post parameter gid.
 */
public class TransactionStatusNotificationServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp){
    doPost(req, resp);
  }

  /**
   * The following parameters are parsed:
   *
   * gid - Google Transaction Id
   */
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) {

    // Get Google Id
    String googleId = req.getParameter("gid");

    // Generate TransactionStatusNotification JWT
    TransactionStatusBody tsb =
        new TransactionStatusBody(googleId, TransactionStatusBody.Status.SUCCESS);
    JwtRequest tsn = new JwtRequest(JwtRequest.Type.TRANSACTION_STATUS, tsb);

    // Respond to request
    try {
      PrintWriter pw;
      pw = resp.getWriter();
      pw.print(Config.makeWalletOnlineServices().javaToJwt(tsn));
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (SignatureException e) {
      e.printStackTrace();
    }
  }
}
