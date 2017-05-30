package pl.edu.agh.mpso.graphs;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import pl.edu.agh.mpso.chart.Chart;
import pl.edu.agh.mpso.chart.ChartSaveUtilities;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Zuzanna on 5/30/2017.
 */
public class BoxAndWhiskers extends Chart<List<List<Double>>> {

    private Map<String, List<List<Double>>> data = new TreeMap<>();

    private BoxAndWhiskerCategoryDataset createSampleDataset(List<List<Double>> table) {
        int i = 0;

        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        for(List<Double> list : table){
            dataset.add(list, "Series " + (i++), " Type ");
        }
        
        return dataset;

    }


    @Override
    public Chart<List<List<Double>>> addSeries(String name, List<List<Double>> values) {
        data.put(name, values);
        return this;
    }

    @Override
    protected void save(File file) throws IOException {
        final BoxAndWhiskerCategoryDataset dataset = createSampleDataset(data.get(data.keySet().iterator().next()));

        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(title, xTitle, yTitle, dataset, false);
        chart.getPlot().setBackgroundPaint(Color.WHITE);


        ChartSaveUtilities.saveChart(file, chart, size[0], size[1]);
    }
}
