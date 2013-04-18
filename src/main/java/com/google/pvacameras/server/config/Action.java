package com.google.pvacameras.server.config;

/**
 * Represents possible app actions, which are web pages shown to the user.
 * UNKNOWN is a special case for url's which shouldn't normally be visited.
 */
public enum Action {
  STORE, SELECT, ITEM, ORDER, CONFIRM, SIGNIN, RECEIPT, UNKNOWN;

  public static Action getAction(String path) {
    if (path == null || path.isEmpty()) {
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

