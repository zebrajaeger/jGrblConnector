package de.zebrajaeger.jgrblconnector.gear;

/**
 * Created by lars on 15.10.2016.
 */
public class SimpleGearSet extends GearSet {
  public SimpleGearSet(float gearRatio) {
    this(gearRatio, gearRatio);
  }

  public SimpleGearSet(float xGearRatio, float yGearRatio) {
    super(new SimpleGear(xGearRatio), new SimpleGear(yGearRatio));
  }
}
