package de.zebrajaeger.jgrblconnector.command;

import org.junit.Test;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

/**
 * Created by lars on 03.09.2016.
 */
public class GrblResponseTest {
    @Test
    public void ofOk(){
        GrblResponse res = GrblResponse.of("ok");
        assertThat(res.getStatus(), is(GrblResponse.Status.OK));
        assertNull("message is not null", res.getMessage());
    }

    @Test
    public void ofError(){
        GrblResponse res = GrblResponse.of("error: foobar");
        assertThat(res.getStatus(), is(GrblResponse.Status.ERROR));
        assertThat(res.getMessage(), is("foobar"));
    }

    @Test
    public void ofUnknown(){
        GrblResponse res = GrblResponse.of("ALARM: 1234");
        assertThat(res.getStatus(), is(GrblResponse.Status.UNKNOWN));
        assertThat(res.getMessage(), is("ALARM: 1234"));
    }

}