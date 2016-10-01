package de.zebrajaeger.jgrblconnector;

import de.zebrajaeger.jgrblconnector.serial.SerialConnectionAdapter;

import java.io.IOException;

/**
 * Created by lars on 04.09.2016.
 */
public class GrblMock extends SerialConnectionAdapter {
    public static final String DUMMY_STATE = "<Idle,MPos:0.000,0.000,0.000,WPos:0.000,0.000,0.000>";
    private StringBuffer rec = new StringBuffer();
    private Object sendLock = new Object();

    @Override
    public void write(byte b) throws IOException {
        if (b == '?') {
            send(DUMMY_STATE);

        } else if (b == '#') {
            throw new IOException("i don't fell comfortable");

        } else if (b == '\r' || b == '\n') {
            if (rec.length() > 0) {
                if (rec.toString().equals("G00")) {
                    executeOkMessage();
                } else {
                    executeErrorMessage("u fucked up!");
                }
                rec = new StringBuffer();
            }

        } else {
            rec.append((char) b);
        }
    }

    private void send(String toSend) {
        synchronized (sendLock) {
            sendEvent(toSend);
        }
    }

    public void executeOkMessage() {
        send("\r\nok\r\n");
    }

    public void executeErrorMessage(String msg) {
        send("error: " + msg + "\r\n");
    }

    public void executeStartMessage(String version) {
        send("\r\nGrbl " + version + " ['$' for help]\r\n");
    }

    public void executeAlarm(String msg) {
        send("ALARM: " + msg + "\n");
    }

    public void executeFeedback(String feedback) {
        send("[" + feedback + "]\n");
    }
}
