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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public static void runParallel(final double par, final int executions) throws InterruptedException {
        Map<Double, Double> results = new HashMap<Double, Double>();
        Thread thread = new Thread(new Runnable() {
            List<Double> fitnessList = new ArrayList<Double>(executions);

            public void run() {
                for(int i = 0; i < executions; i++){
                    System.out.println("" + par + ": " + i + " of " + executions);
                    double fitness = runSimulation(new Rastrigin(), par, 1.0);
                    fitnessList.add(fitness);
                }

                double sum = 0.0;
                for(double f : fitnessList){
                    sum += f;
                }
                double avg = sum / (double) executions;

                results.put(par, avg);
            }
        });

        thread.start();
        thread.join();

        for(double i = 0.0; i <= 1.0; i+=0.05){
            System.out.println("" + i + " : " + results.get(i));
        }
    }

    static void simulate(FitnessFunction fitnessFunction,
                         List<SwarmInformation> speciesArray, int id, int executions, int i) throws IOException {
        SimulationOutput output = null;
        try {
            long tic = System.currentTimeMillis();
            SimulationResult result = run(speciesArray, fitnessFunction);
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
                .setSwarmInformations(getSwarmEntityList(swarmInformations))
                .setInitialVelocity(initialVelocity)
                .setFinalVelocity(finalVelocity).build();
    }

    public static List<SwarmInfoEntity> getSwarmEntityList(List<SwarmInformation> swarmInformations) {
        List<SwarmInfoEntity> result = new ArrayList<>();
        //the original list is grouped by type and aggregated by number of particles
        swarmInformations.stream()
                .collect(
                        Collectors.groupingBy(swarmInformation -> swarmInformation.getType().getType(),
                                Collectors.summingInt(SwarmInformation::getNumberOfParticles)))
                .forEach(((type, numberOfParticles) -> result.add(new SwarmInfoEntity(numberOfParticles, type))));
        return result;
    }

    private static double runSimulation(FitnessFunction fitnessFunction, double inertia, double maxVelocity) {
        int cnt = 0;
        int [] particles = new int[]{40,0,0,0,0,0,0,0};

        List<SwarmInformation> swarmInformations = SwarmUtils.createSwarmInfoListWithCounter(particles, cnt);

        SwarmInformation [] swarmInformationsArray = new SwarmInformation [swarmInformations.size()];
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
                .setSwarmInformations(getSwarmEntityList(particles))
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
