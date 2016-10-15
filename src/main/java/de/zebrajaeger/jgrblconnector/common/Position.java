package de.zebrajaeger.jgrblconnector.common;

import de.zebrajaeger.jgrblconnector.gear.GearSet;

import java.util.Objects;

import javax.annotation.Nonnull;

/**
 * @author Lars Brandt on 03.09.2016.
 */
public class Position {
  private float x;
  private float y;

  public Position(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public static Position of(String sx, String sy) {
    try {
      float x = Float.parseFloat(sx.trim());
      float y = Float.parseFloat(sy.trim());
      return new Position(x, y);
    } catch (NumberFormatException e) {
      // NOP
    }
    return null;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Position position = (Position) o;

    if (Float.compare(position.x, x) != 0) {
      return false;
    }

    return Float.compare(position.y, y) == 0;

  }

  @Override
  public int hashCode() {
    int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
    result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Position{"
        + "x=" + x
        + ", y=" + y
        + '}';
  }

  public static class Builder {
    private String sx;
    private String sy;
    private GearSet gearSet = GearSet.NOGEAR;

    /**
     * @param x something like  "0.000"
     * @param y something like  "0.000"
     * @return the created builder
     */
    public static Builder of(@Nonnull String x, @Nonnull String y) {
      Builder builder = new Builder();
      builder.sx = Objects.requireNonNull(x);
      builder.sy = Objects.requireNonNull(y);
      return builder;
    }

    public Builder gearSet(@Nonnull GearSet newGearSet) {
      this.gearSet = Objects.requireNonNull(newGearSet);
      return this;
    }

    public Position build() {
      float x = Float.parseFloat(sx.trim());
      float y = Float.parseFloat(sy.trim());
      return new Position(gearSet.getXGear().motorSideToDriveSide(x), gearSet.getYGear().motorSideToDriveSide(y));
    }
  }
}
