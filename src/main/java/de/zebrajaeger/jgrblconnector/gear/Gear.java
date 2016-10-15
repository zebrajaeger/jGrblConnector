package de.zebrajaeger.jgrblconnector.gear;

/**
 * @author Lars Brandt on 15.10.2016.
 */
public interface Gear {
  float motorSideToDriveSide(float motorAngle);

  float driveSideToMotorSide(float driveAngle);
}
