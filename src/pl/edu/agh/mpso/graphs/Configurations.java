package pl.edu.agh.mpso.graphs;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.edu.agh.mpso.chart.Chart;
import pl.edu.agh.mpso.chart.Point;
import pl.edu.agh.mpso.chart.ScatterChart;
import pl.edu.agh.mpso.dao.SimulationResultDAO;
import pl.edu.agh.mpso.output.SimulationResult;
import pl.edu.agh.mpso.species.SpeciesType;
import pl.edu.agh.mpso.utils.MiscellaneuosUtils;

public class Configurations {
	private static final String fitnessFunction = "Schwefel";
	private final static int dimensions = 100;
	private final static int iterations = 3000;
	private final static int totalParticles = 25;
	private final static int NUMBER_OF_SPECIES = SpeciesType.values().length;
	
	private final static int [] counts = new int[] {0, 4, 11, 18, 25}; 
	
	private static Map<Integer, List<List<Double>>> filteredResults = new HashMap<Integer, List<List<Double>>>();
	private static Map<Integer, List<Double>> filteredQuality = new HashMap<Integer, List<Double>>();
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		for(int i = 1; i <= NUMBER_OF_SPECIES; i++) getPartialsForSpecies(i);
	}

	private static void getPartialsForSpecies(int speciesId) throws IOException, ClassNotFoundException {
		System.out.println("Getting results");
		SimulationResultDAO dao = SimulationResultDAO.getInstance();

		String className = Class.forName("pl.edu.agh.mpso.fitness." + fitnessFunction).toString().substring(6);
		System.out.println("className: " + className);

		List<SimulationResult> results = dao.getResults(className, dimensions, iterations, totalParticles);
		dao.close();
		System.out.println("Results loaded");

		for(SimulationResult result : results){
			System.out.println("before filtration: " + result.toString());
		}
		
		
		System.out.println("Filtering results");
		for(int cnt : counts){
			List<List<Double>> partialResults = new ArrayList<List<Double>>();
			filteredResults.put(cnt, partialResults);
			List<Double> bestQuality = new ArrayList<Double>();
			filteredQuality.put(cnt, bestQuality);
		}

		System.out.println("HELLO");
		for(SimulationResult result : results){
			System.out.println("###result: " + result.toString());
		}


		for(SimulationResult result : results){
			for(int cnt : counts){
				if(MiscellaneuosUtils.meetsCriteria(result, speciesId, cnt)){
					filteredResults.get(cnt).add(result.getPartial());
					filteredQuality.get(cnt).add(result.getBestFitness());
					break;
				}
			}
		}
		
		System.out.println("Preparing chart data");

		Chart<List<Point>> chart =
				new ScatterChart()
						//.setTitle("PSO " + fitnessFunction + " optimizing, ")
						.setXAxisTitle("Iterations")
						.setYAxisTitle("Quality")
						.setLogScale()
						.setFileFormat("pdf");
		
		int minExecutions = Integer.MAX_VALUE;
		
		for(int cnt : counts){
			String label = "" + cnt + " particles (" + Math.round((float) cnt * 100.0f / (float) totalParticles) + "%)";
			List<Point> points = new ArrayList<Point>();

			List<List<Double>> valuesList = filteredResults.get(cnt);
			if(valuesList.size() < minExecutions) minExecutions = valuesList.size();
			
			for(int i = 0; i < 100; i++){
				//count average
				double sum = 0.0;
				
				for(List<Double> values : valuesList){
					sum += values.get(i);
				}
				
				double x = iterations * i / 100;
				double y = sum / valuesList.size();
				points.add(new Point(x, y));				
			}
			
			chart.addSeries(label, points);
		}

		String path = "partial/article/" + fitnessFunction;
		String suffix = "" + speciesId + "_" + totalParticles + "_" + dimensions + "_" + iterations + "_" + minExecutions;
		
		chart.saveWithDateStamp(path + "/chart_" + suffix);
		
		
		
		System.out.println("Preparing csv results");
		File csvFile = new File("results/" + path + "/results_" + suffix + ".csv");
		PrintWriter writer = new PrintWriter(csvFile);
		writer.append("Count,Average Quality,Standard Deviation\n");
		
		for(int cnt : counts){
			List<Double> values = filteredQuality.get(cnt);
			double avg = average(values);
			double stD = standardDeviation(values, avg);
			writer.append("" + cnt + "," + round(avg) + "," + round(stD) + "\n");
		}
		
		writer.close();
	}
	
	private static double average(List<Double> values){
		double cnt = values.size();
		double sum = 0.0;
		
		for(double value : values){
			sum += value;
		}
		
		return sum / cnt;
	}
	
	private static double standardDeviation(List<Double> values, double average){
		double sum = 0.0;
		
		for(double value : values){
			sum += Math.pow(average - value, 2.0);
		}
		
		double variance = sum / values.size();
		
		return Math.sqrt(variance);
	}
	
	private static double round(double a){
		return  (double) Math.round(a * 100) / 100;
	}
}
