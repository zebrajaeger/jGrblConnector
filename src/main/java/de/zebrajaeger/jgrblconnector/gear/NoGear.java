package de.zebrajaeger.jgrblconnector.gear;

/**
 * @author Lars Brandt on 15.10.2016.
 */
public class NoGear implements Gear {
  private static final Gear instance = new NoGear();

  public static Gear instance() {
    return instance;
  }

  @Override
  public float motorSideToDriveSide(float motorAngle) {
    return motorAngle;
  }

  @Override
  public float driveSideToMotorSide(float driveAngle) {
    return driveAngle;
  }
}
