package com.king.minigame.server.handler;

import com.king.minigame.controller.LoginController;
import com.king.minigame.server.Response;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.king.minigame.utils.Logger.log;

public class LoginRequestHandler {

  private static final Pattern LOGIN_PATTERN = Pattern.compile("/(\\d*?)/login");
  private LoginController loginController;

  public LoginRequestHandler(LoginController loginController) {
    this.loginController = loginController;
  }

  public Optional<Response> handleLoginRequestIfApplies(URI uri) throws IOException {

    log("Request forwarded to LoginRequestHandler");

    String sesskionKey = "";
    int statusCode;
    Matcher loginMatcher = loginUrlRequestMatcher(uri);
    if (loginMatcher.find()) {
      Integer userId = Integer.valueOf(loginMatcher.group(1));
      sesskionKey = loginController.login(userId);
      statusCode = HttpURLConnection.HTTP_OK;
      return Optional.of(new Response(statusCode, sesskionKey));
    } else {
      return Optional.empty();
    }
  }

  public static boolean isLoginRequest(URI uri) {

    return loginUrlRequestMatcher(uri).find();
  }

  private static Matcher loginUrlRequestMatcher(URI uri) {

    String path = uri.getPath();
    return LOGIN_PATTERN.matcher(path);
  }
}
