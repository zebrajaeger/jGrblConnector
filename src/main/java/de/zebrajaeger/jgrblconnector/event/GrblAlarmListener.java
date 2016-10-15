package de.zebrajaeger.jgrblconnector.event;

/**
 * @author Lars Brandt on 04.09.2016.
 */
public interface GrblAlarmListener extends GrblListener {
  void grblAlarm(GrblAlarmEvent event);
}
