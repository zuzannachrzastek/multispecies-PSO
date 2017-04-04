package pl.edu.agh.mpso;

import net.sourceforge.jswarm_pso.FitnessFunction;
import net.sourceforge.jswarm_pso.Neighborhood;
import net.sourceforge.jswarm_pso.Neighborhood1D;
import pl.edu.agh.mpso.dao.SimulationResultDAO;
import pl.edu.agh.mpso.dao.SwarmInfoEntity;
import pl.edu.agh.mpso.output.SimulationOutput;
import pl.edu.agh.mpso.output.SimulationOutputError;
import pl.edu.agh.mpso.output.SimulationOutputOk;
import pl.edu.agh.mpso.output.SimulationResult;
import pl.edu.agh.mpso.species.SpeciesType;
import pl.edu.agh.mpso.swarm.MultiSwarm;
import pl.edu.agh.mpso.swarm.SwarmInformation;
import pl.edu.agh.mpso.transition.order.DefaultOrderFunction;
import pl.edu.agh.mpso.transition.shift.DefaultShiftFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static pl.edu.agh.mpso.Simulation.NUMBER_OF_DIMENSIONS;
import static pl.edu.agh.mpso.Simulation.NUMBER_OF_ITERATIONS;
import static pl.edu.agh.mpso.Simulation.NUMBER_OF_PARTICLES;

/**
 * Created by Zuzanna on 3/30/2017.
 */
public class RunUtils {

    public static void runParallel(final int id, final FitnessFunction fitnessFunction, final List<SwarmInformation> speciesArray, final int executions) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {

            public void run() {
                for (int i = 0; i < executions; i++) {
                    try {
                        simulate(fitnessFunction, speciesArray, id, executions, i);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        thread.join();
    }

    private static void simulate(FitnessFunction fitnessFunction,
                                 List<SwarmInformation> swarmInformations, int id, int executions, int i) throws IOException {
        SimulationOutput output = null;
        try {
            long tic = System.currentTimeMillis();
            SimulationResult result = run(swarmInformations, fitnessFunction);
            long toc = System.currentTimeMillis();
            long diff = toc - tic;
            long seconds = diff / 1000L;
            long minutes = seconds / 60L;
            long hours = minutes / 60L;
            System.out.println("" + id + ": Execution " + (i + 1) + " of " + executions);
            System.out.println("\tDone in: " + hours + " hours " + (minutes % 60) + " minutes " + (seconds % 60) + " seconds");

            //TODO delete output..?
            output = new SimulationOutputOk(result);
            SimulationResultDAO.getInstance().writeResult(result);
            SimulationResultDAO.getInstance().close();
            System.out.println(result.getBestFitness());
        } catch (Throwable e) {
            e.printStackTrace();
            output = new SimulationOutputError(e.toString() + ": " + e.getMessage());
        } finally {
//			Writer writer = new FileWriter("output.json");
//			Gson gson = new Gson();
//			gson.toJson(output, writer);
//			writer.close();
        }
    }

    private static SimulationResult run(List<SwarmInformation> swarmInformations, FitnessFunction fitnessFunction) {
        MultiSwarm multiSwarm = new MultiSwarm(swarmInformations, fitnessFunction);

        Neighborhood neighbourhood = new Neighborhood1D(7, true);
        multiSwarm.setNeighborhood(neighbourhood);

        multiSwarm.setInertia(0.95);
        multiSwarm.setNeighborhoodIncrement(0.9);
        multiSwarm.setParticleIncrement(0.9);
        multiSwarm.setGlobalIncrement(0.9);

        multiSwarm.setMaxPosition(20);
        multiSwarm.setMinPosition(-20);

        multiSwarm.setOrderFunction(new DefaultOrderFunction());
        multiSwarm.setShiftFunction(new DefaultShiftFunction());

//		multiSwarm.setAbsMaxVelocity(2.0);
        multiSwarm.init();

        List<Double> partial = new ArrayList<Double>(NUMBER_OF_ITERATIONS / 100);

        for (int i = 0; i < NUMBER_OF_ITERATIONS; ++i) {
            // Evolve swarm
            multiSwarm.evolve();

            //display partial results
            if (NUMBER_OF_ITERATIONS > 100 && (i % (NUMBER_OF_ITERATIONS / 100) == 0)) {
                partial.add(multiSwarm.getBestFitness());
            }
        }

        //create output.json
        SimulationResult.SimulationResultBuilder builder = new SimulationResult.SimulationResultBuilder();
        return builder.setFitnessFunction(fitnessFunction.getClass().getName())
                .setIterations(NUMBER_OF_ITERATIONS)
                .setDimensions(NUMBER_OF_DIMENSIONS)
                .setPartial(partial)
                .setBestFitness(multiSwarm.getBestFitness())
                .setTotalParticles(NUMBER_OF_PARTICLES)
                .setSwarmInformations(pl.edu.agh.mpso.utils.RunUtils.getSwarmEntityList(swarmInformations))
                .setOrderFunction(multiSwarm.getOrderFunction().getClass().getSimpleName())
                .setShiftFunction(multiSwarm.getShiftFunction().getClass().getSimpleName())
                .build();
    }
}
