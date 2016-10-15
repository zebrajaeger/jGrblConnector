package de.zebrajaeger.jgrblconnector.event;

/**
 * @author Lars Brandt on 03.09.2016.
 */
public interface GrblStatusListener extends GrblListener {
  void grblStatus(GrblStatusEvent status);
}
