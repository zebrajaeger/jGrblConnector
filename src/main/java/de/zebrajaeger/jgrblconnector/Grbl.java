package de.zebrajaeger.jgrblconnector;

import de.zebrajaeger.jgrblconnector.command.GrblCallback;
import de.zebrajaeger.jgrblconnector.command.GrblCommand;
import de.zebrajaeger.jgrblconnector.command.GrblResponse;
import de.zebrajaeger.jgrblconnector.event.GrblListener;
import de.zebrajaeger.jgrblconnector.event.GrblListenerAdapter;
import de.zebrajaeger.jgrblconnector.event.GrblStatusEvent;
import de.zebrajaeger.jgrblconnector.serial.SerialConnection;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by lars on 04.09.2016.
 */
public class Grbl extends GrblListenerAdapter {
  public static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.ENGLISH);

  private long timeout;
  private GrblCore core;
  private Object statusLock = new Object();
  private GrblStatusEvent lastStatus;

  public Grbl(SerialConnection con, long timeout) {
    this.core = new GrblCore(con);
    this.core.addListener(this);
    this.timeout = timeout;
  }

  public void addListener(GrblListener l) {
    core.addListener(l);
  }

  public void removeListener(GrblListener l) {
    core.removeListener(l);
  }

  public GrblResponse moveXRelativeBlocking(float x) throws InterruptedException, IOException {
    return sendCommandBlocking("G91 X" + floatToString(x));
  }

  public GrblResponse moveYRelativeBlocking(float y) throws InterruptedException, IOException {
    return sendCommandBlocking("G91 Y" + floatToString(y));
  }

  public GrblResponse moveXYRelativeBlocking(float x, float y) throws InterruptedException, IOException {
    //System.out.println("## " + floatToString(x));
    return sendCommandBlocking("G91 X" + floatToString(x) + " Y" + floatToString(y));
  }

  /**
   * Waits for all commands in grbl-queue are processed
   */
  public GrblResponse sync() throws InterruptedException, IOException {
    return sendCommandBlocking("G4 P0.01");
  }

  /**
   * Use this or nonblocking version, but never together!
   */
  public GrblStatusEvent requestStatusBlocking() throws IOException, InterruptedException {
    synchronized (statusLock) {
      core.sendStatusRequest();
      statusLock.wait(timeout);
      return lastStatus;
    }
  }

  /**
   * Use this or blocking version, but never together!
   */
  public void requestStatus() throws IOException {
    core.sendStatusRequest();
  }

  @Override
  public void grblStatus(GrblStatusEvent status) {
    synchronized (statusLock) {
      lastStatus = status;
      statusLock.notifyAll();
    }
  }

  private GrblResponse sendCommandBlocking(String command) throws IOException, InterruptedException {
    ResponseCollectingCallback callback = new ResponseCollectingCallback();

    GrblCommand cmd = GrblCommand
        .of(command)
        .callback(callback)
        .build();

    core.sendCommand(cmd);

    synchronized (callback) {
      callback.wait(timeout);
    }

    return callback.getResponse();
  }

  public String floatToString(float f) {
    return NUMBER_FORMAT.format(f);
  }

  public String doubleToString(double f) {
    return NUMBER_FORMAT.format(f);
  }

  private static class ResponseCollectingCallback implements GrblCallback {

    private GrblResponse response;

    @Override
    public void grblResponse(GrblResponse r) {
      this.response = r;

      synchronized (this) {
        this.notifyAll();
      }
    }

    public GrblResponse getResponse() {
      return response;
    }

  }
}
