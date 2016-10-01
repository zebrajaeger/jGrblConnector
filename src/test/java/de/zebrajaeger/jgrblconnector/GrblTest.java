package de.zebrajaeger.jgrblconnector;

import de.zebrajaeger.jgrblconnector.serial.SerialConnectionAdapter;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

/**
 * Created by lars on 04.09.2016.
 */
public class GrblTest {
    private static Logger LOG = LoggerFactory.getLogger(GrblTest.class);

    @Test
    public void floatToString() throws Exception {
        Grbl grbl = new Grbl(new EmptySerialConnection(), 1000);
        assertThat(grbl.floatToString(1.234567f), is("1.235"));
        assertThat(grbl.floatToString(71.234567f), is("71.235"));
        assertThat(grbl.floatToString(0.0f), is("0"));
    }

    @Test
    public void commandTimeout() throws IOException, InterruptedException {
        Grbl grbl = new Grbl(new EmptySerialConnection(), 1000);

        long start = System.currentTimeMillis();
        grbl.moveXRelativeBlocking(0.0f);
        long end = System.currentTimeMillis();
        long diff = end - start;
        LOG.info("time[ms]: " + diff);

        // hopefully range matches on different systems...
        assertThat(diff, greaterThan(900l));
        assertThat(diff, lessThan(2000l));
    }

    @Test
    public void commandAnswer() throws IOException, InterruptedException {
        Grbl grbl = new Grbl(new DummySerialConnection(), 1000);

        long start = System.currentTimeMillis();
        grbl.moveXRelativeBlocking(0.0f);
        long end = System.currentTimeMillis();
        long diff = end - start;
        LOG.info("time[ms]: " + diff);

        // hopefully range matches on different systems...
        assertThat(diff, greaterThan(0l));
        assertThat(diff, lessThan(900l));
    }

    @Test
    public void statusTimeout() throws IOException, InterruptedException {
        Grbl grbl = new Grbl(new EmptySerialConnection(), 1000);

        long start = System.currentTimeMillis();
        grbl.requestStatusBlocking();
        long end = System.currentTimeMillis();
        long diff = end - start;
        LOG.info("time[ms]: " + diff);

        // hopefully range matches on different systems...
        assertThat(diff, greaterThan(900l));
        assertThat(diff, lessThan(2000l));
    }

    @Test
    public void statusAnswer() throws IOException, InterruptedException {
        Grbl grbl = new Grbl(new DummySerialConnection(), 1000);

        long start = System.currentTimeMillis();
        grbl.requestStatusBlocking();
        long end = System.currentTimeMillis();
        long diff = end - start;
        LOG.info("time[ms]: " + diff);

        // hopefully range matches on different systems...
        assertThat(diff, greaterThan(0l));
        assertThat(diff, lessThan(900l));
    }



    /**
     * does nothing and provoke a timeout
     */
    class EmptySerialConnection extends SerialConnectionAdapter {
        @Override
        public void write(byte b) throws IOException {

        }
    }

    /**
     * after receiving '\n' waits 500ms and sends a 'ok\r\n' answer
     * after receiving '?' waits 500ms and sends a status answer
     */
    class DummySerialConnection extends SerialConnectionAdapter {
        @Override
        public void write(byte b) throws IOException {

            // LF
            if (b == '\n') {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                        }
                        DummySerialConnection.this.sendEvent("ok\r\n");
                    }
                }).start();

                // STATUS
            }else if(b=='?'){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                        }
                        DummySerialConnection.this.sendEvent("<Idle,MPos:0.000,0.000,0.000,WPos:0.000,0.000,0.000>\r\n");
                    }
                }).start();

            }
        }
    }
}