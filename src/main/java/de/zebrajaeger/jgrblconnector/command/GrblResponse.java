package de.zebrajaeger.jgrblconnector.command;

/**
 * @author Lars Brandt on 03.09.2016.
 */
public class GrblResponse {

  private Status status;
  private String message;

  public GrblResponse(Status status, String message) {
    this.status = status;
    this.message = message;
  }

  public Status getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  @Override
  public String toString() {
    return "GrblResponse{"
        + "event=" + status
        + ", message='" + message + '\''
        + '}';
  }

  public static GrblResponse of(String line) {
    Status status;
    String message = null;
    if ("ok".equals(line)) {
      status = Status.OK;
    } else if (line.startsWith("error: ")) {
      status = Status.ERROR;
      message = line.substring("error: ".length());

    } else {
      status = Status.UNKNOWN;
      message = line;
    }
    return new GrblResponse(status, message);
  }

  public enum Status {
    OK, ERROR, UNKNOWN;
  }
}
