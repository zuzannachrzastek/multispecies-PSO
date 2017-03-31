package pl.edu.agh.mpso.output;

import java.util.List;

public class SimulationResult {
	private String fitnessFunction;
	private double bestFitness;
	private int dimensions;
	private int iterations;
	private List<Double> partial;
	
	private int totalParticles;

    //TODO wtf
	private int species1;
	private int species2;
	private int species3;
	private int species4;
	private int species5;
	private int species6;
	private int species7;
	private int species8;
	
	private double initialVelocity;
	private double finalVelocity;
	
	private String orderFunction;
	private String shiftFunction;

    private SimulationResult(String fitnessFunction, double bestFitness, int dimensions, int iterations, List<Double> partial, int totalParticles, int species1, int species2, int species3, int species4, int species5, int species6, int species7, int species8, double initialVelocity, double finalVelocity, String orderFunction, String shiftFunction) {
        this.fitnessFunction = fitnessFunction;
        this.bestFitness = bestFitness;
        this.dimensions = dimensions;
        this.iterations = iterations;
        this.partial = partial;
        this.totalParticles = totalParticles;
        this.species1 = species1;
        this.species2 = species2;
        this.species3 = species3;
        this.species4 = species4;
        this.species5 = species5;
        this.species6 = species6;
        this.species7 = species7;
        this.species8 = species8;
        this.initialVelocity = initialVelocity;
        this.finalVelocity = finalVelocity;
        this.orderFunction = orderFunction;
        this.shiftFunction = shiftFunction;
    }

    public static SimulationResultBuilder getBuilder(){
        return new SimulationResultBuilder();
    }

    public double getBestFitness() {
        return bestFitness;
    }

    public List<Double> getPartial() {
        return partial;
    }

    public int getTotalParticles() {
        return totalParticles;
    }

    public String getOrderFunction() {
        return orderFunction;
    }

    public String getShiftFunction() {
        return shiftFunction;
    }

    public int getSpecies1() {
        return species1;
    }

    public int getSpecies2() {
        return species2;
    }

    public int getSpecies3() {
        return species3;
    }

    public int getSpecies4() {
        return species4;
    }

    public int getSpecies5() {
        return species5;
    }

    public int getSpecies6() {
        return species6;
    }

    public int getSpecies7() {
        return species7;
    }

    public int getSpecies8() {
        return species8;
    }

    public static class SimulationResultBuilder {
        private String fitnessFunction;
        private double bestFitness;
        private int dimensions;
        private int iterations;
        private List<Double> partial;

        private int totalParticles;
        private int species1;
        private int species2;
        private int species3;
        private int species4;
        private int species5;
        private int species6;
        private int species7;
        private int species8;

        private double initialVelocity;
        private double finalVelocity;

        private String orderFunction;
        private String shiftFunction;

        public SimulationResultBuilder() {
        }

        public SimulationResultBuilder setFitnessFunction(String fitnessFunction) {
            this.fitnessFunction = fitnessFunction;
            return this;
        }

        public SimulationResultBuilder setBestFitness(double bestFitness) {
            this.bestFitness = bestFitness;
            return this;
        }

        public SimulationResultBuilder setDimensions(int dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        public SimulationResultBuilder setIterations(int iterations) {
            this.iterations = iterations;
            return this;
        }

        public SimulationResultBuilder setPartial(List<Double> partial) {
            this.partial = partial;
            return this;
        }

        public SimulationResultBuilder setTotalParticles(int totalParticles) {
            this.totalParticles = totalParticles;
            return this;
        }

        public SimulationResultBuilder setSpecies1(int species1) {
            this.species1 = species1;
            return this;
        }

        public SimulationResultBuilder setSpecies2(int species2) {
            this.species2 = species2;
            return this;
        }

        public SimulationResultBuilder setSpecies3(int species3) {
            this.species3 = species3;
            return this;
        }

        public SimulationResultBuilder setSpecies4(int species4) {
            this.species4 = species4;
            return this;
        }

        public SimulationResultBuilder setSpecies5(int species5) {
            this.species5 = species5;
            return this;
        }

        public SimulationResultBuilder setSpecies6(int species6) {
            this.species6 = species6;
            return this;
        }

        public SimulationResultBuilder setSpecies7(int species7) {
            this.species7 = species7;
            return this;
        }

        public SimulationResultBuilder setSpecies8(int species8) {
            this.species8 = species8;
            return this;
        }

        public SimulationResultBuilder setInitialVelocity(double initialVelocity) {
            this.initialVelocity = initialVelocity;
            return this;
        }

        public SimulationResultBuilder setFinalVelocity(double finalVelocity) {
            this.finalVelocity = finalVelocity;
            return this;
        }

        public SimulationResultBuilder setOrderFunction(String orderFunction) {
            this.orderFunction = orderFunction;
            return this;
        }

        public SimulationResultBuilder setShiftFunction(String shiftFunction) {
            this.shiftFunction = shiftFunction;
            return this;
        }

        public SimulationResult build(){
            return new SimulationResult(fitnessFunction, bestFitness,dimensions,iterations,partial,totalParticles,species1,species2,species3,species4,species5,species6,species7,species8,initialVelocity,finalVelocity,orderFunction,shiftFunction);
        }
    }
}
