package de.zebrajaeger.jgrblconnector.event;

/**
 * Created by lars on 04.09.2016.
 */
public class GrblStartEvent {
    public static final String GRBL_PREFIX = "Grbl ";
    private String version;

    private GrblStartEvent(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String
    toString() {
        return "GrblStartEvent{" +
                "start='" + version + '\'' +
                '}';
    }

    public static GrblStartEvent of(String start) {
        // info parser removes info part of start line, so we have a trailing space
        start = start.trim();

        // remove prefix so we have the version only
        if (start.startsWith(GRBL_PREFIX)) {
            start = start.substring(GRBL_PREFIX.length());
        }

        return new GrblStartEvent(start);
    }
}
