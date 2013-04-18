package com.google.pvacameras.server.multi;

import org.apache.velocity.VelocityContext;

import java.util.Arrays;

/**
 * Select Servlet displays the start page for XYZ, it shows the available items for selection.
 */
public class SelectPage {

  public void handleRequest(VelocityHelper page) {

    page.header("Don't miss our <span class=\"highlight\">Free shipping</span> offer today")
        .showLogin(true)
        .showCart(false)
        .writeHeader();

    VelocityContext context = new VelocityContext();
    context.put("items", Arrays.asList(Item.CAMERA_1, Item.CAMERA_2, Item.CAMERA_3));
    page.writeTemplate("selection.vm", context);

    page.footer();
  }
}
