package com.google.pvacameras.server.multi;

import com.google.pvacameras.server.HomeServlet;
import com.google.pvacameras.server.config.Config;

import org.apache.velocity.app.Velocity;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Displays a page showing the details for a single item.
 */
public class EntryPointServlet extends HttpServlet {

  enum Action {
    STORE, SELECT, ITEM, ORDER, CONFIRM, SIGNIN, RECEIPT, REDIRECT, UNKNOWN
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) {
    doPost(req, resp); // Get handler forwards request to post handler.
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) {
    // So we don't write to velocity.log.
    Velocity.setProperty(
        "runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogChute");

    // find environment and action from url path.
    String path = getPath(req);
    if (path == null) {
      return;
    }
    String[] pathComponents = path.split("/");
    Config environment = Config.getEnvironment();
    Action action = Action.SELECT;
    if (pathComponents.length >= 1){
      environment = Config.getEnvironment(pathComponents[0]);
      action = Action.REDIRECT;
      if (environment == Config.UNKNOWN){
        action = getAction(pathComponents[0]);
        environment = Config.getEnvironment();
      } else if (pathComponents.length == 2) {
        action = getAction(pathComponents[1]);
      }
    }

    VelocityHelper helper = new VelocityHelper(req, resp, environment);
    switch(action){
      case REDIRECT:
        try {
          resp.sendRedirect("/" + pathComponents[0] + "/select");
        } catch (IOException e) {
          e.printStackTrace();
        }
        return;
      case STORE:
        // TODO(user) the single page version doesn't support setting environment.
        if (pathComponents.length == 2) {
          resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
          new HomeServlet().doPost(req, resp);
        }
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

  private Action getAction(String path) {
    if (path == null || path.isEmpty()){
      return Action.SELECT;
    }
    path = path.toUpperCase();
    try {
      return Action.valueOf(path);
    } catch (IllegalArgumentException e) {
      return Action.UNKNOWN;
    }
  }
}
