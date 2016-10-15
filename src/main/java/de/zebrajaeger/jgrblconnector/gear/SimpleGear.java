package de.zebrajaeger.jgrblconnector.gear;

/**
 * @author Lars Brandt on 15.10.2016.
 */
public class SimpleGear implements Gear {
  private float ratio;

  public SimpleGear(float ratio) {
    this.ratio = ratio;
  }

  @Override
  public float motorSideToDriveSide(float motorAngle) {
    return motorAngle / ratio;
  }

  @Override
  public float driveSideToMotorSide(float driveAngle) {
    return driveAngle * ratio;
  }

  @Override
  public String toString() {
    return "SimpleGear{"
        + "ratio=" + ratio
        + '}';
  }
}
