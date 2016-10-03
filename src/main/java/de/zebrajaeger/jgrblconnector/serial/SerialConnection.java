package de.zebrajaeger.jgrblconnector.serial;

import java.io.IOException;

/**
 * Created by lars on 03.09.2016.
 */
public interface SerialConnection {
  void write(byte b) throws IOException;

  void addReceiveListener(SerialReceiveListener listener);

  void removeReceiveListener(SerialReceiveListener listener);
}
