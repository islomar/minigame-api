package com.king.minigame.controller;

import com.king.minigame.core.Score;
import com.king.minigame.core.GameLevelService;
import com.king.minigame.session.SessionCookieRepository;
import com.king.minigame.session.SessionService;

import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
public class GameLevelController {

  private GameLevelService gameLevelService;

  public GameLevelController(SessionCookieRepository sessionCookieRespository) {
    SessionService sessionService = new SessionService(Clock.systemUTC(), sessionCookieRespository);
    this.gameLevelService = new GameLevelService(sessionService);
  }

  public String getHighScoreListForLevel(Integer levelId) {

    Map<Integer, Score> highScoreList = gameLevelService.getHighScoreListForLevel(levelId);
    //highScoreList = hardcode();

    String highScoreListInCsvFormat = parseToCsv(highScoreList);
    return highScoreListInCsvFormat;
  }

  public void postUserScoreToLevel(String sessionKey, Integer levelId, Integer scoreValue) {
    gameLevelService.postUserScoreToList(sessionKey, levelId, scoreValue);
  }

  private Map<Integer, Score> hardcode() {

    Map<Integer, Score> response = new HashMap<Integer, Score>();
    response.put(111, new Score(100, Instant.now()));
    response.put(111, new Score(300, Instant.now()));
    response.put(222, new Score(200, Instant.now()));

    return response;
  }

  private String parseToCsv(Map<Integer, Score> highScoreList) {
    return highScoreList.entrySet().stream().map(s -> String.format("%d=%d", s.getKey(), s.getValue().getScoreValue())).collect(Collectors.joining(","));
  }
}