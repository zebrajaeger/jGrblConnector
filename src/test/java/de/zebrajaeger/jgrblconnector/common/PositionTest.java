package de.zebrajaeger.jgrblconnector.common;

import de.zebrajaeger.jgrblconnector.gear.GearSet;
import de.zebrajaeger.jgrblconnector.gear.SimpleGear;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by lars on 03.09.2016.
 */
public class PositionTest {

  @Test
  public void of0() throws Exception {
    Position pos = Position.Builder.of("0.000", "0.000").build();
    assertThat(pos.getX(), is(0.0f));
    assertThat(pos.getY(), is(0.0f));
  }

  @Test
  public void of1() throws Exception {
    Position pos = Position.Builder.of("1.234", "2.345").build();
    assertThat(pos.getX(), is(1.234f));
    assertThat(pos.getY(), is(2.345f));
  }

  @Test
  public void ofGeard() throws Exception {
    Position pos = Position.Builder
        .of("1.000", "6.000")
        .gearSet(new GearSet(new SimpleGear(2f), new SimpleGear(3f)))
        .build();
    assertThat(pos.getX(), is(0.500f));
    assertThat(pos.getY(), is(2.000f));
  }

}