package com.king.minigame.server.handler;

import com.king.minigame.controller.GameLevelController;
import com.king.minigame.server.HttpRequestMethod;
import com.king.minigame.server.Response;
import com.king.minigame.session.SessionCookieRepository;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Optional;

import static com.king.minigame.server.HttpRequestMethod.GET;
import static com.king.minigame.server.HttpRequestMethod.POST;
import static com.king.minigame.server.HttpRequestMethod.UNKNOWN;
import static com.king.minigame.server.handler.HighScoreListRequestHandler.isHighScoreListRequest;
import static com.king.minigame.server.handler.LoginRequestHandler.isLoginRequest;
import static com.king.minigame.server.handler.PostUserScoreToLevelRequestHandler.isPostUserScoreToALevelRequest;
import static com.king.minigame.utils.Logger.log;


public class RootHttpHandler implements HttpHandler {

  private final GameLevelController gameLevelController;
  private final LoginRequestHandler loginRequestHandler;
  private final HighScoreListRequestHandler highScoreListRequestHandler;
  private final PostUserScoreToLevelRequestHandler postUserScoreToLevelRequestHandler;


  public RootHttpHandler() {
    SessionCookieRepository sessionCookieRepository = new SessionCookieRepository();

    this.gameLevelController = new GameLevelController(sessionCookieRepository);
    this.loginRequestHandler = new LoginRequestHandler(sessionCookieRepository);
    this.highScoreListRequestHandler = new HighScoreListRequestHandler(gameLevelController);
    this.postUserScoreToLevelRequestHandler = new PostUserScoreToLevelRequestHandler(gameLevelController);
  }

  public void handle(HttpExchange he) throws IOException {

    Optional<Response> response = Optional.empty();

    try {
      URI uri = he.getRequestURI();
      System.out.println("Received request for " + uri);

      InputStream requestBody = he.getRequestBody();

      HttpRequestMethod requestMethod = retrieveHttpRequestMethod(he);

      switch (requestMethod) {
        case GET:
          response = handleGetRequest(uri);
          break;
        case POST:
          response = handlePostRequest(he, uri);
          break;
        default:
          response = Optional.of(new Response(HttpURLConnection.HTTP_BAD_METHOD, ""));
      }
    } catch (IllegalStateException ex) {
      log(ex.getMessage());
      response = Optional.of(new Response(HttpURLConnection.HTTP_FORBIDDEN, ""));
    } catch (IllegalArgumentException ex) {
      log(ex.getMessage());
      response = Optional.of(new Response(HttpURLConnection.HTTP_BAD_REQUEST, ""));
    } catch (Exception ex) {
      log(ex.getMessage());
      response = Optional.of(new Response(HttpURLConnection.HTTP_INTERNAL_ERROR, ""));
    }

    response = handleHttpNotFound(response);

    log(String.format("Response { statusCode: %d, message: '%s' }", response.get().getHttpStatusCode(), response.get().getResponseMessage()));

    he.sendResponseHeaders(response.get().getHttpStatusCode(), response.get().getResponseMessage().length());
    Headers h = he.getResponseHeaders();
    h.set("Content-Type", "text/plain");

    writeOutputStream(he, response.get().getResponseMessage());
  }

  private void writeOutputStream(HttpExchange he, String responseMessage) throws IOException {

    OutputStream
        os = he.getResponseBody();
    os.write(responseMessage.getBytes());
    os.close();
  }

  private Optional<Response> handleGetRequest(URI uri)
      throws IOException {

    Optional<Response> response = Optional.empty();
    if (isLoginRequest(uri)) {
      response = loginRequestHandler.handleLoginRequestIfApplies(uri);
    } else if (isHighScoreListRequest(uri)) {
      response = highScoreListRequestHandler.handleHighScoreListRequestIfApplies(uri);
    }
    return response;
  }

  private Optional<Response> handlePostRequest(HttpExchange he, URI uri) throws IOException {

    Optional<Response> response = Optional.empty();
    if (isPostUserScoreToALevelRequest(uri)) {
      response = postUserScoreToLevelRequestHandler.handlePostUserScoreToLevel(he, uri);
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

    if (isRequestMethod(POST, he)) {
      return POST;
    } else if (isRequestMethod(GET, he)) {
      return GET;
    } else {
      return UNKNOWN;
    }
  }

  private boolean isRequestMethod(HttpRequestMethod method, HttpExchange he) {

    return method.name().equalsIgnoreCase(he.getRequestMethod());
  }

}