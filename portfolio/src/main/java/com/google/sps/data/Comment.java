package com.google.sps.data;

import com.google.auto.value.AutoValue;

//** Class for storing commment properties. */
@AutoValue
public abstract class Comment {

  /** 
   * Creates a commment instance containing its {@code id},
   * {@code text}, and {@code timestamp}. 
   */
  public static Comment create(long id, String text, long timestamp, 
      String name, String email, String nickname) {
    return new AutoValue_Comment(id, text, timestamp, name, email, nickname);
  }

  abstract long id();

  abstract String text();

  abstract long timestamp();
  
  abstract String name();

  abstract String email();

  abstract String nickname();
}

