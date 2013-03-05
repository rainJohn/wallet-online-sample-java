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

package com.google.xyz.server.config;

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

  SANDBOX(System.getProperty("sandbox_merchant_id"),
      System.getProperty("sandbox_merchant_auth_key"),
      "https://wallet-web.sandbox.google.com/online/v2/merchant/merchant.js"),
  PRODUCTION(System.getProperty("production_merchant_id"),
      System.getProperty("production_merchant_auth_key"),
      "https://wallet.google.com/online/v2/merchant/merchant.js"),
  UNKNOWN("", "", "");

  private final String id;
  private final String key;
  private final String url;
  public static final String MERCHANT_NAME = "XYZ Cameras";
  public static final String OAUTH_CLIENT_ID = System.getProperty("oauth_client_id");
  public static final String OAUTH_API_KEY = System.getProperty("oauth_api_key");

  // Set the environment that you're deploying against
  private static Config env = SANDBOX;
  static {
    if (System.getProperty("online_wallet_enviroment") != null) {
      env = Config.valueOf(System.getProperty("online_wallet_enviroment"));
    }
  }

  public static Config getEnvironment() {
    return environment;
  }

  public static Config getEnvironment(String string) {
    Config environment = UNKNOWN;
    try {
      environment = Config.valueOf(string.toUpperCase());
    } catch (IllegalArgumentException e) {
    }
    return environment;
  }

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
   * Helper function to get the Wallet JS URL based on environment. To run for development against a
   * local server you should use getDevJsUrl.
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
    String port = Integer.toString(req.getServerPort());
    String origin = req.getScheme() + "://" + req.getServerName();
    if (!(port.equals("80") || port.equals("443"))) {
      origin += ":" + port;
    }
    return origin;
  }

  public static WalletOnlineService makeWalletOnlineServices() {
    return new WalletOnlineService(Config.getEnvironment().getMerchantId(),
        Config.getEnvironment().getMerchantSecret());
  }}
