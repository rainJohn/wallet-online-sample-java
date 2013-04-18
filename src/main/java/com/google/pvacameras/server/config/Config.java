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
public class Config {

  public static final Config SANDBOX = new Config("Sandbox",
      System.getProperty("sandbox_merchant_id"), System.getProperty("sandbox_merchant_auth_key"),
      "https://wallet-web.sandbox.google.com/online/v2/merchant/merchant.js");
  public static final Config PRODUCTION = new Config("Production",
      System.getProperty("production_merchant_id"),
      System.getProperty("production_merchant_auth_key"),
      "https://wallet.google.com/online/v2/merchant/merchant.js");

  private final String id;
  private final String key;
  private final String url;
  private final String name;
  public static final String MERCHANT_NAME = "XYZ Cameras";
  public static final String OAUTH_CLIENT_ID = System.getProperty("oauth_client_id");
  public static final String OAUTH_API_KEY = System.getProperty("oauth_api_key");

  // Set the environment that you're deploying against
  private static Config environment = SANDBOX;

  public static Config getEnvironment() {
    return environment;
  }

  protected static void setEnvironment(Config environment) {
    Config.environment = environment;
  }

  Config(String name, String id, String key, String url) {
    this.name = name;
    this.id = id;
    this.key = key;
    this.url = url;
  }

  /**
   * Helper function to get Merchant Id based on the configured environment
   *
   * @return Merchant Id
   */
  public String getMerchantId() {
    return id;
  }

  /**
   * Helper function to get Merchant Secret based on the configured environment
   *
   * @return Merchant Secret
   */
  public String getMerchantSecret() {
    return key;
  }

  /**
   * Helper function to get the Wallet JS URL based on environment. To run for development against a
   * local server you should use getDevJsUrl.
   */
  public String getJsUrl() {
    return url;
  }

  /**
   * The nice name for the environment, this is added to the title of each page to indicate which
   * environment you are using.
   */
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return getName();
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
    String port = Integer.toString(req.getServerPort());
    String origin = req.getScheme() + "://" + req.getServerName();
    if (!(port.equals("80") || port.equals("443"))) {
      origin += ":" + port;
    }
    return origin;
  }
}
