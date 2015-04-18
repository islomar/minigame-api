package com.king.minigame.core;

import java.time.Instant;

/**
 *  Class responsible for storing a score value and the moment when it happened.
 */
public final class Score implements Comparable{

  private final Integer scoreValue;
  private final Instant creationTime;

  public Score(Integer scoreValue, Instant creationTime) {

    validateParameters(scoreValue, creationTime);

    this.scoreValue = scoreValue;
    this.creationTime = creationTime;
  }

  public Integer getScoreValue() {
    return scoreValue;
  }

  public Instant getCreationTime() {
    return creationTime;
  }

  private void validateParameters(Integer scoreValue, Instant creationTime) {

    if (scoreValue == null || creationTime == null) {
      throw new IllegalArgumentException("null values are not allowed");
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Score other = (Score) obj;

    return   com.google.common.base.Objects.equal(this.scoreValue, other.scoreValue)
             && com.google.common.base.Objects.equal(this.creationTime, other.creationTime);
  }

  @Override
  public int hashCode() {
    return com.google.common.base.Objects.hashCode(this.scoreValue, this.creationTime);
  }

  @Override
  public String toString() {
    return com.google.common.base.Objects.toStringHelper(this)
        .add("scoreValue", this.scoreValue)
        .add("creationTime", this.creationTime)
        .toString();
  }

  @Override
  public int compareTo(Object o) {
    Score otherScore = (Score)o;
    return otherScore.getScoreValue() - this.getScoreValue();
  }
}