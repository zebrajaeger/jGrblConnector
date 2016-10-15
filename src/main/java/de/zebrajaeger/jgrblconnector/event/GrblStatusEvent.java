package de.zebrajaeger.jgrblconnector.event;

import de.zebrajaeger.jgrblconnector.common.Position;

/**
 * @author Lars Brandt on 03.09.2016.
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

  /**
   * Example: '<Idle,MPos:0.000,0.000,0.000,WPos:0.000,0.000,0.000>' or 'Idle,MPos:0.000,0.000,0.000,WPos:0.000,0.000,0.000'
   */
  public static GrblStatusEvent of(String toParse) {
    String x = toParse;

    if (x.startsWith("<") && x.endsWith(">")) {
      x = x.substring(1, x.length() - 1);
    }

    // TODO depends on setting, the amount of parts can change
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
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    GrblStatusEvent that = (GrblStatusEvent) o;

    if (status != that.status) {
      return false;
    }
    if (mpos != null ? !mpos.equals(that.mpos) : that.mpos != null) {
      return false;
    }
    return wpos != null ? wpos.equals(that.wpos) : that.wpos == null;

  }

  @Override
  public int hashCode() {
    int result = status != null ? status.hashCode() : 0;
    result = 31 * result + (mpos != null ? mpos.hashCode() : 0);
    result = 31 * result + (wpos != null ? wpos.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "GrblStatusEvent{"
        + "event=" + status
        + ", mpos=" + mpos
        + ", wpos=" + wpos
        + '}';
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
