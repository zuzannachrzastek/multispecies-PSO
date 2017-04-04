package pl.edu.agh.mpso;

import net.sourceforge.jswarm_pso.FitnessFunction;
import net.sourceforge.jswarm_pso.Neighborhood;
import net.sourceforge.jswarm_pso.Neighborhood1D;
import pl.edu.agh.mpso.fitness.Rastrigin;
import pl.edu.agh.mpso.output.SimulationResult;
import pl.edu.agh.mpso.species.SpeciesType;
import pl.edu.agh.mpso.swarm.MultiSwarm;
import pl.edu.agh.mpso.swarm.SwarmInformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static pl.edu.agh.mpso.Simulation.*;

/**
 * @author iwanb
 *         command line args:
 *         - function name - must be the same as class from pl.edu.agh.miss.fitness
 *         - number of dimensions
 *         - number of iterations
 *         - species id
 *         - proportional share of given species
 */
public class SpeciesShare {
    private static String className;
    private static int speciesId = 1;
    private static int speciesCnt = NUMBER_OF_PARTICLES;
    private static int numberOfSpecies = SpeciesType.values().length;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, IOException {
        //get optimization problem
        FitnessFunction fitnessFunction = null;
        Class<? extends FitnessFunction> fitnessFunctionClass = Rastrigin.class;
        className = args.length >= 1 ? args[0] : "Rastrigin";
        final String packageName = "pl.edu.agh.miss.fitness";
        try {
            fitnessFunctionClass = (Class<FitnessFunction>) Class.forName(packageName + "." + className);
        } catch (ClassNotFoundException e) {
            System.out.println(className + " " + e.getMessage() + " using Rastrigin function");
        } finally {
            fitnessFunction = fitnessFunctionClass.newInstance();
        }

        //get number of dimensions
        if (args.length >= 2) {
            NUMBER_OF_DIMENSIONS = Integer.valueOf(args[1]);
            if (NUMBER_OF_DIMENSIONS <= 0) NUMBER_OF_DIMENSIONS = 10;
        }
        //get number of iterations
        if (args.length >= 3) {
            NUMBER_OF_ITERATIONS = Integer.valueOf(args[2]);
            if (NUMBER_OF_ITERATIONS <= 0) NUMBER_OF_ITERATIONS = 1000;
        }
        //get species id
        if (args.length >= 4) {
            speciesId = Integer.valueOf(args[3]);
            if (speciesId <= 0) speciesId = 1;
            if (speciesId > numberOfSpecies) speciesId = 1;
        }
        //get species id
        if (args.length >= 5) {
            speciesCnt = Integer.valueOf(args[4]);
            if (speciesCnt < 0) speciesCnt = NUMBER_OF_PARTICLES;
            if (speciesCnt > NUMBER_OF_PARTICLES) speciesCnt = NUMBER_OF_PARTICLES;
        }

        //create array of species share
        int[] speciesArray = new int[numberOfSpecies];

        for (int i = 0; i < numberOfSpecies; i++) {
            if (i == speciesId - 1) {
                speciesArray[i] = speciesCnt;
            } else {
                speciesArray[i] = (NUMBER_OF_PARTICLES - speciesCnt) / (numberOfSpecies - 1);
            }
        }

        //TODO
//		SimulationResult result = RunUtils.run(speciesArray, fitnessFunction);
//		RunUtils.generateOutputFile(speciesArray, fitnessFunction, result);
    }
}
