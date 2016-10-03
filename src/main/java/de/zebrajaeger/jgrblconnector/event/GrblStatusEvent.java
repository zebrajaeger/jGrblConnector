package de.zebrajaeger.jgrblconnector.event;

import de.zebrajaeger.jgrblconnector.common.Position;

/**
 * Created by lars on 03.09.2016.
 */
public class GrblStatusEvent {
  public static final String MPOS = "MPos:";
  public static final String WPOS = "WPos:";
  private Status status;
  private Position mpos;
  private Position wpos;

  public GrblStatusEvent(Status status, Position mpos, Position wpos) {
    this.status = status;
    this.mpos = mpos;
    this.wpos = wpos;
  }

  public Status getStatus() {
    return status;
  }

  public Position getMpos() {
    return mpos;
  }

  public Position getWpos() {
    return wpos;
  }

  @Override
  public String toString() {
    return "GrblStatusEvent{" +
        "event=" + status +
        ", mpos=" + mpos +
        ", wpos=" + wpos +
        '}';
  }

  /**
   * Example: '<Idle,MPos:0.000,0.000,0.000,WPos:0.000,0.000,0.000>' or 'Idle,MPos:0.000,0.000,0.000,WPos:0.000,0.000,0.000'
   */
  public static GrblStatusEvent of(String toParse) {
    String x = toParse;

    if (x.startsWith("<") && x.endsWith(">")) {
      x = x.substring(1, x.length() - 1);
    }

    // TODO depends of setting, the amount of parts can change
    String[] parts = x.split(",");
    if (parts.length == 7) {

      Status status = Status.valueOf(parts[0].trim());

      String temp;
      temp = parts[1];
      if (temp.startsWith(MPOS)) {
        temp = temp.substring(MPOS.length());
      }
      Position mpos = Position.of(temp, parts[2], parts[3]);

      temp = parts[4];
      if (temp.startsWith(WPOS)) {
        temp = temp.substring(WPOS.length());
      }
      Position wpos = Position.of(temp, parts[5], parts[6]);

      return new GrblStatusEvent(status, mpos, wpos);
    }
    return null;
  }

  public enum Status {
    Idle,
    Run,
    Hold,
    Home,
    Alarm,
    Check,
    Door;
  }
}
