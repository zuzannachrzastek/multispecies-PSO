package pl.edu.agh.mpso;

import static pl.edu.agh.mpso.Simulation.NUMBER_OF_DIMENSIONS;
import static pl.edu.agh.mpso.Simulation.NUMBER_OF_ITERATIONS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.jswarm_pso.FitnessFunction;
import net.sourceforge.jswarm_pso.Neighborhood;
import net.sourceforge.jswarm_pso.Neighborhood1D;
import pl.edu.agh.mpso.fitness.Rastrigin;
import pl.edu.agh.mpso.species.SpeciesType;
import pl.edu.agh.mpso.swarm.MultiSwarm;
import pl.edu.agh.mpso.swarm.SwarmInformation;

public class FindBestMaxVelocity {
	private static List<Thread> threads = new ArrayList<Thread>();
	private static Map<Double, Double> results = new HashMap<Double, Double>();
	private static int [] particles = new int[]{40,0,0,0,0,0,0,0};
	private static FitnessFunction fitnessFunction = new Rastrigin();
	
	public static void main(String [] args) throws InterruptedException{
		NUMBER_OF_DIMENSIONS = 100;
		NUMBER_OF_ITERATIONS = 2000;
		int executions = 15;
		
		for(double v = 1; v <= 10; v+=1){
			runParallel(v, executions);
		}
		
		for(Thread thread : threads){
			thread.join();
		}
		
		System.out.println("\nResults:");
		
		for(double v = 1; v <= 10; v+=1){
			System.out.println("" + v + " : " + results.get(v));
		}
	}
	
	private static void runParallel(final double maxV, final int executions){
		Thread thread = new Thread(new Runnable() {
			List<Double> fitnessList = new ArrayList<Double>(executions);
			 
			public void run() {
				for(int i = 0; i < executions; i++){
					System.out.println("" + maxV + ": " + i + " of " + executions);
					double fitness = runSimulation(maxV);
					fitnessList.add(fitness);
				}
				
				double sum = 0.0;
				for(double f : fitnessList){
					sum += f;
				}
				double avg = sum / (double) executions;
				
				results.put(maxV, avg);
			}
		});
		
		threads.add(thread);
		thread.start();
	}
	
	private static double runSimulation(double maxV) {
		int cnt = 0;
		List<SwarmInformation> swarmInformations = new ArrayList<SwarmInformation>();
		
		for(int i = 0; i < particles.length; i++){
			if(particles[i] != 0){
				cnt += particles[i];
				
				SpeciesType type = SpeciesType.values()[i];
				SwarmInformation swarmInformation = new SwarmInformation(particles[i], type);
				
				swarmInformations.add(swarmInformation);
			}
		}
		
		MultiSwarm multiSwarm = new MultiSwarm(swarmInformations, fitnessFunction);
		
		Neighborhood neighbourhood = new Neighborhood1D(cnt / 5, true);
		multiSwarm.setNeighborhood(neighbourhood);
		
		
		multiSwarm.setNeighborhoodIncrement(0.9);
		multiSwarm.setInertia(0.95);
		multiSwarm.setParticleIncrement(0.9);
		multiSwarm.setGlobalIncrement(0.9);
		
		multiSwarm.setMaxPosition(5.12);
		multiSwarm.setMinPosition(-5.12);
		
		multiSwarm.setAbsMaxVelocity(maxV);
		multiSwarm.init();
		
		for(int i = 0; i < NUMBER_OF_ITERATIONS; ++i) {
			// Evolve swarm
			multiSwarm.evolve();
		}
		
		return multiSwarm.getBestFitness();
	}
}
