package pl.edu.agh.mpso.output;

import pl.edu.agh.mpso.dao.SwarmInfoEntity;
import pl.edu.agh.mpso.swarm.SwarmInformation;

import java.io.Serializable;
import java.util.List;

public class SimulationResult {
    private String label;
    private String fitnessFunction;
    private double bestFitness;
    private int dimensions;
    private int iterations;
    private List<Double> partial;

    private int totalParticles;

    private List<SwarmInfoEntity> swarmInformations;

    private double initialVelocity;
    private double finalVelocity;

    private String orderFunction;
    private String shiftFunction;

    public SimulationResult() {
    }

    public SimulationResult(String label, String fitnessFunction, double bestFitness, int dimensions, int iterations, List<Double> partial, int totalParticles, List<SwarmInfoEntity> swarmInformations, double initialVelocity, double finalVelocity, String orderFunction, String shiftFunction) {
        this.label = label;
        this.fitnessFunction = fitnessFunction;
        this.bestFitness = bestFitness;
        this.dimensions = dimensions;
        this.iterations = iterations;
        this.partial = partial;
        this.totalParticles = totalParticles;
        this.swarmInformations = swarmInformations;
        this.initialVelocity = initialVelocity;
        this.finalVelocity = finalVelocity;
        this.orderFunction = orderFunction;
        this.shiftFunction = shiftFunction;
    }

    public static SimulationResultBuilder getBuilder() {
        return new SimulationResultBuilder();
    }

    public String getFitnessFunction() {
        return fitnessFunction;
    }

    public void setFitnessFunction(String fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
    }

    public double getBestFitness() {
        return bestFitness;
    }

    public void setBestFitness(double bestFitness) {
        this.bestFitness = bestFitness;
    }

    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public List<Double> getPartial() {
        return partial;
    }

    public void setPartial(List<Double> partial) {
        this.partial = partial;
    }

    public int getTotalParticles() {
        return totalParticles;
    }

    public void setTotalParticles(int totalParticles) {
        this.totalParticles = totalParticles;
    }

    public List<SwarmInfoEntity> getSwarmInformations() {
        return swarmInformations;
    }

    public void setSwarmInformations(List<SwarmInfoEntity> swarmInformations) {
        this.swarmInformations = swarmInformations;
    }

    public double getInitialVelocity() {
        return initialVelocity;
    }

    public void setInitialVelocity(double initialVelocity) {
        this.initialVelocity = initialVelocity;
    }

    public double getFinalVelocity() {
        return finalVelocity;
    }

    public void setFinalVelocity(double finalVelocity) {
        this.finalVelocity = finalVelocity;
    }

    public String getOrderFunction() {
        return orderFunction;
    }

    public void setOrderFunction(String orderFunction) {
        this.orderFunction = orderFunction;
    }

    public String getShiftFunction() {
        return shiftFunction;
    }

    public void setShiftFunction(String shiftFunction) {
        this.shiftFunction = shiftFunction;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public static class SimulationResultBuilder {
        private String label;
        private String fitnessFunction;
        private double bestFitness;
        private int dimensions;
        private int iterations;
        private List<Double> partial;

        private int totalParticles;
        private List<SwarmInfoEntity> swarmInformations;

        private double initialVelocity;
        private double finalVelocity;

        private String orderFunction;
        private String shiftFunction;

        public SimulationResultBuilder() {
        }

        public SimulationResultBuilder setLabel(String label) {
            this.label = label;
            return this;
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

        public SimulationResultBuilder setSwarmInformations(List<SwarmInfoEntity> swarmInformations) {
            this.swarmInformations = swarmInformations;
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

        public SimulationResult build() {
            return new SimulationResult(label, fitnessFunction, bestFitness, dimensions, iterations, partial, totalParticles, swarmInformations, initialVelocity, finalVelocity, orderFunction, shiftFunction);
        }
    }
}
