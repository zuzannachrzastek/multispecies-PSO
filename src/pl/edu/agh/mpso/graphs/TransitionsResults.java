//package pl.edu.agh.mpso.graphs;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import pl.edu.agh.mpso.dao.SimulationResultDAO;
//import pl.edu.agh.mpso.output.SimulationResult;
//
//public class TransitionsResults {
//    private static final String FITNESS_FUNCTION = "Styblinski";
//    private static final String PACKAGE = "pl.edu.agh.mpso.fitness";
//    private final static int DIMENSIONS = 100;
//    private final static int ITERATIONS = 5000;
//    private final static int TOTAL_PARTICLES = 25;
//
//    private final static Map<String, Map<String, List<Double>>> groupedResults = new HashMap<String, Map<String, List<Double>>>();
//
//    private final static int[][] SPAWN_CONFIGURATIONS = {
//    	{4,3,3,3,3,3,3,3}
//    };
//    public static void main(String[] args) throws IOException {
//        generateChartForFitness(FITNESS_FUNCTION, DIMENSIONS, ITERATIONS, TOTAL_PARTICLES, SPAWN_CONFIGURATIONS);
//    }
//
//    private static void generateChartForFitness(String fitnessFunction, int dimensions, int iterations,
//                                                int totalParticles, int[][] spawnConfigurations) throws IOException {
//        System.out.println("Getting results");
//        SimulationResultDAO dao = SimulationResultDAO.getInstance();
//        List<SimulationResult> results = dao.getResults(PACKAGE + "." + fitnessFunction, dimensions, iterations, totalParticles);
//        dao.close();
//        System.out.println("Results loaded");
//
//        for (SimulationResult result : results) {
//            if (meetsCriteria(spawnConfigurations, result)) {
//            	if(!groupedResults.containsKey(result.getOrderFunction())){
//            		groupedResults.put(result.getOrderFunction(), new HashMap<String, List<Double>>());
//            	}
//            	if(!groupedResults.get(result.getOrderFunction()).containsKey(result.getShiftFunction())){
//            		groupedResults.get(result.getOrderFunction()).put(result.getShiftFunction(), new ArrayList<Double>(30));
//            	}
//            	groupedResults.get(result.getOrderFunction()).get(result.getShiftFunction()).add(result.getBestFitness());
//            }
//        }
//
//        System.out.println("Counting average results");
//
//        File csvFile = new File("results/thesis2/transitions/results_" + fitnessFunction + ".csv");
//		PrintWriter writer = new PrintWriter(csvFile);
//		String firstLine = "," + groupedResults.keySet().toString().replace("[", "").replace("]", "").replace("\\s", "");
//		writer.append(firstLine + "\n");
//
//		Map<String, StringBuffer> lines = new HashMap<String, StringBuffer>();
//
//        for(String orderFunction : groupedResults.keySet()){
//        	for(String shiftFunction : groupedResults.get(orderFunction).keySet()){
//        		if(!lines.containsKey(shiftFunction)){
//        			lines.put(shiftFunction, new StringBuffer(shiftFunction));
//        		}
//        		double avg = average(groupedResults.get(orderFunction).get(shiftFunction));
//        		lines.get(shiftFunction).append("," + round(avg));
//        	}
//        }
//
//        for(Entry<String, StringBuffer>  line : lines.entrySet()){
//        	writer.append(line.getValue().toString() + "\n");
//        }
//
//        writer.close();
//    }
//
//
//    private static boolean meetsCriteria(int[][] spawnConfigurations, SimulationResult result) {
//        final int[] speciesCount = getSpeciesConfiguration(result);
//        for (int[] spawnConfiguration : spawnConfigurations) {
//            if (Arrays.equals(spawnConfiguration, speciesCount)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    //TODO return SwarmInformations
//    private static int[] getSpeciesConfiguration(SimulationResult result) {
//        return new int[]{ result.getSpecies1(), result.getSpecies2(), result.getSpecies3(),
//                                     result.getSpecies4(), result.getSpecies5(), result.getSpecies6(),
//                                     result.getSpecies7(), result.getSpecies8()};
//    }
//
//    private static double average(List<Double> values){
//		double cnt = values.size();
//		double sum = 0.0;
//
//		for(double value : values){
//			sum += value;
//		}
//
//		return sum / cnt;
//	}
//
////	private static double standardDeviation(List<Double> values, double average){
////		double sum = 0.0;
////
////		for(double value : values){
////			sum += Math.pow(average - value, 2.0);
////		}
////
////		double variance = sum / values.size();
////
////		return Math.sqrt(variance);
////	}
////
//	private static double round(double a){
//		return  (double) Math.round(a * 100) / 100;
//	}
//}
