package pl.edu.agh.mpso;

import pl.edu.agh.mpso.chart.*;
import pl.edu.agh.mpso.fitness.Rastrigin;
import pl.edu.agh.mpso.species.SpeciesType;
import pl.edu.agh.mpso.swarm.MultiSwarm;
import pl.edu.agh.mpso.swarm.SwarmInformation;
import pl.edu.agh.mpso.utils.SwarmUtils;

import java.util.*;

import static pl.edu.agh.mpso.Simulation.NUMBER_OF_SKIPPED_ITERATIONS;
import static pl.edu.agh.mpso.utils.ExecutionParameters.DIMENSIONS;
import static pl.edu.agh.mpso.utils.ExecutionParameters.ITERATIONS;

//TODO DOESN'T WORK
@SuppressWarnings("rawtypes")
public class Comparison {
    private static final int EXECUTIONS = 1;
    private static Map<String, Chart> pieCharts = new TreeMap<String, Chart>();
    private static Map<String, Map<Integer, List<Double>>> results = new TreeMap<String, Map<Integer, List<Double>>>();

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        for (int i = 0; i < EXECUTIONS; i++) {
            System.out.println("Execution " + (i + 1) + " of " + EXECUTIONS);
            run("Standard", new int[]{24});
            //run("Swarm 2", new int[] {14, 5, 5});
            run("Multispecies", new int[]{4, 4, 0, 4, 2, 2, 2, 6});
            //run("Swarm 3", new int[] {6, 4, 4, 4, 0, 0, 0, 4});
            //run("Swarm 3", new int[] {2, 4, 0, 4, 2, 2, 2, 8});
            //run("Swarm 3", new int[] {5, 5, 0, 4, 2, 2, 2, 4});
        }

        Chart chart = new ScatterChart().setTitle("PSO Ristrigin optimizing, " + DIMENSIONS + " dimensions, " + ITERATIONS + " iterations").
                setXAxisTitle("Iterations").setYAxisTitle("Fitness").addSubTitle("" + EXECUTIONS + " executions");

        for (String swarmName : results.keySet()) {
            List<Point> points = new ArrayList<Point>();
            for (int key : results.get(swarmName).keySet()) {
                List<Double> values = results.get(swarmName).get(key);
                double sum = 0.0;

                for (double value : values) {
                    sum += value;
                }

                double average = sum / values.size();
                double standardDeviation = standardDeviation(values, average);

                points.add(new Point(key, average, standardDeviation));
            }
            chart.addSeries(swarmName, points);
        }

        chart.addStandardDeviation().saveWithDateStamp("raw/chart");

        try {
            ChartCombiner.combine(chart, pieCharts);
        } catch (Exception e) {

        }
    }

    private static double standardDeviation(List<Double> values, double average) {
        double sum = 0.0;

        for (double value : values) {
            sum += Math.pow(average - value, 2.0);
        }

        double variance = sum / values.size();

        return Math.sqrt(variance);
    }

    private static void run(String name, int[] particles) {
        //create pie chart
        if (!pieCharts.containsKey(name)) {
            Chart<Integer> pieChart = new SpeciesPieChart().addSpeciesData(name, particles);
            pieCharts.put(name, pieChart);
        }

        //create particles
        int cnt = 0;
        List<SwarmInformation> swarmInformations = new ArrayList<SwarmInformation>();

        for (int i = 0; i < particles.length; i++) {
            if (particles[i] != 0) {
                cnt += particles[i];

                SpeciesType type = SpeciesType.values()[i];

                //TODO random
                if (i == 7)
                    type = new SpeciesType(7, new SpeciesType[]{});
                SwarmInformation swarmInformation = new SwarmInformation(particles[i], type);

                swarmInformations.add(swarmInformation);
            }
        }
        MultiSwarm multiSwarm = new MultiSwarm(swarmInformations, new Rastrigin());

        SwarmUtils.setMultiSwarmParameters(multiSwarm, multiSwarm.getNumberOfParticles()/5, 0.95, 100);

        for (int i = 0; i < ITERATIONS; ++i) {
            // Evolve swarm
            multiSwarm.evolve();
            if (i % 1000 == 0 && i > NUMBER_OF_SKIPPED_ITERATIONS) {
                if (!results.containsKey(name)) results.put(name, new HashMap<>());
                if (!results.get(name).containsKey(i)) results.get(name).put(i, new ArrayList<>());
                results.get(name).get(i).add(multiSwarm.getBestFitness());
            }
        }
    }

}
