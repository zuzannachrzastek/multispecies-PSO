package pl.edu.agh.mpso;

import pl.edu.agh.mpso.utils.RunUtils;

public class FindBestInertia {
	
	public static void main(String [] args) throws InterruptedException{
//		NUMBER_OF_DIMENSIONS = 100;
//		NUMBER_OF_ITERATIONS = 2000;
		int executions = 15;

		System.out.println("\nResults:");
		
		for(double i = 0.0; i <= 1.0; i+=0.05){
			RunUtils.runParallel(i, executions);
		}
	}
}
