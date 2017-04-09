package pl.edu.agh.mpso;

import static pl.edu.agh.mpso.Simulation.NUMBER_OF_DIMENSIONS;
import static pl.edu.agh.mpso.Simulation.NUMBER_OF_ITERATIONS;

import java.io.IOException;

import net.sourceforge.jswarm_pso.FitnessFunction;
import pl.edu.agh.mpso.fitness.Schwefel;
import pl.edu.agh.mpso.species.SpeciesType;
import pl.edu.agh.mpso.swarm.SwarmInformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static pl.edu.agh.mpso.Simulation.NUMBER_OF_DIMENSIONS;
import static pl.edu.agh.mpso.Simulation.NUMBER_OF_ITERATIONS;
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
		FitnessFunction fitnessFunction = new Schwefel();
		NUMBER_OF_DIMENSIONS = 100;
		NUMBER_OF_ITERATIONS = 3000;
		int executions = 30;
		
        List<SwarmInformation> species = new ArrayList<>();

//        species.add(new SwarmInformation(3, SpeciesType.ALL));
//        species.add(new SwarmInformation(3, SpeciesType.GLOBAL_AND_LOCAL));
//        species.add(new SwarmInformation(3, SpeciesType.GLOBAL_AND_NEIGHBOUR));
//        species.add(new SwarmInformation(3, SpeciesType.LOCAL_AND_NEIGHBOUR));
//        species.add(new SwarmInformation(0, SpeciesType.GLOBAL_ONLY));
//        species.add(new SwarmInformation(4, SpeciesType.LOCAL_ONLY));
//        species.add(new SwarmInformation(4, SpeciesType.NEIGHBOUR_ONLY));
//
//        for (int i = 0; i < 4; i++) {
//            species.add(new SwarmInformation(SpeciesType.RANDOM));
//        }

//        species.add(new SwarmInformation(3, SpeciesType.ALL));
//        species.add(new SwarmInformation(3, SpeciesType.GLOBAL_AND_LOCAL));
//        species.add(new SwarmInformation(3, SpeciesType.GLOBAL_AND_NEIGHBOUR));
//        species.add(new SwarmInformation(3, SpeciesType.LOCAL_AND_NEIGHBOUR));
//        species.add(new SwarmInformation(4, SpeciesType.GLOBAL_ONLY));
//        species.add(new SwarmInformation(3, SpeciesType.LOCAL_ONLY));
//        species.add(new SwarmInformation(3, SpeciesType.NEIGHBOUR_ONLY));
//
//        for (int i = 0; i < 3; i++) {
//            species.add(new SwarmInformation(SpeciesType.RANDOM));
//        }

//        species.add(new SwarmInformation(2, SpeciesType.ALL));
//        species.add(new SwarmInformation(2, SpeciesType.GLOBAL_AND_LOCAL));
//        species.add(new SwarmInformation(2, SpeciesType.GLOBAL_AND_NEIGHBOUR));
//        species.add(new SwarmInformation(2, SpeciesType.LOCAL_AND_NEIGHBOUR));
//        species.add(new SwarmInformation(11, SpeciesType.GLOBAL_ONLY));
//        species.add(new SwarmInformation(2, SpeciesType.LOCAL_ONLY));
//        species.add(new SwarmInformation(2, SpeciesType.NEIGHBOUR_ONLY));
//
//        for (int i = 0; i < 2; i++) {
//            species.add(new SwarmInformation(SpeciesType.RANDOM));
//        }

//        species.add(new SwarmInformation(2, SpeciesType.ALL));
//        species.add(new SwarmInformation(2, SpeciesType.GLOBAL_AND_LOCAL));
//        species.add(new SwarmInformation(2, SpeciesType.GLOBAL_AND_NEIGHBOUR));
//        species.add(new SwarmInformation(1, SpeciesType.LOCAL_AND_NEIGHBOUR));
//        species.add(new SwarmInformation(15, SpeciesType.GLOBAL_ONLY));
//        species.add(new SwarmInformation(1, SpeciesType.LOCAL_ONLY));
//        species.add(new SwarmInformation(1, SpeciesType.NEIGHBOUR_ONLY));
//
//        for (int i = 0; i < 1; i++) {
//            species.add(new SwarmInformation(SpeciesType.RANDOM));
//        }

        species.add(new SwarmInformation(25, SpeciesType.GLOBAL_ONLY));


		RunUtils.runParallel(0, fitnessFunction, species, executions);
	}
}
