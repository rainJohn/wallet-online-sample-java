package com.google.pvacameras.server.multi;

import com.google.pvacameras.server.config.Config;
import com.google.wallet.online.jwt.Address;
import com.google.wallet.online.jwt.JwtResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Velocity Templates are used for rendering the item details page.
 * This abstract class handles the setup and tear down events which are common between all Servlets.
 */
public class VelocityHelper {
  private PrintWriter printWriter;
  private String merchantJsUrl;

  public VelocityHelper(HttpServletRequest req, HttpServletResponse resp) {
    // So we don't write to velocity.log.
    Velocity.setProperty("runtime.log.logsystem.class",
        "org.apache.velocity.runtime.log.NullLogChute");
    // Escape HTML.
    Velocity.setProperty("eventhandler.referenceinsertion.class",
        "org.apache.velocity.app.event.implement.EscapeHtmlReference");
    Velocity.setProperty("eventhandler.escape.html.match", "/.*/");
    // Initialize Velocity Templates.
    try {
      Velocity.init();
    } catch (Exception e) {
      e.printStackTrace();
    }
    merchantJsUrl = Config.isLocal() ? Config.getDevJsUrl(req) : Config.getJsUrl();
    // Get the response output stream PrintWriter.
    try {
      printWriter = resp.getWriter();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void header() {
    VelocityContext context = new VelocityContext();
    context.put("walletJSUrl", merchantJsUrl);
    context.put("clientId", Config.OAUTH_CLIENT_ID);
    context.put("apiKey", Config.OAUTH_API_KEY);
    writeTemplate("header.vm", context);
  }

  public void footer() {
    writeTemplate("footer.vm", null);
  }

  /**
   * @param templateName the Velocity template to render.
   * @param context the context variables which the template requires, null is no context.
   */
  public void writeTemplate(String templateName, VelocityContext context) {
    if (context == null) {
      context = new VelocityContext();
    }
    try {
      Velocity.mergeTemplate(templateName, "UTF-8", context, printWriter);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Injects the information from an Item into the template.
   */
  public void addItemToContext(Item item, String quantity, VelocityContext context) {
    context.put("itemName", item.getName());
    context.put("itemId", item.getId());
    context.put("itemDescription", item.getDescription());
    if (item.getDescription2() != null) {
      context.put("itemDescription2", item.getDescription2());
    }
    context.put("itemQuantity", quantity);
    context.put("itemPrice", String.format("%.2f", (double) item.getPrice()));
    context.put("itemImage", item.getImageUrl());
  }

  /**
   * Injects the information from a JwtResponse into the template.
   */
  public void addJwtResponseToContext(JwtResponse mwrResponse, VelocityContext context) {
    Address ship = mwrResponse.getResponse().getShip().getShippingAddress();
    context.put("shippingName", ship.getName());
    context.put("shippingAddress1", ship.getAddress1());
    context.put("shippingCity", ship.getCity());
    context.put("shippingState", ship.getState());
    context.put("shippingPost", ship.getPostalCode());
    context.put("buyerEmail", mwrResponse.getResponse().getEmail());
    Collection<String> billing = mwrResponse.getResponse().getPay().getDescription();
    if (!billing.isEmpty()){
      context.put("buyerBilling", billing.iterator().next());
    }
  }
}
