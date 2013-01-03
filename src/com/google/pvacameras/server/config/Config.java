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

package com.google.pvacameras.server.config;

import javax.servlet.http.HttpServletRequest;

/**
 * Static configuration class that handles constants. We recommend not using this method and storing
 * your keys in a secure area.
 *
 * Available environments
 *
 *  SANDBOX - Test environment where no real world transactions are processed
 *  PRODUCTION -Production environment with real credit cards
 *
 */
public enum Config {
  SANDBOX("Merchant Id", "Merchant Key",
      "https://wallet-web.sandbox.google.com/online/v2/merchant/merchant.js"),
  PRODUCTION("Merchant Id", "Merchant Key",
      "https://wallet.google.com/online/v2/merchant/merchant.js");

  public final String id;
  public final String key;
  public final String url;
 
  // Set the environment that you're deploying against
  private static Config env = Config.SANDBOX;
  public static final String MERCHANT_NAME = "XYZ Cameras";
  public static final String OAUTH_CLIENT_ID =
      "OAuth Client ID";
  public static final String OAUTH_API_KEY = "OAuth API Key";

  public static final String TAX = "9.99";
  public static final String SHIPPING = "1.99";
  Config(String id, String key, String url) {
    this.id = id;
    this.key = key;
    this.url = url;
  }

  /**
   * Helper function to get Merchant Id based on the configured environment
   *
   * @return Merchant Id
   */
  public static String getMerchantId() {
    return env.id;
  }

  /**
   * Helper function to get Merchant Secret based on the configured environment
   *
   * @return Merchant Secret
   */
  public static String getMerchantSecret() {
    return env.key;
  }

  /**
   * Helper function to get the Wallet JS URL based on environment
   *
   * @return Wallet JS URL
   */
  public static String getJsUrl() {
    return env.url;
  }

  // Request currency
  public static final String CURRENCY = "USD";

  /**
   * Helper function to return the protocol://domain:port
   *
   * @param req servlet request
   * @return request protocol://domain:port
   */
  public static String getDomain(HttpServletRequest req) {
    String domain = req.getServerName();
    String protocol = req.getScheme();
    String port = Integer.toString(req.getServerPort());
    String origin = protocol + "://" + domain;
    if (!(port.equals("80") || port.equals("443"))) {
      origin += ":" + port;
    }
    return origin;
  }
}
