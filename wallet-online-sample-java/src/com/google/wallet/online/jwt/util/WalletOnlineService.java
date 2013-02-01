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

import com.google.common.collect.Lists;
import com.google.wallet.online.jwt.JwtRequest;
import com.google.wallet.online.jwt.JwtResponse;

import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.JsonTokenParser;
import net.oauth.jsontoken.crypto.HmacSHA256Verifier;
import net.oauth.jsontoken.crypto.SignatureAlgorithm;
import net.oauth.jsontoken.crypto.Verifier;
import net.oauth.jsontoken.discovery.VerifierProvider;
import net.oauth.jsontoken.discovery.VerifierProviders;

import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.List;

/**
 * This service converts between JWT string and Java object and vice versa
 *
 * @author pying
 *
 */
public class WalletOnlineService {

  private String id;
  private String secret;

  /**
   * Constructs the Wallet Online service
   * @param id Seller Id
   * @param secret Seller secret
   */
  public WalletOnlineService(String id, String secret) {
    this.id = id;
    this.secret = secret;
  }

  /**
   * Converts JWT object to JWT string
   *
   * @param jwt JWT object
   * @return JWT string
   */
  public String javaToJwt(JwtRequest jwt) throws InvalidKeyException, SignatureException {
    String jwtString = null;
    jwt.setIssuer(id);

    //Convert
    jwtString = JWTGenerator.generate(jwt, secret);
    return jwtString;
  }

  /**
   * Converts JWT strings into Java objects
   *
   * @param jwt JWT string
   * @return Java object representation of JWT.
   */
  public JwtResponse jwtToJava(String jwt)
      throws InvalidKeyException {
    JwtResponse jwtObj = new JwtResponse();

    final Verifier hmacVerifier = new HmacSHA256Verifier(secret.getBytes());
    VerifierProvider hmacLocator = new VerifierProvider() {

      @Override
      public List<Verifier> findVerifier(String id, String key){
        return Lists.newArrayList(hmacVerifier);
      }
    };
    VerifierProviders locators = new VerifierProviders();
    locators.setVerifierProvider(SignatureAlgorithm.HS256, hmacLocator);

    //Ignore Audience does not mean that the Signature is ignored
    JsonTokenParser parser = new JsonTokenParser(locators,
        new IgnoreAudience());
    JsonToken jt = parser.deserialize(jwt);

    jwtObj.jsonTokenToJwt(jt);

    return jwtObj;
  }
}
