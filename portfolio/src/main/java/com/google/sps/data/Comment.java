//TODO: Add file header.

package com.google.sps.data;

//TODO: Add class header.
public final class Comment {

  private final long id;
  private final String text;
  private final long timestamp;

  public Comment(long id, String text, long timestamp) {
    this.id = id;
    this.text = text;
    this.timestamp = timestamp;
  }
}