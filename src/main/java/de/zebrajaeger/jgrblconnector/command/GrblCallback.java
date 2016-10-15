package de.zebrajaeger.jgrblconnector.command;

/**
 * @author Lars Brandt on 03.09.2016.
 */
public interface GrblCallback {
  void grblResponse(GrblResponse response);
}
