package pl.edu.agh.mpso;

import net.sourceforge.jswarm_pso.FitnessFunction;
import pl.edu.agh.mpso.fitness.Schwefel;
import pl.edu.agh.mpso.species.SpeciesType;
import pl.edu.agh.mpso.swarm.SwarmInformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static pl.edu.agh.mpso.Simulation.NUMBER_OF_DIMENSIONS;
import static pl.edu.agh.mpso.Simulation.NUMBER_OF_ITERATIONS;

/**
 * 
 * @author iwanb
 * command line args:
 * - function name - must be the same as class from pl.edu.agh.miss.fitness
 * - number of dimensions
 * - number of iterations
 * - species id
 * - proportional share of given species
 */
public class LocalRun {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, IOException, InterruptedException {
		FitnessFunction fitnessFunction = new Schwefel();
		NUMBER_OF_DIMENSIONS = 100;
		NUMBER_OF_ITERATIONS = 3000;
		int executions = 30;
		
        List<SwarmInformation> species = new ArrayList<>();
        species.add(new SwarmInformation(1, SpeciesType.ALL));
        species.add(new SwarmInformation(4, SpeciesType.GLOBAL_AND_LOCAL));
        species.add(new SwarmInformation(5, SpeciesType.LOCAL_AND_NEIGHBOUR));
        for (int i = 0; i < 15; i++) {
            species.add(new SwarmInformation(new SpeciesType(7)));
        }

//		runParallel(0, fitnessFunction, new int[]{25,0,0,0,0,0,0,0}, executions);
//		runParallel(0, fitnessFunction, new int[]{0,10,5,5,0,0,0,5}, executions);
//		runParallel(0, fitnessFunction, new int[]{1,4,0,2,0,0,0,18}, executions);
//		runParallel(0, fitnessFunction, new int[]{0,5,5,5,0,0,0,10}, executions);
		RunUtils.runParallel(0, fitnessFunction, species, executions);
//		runParallel(0, fitnessFunction, new int[]{5,7,6,7,0,0,0,0}, executions);
//		runParallel(0, fitnessFunction, new int[]{0,6,5,6,1,1,1,5}, executions);
	}



}
