package com.king.minigame.core;

import com.king.minigame.session.SessionService;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertTrue;

@Test
public class GameLevelServiceTest {

  @Mock
  SessionService sessionService;

  @InjectMocks
  GameLevelService gameLevelService;

  @BeforeMethod
  public void setUp() {

    gameLevelService = new GameLevelService(sessionService);
    initMocks(this);
  }

  public void should_return_empty_map_if_level_has_no_scores() {

    Integer userId = 1;
    Integer levelId = 123;
    Integer scoreValue = 300;

    //TODO: hacer tests con sesión inválida, ver que no funciona
    when(sessionService.hasUserValidSessionKey(anyInt())).thenReturn(true);

    assertTrue(gameLevelService.getHighScoreListForLevel(levelId).isEmpty());
  }

  @Test(enabled = false)
  //PENDING TO MAKE IT WORK
  public void xxx() {

//    String sessionKey = UUID.randomUUID().toString();
//    Integer levelId = 123;
//    Integer scoreValue = 300;
//
//    Map<Integer, Score> expectedUserScoreMap = new HashMap<>();
//
//    //TODO: hacer tests con sesión inválida, ver que no funciona
//    when(sessionService.hasUserValidSessionKey(anyInt())).thenReturn(true);
//    scoreService.postUserScoreToLevel(sessionKey, levelId, scoreValue);
//    expectedUserScoreMap.put(sessionKey, new Score(scoreValue, Instant.now()));
//
//    scoreService.postUserScoreToLevel(sessionKey, levelId, 200);
//    expectedUserScoreMap.put(sessionKey, new Score(scoreValue, Instant.now()));
//
//    scoreService.postUserScoreToLevel(2, levelId, 100);
//    expectedUserScoreMap.put(2, new Score(100, Instant.now()));
//
//    scoreService.postUserScoreToLevel(3, levelId, 500);
//    expectedUserScoreMap.put(3, new Score(500, Instant.now()));
//
//    scoreService.postUserScoreToLevel(userId, 43, scoreValue);
//    expectedUserScoreMap.put(userId, new Score(43, Instant.now()));
//
//
//    assertThat(scoreService.getHighScoreListForLevel(levelId).size(), is(3));
//    assertThat(scoreService.getHighScoreListForLevel(levelId), is(expectedUserScoreMap));
  }

}