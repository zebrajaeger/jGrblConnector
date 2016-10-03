package de.zebrajaeger.jgrblconnector.command;

/**
 * Created by lars on 03.09.2016.
 */
public class GrblCommand {
  private String command;
  private GrblCallback callback;
  private Long queueTimeout;
  private Long responseTimeout;

  private GrblCommand(String command, GrblCallback callback, Long queueTimeout, Long responseTimeout) {
    this.command = command;
    this.callback = callback;
    this.queueTimeout = queueTimeout;
    this.responseTimeout = responseTimeout;
  }

  public String getCommand() {
    return command;
  }

  public GrblCallback getCallback() {
    return callback;
  }

  public Long getQueueTimeout() {
    return queueTimeout;
  }

  public Long getResponseTimeout() {
    return responseTimeout;
  }

  public static Builder of(String command) {
    return new Builder(command);
  }

  public static class Builder {
    private String command;
    private GrblCallback callback;
    private Long queueTimeout;
    private Long responseTimeout;

    private Builder(String command) {
      this.command = command;
    }

    public Builder callback(GrblCallback withCallback) {
      this.callback = withCallback;
      return this;
    }

    public Builder queueTimeout(Long withQueueTimeout) {
      this.queueTimeout = withQueueTimeout;
      return this;
    }

    public Builder responseTimeout(Long withResponseTimeout) {
      this.responseTimeout = withResponseTimeout;
      return this;
    }

    public GrblCommand build() {
      return new GrblCommand(command, callback, queueTimeout, responseTimeout);
    }
  }

  @Override
  public String toString() {
    return "GrblCommand{"
        + "responseTimeout=" + responseTimeout
        + ", queueTimeout=" + queueTimeout
        + ", callback=" + callback
        + ", command='" + command + '\''
        + '}';
  }
}
