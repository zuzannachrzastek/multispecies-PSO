package pl.edu.agh.mpso;

import static pl.edu.agh.mpso.Simulation.NUMBER_OF_DIMENSIONS;
import static pl.edu.agh.mpso.Simulation.NUMBER_OF_ITERATIONS;

import java.io.IOException;

import net.sourceforge.jswarm_pso.FitnessFunction;
import pl.edu.agh.mpso.fitness.Styblinski;
import pl.edu.agh.mpso.species.SpeciesType;
import pl.edu.agh.mpso.utils.RunUtils;

public class LocalRunSpeciesShare{

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, IOException, InterruptedException {
		FitnessFunction fitnessFunction = new Styblinski();
		int NUMBER_OF_SPECIES = SpeciesType.values().length;
		NUMBER_OF_DIMENSIONS = 100;
		NUMBER_OF_ITERATIONS = 3000;
		int executions = 30;

		for(int species = 1; species <= NUMBER_OF_SPECIES; species++){
			RunUtils.runParallel(fitnessFunction, species, executions, NUMBER_OF_SPECIES);
		}
	}
}
