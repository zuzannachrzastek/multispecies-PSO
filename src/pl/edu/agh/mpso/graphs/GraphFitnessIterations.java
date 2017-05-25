package pl.edu.agh.mpso.graphs;

import pl.edu.agh.mpso.chart.Chart;
import pl.edu.agh.mpso.chart.Point;
import pl.edu.agh.mpso.chart.ScatterChart;
import pl.edu.agh.mpso.dao.SimulationResultDAO;
import pl.edu.agh.mpso.dao.SwarmInfoEntity;
import pl.edu.agh.mpso.output.SimulationResult;
import pl.edu.agh.mpso.species.SpeciesType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static pl.edu.agh.mpso.Simulation.NUMBER_OF_PARTICLES;
import static pl.edu.agh.mpso.utils.ExecutionParameters.*;

/**
 * Created by Zuzanna on 5/25/2017.
 */
public class GraphFitnessIterations {
    private static final String PACKAGE = "pl.edu.agh.mpso.fitness";
    private static final String fitnessFunction = "Rastrigin";
    private final static int dimensions = 100;
    private final static int iterations = 3000;
    private final static int totalParticles = 25;
    private final static int NUMBER_OF_SPECIES = SpeciesType.values().length;
    private final static String labelStandard = "PSO::2017.05.25::18:04";
    private final static String labelModified = "PSO::2017.05.25::18:09";

    private final static int[] counts = new int[]{0, 4, 11, 18, 25};

    private static Map<Integer, List<List<Double>>> filteredResults = new HashMap<>();
    private static Map<Integer, List<Double>> filteredQuality = new HashMap<>();

    public static void main(String[] args) throws IOException {
        getPartialsForSpecies(1);
    }


    private static void getPartialsForSpecies(int speciesId) throws IOException {
        System.out.println("Getting results");
        SimulationResultDAO dao = SimulationResultDAO.getInstance();
        List<SimulationResult> resultsStandard = dao.getResultsByLabel(PACKAGE + "." + fitnessFunction, dimensions, iterations, totalParticles, labelStandard);
        List<SimulationResult> resultsModified = dao.getResultsByLabel(PACKAGE + "." + fitnessFunction, dimensions, iterations, totalParticles, labelModified);
        dao.close();
        System.out.println("ResultsStandard loaded" + resultsStandard.size());
        System.out.println("ResultsModified loaded" + resultsModified.size());


        System.out.println("Filtering results");
        for (int cnt : counts) {
            List<List<Double>> partialResults = new ArrayList<List<Double>>();
            filteredResults.put(cnt, partialResults);
            List<Double> bestQuality = new ArrayList<Double>();
            filteredQuality.put(cnt, bestQuality);
        }

        Chart<List<Point>> chart =
                new ScatterChart(22, 22, 16, 16)
                        //.setTitle("PSO " + fitnessFunction + " optimizing, ")
                        .setXAxisTitle("Iterations")
                        .setYAxisTitle("Quality")
                        .setLogScale()
                        .setFileFormat("pdf");
        int minExecutions = Integer.MAX_VALUE;
        chart.addSeries(labelStandard, addData(resultsStandard, chart, minExecutions));
        chart.addSeries(labelModified, addData(resultsStandard, chart, minExecutions));

        String path = "thesis2/share/" + fitnessFunction;
        String suffix = "" + speciesId + "_" + totalParticles + "_" + dimensions + "_" + iterations + "_" + minExecutions;

        chart.save("results/" + speciesId + ".pdf");


        System.out.println("Preparing csv results");
        File avgCsvFile = new File("results/" + "/average.csv");
        File stdCsvFile = new File("results/" + "/deviation.csv");
        PrintWriter avgWriter = new PrintWriter(new FileOutputStream(avgCsvFile, true));
        PrintWriter stdWriter = new PrintWriter(new FileOutputStream(stdCsvFile, true));
//		writer.append("Count,Average Quality,Standard Deviation\n");

        avgWriter.append(SpeciesType.values()[speciesId].toString() + ",");

        for (int i = 0; i < counts.length; i++) {
            List<Double> values = filteredQuality.get(counts[i]);
            double avg = average(values);
            double stD = standardDeviation(values, avg);
            avgWriter.append("" + round(avg));
            stdWriter.append("" + round(stD));

            if (i == counts.length - 1) {
                avgWriter.append("\n");
                stdWriter.append("\n");
            } else {
                avgWriter.append(",");
                stdWriter.append(",");
            }
        }

//		for(int cnt : counts){
//			List<Double> values = filteredQuality.get(cnt);
//			double avg = average(values);
//			double stD = standardDeviation(values, avg);
//			avgWriter.append("" + cnt + "," + round(avg) + "," + round(stD) + "\n");
//		}

        stdWriter.close();
        avgWriter.close();
    }

    private static List<Point> addData(List<SimulationResult> resultsStandard, Chart<List<Point>> chart, int minExecutions) {
        List<List<Double>> partial = resultsStandard.stream().map(SimulationResult::getPartial).collect(Collectors.toList());


        System.out.println("Preparing chart data" + partial.size());


        List<Point> points = new ArrayList<Point>();

//        List<List<Double>> valuesList = partial;
        if (partial.size() < minExecutions) minExecutions = partial.size();

        for (int i = 1; i < EXECUTIONS; i++) {
            //count average
            double sum = 0.0;

            for (List<Double> values : partial) {
                sum += values.get(i);
            }

            double x = iterations * i / 100;
            double y = sum / partial.size();
            points.add(new Point(x, y));
        }



        return points;
    }

    private static double average(List<Double> values) {
        double cnt = values.size();
        double sum = 0.0;

        for (double value : values) {
            sum += value;
        }

        return sum / cnt;
    }

    private static double standardDeviation(List<Double> values, double average) {
        double sum = 0.0;

        for (double value : values) {
            sum += Math.pow(average - value, 2.0);
        }

        double variance = sum / values.size();

        return Math.sqrt(variance);
    }

    private static double round(double a) {
        return (double) Math.round(a * 100) / 100;
    }

    private static boolean meetsCriteria(SimulationResult result, int speciesId, int speciesCnt) {
        try {
            List<SwarmInfoEntity> collect = result.getSwarmInformations().stream()
                    .filter(swarmInfoEntity -> swarmInfoEntity.getType() == speciesId)
                    .collect(Collectors.toList());
            int speciesNo = collect.isEmpty() ? 0 : collect.get(0).getNumberOfParticles();

            return result.getTotalParticles() == NUMBER_OF_PARTICLES && speciesNo == speciesCnt;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
