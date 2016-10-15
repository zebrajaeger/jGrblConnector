package de.zebrajaeger.jgrblconnector;

import de.zebrajaeger.jgrblconnector.command.GrblCallback;
import de.zebrajaeger.jgrblconnector.command.GrblCommand;
import de.zebrajaeger.jgrblconnector.command.GrblResponse;
import de.zebrajaeger.jgrblconnector.event.GrblListener;
import de.zebrajaeger.jgrblconnector.event.GrblListenerAdapter;
import de.zebrajaeger.jgrblconnector.event.GrblStatusEvent;
import de.zebrajaeger.jgrblconnector.gear.Gear;
import de.zebrajaeger.jgrblconnector.gear.NoGear;
import de.zebrajaeger.jgrblconnector.serial.SerialConnection;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Lars Brandt on 04.09.2016.
 */
public class Grbl extends GrblListenerAdapter {
  public static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.ENGLISH);

  private long timeout;
  @Nonnull
  private GrblCore core;
  @Nonnull
  private Object statusLock = new Object();
  @Nullable
  private GrblStatusEvent lastStatus;
  @Nonnull
  private Gear gear = NoGear.instance();

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

  @Nonnull
  public Gear getGear() {
    return gear;
  }

  public void setGear(@Nonnull Gear gear) {
    this.gear = gear;
  }

  public GrblResponse moveXRelativeBlocking(float x) throws InterruptedException, IOException {
    return sendCommandBlocking("G91 X" + floatToString(gear.driveSideToMotorSide(x)));
  }

  public GrblResponse moveYRelativeBlocking(float y) throws InterruptedException, IOException {
    return sendCommandBlocking("G91 Y" + floatToString(gear.driveSideToMotorSide(y)));
  }

  public GrblResponse moveXYRelativeBlocking(float x, float y) throws InterruptedException, IOException {
    //System.out.println("## " + floatToString(x));
    return sendCommandBlocking("G91 "
        + " X" + floatToString(gear.driveSideToMotorSide(x))
        + " Y" + floatToString(gear.driveSideToMotorSide(y)));
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
