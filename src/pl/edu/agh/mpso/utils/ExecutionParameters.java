package pl.edu.agh.mpso.utils;

import net.sourceforge.jswarm_pso.FitnessFunction;
import pl.edu.agh.mpso.fitness.Rastrigin;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by Roksana on 2017-05-09.
 */
public class ExecutionParameters {
    public static final int SEARCH_SPACE_SIZE = 2;
    public static final int NEIGHBOURHOOD_SIZE = 10;
    public static final double INERTIA = 0.95;
    public static final double NEIGHBOURHOOD_INCREMENT = 0;
    public static final double PARTICLE_INCREMENT = 0.9;
    public static final double GLOBAL_INCREMENT = 0.9;
    public static final int EXECUTIONS = 30;
    public static final int ITERATIONS = 100;
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd::HH:mm");
    public static final String LABEL = "PSO_MODIFIED::" + DATE_FORMAT.format(new Timestamp(System.currentTimeMillis()));
    public static final FitnessFunction FITNESS_FUNCTION = new Rastrigin();
    public static final int DIMENSIONS = 2;
}
