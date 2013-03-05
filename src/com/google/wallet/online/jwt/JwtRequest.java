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

package com.google.wallet.online.jwt;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for Json Web Token Payment Express API requests
 *
 * Defines a few fields that are commonly required origin, google_transaction_id
 * and transaction_id
 *
 *
 */
public class JwtRequest extends Jwt {

  /**
   * Defines type of JWT request
   */
  public enum Type {
    MASKED_WALLET ("google/wallet/online/masked/v2/request"),
    FULL_WALLET ("google/wallet/online/full/v2/request"),
    TRANSACTION_STATUS("google/wallet/online/transactionstatus/v2");

    private final String type;
    Type(String type){
      this.type = type;
    }

    /**
     * @return the typ
     */
    public String getType() {
      return type;
    }
  }

  private static final String AUD = "Google";

  private WalletBody request;

  public JwtRequest(Type type, WalletBody walletBody){
    setRequest(walletBody);
    setType(type.getType());
    setAudience(AUD);
  }

  /**
   * Converts object params to a HashMap to iterate through and store in the
   * Json Web Token
   *
   * @return HashMap of the JWT content elements
   */
  public Map<String, JsonElement> getContent() {
    Gson gson = new Gson();
    JsonObject obj = gson.toJsonTree(getRequest()).getAsJsonObject();
    Map<String, JsonElement> content = new HashMap<String, JsonElement>();
    content.put("request", obj);
    return content;
  }

  /**
   * Returns the object body representing the request portion of the JWT
   * @return Object representing the body of the message
   */
  public WalletBody getRequest() {
    return request;
  }

  /**
   * @param request the request to set
   */
  public void setRequest(WalletBody request) {
    this.request = request;
  }
}
