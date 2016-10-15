package de.zebrajaeger.jgrblconnector.gear;

/**
 * @author Lars Brandt on 15.10.2016.
 */
public class NoGear implements Gear {
  public static final Gear INSTANCE = new NoGear();

  @Override
  public float motorSideToDriveSide(float motorAngle) {
    return motorAngle;
  }

  @Override
  public float driveSideToMotorSide(float driveAngle) {
    return driveAngle;
  }

  @Override
  public String toString() {
    return "NoGear{}";
  }
}
