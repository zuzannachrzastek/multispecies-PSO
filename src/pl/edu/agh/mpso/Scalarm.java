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
//
//import net.sourceforge.jswarm_pso.FitnessFunction;
//import net.sourceforge.jswarm_pso.Neighborhood;
//import net.sourceforge.jswarm_pso.Neighborhood1D;
//import pl.edu.agh.mpso.dao.SimulationResultDAO;
//import pl.edu.agh.mpso.fitness.Rastrigin;
//import pl.edu.agh.mpso.output.SimulationOutput;
//import pl.edu.agh.mpso.output.SimulationOutputError;
//import pl.edu.agh.mpso.output.SimulationOutputOk;
//import pl.edu.agh.mpso.output.SimulationResult;
//import pl.edu.agh.mpso.species.SpeciesType;
//import pl.edu.agh.mpso.swarm.MultiSwarm;
//import pl.edu.agh.mpso.swarm.SwarmInformation;
//
//import com.google.gson.Gson;
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
//
//		SimulationOutput output = null;
//		try{
//			SimulationResult result = run(speciesArray, fitnessFunction);
//			output = new SimulationOutputOk();
//			((SimulationOutputOk) output).results = result;
//			SimulationResultDAO.getInstance().writeResult(result);
//			SimulationResultDAO.getInstance().close();
//		} catch (Throwable e){
//			output = new SimulationOutputError();
//			((SimulationOutputError)output).reason = e.toString() + ": " + e.getMessage();
//		} finally {
//			Writer writer = new FileWriter("output.json");
//			Gson gson = new Gson();
//			gson.toJson(output, writer);
//			writer.close();
//		}
//	}
//
//	private static SimulationResult run(int [] particles, FitnessFunction fitnessFunction) {
//		int cnt = 0;
//		List<SwarmInformation> swarmInformations = new ArrayList<SwarmInformation>();
//
//		for(int i = 0; i < particles.length; i++){
//			if(particles[i] != 0){
//				cnt += particles[i];
//
//				SpeciesType type = SpeciesType.values()[i];
//				SwarmInformation swarmInformation = new SwarmInformation(particles[i], type);
//
//				swarmInformations.add(swarmInformation);
//			}
//		}
//
//		SwarmInformation [] swarmInformationsArray = new SwarmInformation [swarmInformations.size()];
//		MultiSwarm multiSwarm = new MultiSwarm(swarmInformations.toArray(swarmInformationsArray), fitnessFunction);
//
//		Neighborhood neighbourhood = new Neighborhood1D(cnt / 5, true);
//		multiSwarm.setNeighborhood(neighbourhood);
//
//
//		multiSwarm.setNeighborhoodIncrement(0.9);
//		multiSwarm.setInertia(0.95);
//		multiSwarm.setParticleIncrement(0.9);
//		multiSwarm.setGlobalIncrement(0.9);
//
//		multiSwarm.setMaxPosition(100);
//		multiSwarm.setMinPosition(-100);
//
//		List<Double> partial = new ArrayList<Double>(NUMBER_OF_ITERATIONS / 100);
//
//		for(int i = 0; i < NUMBER_OF_ITERATIONS; ++i) {
//			// Evolve swarm
//			multiSwarm.evolve();
//
//			//display partial results
//			if(NUMBER_OF_ITERATIONS > 100 && (i % (NUMBER_OF_ITERATIONS / 100) == 0)){
//				partial.add(multiSwarm.getBestFitness());
//				System.out.println(multiSwarm.getBestFitness());
//			}
//		}
//
//		//print final results
//		System.out.println(multiSwarm.getBestFitness());
//
//		//create output.json
//		SimulationResult.SimulationResultBuilder builder = new SimulationResult.SimulationResultBuilder();
//		return builder.setFitnessFunction(fitnessFunction.getClass().getName())
//				.setIterations(NUMBER_OF_ITERATIONS)
//				.setDimensions(NUMBER_OF_DIMENSIONS)
//				.setPartial(partial)
//				.setBestFitness(multiSwarm.getBestFitness())
//				.setTotalParticles(NUMBER_OF_PARTICLES)
//				.setSpecies1(swarmInformations.get(0).getNumberOfParticles())
//				.setSpecies2(swarmInformations.get(1).getNumberOfParticles())
//				.setSpecies3(swarmInformations.get(2).getNumberOfParticles())
//				.setSpecies4(swarmInformations.get(3).getNumberOfParticles())
//				.setSpecies5(swarmInformations.get(4).getNumberOfParticles())
//				.setSpecies6(swarmInformations.get(5).getNumberOfParticles())
//				.setSpecies7(swarmInformations.get(6).getNumberOfParticles())
//				.setSpecies8(swarmInformations.get(7).getNumberOfParticles())
//				.setOrderFunction(multiSwarm.getOrderFunction().getClass().getSimpleName())
//				.setShiftFunction(multiSwarm.getShiftFunction().getClass().getSimpleName())
//				.build();
//	}
//}
