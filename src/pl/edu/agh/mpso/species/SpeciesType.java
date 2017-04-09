package pl.edu.agh.mpso.species;

import java.awt.Color;

public class SpeciesType {
    public static final SpeciesType ALL = new SpeciesType(0, 1.0, 1.0, 1.0);
    public static final SpeciesType GLOBAL_AND_LOCAL = new SpeciesType(1, 1.0, 1.0, 0.0);
    public static final SpeciesType GLOBAL_AND_NEIGHBOUR = new SpeciesType(2, 0.0, 1.0, 1.0);
    public static final SpeciesType LOCAL_AND_NEIGHBOUR = new SpeciesType(3, 1.0, 0.0, 1.0);
    public static final SpeciesType GLOBAL_ONLY = new SpeciesType(4, 1.0, 0.0, 0.0);
    public static final SpeciesType LOCAL_ONLY = new SpeciesType(5, 0.0, 1.0, 0.0);
    public static final SpeciesType NEIGHBOUR_ONLY = new SpeciesType(6, 0.0, 0.0, 1.0);
    public static final SpeciesType RANDOM = new SpeciesType(7);

    private final int type;
    private final Color color;

    private static final String[] typeNames = new String[]{
            "Normal", "Global and local", "Global and neighbour", "Local and neighbour",
            "Global only", "Local only", "Neighbour only", "Random weights"
    };
    private double global;
    private double local;
    private double neighbour;


    private SpeciesType(int type, double global, double local, double neighbour) {
        this.type = type;
        this.global = global;
        this.local = local;
        this.neighbour = neighbour;
        int rgb = 255 / (8 - type);
        this.color = new Color(rgb, rgb, rgb);
    }

    public SpeciesType(int type) {
        this.type = type;
        this.global = Math.random() * 3.0;
        this.local = Math.random() * 3.0 - global;
        this.neighbour = 3.0 - global - local;
        int rgb = 255 / (8 - type);
        this.color = new Color(rgb, rgb, rgb);
        System.out.println(global + "," + local + "," + neighbour + "," + type);

    }

    @Override
    public String toString() {
        return typeNames[type];
    }

    public int getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    public double[] getWeights() {
        return new double[]{global, local, neighbour};
    }

    public static SpeciesType[] values() {
        return new SpeciesType[]{ALL, GLOBAL_AND_LOCAL, GLOBAL_AND_NEIGHBOUR, LOCAL_AND_NEIGHBOUR, GLOBAL_ONLY, LOCAL_ONLY, NEIGHBOUR_ONLY, RANDOM};
    }

}
