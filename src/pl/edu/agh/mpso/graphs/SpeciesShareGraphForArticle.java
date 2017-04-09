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

public class SpeciesShareGraphForArticle {
    private static final String PACKAGE = "pl.edu.agh.mpso.fitness";
    private static final String fitnessFunction = "Rastrigin";
    private final static int dimensions = 100;
    private final static int iterations = 3000;
    private final static int totalParticles = 25;
    private final static int NUMBER_OF_SPECIES = SpeciesType.values().length;

    private final static int[] counts = new int[]{0, 4, 8, 18, 25};

    private static Map<Integer, List<List<Double>>> filteredResults = new HashMap<>();
    private static Map<Integer, List<Double>> filteredQuality = new HashMap<>();

    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= NUMBER_OF_SPECIES; i++) getPartialsForSpecies(i);
    }


    private static void getPartialsForSpecies(int speciesId) throws IOException {
        System.out.println("Getting results");
        SimulationResultDAO dao = SimulationResultDAO.getInstance();
        List<SimulationResult> results = dao.getResults(PACKAGE + "." + fitnessFunction, dimensions, iterations, totalParticles);
        dao.close();
        System.out.println("Results loaded" + results.size());


        System.out.println("Filtering results");
        for (int cnt : counts) {
            List<List<Double>> partialResults = new ArrayList<List<Double>>();
            filteredResults.put(cnt, partialResults);
            List<Double> bestQuality = new ArrayList<Double>();
            filteredQuality.put(cnt, bestQuality);
        }

        for (SimulationResult result : results) {
            for (int cnt : counts) {
                if (meetsCriteria(result, speciesId, cnt)) {
                    filteredResults.get(cnt).add(result.getPartial());
//                    System.out.println(cnt + result.getPartial().toString());
                    filteredQuality.get(cnt).add(result.getBestFitness());
                    break;
                }
            }

        }

        System.out.println("Preparing chart data" + filteredResults.size());

        Chart<List<Point>> chart =
                new ScatterChart(22, 22, 16, 16)
                        //.setTitle("PSO " + fitnessFunction + " optimizing, ")
                        .setXAxisTitle("Iterations")
                        .setYAxisTitle("Quality")
                        .setLogScale()
                        .setFileFormat("pdf");

        int minExecutions = Integer.MAX_VALUE;

        for (int cnt : counts) {
            String label = "" + cnt + " particles (" + Math.round((float) cnt * 100.0f / (float) totalParticles) + "%)";
            List<Point> points = new ArrayList<Point>();

            List<List<Double>> valuesList = filteredResults.get(cnt);
            if (valuesList.size() < minExecutions) minExecutions = valuesList.size();

            for (int i = 1; i < 100; i++) {
                //count average
                double sum = 0.0;

                for (List<Double> values : valuesList) {
                    sum += values.get(i);
                }

                double x = iterations * i / 100;
                double y = sum / valuesList.size();
                points.add(new Point(x, y));
            }

            chart.addSeries(label, points);
        }

        String path = "thesis2/share/" + fitnessFunction;
        String suffix = "" + speciesId + "_" + totalParticles + "_" + dimensions + "_" + iterations + "_" + minExecutions;

        chart.save("results/" + speciesId + ".pdf");


        System.out.println("Preparing csv results");
        File avgCsvFile = new File("results/" + "/average.csv");
        File stdCsvFile = new File("results/" + "/deviation.csv");
        PrintWriter avgWriter = new PrintWriter(new FileOutputStream(avgCsvFile, true));
        PrintWriter stdWriter = new PrintWriter(new FileOutputStream(stdCsvFile, true));
//		writer.append("Count,Average Quality,Standard Deviation\n");

        avgWriter.append(SpeciesType.values()[speciesId - 1].toString() + ",");

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
