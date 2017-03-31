package pl.edu.agh.mpso.utils;

import com.google.gson.Gson;
import net.sourceforge.jswarm_pso.FitnessFunction;
import net.sourceforge.jswarm_pso.Neighborhood;
import net.sourceforge.jswarm_pso.Neighborhood1D;
import pl.edu.agh.mpso.dao.SimulationResultDAO;
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
import java.util.List;

import static pl.edu.agh.mpso.Simulation.NUMBER_OF_DIMENSIONS;
import static pl.edu.agh.mpso.Simulation.NUMBER_OF_ITERATIONS;
import static pl.edu.agh.mpso.Simulation.NUMBER_OF_PARTICLES;

/**
 * Created by Zuzanna on 3/30/2017.
 */
public abstract class RunUtils {

    public static void runParallel(final int id, final FitnessFunction fitnessFunction, final int[] speciesArray, final int executions) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {

            public void run() {
                for(int i = 0; i < executions; i++){
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

    static void runParallel(final FitnessFunction fitnessFunction, final int speciesId, final int executions, final int NUMBER_OF_SPECIES) throws InterruptedException {
        Thread thread = new Thread(new Runnable() {

            private final int [] speciesShares = new int [] {0, 4, 11, 18, 25};

            public void run() {
                for(int share : speciesShares){
                    System.out.println("Species " + speciesId + " share " + share);

                    int [] speciesArray = new int[NUMBER_OF_SPECIES];

                    for(int i = 0; i < NUMBER_OF_SPECIES; i++){
                        if(i == speciesId - 1){
                            speciesArray[i] = share;
                        } else {
                            speciesArray[i] = (NUMBER_OF_PARTICLES - share) / (NUMBER_OF_SPECIES - 1);
                        }
                    }

                    for(int i = 0; i < executions; i++){
                        try {
                            simulate(fitnessFunction, speciesArray, speciesId, executions, i);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        thread.start();
        thread.join();
    }

    static void simulate(FitnessFunction fitnessFunction,
                                 int[] speciesArray, int id, int executions, int i) throws IOException {
        SimulationOutput output = null;
        try{
            long tic = System.currentTimeMillis();
            SimulationResult result = run(speciesArray, fitnessFunction);
            long toc = System.currentTimeMillis();
            long diff = toc - tic;
            long seconds = diff / 1000L;
            long minutes = seconds / 60L;
            long hours = minutes / 60L;
            System.out.println("" + id + ": Execution " + (i+1) + " of " + executions);
            System.out.println("\tDone in: " + hours + " hours " + (minutes%60) + " minutes " + (seconds % 60) + " seconds");

            output = new SimulationOutputOk();
            ((SimulationOutputOk) output).results = result;
            SimulationResultDAO.getInstance().writeResult(result);
            SimulationResultDAO.getInstance().close();
            System.out.println(result.bestFitness);
        } catch (Throwable e){
            e.printStackTrace();
            output = new SimulationOutputError();
            ((SimulationOutputError)output).reason = e.toString() + ": " + e.getMessage();
        }
    }

    public static SimulationResult runWithCounter(int [] particles, FitnessFunction fitnessFunction, double initialVelocity, double finalVelocity, int VELOCITY_UPDATES){
        int cnt = 0;
        List<SwarmInformation> swarmInformations = SwarmUtils.createSwarmInfoList(particles);

        SwarmInformation [] swarmInformationsArray = new SwarmInformation [swarmInformations.size()];
        MultiSwarm multiSwarm = new MultiSwarm(swarmInformations.toArray(swarmInformationsArray), fitnessFunction);

        SwarmUtils.setMultiSwarmParameters(multiSwarm,cnt/5,20);

        multiSwarm.setVelocityFunction(new LinearVelocityFunction(initialVelocity, finalVelocity).setUpdatesCnt(VELOCITY_UPDATES).setUpdatesInterval(NUMBER_OF_ITERATIONS / VELOCITY_UPDATES));
        multiSwarm.init();

        List<Double> partial = new ArrayList<Double>(NUMBER_OF_ITERATIONS / 100);

        for(int i = 0; i < NUMBER_OF_ITERATIONS; ++i) {
            // Evolve swarm
            multiSwarm.evolve();

            //display partial results
            if(NUMBER_OF_ITERATIONS > 100 && (i % (NUMBER_OF_ITERATIONS / 100) == 0)){
                partial.add(multiSwarm.getBestFitness());
                System.out.println(multiSwarm.getBestFitness());
            }
        }

        //print final results
        System.out.println(multiSwarm.getBestFitness());

        //create output.json
        SimulationResult output = new SimulationResult();
        output.fitnessFunction = fitnessFunction.getClass().getName();
        output.iterations = NUMBER_OF_ITERATIONS;
        output.dimensions = NUMBER_OF_DIMENSIONS;
        output.partial = partial;
        output.bestFitness = multiSwarm.getBestFitness();
        output.totalParticles = NUMBER_OF_PARTICLES;

        output.species1 = particles[0];
        output.species2 = particles[1];
        output.species3 = particles[2];
        output.species4 = particles[3];
        output.species5 = particles[4];
        output.species6 = particles[5];
        output.species7 = particles[6];
        output.species8 = particles[7];

        output.initialVelocity = initialVelocity;
        output.finalVelocity = finalVelocity;

        return output;
    }


    public static SimulationResult run(int [] particles, FitnessFunction fitnessFunction) {
        List<SwarmInformation> swarmInformations = SwarmUtils.createSwarmInfoList(particles);

        SwarmInformation [] swarmInformationsArray = new SwarmInformation [swarmInformations.size()];
        MultiSwarm multiSwarm = new MultiSwarm(swarmInformations.toArray(swarmInformationsArray), fitnessFunction);

        SwarmUtils.setMultiSwarmParameters(multiSwarm,7,20);

        multiSwarm.setOrderFunction(new DefaultOrderFunction());
        multiSwarm.setShiftFunction(new DefaultShiftFunction());

//		multiSwarm.setAbsMaxVelocity(2.0);
        multiSwarm.init();

        List<Double> partial = new ArrayList<Double>(NUMBER_OF_ITERATIONS / 100);

        for(int i = 0; i < NUMBER_OF_ITERATIONS; ++i) {
            // Evolve swarm
            multiSwarm.evolve();

            //display partial results
            if(NUMBER_OF_ITERATIONS > 100 && (i % (NUMBER_OF_ITERATIONS / 100) == 0)){
                partial.add(multiSwarm.getBestFitness());
            }
        }

        //create output.json
        SimulationResult output = new SimulationResult();
        output.fitnessFunction = fitnessFunction.getClass().getName();
        output.iterations = NUMBER_OF_ITERATIONS;
        output.dimensions = NUMBER_OF_DIMENSIONS;
        output.partial = partial;
        output.bestFitness = multiSwarm.getBestFitness();
        output.totalParticles = NUMBER_OF_PARTICLES;

        output.species1 = particles[0];
        output.species2 = particles[1];
        output.species3 = particles[2];
        output.species4 = particles[3];
        output.species5 = particles[4];
        output.species6 = particles[5];
        output.species7 = particles[6];
        output.species8 = particles[7];

        output.orderFunction = multiSwarm.getOrderFunction().getClass().getSimpleName();
        output.shiftFunction = multiSwarm.getShiftFunction().getClass().getSimpleName();

        return output;
    }

    public static void generateOutputFile(int [] speciesArray, FitnessFunction fitnessFunction, SimulationResult result) throws IOException {
        SimulationOutput output = null;
        try{
            output = new SimulationOutputOk();
            ((SimulationOutputOk) output).results = result;
            SimulationResultDAO.getInstance().writeResult(result);
            SimulationResultDAO.getInstance().close();
        } catch (Throwable e){
            output = new SimulationOutputError();
            ((SimulationOutputError)output).reason = e.toString() + ": " + e.getMessage();
        } finally {
            Writer writer = new FileWriter("output.json");
            Gson gson = new Gson();
            gson.toJson(output, writer);
            writer.close();
        }
    }
}
