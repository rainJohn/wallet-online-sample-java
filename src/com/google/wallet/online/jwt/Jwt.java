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

/**
 * Base class for Json Web Tokens
 */
public abstract class Jwt {

  private String iss;
  private String aud;
  private String typ;
  private Long iat;
  private Long exp;

  public String getIssuer() {
    return iss;
  }

  public void setIssuer(String iss) {
    this.iss = iss;
  }

  public Long getIat() {
    return iat;
  }

  public void setIat(Long iat) {
    this.iat = iat;
  }

  public Long getExp() {
    return exp;
  }

  public void setExp(Long exp) {
    this.exp = exp;
  }

  public String getType() {
    return typ;
  }

  public void setType(String typ) {
    this.typ = typ;
  }

  public String getAudience() {
    return aud;
  }

  public void setAudience(String aud) {
    this.aud = aud;
  }
}
