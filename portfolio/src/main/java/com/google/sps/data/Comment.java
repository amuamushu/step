package com.google.sps.data;

//** Class for storing commment properties. */
public final class Comment {

  private final long id;
  private final String text;
  private final long timestamp;

  /** 
   * Creates a commment instance containing its {@code id},
   * {@code text}, and {@code timestamp}. 
   */
  public Comment(long id, String text, long timestamp) {
    this.id = id;
    this.text = text;
    this.timestamp = timestamp;
  }
}