//package pl.edu.agh.mpso;
//
//import static pl.edu.agh.mpso.Simulation.NUMBER_OF_DIMENSIONS;
//import static pl.edu.agh.mpso.Simulation.NUMBER_OF_ITERATIONS;
//import static pl.edu.agh.mpso.Simulation.NUMBER_OF_PARTICLES;
//
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.Writer;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import com.google.gson.Gson;
//import net.sourceforge.jswarm_pso.FitnessFunction;
//import net.sourceforge.jswarm_pso.Neighborhood;
//import net.sourceforge.jswarm_pso.Neighborhood1D;
//import pl.edu.agh.mpso.dao.SimulationResultDAO;
//import pl.edu.agh.mpso.dao.SwarmInfoEntity;
//import pl.edu.agh.mpso.fitness.Rastrigin;
//import pl.edu.agh.mpso.output.SimulationOutput;
//import pl.edu.agh.mpso.output.SimulationOutputError;
//import pl.edu.agh.mpso.output.SimulationOutputOk;
//import pl.edu.agh.mpso.output.SimulationResult;
//import pl.edu.agh.mpso.species.SpeciesType;
//import pl.edu.agh.mpso.swarm.MultiSwarm;
//import pl.edu.agh.mpso.swarm.SwarmInformation;
//
//import pl.edu.agh.mpso.utils.RunUtils;
//import pl.edu.agh.mpso.utils.SwarmUtils;
//
///**
// *
// * @author iwanb
// * command line args:
// * - function name - must be the same as class from pl.edu.agh.miss.fitness
// * - number of dimensions
// * - number of iterations
// * - proportional share of 1st species
// * - proportional share of 2nd species
// * - proportional share of 3rd species
// * - proportional share of 4th species
// * - proportional share of 5th species
// * - proportional share of 6th species
// * - proportional share of 7th species
// * - proportional share of 8th species
// */
//public class Scalarm {
//	private static String className;
//
//	@SuppressWarnings("unchecked")
//	public static void main(String[] args) throws InstantiationException, IllegalAccessException, IOException {
//		//get optimization problem
//		FitnessFunction fitnessFunction = null;
//		Class<? extends FitnessFunction> fitnessFunctionClass = Rastrigin.class;
//		className = args.length >= 1 ? args[0] : "Rastrigin";
//		final String packageName = "pl.edu.agh.miss.fitness";
//		try {
//			fitnessFunctionClass = (Class<FitnessFunction>) Class.forName(packageName + "." + className);
//		} catch (ClassNotFoundException e) {
//			System.out.println(className + " " + e.getMessage() + " using Rastrigin function");
//		} finally {
//			fitnessFunction = fitnessFunctionClass.newInstance();
//		}
//
//		//get number of dimensions
//		if(args.length >= 2){
//			NUMBER_OF_DIMENSIONS = Integer.valueOf(args[1]);
//			if(NUMBER_OF_DIMENSIONS <= 0) NUMBER_OF_DIMENSIONS = 10;
//		}
//		//get number of iterations
//		if(args.length >= 3){
//			NUMBER_OF_ITERATIONS = Integer.valueOf(args[2]);
//			if(NUMBER_OF_ITERATIONS <= 0) NUMBER_OF_ITERATIONS = 1000;
//		}
//
//		//create array of species share
//		int numberOfSpecies = SpeciesType.values().length;
//		int [] speciesArray = new int[numberOfSpecies];
//		int argsSum = 0;
//
//		for(int i = 3; i < Math.min(numberOfSpecies + 3, args.length); i++){
//			argsSum += Integer.valueOf(args[i]);
//		}
//
//		if(argsSum == 0){
//			speciesArray[0] = NUMBER_OF_PARTICLES;
//		} else {
//			for(int i = 0; i < Math.min(numberOfSpecies, args.length - 3); i++){
//				float speciesShare = (float) Integer.valueOf(args[i + 3]) / (float) argsSum;
//				speciesArray[i] = (int) (speciesShare * NUMBER_OF_PARTICLES);
//			}
//		}
//		//TODO transform speciesArray to SwarmInformation
////		MultiSwarm multiSwarm = new MultiSwarm(speciesArray, fitnessFunction);
////		SimulationResult result = RunUtils.run(multiSwarm, speciesArray, fitnessFunction, 0.95, 100, multiSwarm.getNumberOfParticles()/5);
//
////		RunUtils.generateOutputFile(result);
//	}
//}
