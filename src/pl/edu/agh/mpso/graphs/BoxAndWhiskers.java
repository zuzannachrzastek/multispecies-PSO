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

    private BoxAndWhiskerCategoryDataset createSampleDataset() {

        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        for (String key : data.keySet()) {
            int i = 0;
            for (List<Double> list : data.get(key)) {
                dataset.add(list, key, "Series " + (i++));
            }
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
        final BoxAndWhiskerCategoryDataset dataset = createSampleDataset();

        JFreeChart chart = ChartFactory.createBoxAndWhiskerChart(title, xTitle, yTitle, dataset, true);
        chart.getPlot().setBackgroundPaint(Color.WHITE);

        ChartSaveUtilities.saveChart(file, chart, sizeBoxandWhisker[0], sizeBoxandWhisker[1]);
    }
}
