package de.zebrajaeger.jgrblconnector.serial;

/**
 * @author Lars Brandt on 03.09.2016.
 */
public interface SerialReceiveListener {
  void onReceive(byte b);
}
