package com.google.sps.data;

import com.google.auto.value.AutoValue;


/** Stores data related to a comment. */
@AutoValue
public abstract class Comment {
  abstract long id();

  abstract String text();

  abstract long timestamp();

  abstract String name();

  abstract String nickname();

  abstract String imageUrl();

  /** 
   * Returns a builder instance that can be used to create Comments.
   */
  public static Builder builder() {
    return new AutoValue_Comment.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setId(long id);
    public abstract Builder setText(String text);
    public abstract Builder setTimestamp(long timestamp);
    public abstract Builder setName(String name);
    public abstract Builder setNickname(String nickname);
    public abstract Builder setImageUrl(String imageUrl);
    public abstract Comment build();
  }
}

