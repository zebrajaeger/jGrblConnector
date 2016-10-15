package de.zebrajaeger.jgrblconnector.event;

import de.zebrajaeger.jgrblconnector.common.Position;
import de.zebrajaeger.jgrblconnector.gear.GearSet;

/**
 * @author Lars Brandt on 03.09.2016.
 */
public class GrblStatusEvent {
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

  public static class Builder {
    public static final String MPOS = "MPos:";
    public static final String WPOS = "WPos:";

    private String toParse;
    private GearSet gearSet = GearSet.NOGEAR;

    /**
     * Example: '<Idle,MPos:0.000,0.000,0.000,WPos:0.000,0.000,0.000>' or 'Idle,MPos:0.000,0.000,0.000,WPos:0.000,0.000,0.000'
     */
    public static Builder of(String toParse) {
      Builder result = new Builder();
      result.toParse = toParse;
      return result;
    }

    public Builder gearSet(GearSet newGearSet) {
      this.gearSet = newGearSet;
      return this;
    }

    public GrblStatusEvent build() {
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
        Position mpos = Position.Builder.of(temp, parts[2]).gearSet(gearSet).build();

        temp = parts[4];
        if (temp.startsWith(WPOS)) {
          temp = temp.substring(WPOS.length());
        }
        Position wpos = Position.Builder.of(temp, parts[5]).gearSet(gearSet).build();

        return new GrblStatusEvent(status, mpos, wpos);
      }
      return null;
    }
  }
}
