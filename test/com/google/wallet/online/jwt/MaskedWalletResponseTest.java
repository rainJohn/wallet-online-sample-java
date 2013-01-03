package com.google.wallet.online.jwt;

import static org.junit.Assert.*;

import com.google.pvacameras.server.config.Config;
import com.google.wallet.online.jwt.util.WalletOnlineService;

import org.junit.Test;

/**
 * Test to ensure Masked Wallet Response decoding is functioning properly
 */
public class MaskedWalletResponseTest {

  @Test
  public void test() {
    String jwt =
        "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJHb29nbGUiLCJhdWQiOiIxNTkwMDI0NTkzOTk3NTY1NTIwNiIsImlhdC" +
        "I6MTM0MjgyOTkyOSwiZXhwIjoxMzQyODMwODI5LCJ0eXAiOiJnb29nbGUvd2FsbGV0L29ubGluZS9tYXNrZWQvd" +
        "jIvcmVzcG9uc2UiLCJyZXNwb25zZSI6eyJnb29nbGVUcmFuc2FjdGlvbklkIjoiYTQ3NWY5NjQtZmMzMC00YTgx" +
        "LTg5MjgtMjJmNzU3NzBiN2FiIiwiZW1haWwiOiJweWluZ0Bnb29nbGUuY29tIiwicGF5Ijp7Im9iamVjdElkIjo" +
        "iMTcxMjgyNjMxNjA3MjkyNjIwMzkuQy4xMDMxNjMxMjgxODg0Nzk5IiwiZGVzY3JpcHRpb24iOlsiVmlzYSBjYX" +
        "JkIGVuZGluZyBpbiAxMjM0Il0sImJpbGxpbmdBZGRyZXNzIjp7Im5hbWUiOiJwZW5nIHlpbmciLCJhZGRyZXNzM" +
        "SI6IjEyMyBmYWtlIHN0IiwiYWRkcmVzczIiOiIiLCJjb3VudHJ5Q29kZSI6IlVTIiwiY2l0eSI6Im1vdW50YWlu" +
        "IHZpZXciLCJzdGF0ZSI6IkNBIiwicG9zdGFsQ29kZSI6Ijk0MDQzIiwicGhvbmVOdW1iZXIiOiIzMTI0NTY3ODk" +
        "wIiwicG9zdEJveCI6ZmFsc2UsImNvbXBhbnlOYW1lIjoiIiwidHlwZSI6IkZVTEwifX0sInNoaXAiOnsib2JqZW" +
        "N0SWQiOiIxNzEyODI2MzE2MDcyOTI2MjAzOS5HLjkyMzIxMjM1ODE0NjMxNCIsInNoaXBwaW5nQWRkcmVzcyI6e" +
        "yJuYW1lIjoicGVuZyIsImFkZHJlc3MxIjoiMTYwMCBhbXBoaXRoZWF0cmUgcGt3eSIsImFkZHJlc3MyIjoiIiwi" +
        "Y291bnRyeUNvZGUiOiJVUyIsImNpdHkiOiJtb3VudGFpbiB2aWV3Iiwic3RhdGUiOiJDQSIsInBvc3RhbENvZGU" +
        "iOiI5NDA4OSIsInBob25lTnVtYmVyIjoiMTIzIDQ1Ni03ODkwIiwicG9zdEJveCI6ZmFsc2UsImNvbXBhbnlOYW" +
        "1lIjoiIiwidHlwZSI6IkZVTEwifX19fQ.v2uFhv8tPGaADHf8SGDynImAsOfeDqSjr4AznO89pMQ";
    
    WalletOnlineService ows =
        new WalletOnlineService(Config.getMerchantId(), Config.getMerchantSecret());
    try {
      JwtResponse mwr = ows.jwtToJava(jwt);
      assertEquals("Billing Address Name", "peng ying",
          mwr.getResponse().getPay().getBilling_address().getName());
      assertEquals("Shipping Address Line 1", "1600 amphitheatre pkwy",
          mwr.getResponse().getShip().getShipping_address().getAddress1());
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }
}
