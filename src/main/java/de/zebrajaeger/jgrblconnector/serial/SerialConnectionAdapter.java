package de.zebrajaeger.jgrblconnector.serial;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lars on 04.09.2016.
 */
public abstract class SerialConnectionAdapter implements SerialConnection {

  private List<SerialReceiveListener> listeners = new LinkedList<>();

  protected void sendEvent(String bytes) {
    sendEvent(bytes.getBytes());
  }

  protected void sendEvent(byte[] bytes) {
    for (byte b : bytes) {
      for (SerialReceiveListener l : listeners) {
        l.onReceive(b);
      }
    }
  }

  protected void sendEvent(byte b) {
    for (SerialReceiveListener l : listeners) {
      l.onReceive(b);
    }
  }

  @Override
  public void addReceiveListener(SerialReceiveListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeReceiveListener(SerialReceiveListener listener) {
    listeners.remove(listener);
  }
}
