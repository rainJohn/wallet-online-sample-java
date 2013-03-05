package com.google.pvacameras.server.multi;

import org.apache.velocity.VelocityContext;

/**
 * Select Servlet displays the start page for XYZ, it shows the available items for selection.
 */
public class SigninPage {

  public void handleRequest(VelocityHelper page) {

    page.header("Sign in")
        .showLogin(false)
        .showCart(false)
        .writeHeader();

    VelocityContext context = new VelocityContext();
    page.writeTemplate("sign-in.vm", context);

    page.footer();
  }
}
