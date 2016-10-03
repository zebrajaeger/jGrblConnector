package de.zebrajaeger.jgrblconnector.event;

/**
 * Created by lars on 03.09.2016.
 */
public interface GrblStatusListener extends GrblListener {
  void grblStatus(GrblStatusEvent status);
}
