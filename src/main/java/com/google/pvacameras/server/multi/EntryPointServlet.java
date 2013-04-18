package com.google.pvacameras.server.multi;

import com.google.pvacameras.server.HomeServlet;
import com.google.pvacameras.server.config.Action;
import com.google.pvacameras.server.config.Config;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Displays a page showing the details for a single item.
 */
public class EntryPointServlet extends HttpServlet {

  private static final Logger logger = Logger.getLogger(EntryPointServlet.class.getSimpleName());

  @Override
  public void init() throws ServletException {
    // Initialize Velocity Templates
    try {
      VelocityHelper.init();
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    doPost(req, resp); // Get handler forwards request to post handler.
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    // find action from url path.
    String path = getPath(req);
    if (path == null) {
      return;
    }
    String[] pathComponents = path.split("/");
    Action action = Action.SELECT;
    if (pathComponents.length >= 1){
      action = Action.getAction(pathComponents[0]);
    }
    Config environment = Config.getEnvironment();

    VelocityHelper helper = new VelocityHelper(req, resp, environment);
    try {
      switch(action){
        case STORE:
          // TODO(user): Should use request dispatcher rather than creating
          // new instance of servlet?
          new HomeServlet().doPost(req, resp);
          break;
        case SELECT:
          new SelectPage().handleRequest(helper);
          break;
        case ITEM:
          new ItemPage().handleRequest(helper);
          break;
        case ORDER:
          new OrderPage().handleRequest(helper);
          break;
        case CONFIRM:
          new ConfirmPage().handleRequest(helper);
          break;
        case SIGNIN:
          new SigninPage().handleRequest(helper);
          break;
        case RECEIPT:
          new ReceiptPage().handleRequest(helper);
          break;
        default:
          resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
      }

    } catch (SignatureException e) {
      logger.log(Level.SEVERE, "Signature exception", e);
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

    } catch (InvalidKeyException e) {
      logger.log(Level.SEVERE, "Invalid Key Exception ", e);
      resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private String getPath(HttpServletRequest req) {
    try {
      URI uri = new URI(req.getRequestURI());
      String path = uri.getPath();
      if (path.startsWith("/")) {
        path = path.substring(1);
      }
      return path;
    } catch (URISyntaxException e) {
      e.printStackTrace();
      return null;
    }
  }
}
