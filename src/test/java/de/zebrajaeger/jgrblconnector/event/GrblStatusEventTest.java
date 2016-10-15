package de.zebrajaeger.jgrblconnector.event;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


/**
 * Created by lars on 03.09.2016.
 */
public class GrblStatusEventTest {
    @Test
    public void of() throws Exception {
        GrblStatusEvent stat = GrblStatusEvent.Builder
            .of("<Idle,MPos:1.000,2.000,3.000,WPos:4.000,5.000,6.000>")
            .build();
        assertThat(stat.getStatus(), is(GrblStatusEvent.Status.Idle));
        assertThat(stat.getMpos().getX(), is(1f));
        assertThat(stat.getMpos().getY(), is(2f));
        assertThat(stat.getWpos().getX(), is(4f));
        assertThat(stat.getWpos().getY(), is(5f));
    }
}