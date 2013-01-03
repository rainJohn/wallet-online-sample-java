package com.google.wallet.online.jwt;

import static org.junit.Assert.*;

import com.google.pvacameras.server.config.Config;
import com.google.wallet.online.jwt.util.WalletOnlineService;

import org.junit.Test;

/**
 * Test to ensure the Full Wallet Response decoding is functioning correctly
 */
public class FullWalletResponseTest {

  @Test
  public void testParsing() {
    String jwt =
        "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIxNTkwMDI0NTkzOTk3NTY1NTIwNiIsImF1ZCI6Ik" +
        "dvb2dsZSIsImlhdCI6MTM0MjgwODI0NywiZXhwIjoxMzQyODA5MTUwLCJ0eXAiOiJnb29nb" +
        "GUvd2FsbGV0L29ubGluZS9mdWxsL3YyL3Jlc3BvbnNlIiwicmVxdWVzdCI6eyJnb29nbGVU" +
        "cmFuc2FjdGlvbklkIjoiYmMzZDU4NzgtZDFhZC00MDI5LWJmMmEtNzNmMmY4NDM4NjFmIiw" +
        "ib3JpZ2luIjoiaHR0cDovL2xvY2FsaG9zdDo4ODg4IiwiY2FydCI6eyJ0b3RhbFByaWNlIj" +
        "oiMTM0OS41NSIsImN1cnJlbmN5Q29kZSI6IlVTRCIsImxpbmVJdGVtcyI6W3siZGVzY3Jpc" +
        "HRpb24iOiJFT1MgN0QgU0xSIERpZ2l0YWwgQ2FtZXJhIiwicXVhbnRpdHkiOiIxIiwidW5p" +
        "dFByaWNlIjoiMTM0OS41NSIsInRvdGFsUHJpY2UiOiIxMzQ5LjU1IiwiY3VycmVuY3lDb2R" +
        "lIjoiVVNEIiwiaXNEaWdpdGFsIjpmYWxzZX1dfX0sInJlc3BvbnNlIjp7Imdvb2dsZVRyYW" +
        "5zYWN0aW9uSWQiOiJiYzNkNTg3OC1kMWFkLTQwMjktYmYyYS03M2YyZjg0Mzg2MWYiLCJlb" +
        "WFpbCI6InB5aW5nQGdvb2dsZS5jb20iLCJwYXkiOnsib2JqZWN0SWQiOiIxNzEyODI2MzE2" +
        "MDcyOTI2MjAzOS5DLjEwMzE2MzEyODE4ODQ3OTkiLCJleHBpcmF0aW9uTW9udGgiOjgsImV" +
        "4cGlyYXRpb25ZZWFyIjoyMDEyLCJiaWxsaW5nQWRkcmVzcyI6eyJuYW1lIjoicGVuZyB5aW" +
        "5nIiwiYWRkcmVzczEiOiIxMjMgZmFrZSBzdCIsImFkZHJlc3MyIjoiIiwiY291bnRyeUNvZ" +
        "GUiOiJVUyIsImNpdHkiOiJtb3VudGFpbiB2aWV3Iiwic3RhdGUiOiJDQSIsInBvc3RhbENv" +
        "ZGUiOiI5NDA0MyIsInBob25lTnVtYmVyIjoiMzEyNDU2Nzg5MCIsInBvc3RCb3giOmZhbHN" +
        "lLCJjb21wYW55TmFtZSI6IiIsInR5cGUiOiJGVUxMIn0sImludGVybmFsIjp7ImlpbiI6Ij" +
        "U2Nzg5MCIsInJlc3QiOiJUT0RPKGxlZW1hcnNoYWxsKTogZ2V0IHJlYWwifX0sInNoaXAiO" +
        "nsib2JqZWN0SWQiOiIxNzEyODI2MzE2MDcyOTI2MjAzOS5HLjkyMzIxMjM1ODE0NjMxNCIs" +
        "InNoaXBwaW5nQWRkcmVzcyI6eyJuYW1lIjoicGVuZyIsImFkZHJlc3MxIjoiMTYwMCBhbXB" +
        "oaXRoZWF0cmUgcGt3eSIsImFkZHJlc3MyIjoiIiwiY291bnRyeUNvZGUiOiJVUyIsImNpdH" +
        "kiOiJtb3VudGFpbiB2aWV3Iiwic3RhdGUiOiJDQSIsInBvc3RhbENvZGUiOiI5NDA4OSIsI" +
        "nBob25lTnVtYmVyIjoiMTIzIDQ1Ni03ODkwIiwicG9zdEJveCI6ZmFsc2UsImNvbXBhbnlO" +
        "YW1lIjoiIiwidHlwZSI6IkZVTEwifX19fQ.-kZCJtKV6tsMXsOg2GktEZig9Piq_0MrWQpP" +
        "SI2ibk8";
    
    WalletOnlineService ows =
        new WalletOnlineService(Config.getMerchantId(), Config.getMerchantSecret());

    try {
      JwtResponse fwr = ows.jwtToJava(jwt);
      assertEquals("Billing Address Name", "peng ying",
          fwr.getResponse().getPay().getBilling_address().getName());
      assertEquals("Shipping Address Line 1", "1600 amphitheatre pkwy",
          fwr.getResponse().getShip().getShipping_address().getAddress1());
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }
}
