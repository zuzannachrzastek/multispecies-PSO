package pl.edu.agh.mpso.utils;

import net.sourceforge.jswarm_pso.FitnessFunction;
import pl.edu.agh.mpso.fitness.Rastrigin;
import pl.edu.agh.mpso.output.SimulationResult;
import pl.edu.agh.mpso.species.SpeciesType;
import pl.edu.agh.mpso.swarm.MultiSwarm;
import pl.edu.agh.mpso.swarm.SwarmInformation;
import pl.edu.agh.mpso.transition.order.DefaultOrderFunction;
import pl.edu.agh.mpso.transition.shift.DefaultShiftFunction;
import pl.edu.agh.mpso.velocity.LinearVelocityFunction;

import java.util.*;

import static pl.edu.agh.mpso.utils.ExecutionParameters.*;

/**
 * Created by Zuzanna on 3/30/2017.
 */
public abstract class RunUtils {

    public static void runParallel(final int id, final FitnessFunction fitnessFunction, final List<SwarmInformation> speciesArray, final int executions, String label) throws InterruptedException {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < executions; i++) {
                try {
                    SimulationUtils.simulate(fitnessFunction, speciesArray, id, ITERATIONS, i, label);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        thread.join();
    }

    public static void runParallel(final int id, final FitnessFunction fitnessFunction, final int executions,
                                   final int[] speciesShares, final int speciesId, int number_of_particles, String label) throws InterruptedException {
        Thread thread = new Thread(() -> {
            for (int share : speciesShares) {
                System.out.println("Species " + speciesId + " share " + share);

                List<SwarmInformation> speciesArray = new ArrayList<>();

                int numberOfParticles = 0;
                for (int speciesType = 0; speciesType < SpeciesType.values().length; speciesType++) {
                    if (speciesType == speciesId) {
                        numberOfParticles = share;
                    } else {
                        numberOfParticles = (number_of_particles - share) / (SpeciesType.values().length - 1);
                    }
                    addSwarmInformation(numberOfParticles, speciesArray, speciesType);
                }

                for (int i = 0; i < executions; i++) {
                    try {
                        SimulationUtils.simulate(fitnessFunction, speciesArray, id, executions, i, label);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
        thread.join();
    }

    private static void addSwarmInformation(int numberOfParticles, List<SwarmInformation> speciesArray, int speciesType) {
        if (numberOfParticles <= 0)
            return;
        if (speciesType == SpeciesType.RANDOM.getType()) {
            for (int i = 0; i < numberOfParticles; i++) {
                speciesArray.add(new SwarmInformation(new SpeciesType(SpeciesType.RANDOM.getType())));
            }
        } else {
            speciesArray.add(new SwarmInformation(numberOfParticles, SpeciesType.values()[speciesType]));
        }
    }

    public static void runParallel(final double inertia, final double maxVelocity, final int executions, Map<Double, Double> results, int[] particles, String param) throws InterruptedException {
        List<Double> fitnessList = new ArrayList<Double>(executions);

        Thread thread = new Thread(() -> {
            for (int i = 0; i < executions; i++) {
                double fitness = SimulationUtils.runSimulation(new Rastrigin(), inertia, maxVelocity, particles);
                fitnessList.add(fitness);
            }

            double sum = 0.0;
            for (double f : fitnessList) {
                sum += f;
            }
            double avg = sum / (double) executions;

            if (param.equals("inertia")) {
                results.put(inertia, avg);
            } else {
                results.put(maxVelocity, avg);
            }
        });

        thread.start();
        thread.join();
    }

    public static SimulationResult runWithCounter(MultiSwarm multiSwarm, FitnessFunction fitnessFunction, double initialVelocity, double finalVelocity, int VELOCITY_UPDATES, List<SwarmInformation> swarmInformations, double inertia, int searchSpaceSize, int size) {
        SwarmUtils.setMultiSwarmParameters(multiSwarm, size, inertia, searchSpaceSize);
        multiSwarm.setVelocityFunction(new LinearVelocityFunction(initialVelocity, finalVelocity).setUpdatesCnt(VELOCITY_UPDATES).setUpdatesInterval(ITERATIONS / VELOCITY_UPDATES));
        multiSwarm.init();

        List<Double> partial = new ArrayList<Double>(ITERATIONS);// / 100);

        evolveAndDisplay(multiSwarm, partial);

        //print final results
//        System.out.println(multiSwarm.getBestFitness());

        //create output.json
        SimulationResult.SimulationResultBuilder builder = new SimulationResult.SimulationResultBuilder();
        return builder.setFitnessFunction(fitnessFunction.getClass().getName())
                .setIterations(ITERATIONS)
                .setDimensions(DIMENSIONS)
                .setPartial(partial)
                .setBestFitness(multiSwarm.getBestFitness())
                .setTotalParticles(multiSwarm.getNumberOfParticles())
                .setSwarmInformations(SwarmUtils.getSwarmEntityList(swarmInformations))
                .setInitialVelocity(initialVelocity)
                .setFinalVelocity(finalVelocity).build();
    }

    public static void evolveAndDisplay(MultiSwarm multiSwarm, List<Double> partial) {
        for (int i = 0; i < ITERATIONS; ++i) {
            // Evolve swarm
            multiSwarm.evolve();

            //display partial results
//            if (NUMBER_OF_ITERATIONS > 100 && (i % (NUMBER_OF_ITERATIONS / 100) == 0)) {
                partial.add(multiSwarm.getBestFitness());
//                System.out.println("best fitness: " + multiSwarm.getBestFitness());
//            }
        }
    }


    public static SimulationResult run(MultiSwarm multiSwarm, List<SwarmInformation> particles, FitnessFunction fitnessFunction, double inertia, int searchSpaceSize, int neighbourhoodSize, String label) {

        SwarmUtils.setMultiSwarmParameters(multiSwarm, neighbourhoodSize, inertia, searchSpaceSize);

        multiSwarm.setOrderFunction(new DefaultOrderFunction());
        multiSwarm.setShiftFunction(new DefaultShiftFunction());

//		multiSwarm.setAbsMaxVelocity(2.0);
        multiSwarm.init();

        List<Double> partial = new ArrayList<>(ITERATIONS);// / 100);

        evolveAndDisplay(multiSwarm, partial);

        //create output.json
        SimulationResult.SimulationResultBuilder builder = new SimulationResult.SimulationResultBuilder();
        SimulationResult result = builder.setFitnessFunction(fitnessFunction.getClass().getName())
                .setLabel(label)
                .setIterations(ITERATIONS)
                .setDimensions(DIMENSIONS)
                .setPartial(partial)
                .setBestFitness(multiSwarm.getBestFitness())
                .setTotalParticles(multiSwarm.getNumberOfParticles())
                .setSwarmInformations(SwarmUtils.getSwarmEntityList(particles))
                .setOrderFunction(multiSwarm.getOrderFunction().getClass().getSimpleName())
                .setShiftFunction(multiSwarm.getShiftFunction().getClass().getSimpleName())
                .build();
        System.out.println("Current experiment label: " + result.getLabel());
        return result;
    }
}
