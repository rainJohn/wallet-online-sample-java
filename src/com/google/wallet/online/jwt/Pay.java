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

import java.util.Collection;

/**
 * Pay Bean
 *
 * This object contains payment instrument data
 *
 *
 */
public class Pay {

  private String estimatedTotalPrice;
  private String currencyCode;
  private String objectId;
  private Integer expirationMonth;
  private Integer expirationYear;
  private Collection<String> description;
  private Address billingAddress;

  public Pay() {
    //Empty constructor used in gson conversion of JSON -> Java Objects
  }

  public Pay(String etp, String cur) {
    setEstimatedTotalPrice(etp);
    setCurrencyCode(cur);
  }

  public String getEstimatedTotalPrice() {
    return estimatedTotalPrice;
  }

  public void setEstimatedTotalPrice(String estimatedTotalPrice) {
    this.estimatedTotalPrice = estimatedTotalPrice;
  }

  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  public String getObjectId() {
    return objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  public Collection<String> getDescription() {
    return description;
  }

  public void setDescription(Collection<String> description) {
    this.description = description;
  }

  public Address getBillingAddress() {
    return billingAddress;
  }

  public void setBillingAddress(Address billingAddress) {
    this.billingAddress = billingAddress;
  }

  public Integer getExpirationMonth() {
    return expirationMonth;
  }

  public void setExpirationMonth(Integer expirationMonth) {
    this.expirationMonth = expirationMonth;
  }

  public Integer getExpirationYear() {
    return expirationYear;
  }

  public void setExpirationYear(Integer expirationYear) {
    this.expirationYear = expirationYear;
  }
}
