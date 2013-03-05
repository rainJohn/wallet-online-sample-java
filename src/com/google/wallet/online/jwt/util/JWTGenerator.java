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

package com.google.wallet.online.jwt.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.wallet.online.jwt.JwtRequest;

import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.crypto.HmacSHA256Signer;

import org.joda.time.Instant;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.Calendar;
import java.util.Map;

/**
 * Helper class to convert Java Objects representations to JSON Web Tokens
 *
 *
 */
public class JWTGenerator {

  /**
   * Default Constructor
   */
  private JWTGenerator() {
  }

  /**
   * Generates the Json Web Token based on JWTRequest object provided.
   *
   * @param target
   *          the JWTRequest object to convert into a JWT string
   * @return Signed Json Web Token
   * @throws InvalidKeyException
   * @throws SignatureException
   */
  public static String generate(JwtRequest target, String merchantSecret)
      throws InvalidKeyException,
    SignatureException {
    Calendar cal = Calendar.getInstance();

    // Get signer for JWT
    HmacSHA256Signer signer = new HmacSHA256Signer(target.getIssuer(), null,
        merchantSecret.getBytes());

    // Create new JWT and set params
    JsonToken token = new JsonToken(signer);
    token.setAudience(target.getAudience());
    token.setParam("typ", target.getType());
    token.setIssuedAt(target.getIat() != null ? new Instant(target.getIat())
        : new Instant(cal.getTimeInMillis() - 5000L));

    token.setExpiration(target.getExp() != null ? new Instant(target.getExp())
        : new Instant(cal.getTimeInMillis() + 6000000L));

    // Get the payload object to modify
    JsonObject payload = token.getPayloadAsJsonObject();

    // Get the payload content to as a hashmap to insert into the payload object
    Map<String, JsonElement> params = target.getContent();

    // Iterate through HashMap adding each item to the payload
    for (String key : params.keySet()) {
      payload.add(key, params.get(key));
    }

    return token.serializeAndSign();
  }
}
