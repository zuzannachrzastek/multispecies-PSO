package pl.edu.agh.mpso;

import pl.edu.agh.mpso.utils.RunUtils;

import java.util.HashMap;
import java.util.Map;

public class FindBestMaxVelocity {
	
	public static void main(String [] args) throws InterruptedException{
		Map<Double, Double> results = new HashMap<Double, Double>();
		int [] particles = new int[]{40,0,0,0,0,0,0,0};
//		NUMBER_OF_DIMENSIONS = 100;
//		NUMBER_OF_ITERATIONS = 2000;
		int executions = 15;
		
		for(double v = 1; v <= 10; v+=1){
			RunUtils.runParallel(0.95, v, executions, results, particles, "velocity");
		}
		
		System.out.println("\nResults:");
		
		for(double v = 1; v <= 10; v+=1){
			System.out.println("" + v + " : " + results.get(v));
		}
	}
}
