package pl.edu.agh.mpso;

import static pl.edu.agh.mpso.Simulation.NUMBER_OF_DIMENSIONS;
import static pl.edu.agh.mpso.Simulation.NUMBER_OF_ITERATIONS;
import static pl.edu.agh.mpso.Simulation.NUMBER_OF_PARTICLES;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.jswarm_pso.FitnessFunction;
import net.sourceforge.jswarm_pso.Neighborhood;
import net.sourceforge.jswarm_pso.Neighborhood1D;
import pl.edu.agh.mpso.dao.SwarmInfoEntity;
import pl.edu.agh.mpso.fitness.Rastrigin;
import pl.edu.agh.mpso.output.SimulationResult;
import pl.edu.agh.mpso.species.SpeciesType;
import pl.edu.agh.mpso.swarm.MultiSwarm;
import pl.edu.agh.mpso.swarm.SwarmInformation;

import pl.edu.agh.mpso.utils.RunUtils;

/**
 *
 * @author iwanb
 * command line args:
 * - function name - must be the same as class from pl.edu.agh.miss.fitness
 * - number of dimensions
 * - number of iterations
 * - proportional share of 1st species
 * - proportional share of 2nd species
 * - proportional share of 3rd species
 * - proportional share of 4th species
 * - proportional share of 5th species
 * - proportional share of 6th species
 * - proportional share of 7th species
 * - proportional share of 8th species
 */
public class Scalarm {
	private static String className;

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
		if(args.length >= 2){
			NUMBER_OF_DIMENSIONS = Integer.valueOf(args[1]);
			if(NUMBER_OF_DIMENSIONS <= 0) NUMBER_OF_DIMENSIONS = 10;
		}
		//get number of iterations
		if(args.length >= 3){
			NUMBER_OF_ITERATIONS = Integer.valueOf(args[2]);
			if(NUMBER_OF_ITERATIONS <= 0) NUMBER_OF_ITERATIONS = 1000;
		}

		//create array of species share
		int numberOfSpecies = SpeciesType.values().length;
		int [] speciesArray = new int[numberOfSpecies];
		int argsSum = 0;

		for(int i = 3; i < Math.min(numberOfSpecies + 3, args.length); i++){
			argsSum += Integer.valueOf(args[i]);
		}

		if(argsSum == 0){
			speciesArray[0] = NUMBER_OF_PARTICLES;
		} else {
			for(int i = 0; i < Math.min(numberOfSpecies, args.length - 3); i++){
				float speciesShare = (float) Integer.valueOf(args[i + 3]) / (float) argsSum;
				speciesArray[i] = (int) (speciesShare * NUMBER_OF_PARTICLES);
			}
		}

		//TODO
//		SimulationResult result = RunUtils.run(speciesArray, fitnessFunction);
//		RunUtils.generateOutputFile(speciesArray, fitnessFunction, result);
	}

	private static SimulationResult run(List<SwarmInformation> swarmInformations, FitnessFunction fitnessFunction) {
		MultiSwarm multiSwarm = new MultiSwarm(swarmInformations, fitnessFunction);
		
		Neighborhood neighbourhood = new Neighborhood1D(multiSwarm.getNumberOfParticles() / 5, true);
		multiSwarm.setNeighborhood(neighbourhood);
		
		
		multiSwarm.setNeighborhoodIncrement(0.9);
		multiSwarm.setInertia(0.95);
		multiSwarm.setParticleIncrement(0.9);
		multiSwarm.setGlobalIncrement(0.9);
		
		multiSwarm.setMaxPosition(100);
		multiSwarm.setMinPosition(-100);
		
		List<Double> partial = new ArrayList<Double>(NUMBER_OF_ITERATIONS / 100);

		RunUtils.evolveAndDisplay(multiSwarm, partial);
		
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
				.setSwarmInformations(swarmInformations.stream()
						.map(a -> new SwarmInfoEntity(a.getNumberOfParticles(), a.getType().getType()))
						.collect(Collectors.toList()))
                .build();
	}
}
