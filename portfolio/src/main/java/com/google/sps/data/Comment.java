package com.google.sps.data;

import com.google.auto.value.AutoValue;

/** Stores data related to a comment. */
@AutoValue
public abstract class Comment {

  /** 
   * Creates a commment instance containing its {@code id},
   * {@code text}, and {@code timestamp}. 
   */
   //TODO: Use AutoBUilder because Image is optional.
  public static Comment create(long id, String text, long timestamp, 
      String name, String email, String nickname, String image) {
    return new AutoValue_Comment(id, text, timestamp, name, email, nickname, image);
  }

  abstract long id();

  abstract String text();

  abstract long timestamp();

  abstract String name();

  abstract String email();

  abstract String nickname();

  abstract String image();
}

