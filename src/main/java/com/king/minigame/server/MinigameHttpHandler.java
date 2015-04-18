package com.king.minigame.server;

import com.king.minigame.controller.ListController;
import com.king.minigame.controller.LoginController;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.king.minigame.server.HttpRequestMethod.GET;
import static com.king.minigame.server.HttpRequestMethod.POST;
import static com.king.minigame.server.HttpRequestMethod.UNKNOWN;

/**
 * https://leonardom.wordpress.com/2009/08/06/getting-parameters-from-httpexchange/
 */
public class MinigameHttpHandler implements HttpHandler {

  private static final Pattern LOGIN_PATTERN = Pattern.compile("/(\\d*?)/login");
  private static final Pattern HIGH_SCORE_LIST_PATTERN = Pattern.compile("/(\\d*?)/highscorelist");

  private final LoginController loginController;
  private final ListController listController;

  public MinigameHttpHandler() {
    this.loginController = new LoginController();
    this.listController = new ListController();
  }

  public void handle(HttpExchange he) throws IOException {

    Optional<Response> response = Optional.empty();//"Path: " + uri.getPath() + "\n";

    try {
      URI uri = he.getRequestURI();

      InputStream requestBody = he.getRequestBody();


      HttpRequestMethod requestMethod = retrieveHttpRequestMethod(he);

      switch (requestMethod) {
        case GET:
          response = handleGetRequest(he, uri);
          break;
        case POST:
          break;
        default:
          response = Optional.of(new Response(HttpURLConnection.HTTP_BAD_METHOD, ""));
      }
    } catch (Exception ex) {
      response = Optional.of(new Response(HttpURLConnection.HTTP_INTERNAL_ERROR, ""));
    }

    response = handleHttpNotFound(response);

    he.sendResponseHeaders(response.get().getHttpStatusCode(), response.get().getResponseMessage().length());
//    String query = he.getRequestURI().getQuery();
//    response += "query: " + query + "\n";

    Headers h = he.getResponseHeaders();
    h.set("Content-Type", "text/plain");

    writeOutputStream(he, response.get().getResponseMessage());
  }

  private void writeOutputStream(HttpExchange he, String responseMessage) throws IOException {OutputStream
      os = he.getResponseBody();
    os.write(responseMessage.getBytes());
    os.close();
  }

  private Optional<Response> handleGetRequest(HttpExchange he, URI uri)
      throws IOException {

    Optional<Response> response = Optional.empty();
    if (isLoginRequest(uri)) {
      response = handleLoginRequestIfApplies(he, uri);
    } else if (isHighScoreListRequest(uri)) {
      response = handleHighScoreListRequestIfApplies(he, uri);
    }
    return response;
  }

  private Optional<Response> handleHttpNotFound(Optional<Response> response) {

    if (!response.isPresent()) {
      response = Optional.of(new Response(HttpURLConnection.HTTP_NOT_FOUND, ""));
    }
    return response;
  }

  private HttpRequestMethod retrieveHttpRequestMethod(HttpExchange he) {
    if (isRequest(POST, he)) {
      return POST;
    } else if (isRequest(GET, he)) {
      return GET;
    } else {
      return UNKNOWN;
    }
  }

  private boolean isLoginRequest(URI uri) {

    Matcher loginMatcher = getLoginUrlRequestMatcher(uri);
    return loginMatcher.find();
  }

  private boolean isHighScoreListRequest(URI uri) {

    Matcher highScoreListMatcher = getHighScoreListRequestMatcher(uri);
    return highScoreListMatcher.find();
  }

  private boolean isRequest(HttpRequestMethod method, HttpExchange he) {

    return method.name().equalsIgnoreCase(he.getRequestMethod());
  }

  private Optional<Response> handleLoginRequestIfApplies(HttpExchange he, URI uri) throws IOException {

    String sesskionKey = "";
    int statusCode;
    Matcher loginMatcher = getLoginUrlRequestMatcher(uri);
    if (loginMatcher.find()) {
      Integer userId = Integer.valueOf(loginMatcher.group(1));
      sesskionKey = loginController.login(userId);
      statusCode = HttpURLConnection.HTTP_OK;
      return Optional.of(new Response(statusCode, sesskionKey));
    } else {
      return Optional.empty();
    }
  }

  private Matcher getLoginUrlRequestMatcher(URI uri) {

    String path = uri.getPath();
    return LOGIN_PATTERN.matcher(path);
  }

  private Optional<Response> handleHighScoreListRequestIfApplies(HttpExchange he, URI uri) throws IOException {

    String highScoreListInCsvFormat = "";
    int statusCode;
    Matcher highScoreListMatcher = getHighScoreListRequestMatcher(uri);
    if (highScoreListMatcher.find()) {
      Integer listId = Integer.valueOf(highScoreListMatcher.group(1));
      highScoreListInCsvFormat = listController.getHighScoreListForLevel(listId);
      statusCode = HttpURLConnection.HTTP_OK;
      return Optional.ofNullable(new Response(statusCode, highScoreListInCsvFormat));
    } else {
      return Optional.empty();
    }
  }

  private Matcher getHighScoreListRequestMatcher(URI uri) {

    String path = uri.getPath();
    return HIGH_SCORE_LIST_PATTERN.matcher(path);
  }
}
