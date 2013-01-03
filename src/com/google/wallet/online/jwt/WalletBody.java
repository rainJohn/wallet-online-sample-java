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
 * Bean to represent the request/response body
 *
 * @author pying
 *
 */
public class WalletBody {

  String googleTransactionId;
  String merchantTransactionId;
  String clientId;
  String merchantName;
  String origin;
  String email;
  Boolean phoneNumberRequired;
  Pay pay;
  Ship ship;


  public WalletBody() {
    //Empty constructor used in gson conversion of JSON -> Java Objects
  }

  public WalletBody(String gti, String ci, String mn, String o, Pay p, Ship s) {
    setGoogleTransactionId(gti);
    setClientId(ci);
    setMerchantName(mn);
    setOrigin(o);
    setPay(p);
    setShip(s);
  }

  public WalletBody(String ci, String mn, String o) {
    setClientId(ci);
    setMerchantName(mn);
    setOrigin(o);
  }

  public WalletBody(String ci, String mn, String o, Pay p, Ship s) {
    setClientId(ci);
    setMerchantName(mn);
    setOrigin(o);
    setPay(p);
    setShip(s);
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public String getOrigin() {
    return origin;
  }

  public String getGoogleTransactionId() {
    return googleTransactionId;
  }

  public void setGoogleTransactionId(String gti) {
    this.googleTransactionId = gti;
  }

  public String getTransactionId() {
    return merchantTransactionId;
  }

  public void setTransactionId(String merchantTransactionId) {
    this.merchantTransactionId = merchantTransactionId;
  }

  public Pay getPay() {
    return pay;
  }

  public void setPay(Pay pay) {
    this.pay = pay;
  }

  public Ship getShip() {
    return ship;
  }

  public void setShip(Ship ship) {
    this.ship = ship;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getMerchantName() {
    return merchantName;
  }

  public void setMerchantName(String merchantName) {
    this.merchantName = merchantName;
  }

  public Boolean getPhoneNumberRequired() {
    return phoneNumberRequired;
  }

  public void setPhoneNumberRequired(Boolean phoneNumberRequired) {
    this.phoneNumberRequired = phoneNumberRequired;
  }
  
  public String getMerchantTransactionId() {
    return merchantTransactionId;
  }

  public void setMerchantTransactionId(String merchantTransactionId) {
    this.merchantTransactionId = merchantTransactionId;
  }
  
}
