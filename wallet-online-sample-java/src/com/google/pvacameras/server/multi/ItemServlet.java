package com.google.pvacameras.server.multi;

import org.apache.velocity.VelocityContext;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Displays a page showing the details for a single item.
 */
public class ItemServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) {
    doPost(req, resp); // Get handler forwards request to post handler.
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) {
    Item item = Item.getForId(req.getParameter("itemId"));

    VelocityHelper page = new VelocityHelper(req, resp);
    page.header();

    VelocityContext context = new VelocityContext();
    page.addItemToContext(item, "1", context);
    page.writeTemplate("item.vm", context);

    page.footer();
  }
}
