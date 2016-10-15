package de.zebrajaeger.jgrblconnector.event;

/**
 * @author Lars Brandt on 04.09.2016.
 */
public class GrblInfoEvent {
  private String info;

  private GrblInfoEvent(String info) {
    this.info = info;
  }

  public String getInfo() {
    return info;
  }

  @Override
  public String toString() {
    return "GrblInfoEvent{"
        + "info='" + info + '\''
        + '}';
  }

  /**
   * Example: '['$' for help]' or ''$' for help' Removes the trailing and leading bracket
   */
  public static GrblInfoEvent of(String info) {
    if (info.startsWith("[") && info.endsWith("]")) {
      info = info.substring(1, info.length() - 1);
    }
    return new GrblInfoEvent(info);
  }
}
