package com.google.pvacameras.server.multi;

import com.google.pvacameras.server.config.Config;
import com.google.wallet.online.jwt.Address;
import com.google.wallet.online.jwt.JwtResponse;
import com.google.wallet.online.jwt.Ship;
import com.google.wallet.online.jwt.util.WalletOnlineService;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Velocity Templates are used for rendering the item details page.
 * This class handles the setup and tear down events which are common between all Servlets.
 */
public class VelocityHelper {
  private PrintWriter printWriter;
  private String merchantJsUrl;
  private Config environment;
  private HttpSession session;
  private HttpServletRequest req;
  private VelocityContext context;

  public VelocityHelper(HttpServletRequest req, HttpServletResponse resp, Config environment) {
    this.environment = environment;
    // So we don't write to velocity.log.
    Velocity.setProperty("runtime.log.logsystem.class",
        "org.apache.velocity.runtime.log.NullLogChute");

    session = req.getSession();
    this.req = req;

    // Initialize Velocity Templates.
    try {
      Velocity.init();
    } catch (Exception e) {
      e.printStackTrace();
    }
    merchantJsUrl = environment.isLocal() ? Config.getDevJsUrl(req) : environment.getJsUrl();
    // Get the response output stream PrintWriter.
    try {
      printWriter = resp.getWriter();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public VelocityHelper header(String saleHeader) {
    context = new VelocityContext();
    context.put("saleHeader", saleHeader);
    return this;
  }

  public VelocityHelper showLogin(boolean showLogin) {
    if (!showLogin) {
      context.put("noLoginButton", "");
    }
    return this;
  }

  public VelocityHelper showCart(boolean showCart) {
    if (!showCart) {
      context.put("noCartDisplay", "");
    }
    return this;
  }

  public void writeHeader(){
    context.put("walletJSUrl", merchantJsUrl);
    context.put("clientId", Config.OAUTH_CLIENT_ID);
    context.put("apiKey", Config.OAUTH_API_KEY);
    String name = (String) session.getAttribute("name");
    if (name != null && !name.isEmpty()) {
      context.put("name", name);
    }
    String email = (String) session.getAttribute("email");
    if (email != null && !email.isEmpty()) {
      context.put("email", email);
      if (email.length() > 9) {
        context.put("truncEmail", email.substring(0, 6) + "...");
      } else {
        context.put("truncEmail", email);
      }
    }
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
   * Injects the information from an Item stored in session into the template.
   */
  public void addItemToContext(VelocityContext context) {
    Item item = getItem();
    int quantity = getQuantity();

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
    Ship ship = mwrResponse.getResponse().getShip();
    if (ship != null) {
      Address address = ship.getShippingAddress();
      context.put("shippingName", address.getName());
      context.put("shippingAddress1", address.getAddress1());
      context.put("shippingCity", address.getCity());
      context.put("shippingState", address.getState());
      context.put("shippingPost", address.getPostalCode());
      context.put("shippingPhone", address.getPhoneNumber());
    }
    context.put("buyerEmail", mwrResponse.getResponse().getEmail());
    Collection<String> billing = mwrResponse.getResponse().getPay().getDescription();
    if (!billing.isEmpty()){
      context.put("buyerBilling", billing.iterator().next());
    }
  }

  public int getQuantity() {
    String quantity = (String) session.getAttribute("quantity");
    try {
      return Integer.parseInt(quantity);
    } catch (NumberFormatException e) {
      return 1;
    }
  }

  public Item getItem() {
    String itemId = (String) session.getAttribute("itemId");
    return Item.getForId(itemId);
  }

  public WalletOnlineService makeWalletOnlineServices() {
    return new WalletOnlineService(environment.getMerchantId(), environment.getMerchantSecret());
  }

  public PrintWriter getPrintWriter() {
    return printWriter;
  }

  public String getDomain() {
    return Config.getDomain(req);
  }

  public String getParameter(String key) {
    return req.getParameter(key);
  }

  /**
   * Gets a value from a get/post param if available, and saves it into the session. If no get/post
   * param is available this method returns the value from session.
   */
  public String getUpdatedValue(String key) {
    String value = getParameter(key);
    if (value != null) {
      session.setAttribute(key, value);
      return value;
    } else {
      return (String) session.getAttribute(key);
    }
  }
}
