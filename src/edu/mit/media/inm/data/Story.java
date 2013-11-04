package edu.mit.media.inm.data;


/**
 * Represents a Story in the Feed, or compused by Tell.
 * 
 * @author joyc4luck
 *
 */
public class Story {
  public long id;
  public String author;
  public String image;
  public String story;
  public String title;
  public long date;

  // Will be used by the ArrayAdapter in the ListView
  @Override
  public String toString() {
    return title;
  }
} 