package pl.edu.agh.mpso;

import static pl.edu.agh.mpso.Simulation.NUMBER_OF_DIMENSIONS;
import static pl.edu.agh.mpso.Simulation.NUMBER_OF_ITERATIONS;
import static pl.edu.agh.mpso.Simulation.NUMBER_OF_PARTICLES;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.jswarm_pso.FitnessFunction;
import net.sourceforge.jswarm_pso.Neighborhood;
import net.sourceforge.jswarm_pso.Neighborhood1D;
import pl.edu.agh.mpso.dao.SimulationResultDAO;
import pl.edu.agh.mpso.fitness.Schwefel;
import pl.edu.agh.mpso.output.SimulationOutput;
import pl.edu.agh.mpso.output.SimulationOutputError;
import pl.edu.agh.mpso.output.SimulationOutputOk;
import pl.edu.agh.mpso.output.SimulationResult;
import pl.edu.agh.mpso.species.SpeciesType;
import pl.edu.agh.mpso.swarm.MultiSwarm;
import pl.edu.agh.mpso.swarm.SwarmInformation;
import pl.edu.agh.mpso.transition.order.DefaultOrderFunction;
import pl.edu.agh.mpso.transition.order.OrderFunction;
import pl.edu.agh.mpso.transition.shift.DefaultShiftFunction;
import pl.edu.agh.mpso.transition.shift.ShiftFunction;

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
