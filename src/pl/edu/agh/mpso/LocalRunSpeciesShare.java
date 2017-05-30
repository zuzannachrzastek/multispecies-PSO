package pl.edu.agh.mpso;

import net.sourceforge.jswarm_pso.FitnessFunction;
import net.sourceforge.jswarm_pso.Swarm;
import pl.edu.agh.mpso.fitness.Rastrigin;
import pl.edu.agh.mpso.species.SpeciesType;
import pl.edu.agh.mpso.utils.RunUtils;

import java.io.IOException;

import static pl.edu.agh.mpso.utils.ExecutionParameters.*;

public class LocalRunSpeciesShare {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, IOException, InterruptedException {
        FitnessFunction fitnessFunction = new Rastrigin();
        final int[] speciesShares = new int[]{0, 4, 11, 18, 25};

        for (SpeciesType speciesType : SpeciesType.values()) {
            RunUtils.runParallel(0, fitnessFunction, EXECUTIONS, speciesShares, speciesType.getType(), Swarm.DEFAULT_NUMBER_OF_PARTICLES);

        }
    }
}
