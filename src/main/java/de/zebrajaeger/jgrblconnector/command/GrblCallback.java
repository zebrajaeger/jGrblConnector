package de.zebrajaeger.jgrblconnector.command;

/**
 * Created by lars on 03.09.2016.
 */
public interface GrblCallback {
    void grblResponse(GrblResponse response);
}
