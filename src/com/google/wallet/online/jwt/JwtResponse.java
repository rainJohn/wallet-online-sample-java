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
import com.google.gson.JsonObject;

import net.oauth.jsontoken.JsonToken;
/**
 * Base class for Payment Express Json Web Token request responses.
 *
 *
 */
public class JwtResponse extends Jwt {

  private WalletBody response;

  private static final String RESPONSE = "response";

  /**
   * Getter for the selectors
   *
   * @return selection containing card data
   */
  public WalletBody getResponse() {
    return response;
  }

  /**
   * Setters for the selectors
   *
   * @param response response body
   */
  protected void setResponse(WalletBody response) {
    this.response = response;
  }

  public void jsonTokenToJwt(JsonToken jwt) {
    setIat(jwt.getIssuedAt().getMillis());
    setIssuer(jwt.getIssuer());
    setAudience(jwt.getAudience());
    setType(jwt.getParamAsPrimitive("typ").getAsString());
    JsonObject payload = jwt.getPayloadAsJsonObject();
    JsonObject resp = payload.getAsJsonObject(RESPONSE);
    Gson gson = new Gson();
    setResponse(gson.fromJson(resp, WalletBody.class));
  }
}
