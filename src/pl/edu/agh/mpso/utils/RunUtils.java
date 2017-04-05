package pl.edu.agh.mpso.utils;

import com.google.gson.Gson;
import net.sourceforge.jswarm_pso.FitnessFunction;
import net.sourceforge.jswarm_pso.Neighborhood;
import net.sourceforge.jswarm_pso.Neighborhood1D;
import pl.edu.agh.mpso.dao.SimulationResultDAO;
import pl.edu.agh.mpso.dao.SwarmInfoEntity;
import pl.edu.agh.mpso.fitness.Rastrigin;
import pl.edu.agh.mpso.output.SimulationOutput;
import pl.edu.agh.mpso.output.SimulationOutputError;
import pl.edu.agh.mpso.output.SimulationOutputOk;
import pl.edu.agh.mpso.output.SimulationResult;
import pl.edu.agh.mpso.species.SpeciesType;
import pl.edu.agh.mpso.swarm.MultiSwarm;
import pl.edu.agh.mpso.swarm.SwarmInformation;
import pl.edu.agh.mpso.transition.order.DefaultOrderFunction;
import pl.edu.agh.mpso.transition.shift.DefaultShiftFunction;
import pl.edu.agh.mpso.velocity.LinearVelocityFunction;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

import static pl.edu.agh.mpso.Simulation.*;

/**
 * Created by Zuzanna on 3/30/2017.
 */
public abstract class RunUtils {

    public static void runParallel(final int id, final FitnessFunction fitnessFunction, final List<SwarmInformation> speciesArray, final int executions) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {

            public void run() {
                for (int i = 0; i < executions; i++) {
                    try {
                        SimulationUtils.simulate(fitnessFunction, speciesArray, id, executions, i);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        thread.join();
    }

    public static void runParallel(final double inertia, final double maxVelocity, final int executions, Map<Double, Double> results, int[] particles, String param) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {
            List<Double> fitnessList = new ArrayList<Double>(executions);

            public void run() {
                for(int i = 0; i < executions; i++){
//                    System.out.println("" + par + ": " + i + " of " + executions);
                    double fitness = SimulationUtils.runSimulation(new Rastrigin(), inertia, maxVelocity, particles);
                    fitnessList.add(fitness);
                }

                double sum = 0.0;
                for(double f : fitnessList){
                    sum += f;
                }
                double avg = sum / (double) executions;

                if(param.equals("inertia")){
                    results.put(inertia, avg);
                } else {
                    results.put(maxVelocity, avg);
                }
            }
        });

        thread.start();
        thread.join();
    }

    public static SimulationResult runWithCounter(int[] particles, FitnessFunction fitnessFunction, double initialVelocity, double finalVelocity, int VELOCITY_UPDATES) {
        int cnt = 0;
        //TODO swarmInformations should be passed as a parameter
        List<SwarmInformation> swarmInformations = SwarmUtils.createSwarmInfoList(particles);

        MultiSwarm multiSwarm = new MultiSwarm(swarmInformations, fitnessFunction);

        SwarmUtils.setMultiSwarmParameters(multiSwarm,cnt / 5, 0.95,20);

        multiSwarm.setVelocityFunction(new LinearVelocityFunction(initialVelocity, finalVelocity).setUpdatesCnt(VELOCITY_UPDATES).setUpdatesInterval(NUMBER_OF_ITERATIONS / VELOCITY_UPDATES));
        multiSwarm.init();

        List<Double> partial = new ArrayList<Double>(NUMBER_OF_ITERATIONS / 100);

        for (int i = 0; i < NUMBER_OF_ITERATIONS; ++i) {
            // Evolve swarm
            multiSwarm.evolve();

            //display partial results
            if (NUMBER_OF_ITERATIONS > 100 && (i % (NUMBER_OF_ITERATIONS / 100) == 0)) {
                partial.add(multiSwarm.getBestFitness());
                System.out.println(multiSwarm.getBestFitness());
            }
        }

        //print final results
        System.out.println(multiSwarm.getBestFitness());

        //create output.json
        SimulationResult.SimulationResultBuilder builder = new SimulationResult.SimulationResultBuilder();
        return builder.setFitnessFunction(fitnessFunction.getClass().getName())
                .setIterations(NUMBER_OF_ITERATIONS)
                .setDimensions(NUMBER_OF_DIMENSIONS)
                .setPartial(partial)
                .setBestFitness(multiSwarm.getBestFitness())
                .setTotalParticles(NUMBER_OF_PARTICLES)
                .setSwarmInformations(SwarmUtils.getSwarmEntityList(swarmInformations))
                .setInitialVelocity(initialVelocity)
                .setFinalVelocity(finalVelocity).build();
    }


    public static SimulationResult run(List<SwarmInformation> particles, FitnessFunction fitnessFunction) {
        MultiSwarm multiSwarm = new MultiSwarm(particles, fitnessFunction);

        SwarmUtils.setMultiSwarmParameters(multiSwarm, 7, 0.95,20);

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
                .setSwarmInformations(SwarmUtils.getSwarmEntityList(particles))
                .setOrderFunction(multiSwarm.getOrderFunction().getClass().getSimpleName())
                .setShiftFunction(multiSwarm.getShiftFunction().getClass().getSimpleName())
                .build();
    }

    public static void generateOutputFile(int[] speciesArray, FitnessFunction fitnessFunction, SimulationResult result) throws IOException {
        SimulationOutput output = null;
        try {
            output = new SimulationOutputOk(result);
            SimulationResultDAO.getInstance().writeResult(result);
            SimulationResultDAO.getInstance().close();
        } catch (Throwable e) {
            output = new SimulationOutputError(e.toString() + ": " + e.getMessage());
        } finally {
            Writer writer = new FileWriter("output.json");
            Gson gson = new Gson();
            gson.toJson(output, writer);
            writer.close();
        }
    }
}
