package de.zebrajaeger.jgrblconnector.common;

/**
 * Created by lars on 03.09.2016.
 */
public class Position {
    private float x;
    private float y;
    private float z;

    public Position(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    public static Position of(String sx,String sy,String sz) {
        try {
            float x = Float.parseFloat(sx.trim());
            float y = Float.parseFloat(sy.trim());
            float z = Float.parseFloat(sz.trim());
            return new Position(x, y, z);
        }catch(NumberFormatException e){
            // NOP
        }
        return null;
    }

    public static Position of(String toParse) {
        String[] parts = toParse.split(",");
        if(parts.length==3){
            return of(parts[0],parts[1],parts[2]);
        }

        return null;
    }
}
