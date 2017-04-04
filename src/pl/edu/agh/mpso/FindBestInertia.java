package pl.edu.agh.mpso;

import pl.edu.agh.mpso.utils.RunUtils;

import java.util.HashMap;
import java.util.Map;

public class FindBestInertia {
	
	public static void main(String [] args) throws InterruptedException{
		Map<Double, Double> results = new HashMap<Double, Double>();
//		NUMBER_OF_DIMENSIONS = 100;
//		NUMBER_OF_ITERATIONS = 2000;
		int executions = 15;
		
		for(double i = 0.0; i <= 1.0; i+=0.05){
			RunUtils.runParallel(i, executions, results);
		}

		System.out.println("\nResults:");

		for(double i = 0.0; i <= 1.0; i+=0.05){
			System.out.println("" + i + " : " + results.get(i));
		}
	}
}
