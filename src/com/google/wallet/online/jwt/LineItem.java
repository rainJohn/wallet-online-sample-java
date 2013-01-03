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
 * This bean class represents cart line items for the Full Wallet Request. These line items
 * will be stored in receipts for customers to review
 *
 * @author pying (peng ying)
 *
 */
public class LineItem {
  private String description;
  private Integer quantity;
  private String unitPrice;
  private String totalPrice;
  private String role;
  private Boolean isDigital;

  /**
   * Defines whether the line item is an item, tax or shipping
   *
   * If tax/shipping are not defined, then the library assumes it's an item
   */
  public enum Role {
    TAX, SHIPPING
  }

  /**
   * Constructor that defines basic properties of an item
   *
   * @param desc Item description
   * @param quantity Item quantity
   * @param price Item price in xxx.xx format
   */
  public LineItem(String desc, Integer quantity, String price) {
    this.description = desc;
    this.quantity = quantity;
    this.unitPrice = price;
    this.totalPrice = ((Double) (quantity * Double.valueOf(price))).toString();
  }

  public LineItem(String desc, String price, Role role) {
    this.description = desc;
    this.totalPrice = price;
    setRole(role);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
    setTotalPrice(((Double) (quantity * Double.valueOf(getUnitPrice()))).toString());
  }

  public String getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(String unitPrice) {
    this.unitPrice = unitPrice;
    setTotalPrice(((Double) (quantity * Double.valueOf(unitPrice))).toString());
  }

  public String getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(String totalPrice) {
    this.totalPrice = totalPrice;
  }

  public String getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role.toString();
  }

  public Boolean getIsDigital() {
    return isDigital;
  }

  public void setIsDigital(Boolean isDigital) {
    this.isDigital = isDigital;
  }
}
