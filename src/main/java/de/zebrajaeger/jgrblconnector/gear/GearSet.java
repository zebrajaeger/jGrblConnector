package de.zebrajaeger.jgrblconnector.gear;

/**
 * Created by lars on 15.10.2016.
 */
public class GearSet {
  public static final GearSet NOGEAR = new GearSet();

  private Gear xGear = NoGear.INSTANCE;
  private Gear yGear = NoGear.INSTANCE;

  public GearSet() {
  }

  public GearSet(Gear xGear, Gear yGear) {
    this.xGear = xGear;
    this.yGear = yGear;
  }

  public Gear getXGear() {
    return xGear;
  }

  public Gear getYGear() {
    return yGear;
  }

  @Override
  public String toString() {
    return "GearSet{"
        + "xGear=" + xGear
        + ", yGear=" + yGear
        + '}';
  }
}
