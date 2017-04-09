package pl.edu.agh.mpso;

import net.sourceforge.jswarm_pso.FitnessFunction;
import pl.edu.agh.mpso.fitness.Rastrigin;
import pl.edu.agh.mpso.species.SpeciesType;
import pl.edu.agh.mpso.utils.RunUtils;

import java.io.IOException;

import static pl.edu.agh.mpso.Simulation.NUMBER_OF_DIMENSIONS;
import static pl.edu.agh.mpso.Simulation.NUMBER_OF_ITERATIONS;

public class LocalRunSpeciesShare {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, IOException, InterruptedException {
        FitnessFunction fitnessFunction = new Rastrigin();
        final int[] speciesShares = new int[]{0, 4, 11, 18, 25};
        NUMBER_OF_DIMENSIONS = 100;
        NUMBER_OF_ITERATIONS = 3000;
        int executions = 30;

        for (SpeciesType speciesType : SpeciesType.values()) {
            RunUtils.runParallel(0, fitnessFunction, executions, speciesShares, speciesType.getType());

        }
    }
}
