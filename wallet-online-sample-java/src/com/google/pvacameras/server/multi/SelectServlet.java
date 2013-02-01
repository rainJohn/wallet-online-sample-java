package com.google.pvacameras.server.multi;

import org.apache.velocity.VelocityContext;

import java.util.Arrays;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Select Servlet displays the start page for XYZ, it shows the available items for selection.
 */
public class SelectServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) {
    doPost(req, resp); // Get handler forwards request to post handler.
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) {
    VelocityHelper page = new VelocityHelper(req, resp);

    page.header();

    VelocityContext context = new VelocityContext();
    context.put("items", Arrays.asList(Item.CAMERA_1, Item.CAMERA_2, Item.CAMERA_3));
    page.writeTemplate("selection.vm", context);

    page.footer();
  }
}
