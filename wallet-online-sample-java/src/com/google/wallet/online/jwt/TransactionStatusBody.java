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
 * Container for success or failure reason for the transaction status.
 */
public class TransactionStatusBody extends WalletBody {

  /**
   * Enumeration to define the payment processing status
   */
  public enum Status {
    SUCCESS, FAILURE
  }

  /**
   * Enumeration to define the failure reason
   */
  public enum Reason {
    BAD_CVC, BAD_CARD, DECLINED, OTHER
  }

  private Status status;
  private Reason reason;
  private String detailedReason;

  public TransactionStatusBody(String gid, Status status) {
    setGoogleTransactionId(gid);
    setStatus(status);
  }

  public TransactionStatusBody(String gid, Status status, Reason reason) {
    setGoogleTransactionId(gid);
    setStatus(status);
    setReason(reason);
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Reason getReason() {
    return reason;
  }

  public void setReason(Reason reason) {
    this.reason = reason;
  }

  public String getDetailedReason() {
    return detailedReason;
  }

  public void setDetailedReason(String detailedReason) {
    this.detailedReason = detailedReason;
  }
}
