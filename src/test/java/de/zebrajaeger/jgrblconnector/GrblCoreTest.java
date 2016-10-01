package de.zebrajaeger.jgrblconnector;

import de.zebrajaeger.jgrblconnector.command.GrblCallback;
import de.zebrajaeger.jgrblconnector.command.GrblCommand;
import de.zebrajaeger.jgrblconnector.command.GrblResponse;
import de.zebrajaeger.jgrblconnector.event.GrblAlarmEvent;
import de.zebrajaeger.jgrblconnector.event.GrblAlarmListener;
import de.zebrajaeger.jgrblconnector.event.GrblInfoEvent;
import de.zebrajaeger.jgrblconnector.event.GrblInfoListener;
import de.zebrajaeger.jgrblconnector.event.GrblStartEvent;
import de.zebrajaeger.jgrblconnector.event.GrblStatusListener;
import de.zebrajaeger.jgrblconnector.event.GrblStartListener;
import de.zebrajaeger.jgrblconnector.event.GrblStatusEvent;

import junit.framework.Assert;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by lars on 03.09.2016.
 */
public class GrblCoreTest {

    private GrblMock grblMock;
    private GrblCore grblCore;
    private TestListener listener;
    private TestCallback callback;

    @Before
    public void setup() {
        grblMock = new GrblMock();
        grblCore = new GrblCore(grblMock);
        listener = new TestListener();
        callback = new TestCallback();
        grblCore.addListener(listener);
    }

    @After
    public void tearDown() {
        grblMock = null;
        grblCore = null;
        listener = null;
        callback = null;
    }

    @Test
    public void startupMessage() throws InterruptedException, IOException {
        String version = "dummy v1.0";
        grblMock.executeStartMessage(version);

        // the start event itself...
        Assert.assertNotNull(listener.getStartEvent());
        assertThat(listener.getStartEvent().getVersion(), is(version));

        // but it contains a info block so we have check it
        Assert.assertNotNull(listener.getInfoEvent());
        assertThat(listener.getInfoEvent().getInfo(), is("'$' for help"));
    }

    @Test
    public void alarm() throws InterruptedException, IOException {
        String msg = "Foo Bar";
        grblMock.executeAlarm(msg);

        Assert.assertNotNull(listener.getAlarmEvent());
        assertThat(listener.getAlarmEvent().getMessage(), is(msg));
    }

    @Test
    public void commandResposeOk() throws InterruptedException, IOException {

        GrblCommand cmd = GrblCommand
                .of("G00")
                .callback(callback)
                .build();

        grblCore.sendCommand(cmd);
        for (int i = 0; i < 20 && callback.getResponse() == null; ++i) {
            Thread.sleep(100);
        }
        Assert.assertNotNull(callback.getResponse());
        assertThat(callback.getResponse().getStatus(), Matchers.is(GrblResponse.Status.OK));
        Assert.assertNull(callback.getResponse().getMessage());
    }

    @Test
    public void commandResposeError() throws InterruptedException, IOException {

        GrblCommand cmd = GrblCommand
                .of("G01")
                .callback(callback)
                .build();

        grblCore.sendCommand(cmd);
        for (int i = 0; i < 20 && callback.getResponse() == null; ++i) {
            Thread.sleep(100);
        }
        Assert.assertNotNull(callback.getResponse());
        assertThat(callback.getResponse().getStatus(), is(GrblResponse.Status.ERROR));
        assertThat(callback.getResponse().getMessage(), is("u fucked up!"));
    }

    @Test
    public void commandBlocking() throws InterruptedException {
        final List<String> result = new LinkedList<>();
        List<Thread> threads = new LinkedList<>();

        for (int i = 0; i < 10; ++i) {
            final int finalI = i;
            final GrblCommand cmd = GrblCommand
                    .of("G00")
                    .callback(new GrblCallback() {
                        @Override
                        public void grblResponse(GrblResponse response) {
                            System.out.println("cmd" + finalI + ".response");
                            result.add("cmd" + finalI + ".response");

                        }
                    })
                    .build();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("cmd" + finalI + ".startThread");
                        Thread.sleep((long) (Math.random() * 1000));
                        System.out.println("cmd" + finalI + ".send");
                        grblCore.sendCommand(cmd);
                    } catch (InterruptedException | IOException e) {
                    }
                }
            });
            threads.add(thread);
            thread.start();
        }

        // wait until all threads have finished
        boolean wait = true;
        while (wait) {
            Thread.sleep(100);
            wait = false;
            for (Thread t : threads) {
                wait |= t.isAlive();
            }
        }

    }

    @Test
    public void commandWithStatus() throws InterruptedException, IOException {

        GrblCommand cmd = GrblCommand
                .of("G0?0")
                .callback(callback)
                .build();

        grblCore.sendCommand(cmd);
        for (int i = 0; i < 20 && callback.getResponse() == null; ++i) {
            Thread.sleep(100);
        }

        Assert.assertNotNull(callback.getResponse());
        assertThat(callback.getResponse().getStatus(), is(GrblResponse.Status.OK));
        assertThat(callback.getResponse().getMessage(), is(nullValue()));

        assertNotNull(listener.getStatusEvent());
        assertThat(listener.getStatusEvent().getStatus(), is(GrblStatusEvent.Status.Idle));

        // TODO check order of response
    }

    class TestCallback implements GrblCallback {
        private GrblResponse response;

        @Override
        public void grblResponse(GrblResponse response) {
            this.response = response;
        }

        public GrblResponse getResponse() {
            return response;
        }
    }

    class TestListener implements GrblStartListener, GrblStatusListener, GrblAlarmListener, GrblInfoListener {
        private GrblStartEvent startEvent;
        private GrblStatusEvent statusEvent;
        private GrblAlarmEvent alarmEvent;
        private GrblInfoEvent infoEvent;

        @Override
        public void grblStatus(GrblStatusEvent status) {
            this.statusEvent = status;
        }

        @Override
        public void grblAlarm(GrblAlarmEvent event) {
            alarmEvent = event;
        }

        @Override
        public void grblInfo(GrblInfoEvent event) {
            infoEvent = event;
        }

        @Override
        public void grblStart(GrblStartEvent event) {
            startEvent = event;
        }

        public GrblStatusEvent getStatusEvent() {
            return statusEvent;
        }

        public GrblStartEvent getStartEvent() {
            return startEvent;
        }

        public GrblAlarmEvent getAlarmEvent() {
            return alarmEvent;
        }

        public GrblInfoEvent getInfoEvent() {
            return infoEvent;
        }
    }

}