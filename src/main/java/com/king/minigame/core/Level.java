package com.king.minigame.core;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *  Class representing a level and all its associated user scores.
 */
public class Level {

  private final Integer levelId;
  private ListMultimap<Integer, Score> userScores;

  public Level(final Integer levelId) {

    validateParameters(levelId);
    userScores = ArrayListMultimap.create();
    this.levelId = levelId;
  }

  public int getLevelId() {

    return this.levelId;
  }

  public ListMultimap<Integer, Score> getAllUserScores() {

    return ImmutableListMultimap.copyOf(userScores);
  }

  public void addScoreForUser(Integer userId, Score score) {

    validateUserScoreParameters(userId, score);

    userScores.put(userId, score);
  }


  private void validateUserScoreParameters(Integer userId, Score score) {

    if (userId == null || score == null) {
      throw new IllegalArgumentException("null values are not allowed");
    }
  }

  private void validateParameters(Integer levelId) {

    if (levelId == null) {
      throw new IllegalArgumentException("levelId can not be null");
    }
  }

}
