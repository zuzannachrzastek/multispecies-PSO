package pl.edu.agh.mpso;

import static pl.edu.agh.mpso.Simulation.NUMBER_OF_DIMENSIONS;
import static pl.edu.agh.mpso.Simulation.NUMBER_OF_ITERATIONS;

import java.io.IOException;

import net.sourceforge.jswarm_pso.FitnessFunction;
import pl.edu.agh.mpso.fitness.Schwefel;
import pl.edu.agh.mpso.utils.RunUtils;

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
//		RunUtils runUtils = new RunUtils();
		FitnessFunction fitnessFunction = new Schwefel();
		NUMBER_OF_DIMENSIONS = 100;
		NUMBER_OF_ITERATIONS = 3000;
		int executions = 30;
		
//		runParallel(0, fitnessFunction, new int[]{25,0,0,0,0,0,0,0}, executions);
//		runParallel(0, fitnessFunction, new int[]{0,10,5,5,0,0,0,5}, executions);
//		runParallel(0, fitnessFunction, new int[]{1,4,0,2,0,0,0,18}, executions);
//		runParallel(0, fitnessFunction, new int[]{0,5,5,5,0,0,0,10}, executions);
		RunUtils.runParallel(0, fitnessFunction, new int[]{1,4,0,5,0,0,0,15}, executions);
//		runParallel(0, fitnessFunction, new int[]{5,7,6,7,0,0,0,0}, executions);
//		runParallel(0, fitnessFunction, new int[]{0,6,5,6,1,1,1,5}, executions);
	}
}
