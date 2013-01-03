package com.google.wallet.online.jwt;

import static org.junit.Assert.*;

import com.google.wallet.online.jwt.util.WalletOnlineService;

import org.junit.Test;

import java.security.InvalidKeyException;
import java.security.SignatureException;

/**
 * Test the Masked Wallet Classes
 */
public class MaskedWalletRequestTest {

  @Test
  public void test() {
    WalletBody mwb = new WalletBody(
        "765560632621-8pf9sqbicgb8citielj6tnh8n46i7i7s.apps.googleusercontent.com", "PVA Cameras",
        "test", new Pay("1349.55", "USD"), new Ship());
    JwtRequest mwr = new JwtRequest(JwtRequest.Type.MASKED_WALLET, mwb);
    mwr.setIat(1342827493L);
    WalletOnlineService ows = new WalletOnlineService("15900245939975655206", "ABCDEFGHIJKLMNOP");

    try {
      assertEquals("MWR JWT", ows.javaToJwt(mwr),
          "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIxNTkwMDI0NTkzOTk3NTY1NTIwNiIsImF1ZCI6Ikdvb2dsZSIsInR5" +
          "cCI6Imdvb2dsZS93YWxsZXQvb25saW5lL21hc2tlZC92Mi9yZXF1ZXN0IiwiaWF0IjoxMzQyODI3NDkzLCJyZ" +
          "XF1ZXN0Ijp7ImNsaWVudElkIjoiNzY1NTYwNjMyNjIxLThwZjlzcWJpY2diOGNpdGllbGo2dG5oOG40Nmk3aT" +
          "dzLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwibWVyY2hhbnROYW1lIjoiUFZBIENhbWVyYXMiLCJvcml" +
          "naW4iOiJodHRwOi8vbG9jYWxob3N0Ojg4ODgiLCJwYXkiOnsiZXN0aW1hdGVkVG90YWxQcmljZSI6IjEzNDku" +
          "NTUiLCJjdXJyZW5jeUNvZGUiOiJVU0QifSwic2hpcCI6eyJwaG9uZU51bWJlclJlcXVpcmVkIjp0cnVlfX19." +
          "QcJp_l8xDZZlgJjK19z_erMji2ESq0rGgsWmuTy7m3E");
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (SignatureException e) {
      e.printStackTrace();
    }
  }
}
