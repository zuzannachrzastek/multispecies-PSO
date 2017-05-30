//package pl.edu.agh.mpso;
//
//import static pl.edu.agh.mpso.Simulation.NUMBER_OF_DIMENSIONS;
//import static pl.edu.agh.mpso.Simulation.NUMBER_OF_ITERATIONS;
//import static pl.edu.agh.mpso.Simulation.NUMBER_OF_PARTICLES;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import net.sourceforge.jswarm_pso.FitnessFunction;
//import net.sourceforge.jswarm_pso.Neighborhood;
//import net.sourceforge.jswarm_pso.Neighborhood1D;
//import pl.edu.agh.mpso.dao.SwarmInfoEntity;
//import pl.edu.agh.mpso.fitness.Rastrigin;
//import pl.edu.agh.mpso.output.SimulationResult;
//import pl.edu.agh.mpso.species.SpeciesType;
//import pl.edu.agh.mpso.swarm.MultiSwarm;
//import pl.edu.agh.mpso.swarm.SwarmInformation;
//import pl.edu.agh.mpso.utils.MiscellaneuosUtils;
//import pl.edu.agh.mpso.utils.RunUtils;
//import pl.edu.agh.mpso.utils.SwarmUtils;
//import pl.edu.agh.mpso.velocity.LinearVelocityFunction;
//
///**
// *
// * @author iwanb
// * command line args:
// * - function name - must be the same as class from pl.edu.agh.miss.fitness
// * - number of dimensions
// * - number of iterations
// * - species id
// * - number of particles
// * - initial velocity
// * - final velocity
// */
//public class BestVelocity {
//	private static String className;
//	private static int speciesId = 1;
//	private static int speciesCnt = NUMBER_OF_PARTICLES;
//	private static int numberOfSpecies = SpeciesType.values().length;
//	private static double initialVelocity = 0.2;
//	private static double finalVelocity = 2.5;
//
//	private static int MAX_POS = 20;
//	private static int VELOCITY_UPDATES = 100;
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
//		//get species id
//		if(args.length >= 4){
//			speciesId = Integer.valueOf(args[3]);
//			if(speciesId <= 0) speciesId = 1;
//			if(speciesId > numberOfSpecies) speciesId = 1;
//		}
//
//		//get species cnt
//		if(args.length >= 5){
//			speciesCnt = Integer.valueOf(args[4]);
//			if(speciesCnt < 0) speciesCnt = NUMBER_OF_PARTICLES;
//			if(speciesCnt > NUMBER_OF_PARTICLES) speciesCnt = NUMBER_OF_PARTICLES;
//		}
//
//		//get initial velocity
//		if(args.length >= 6){
//			initialVelocity = Double.valueOf(args[5]);
//		}
//
//		//get final velocity
//		if(args.length >= 7){
//			finalVelocity = Double.valueOf(args[6]);
//		}
//
//		//create array of species share
//		int [] speciesArray = new int[numberOfSpecies];
//
//		for(int i = 0; i < numberOfSpecies; i++){
//			if(i == speciesId - 1){
//				speciesArray[i] = speciesCnt;
//			} else {
//				speciesArray[i] = 0;
//			}
//		}
//
//		List<SwarmInformation> swarmInformations = SwarmUtils.createSwarmInfoList(speciesArray);
//		int cnt = 0;
//
//		MultiSwarm multiSwarm = new MultiSwarm(swarmInformations, fitnessFunction);
//		SimulationResult result = RunUtils.runWithCounter(multiSwarm, fitnessFunction, initialVelocity, finalVelocity, VELOCITY_UPDATES, swarmInformations, 0.95, 20, cnt/5);
//		MiscellaneuosUtils.generateOutputFile(result);
//	}
//}
