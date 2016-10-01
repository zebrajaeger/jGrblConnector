package de.zebrajaeger.jgrblconnector.common;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by lars on 03.09.2016.
 */
public class PositionTest {

    @Test
    public void of0() throws Exception {
        Position pos = Position.of("0.000,0.000,0.000");
        assertThat(pos.getX(), is(0.0f));
        assertThat(pos.getY(), is(0.0f));
        assertThat(pos.getZ(), is(0.0f));
    }

    @Test
    public void of1() throws Exception {
        Position pos = Position.of("1.234,2.345,3.456");
        assertThat(pos.getX(), is(1.234f));
        assertThat(pos.getY(), is(2.345f));
        assertThat(pos.getZ(), is(3.456f));
    }

}