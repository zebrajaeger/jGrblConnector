package de.zebrajaeger.jgrblconnector;

import de.zebrajaeger.jgrblconnector.command.GrblCommand;
import de.zebrajaeger.jgrblconnector.command.GrblResponse;
import de.zebrajaeger.jgrblconnector.event.GrblAlarmEvent;
import de.zebrajaeger.jgrblconnector.event.GrblAlarmListener;
import de.zebrajaeger.jgrblconnector.event.GrblInfoEvent;
import de.zebrajaeger.jgrblconnector.event.GrblInfoListener;
import de.zebrajaeger.jgrblconnector.event.GrblStartEvent;
import de.zebrajaeger.jgrblconnector.event.GrblStatusListener;
import de.zebrajaeger.jgrblconnector.event.GrblListener;
import de.zebrajaeger.jgrblconnector.event.GrblStartListener;
import de.zebrajaeger.jgrblconnector.event.GrblStatusEvent;
import de.zebrajaeger.jgrblconnector.serial.SerialConnection;
import de.zebrajaeger.jgrblconnector.serial.SerialReceiveListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lars on 03.09.2016.
 */
public class GrblCore implements SerialReceiveListener {
    public static final int EXPECTED_MAX_LINE_LENGTH = 100;
    public static final int EXPECTED_MAX_STATUS_LENGTH = 80;
    private SerialConnection con;
    private ReceiveStatus status = ReceiveStatus.STREAM;
    private StringBuffer statusBuffer = null;
    private StringBuffer infoBuffer = null;
    private StringBuffer lineBuffer = new StringBuffer(EXPECTED_MAX_LINE_LENGTH);
    private List<GrblStartListener> startListeners = new ArrayList<>();
    private List<GrblStatusListener> statusListeners = new ArrayList<>();
    private List<GrblAlarmListener> alarmListeners = new ArrayList<>();
    private List<GrblInfoListener> infoListeners = new ArrayList<>();

    private GrblCommand currentCommand = null;

    public GrblCore(SerialConnection con) {
        this.con = con;
        con.addReceiveListener(this);
    }

    public void addListener(GrblListener l) {
        if (l instanceof GrblStartListener) {
            addStartListener((GrblStartListener) l);
        }
        if (l instanceof GrblStatusListener) {
            addStatusListener((GrblStatusListener) l);
        }
        if (l instanceof GrblAlarmListener) {
            addAlarmListener((GrblAlarmListener) l);
        }
        if (l instanceof GrblInfoListener) {
            addInfoListener((GrblInfoListener) l);
        }
    }

    public void removeListener(GrblListener l){
        if (l instanceof GrblStartListener) {
            removeStartListener((GrblStartListener) l);
        }
        if (l instanceof GrblStatusListener) {
            removeStatusListener((GrblStatusListener) l);
        }
        if (l instanceof GrblAlarmListener) {
            removeAlarmListener((GrblAlarmListener) l);
        }
        if (l instanceof GrblInfoListener) {
            removeInfoListener((GrblInfoListener) l);
        }
    }

    public void addStartListener(GrblStartListener l) {
        startListeners.add(l);
    }

    public void addStatusListener(GrblStatusListener l) {
        statusListeners.add(l);
    }

    public void addAlarmListener(GrblAlarmListener l) {
        alarmListeners.add(l);
    }

    public void addInfoListener(GrblInfoListener l) {
        infoListeners.add(l);
    }

    public void removeStartListener(GrblStartListener l) {
        startListeners.remove(l);
    }

    public void removeStatusListener(GrblStatusListener l) {
        statusListeners.remove(l);
    }

    public void removeAlarmListener(GrblAlarmListener l) {
        alarmListeners.remove(l);
    }

    public void removeInfoListener(GrblInfoListener l) {
        infoListeners.remove(l);
    }

    /**
     * send a command blocking.
     * If another command is in process, this method will block until the other command got a response or has a timeout
     */
    public void sendCommand(GrblCommand cmd) throws InterruptedException, IOException {

        synchronized (this) {
            while (currentCommand != null) {
                wait();
            }
            currentCommand = cmd;

            // send command
            boolean ok = false;
            try {
                // encoding ??
                for (byte b : cmd.getCommand().getBytes()) {
                    con.write(b);
                }
                //con.write((byte) '\r');
                con.write((byte) '\n');
                ok = true;
            } finally {
                // unlock if something went wrong
                if (!ok) {
                    currentCommand = cmd;
                    notifyAll();
                }
            }
        }
    }

    /**
     * this is a realtime commando and is processed immediately
     */
    public void sendStatusRequest() throws IOException {
        con.write((byte) '?');
    }

    @Override
    public void onReceive(byte b) {

        // STATUS BEGIN
        if (b == '<' && status == ReceiveStatus.STREAM) {
            status = ReceiveStatus.STATUS;
            statusBuffer = new StringBuffer(EXPECTED_MAX_STATUS_LENGTH);
            statusBuffer.append('<');

            // STATUS END
        } else if (b == '>' && status == ReceiveStatus.STATUS) {
            status = ReceiveStatus.STREAM;
            statusBuffer.append('>');
            handleStatus(statusBuffer.toString());
            statusBuffer = null;

            // STATUS RECEIVE
        } else if (status == ReceiveStatus.STATUS) {
            statusBuffer.append((char) b);

            // INFO BEGIN
        } else if (b == '[' && status == ReceiveStatus.STREAM) {
            status = ReceiveStatus.INFO;
            infoBuffer = new StringBuffer(EXPECTED_MAX_STATUS_LENGTH);
            infoBuffer.append('[');

            // INFO END
        } else if (b == ']' && status == ReceiveStatus.INFO) {
            status = ReceiveStatus.STREAM;
            infoBuffer.append(']');
            handleInfo(infoBuffer.toString());
            infoBuffer = null;

            // INFO RECEIVE
        } else if (status == ReceiveStatus.INFO) {
            infoBuffer.append((char) b);

        } else {
            // STREAM LINEBREAK
            if (b == '\r' || b == '\n') {
                if (lineBuffer.length() != 0) {
                    handleNewLine(lineBuffer.toString());
                    lineBuffer = new StringBuffer(101);
                }
            } else {
                // STREAM RECEIVE
                lineBuffer.append((char) b);
            }
        }
    }

    private void handleNewLine(String line) {

        // COMMAND FEEDBACK
        if (line.startsWith("ok") || line.startsWith("error:")) {
            final GrblResponse response = GrblResponse.of(line);
            if (response.getStatus() != GrblResponse.Status.UNKNOWN) {
                final GrblCommand cmd = currentCommand;
                synchronized (this) {
                    currentCommand = null;
                    notifyAll();
                }

                if (cmd.getCallback() != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            cmd.getCallback().grblResponse(response);
                        }
                    }).start();
                }
            }

            // ALARM MESSAGE
        } else if (line.startsWith("ALARM:")) {
            handleAlarm(line);

        } else if (line.startsWith("Grbl ")) {
            handleStart(line);
        }
    }

    private void handleStart(String line) {
        GrblStartEvent s = GrblStartEvent.of(line);
        for (GrblStartListener l : startListeners) {
            l.grblStart(s);
        }
    }

    private void handleAlarm(String s) {
        GrblAlarmEvent alarm = GrblAlarmEvent.of(s);
        for (GrblAlarmListener l : alarmListeners) {
            l.grblAlarm(alarm);
        }
    }

    private void handleInfo(String s) {
        GrblInfoEvent info = GrblInfoEvent.of(s);
        for (GrblInfoListener l : infoListeners) {
            l.grblInfo(info);
        }
    }

    private void handleStatus(String line) {
        GrblStatusEvent s = GrblStatusEvent.of(line);
        for (GrblStatusListener l : statusListeners) {
            l.grblStatus(s);
        }
    }

    protected enum ReceiveStatus {
        STREAM,
        INFO,
        STATUS
    }
}
