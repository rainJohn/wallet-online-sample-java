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
import com.google.wallet.online.jwt.util.JwtGenerator;

import net.oauth.jsontoken.JsonToken;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.SignatureException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet checks the JWT's signature to ensure that it's valid. true or false is returned once
 * the JWT has been checked.
 */
public class JwtValidatorServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) {
    doPost(req, resp);
  }

  /**
   * The following parameters are parsed:
   *
   * jwt - the jwt to validate
   */
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) {
    String jwt = req.getParameter("jwt");

    PrintWriter pw = null;
    try {
      pw = resp.getWriter();
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      JsonToken jsonToken = JwtGenerator.jwtToJsonToken(jwt, 
          Config.getEnvironment().getMerchantSecret());
      pw.write("true");
    } catch (InvalidKeyException e) {
      pw.write("false");
    } catch (SignatureException e) {
      pw.write("false");
    }
  }
}
