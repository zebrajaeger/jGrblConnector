package de.zebrajaeger.jgrblconnector.event;

/**
 * @author Lars Brandt on 04.09.2016.
 */
public class GrblAlarmEvent {
  public static final String ALARM_PREFIX = "ALARM: ";
  private String message;

  private GrblAlarmEvent(String alarm) {
    this.message = alarm;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return "GrblAlarmEvent{"
        + "message='" + message + '\''
        + '}';
  }

  public static GrblAlarmEvent of(String message) {
    if (message.startsWith(ALARM_PREFIX)) {
      message = message.substring(ALARM_PREFIX.length());
    }
    return new GrblAlarmEvent(message);
  }
}
