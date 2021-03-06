package de.zebrajaeger.jgrblconnector;

import de.zebrajaeger.jgrblconnector.command.GrblCallback;
import de.zebrajaeger.jgrblconnector.command.GrblCommand;
import de.zebrajaeger.jgrblconnector.command.GrblResponse;
import de.zebrajaeger.jgrblconnector.event.GrblListener;
import de.zebrajaeger.jgrblconnector.event.GrblListenerAdapter;
import de.zebrajaeger.jgrblconnector.event.GrblStatusEvent;
import de.zebrajaeger.jgrblconnector.gear.GearSet;
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
        return sendCommandBlocking("G91 X" + floatToString(core.getGearSet().getXGear().driveSideToMotorSide(x)));
    }

    public GrblResponse moveYRelativeBlocking(float y) throws InterruptedException, IOException {
        return sendCommandBlocking("G91 Y" + floatToString(core.getGearSet().getYGear().driveSideToMotorSide(y)));
    }

    public GrblResponse moveXYRelativeBlocking(float x, float y) throws InterruptedException, IOException {
        //System.out.println("## " + floatToString(x));
        return sendCommandBlocking("G91 "
                + " X" + floatToString(core.getGearSet().getXGear().driveSideToMotorSide(x))
                + " Y" + floatToString(core.getGearSet().getYGear().driveSideToMotorSide(y)));
    }

    /**
     * focus = spindle enabled
     * trigger = spindle direction
     * <p>
     * hint: USE_SPINDLE_DIR_AS_ENABLE_PIN must be enabled in settigs.h
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public GrblResponse trigger(boolean focus, boolean trigger) throws IOException, InterruptedException {
        sync();
        if (!focus && !trigger) {
            sendCommandBlocking("M3"); // spindle CW to set direction pin = LOW
            return sendCommandBlocking("M5"); // spindle OFF
        } else if (focus && !trigger) {
            return sendCommandBlocking("M3"); // spindle CW
        } else {
            return sendCommandBlocking("M4"); // spindle CCW
        }
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

    public void setGearSet(GearSet gearSet) {
        core.setGearSet(gearSet);
    }

    public GearSet getGearSet() {
        return core.getGearSet();
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
