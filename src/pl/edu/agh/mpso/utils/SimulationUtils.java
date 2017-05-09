package pl.edu.agh.mpso.utils;

import net.sourceforge.jswarm_pso.FitnessFunction;
import pl.edu.agh.mpso.dao.SimulationResultDAO;
import pl.edu.agh.mpso.output.SimulationOutput;
import pl.edu.agh.mpso.output.SimulationOutputError;
import pl.edu.agh.mpso.output.SimulationOutputOk;
import pl.edu.agh.mpso.output.SimulationResult;
import pl.edu.agh.mpso.swarm.MultiSwarm;
import pl.edu.agh.mpso.swarm.SwarmInformation;

import java.io.IOException;
import java.util.List;

import static pl.edu.agh.mpso.Simulation.NUMBER_OF_ITERATIONS;
import static pl.edu.agh.mpso.utils.ExecutionParameters.*;

/**
 * Created by Zuzanna on 4/5/2017.
 */
public class SimulationUtils {

    public static void simulate(FitnessFunction fitnessFunction,
                         List<SwarmInformation> speciesArray, int id, int executions, int i) throws IOException {
        SimulationOutput output = null;
        try {
            long tic = System.currentTimeMillis();

            MultiSwarm multiSwarm = new MultiSwarm(speciesArray, fitnessFunction);
            SimulationResult result = RunUtils.run(multiSwarm, speciesArray, fitnessFunction, INERTIA, SEARCH_SPACE_SIZE, SIZE);

            long toc = System.currentTimeMillis();
            long diff = toc - tic;
            long seconds = diff / 1000L;
            long minutes = seconds / 60L;
            long hours = minutes / 60L;
            System.out.println("" + id + ": Execution " + (i + 1) + " of " + executions);
            System.out.println("\tDone in: " + hours + " hours " + (minutes % 60) + " minutes " + (seconds % 60) + " seconds");

            output = new SimulationOutputOk(result);
            SimulationResultDAO.getInstance().writeResult(result);
            SimulationResultDAO.getInstance().close();
            System.out.println(result.getBestFitness());
        } catch (Throwable e) {
            e.printStackTrace();
            output = new SimulationOutputError(e.toString() + ": " + e.getMessage());
        }
    }

    public static double runSimulation(FitnessFunction fitnessFunction, double inertia, double maxVelocity, int[] particles) {
        int cnt = 0;

        List<SwarmInformation> swarmInformations = SwarmUtils.createSwarmInfoListWithCounter(particles, cnt);

//        SwarmInformation [] swarmInformationsArray = new SwarmInformation [swarmInformations.size()];
        MultiSwarm multiSwarm = new MultiSwarm(swarmInformations, fitnessFunction);

        SwarmUtils.setMultiSwarmParameters(multiSwarm, cnt/5, inertia, 5.12);

        multiSwarm.setAbsMaxVelocity(maxVelocity);
        multiSwarm.init();

        for(int i = 0; i < NUMBER_OF_ITERATIONS; ++i) {
            // Evolve swarm
            multiSwarm.evolve();
        }

        return multiSwarm.getBestFitness();
    }
}
