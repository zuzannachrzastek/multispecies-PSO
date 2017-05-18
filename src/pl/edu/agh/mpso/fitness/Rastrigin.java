package pl.edu.agh.mpso.fitness;

import net.sourceforge.jswarm_pso.FitnessFunction;

public class Rastrigin extends FitnessFunction{
	private final int A = 10;
	
	public Rastrigin() {
		super(false);
	}
	
	public Rastrigin(boolean maximize){
		super(maximize);
	}
	
	@Override
	public double evaluate(double[] position) {
		double result = 0;
		
		for(double x : position){
			result += x * x - A * Math.cos(2 * Math.PI * x) + A;
		}
		
		return result;
	}

}
