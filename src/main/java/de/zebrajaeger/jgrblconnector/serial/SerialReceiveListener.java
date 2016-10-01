package de.zebrajaeger.jgrblconnector.serial;

/**
 * Created by lars on 03.09.2016.
 */
public interface SerialReceiveListener {
    void onReceive(byte b);
}
