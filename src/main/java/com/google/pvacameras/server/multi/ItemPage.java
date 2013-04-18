package com.google.pvacameras.server.multi;

import org.apache.velocity.VelocityContext;

/**
 * Displays a page showing the details for a single item.
 */
public class ItemPage {

  public void handleRequest(VelocityHelper page) {

    page.getUpdatedValue("itemId");

    page.header("Camera")
        .showLogin(true)
        .showCart(false)
        .writeHeader();

    VelocityContext context = new VelocityContext();
    page.addItemToContext(context);
    page.writeTemplate("item.vm", context);

    page.footer();
  }
}
