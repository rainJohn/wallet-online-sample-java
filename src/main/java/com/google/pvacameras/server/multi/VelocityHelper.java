package com.google.pvacameras.server.multi;

import com.google.pvacameras.server.config.Config;
import com.google.wallet.online.jwt.Address;
import com.google.wallet.online.jwt.JwtResponseContainer;
import com.google.wallet.online.jwt.ShipResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Velocity Templates are used for rendering the item details page.
 * @see <a href="http://velocity.apache.org/engine/devel/developer-guide.html">Velocity Guide</a>
 * This class handles the setup and tear down events which are common between all Servlets.
 */
public class VelocityHelper {
  private final PrintWriter printWriter;
  private final String merchantJsUrl;
  private final Config environment;
  private final HttpSession session;
  private final HttpServletRequest req;
  private VelocityContext context;

  /**
   * Initialize the velocity configuration
   */
  static void init() throws Exception {
    // Initialize Velocity Templates
    Properties velocityProperties = new Properties();
    velocityProperties.put("resource.loader", "class");
    velocityProperties.put("class.resource.loader.description",
        "Velocity Classpath Resource Loader");
    velocityProperties.put("class.resource.loader.class",
        "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    // So we don't write to velocity.log
    velocityProperties.put("runtime.log.logsystem.class",
        "org.apache.velocity.runtime.log.NullLogChute");
    Velocity.init(velocityProperties);
  }

  public VelocityHelper(HttpServletRequest req, HttpServletResponse resp, Config environment)
      throws IOException {
    this.environment = environment;
    this.session = req.getSession();
    this.req = req;
    this.merchantJsUrl = environment.getJsUrl();
    // Get the response output stream PrintWriter.
    this.printWriter = resp.getWriter();
  }

  /**
   * @return the environment
   */
  public Config getEnvironment() {
    return environment;
  }

  public VelocityHelper header(String saleHeader) {
    context = new VelocityContext();
    context.put("environment", environment.getName());
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
  public void addJwtResponseToContext(JwtResponseContainer mwrResponse, VelocityContext context) {
    ShipResponse ship = mwrResponse.getResponse().getShip();
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
