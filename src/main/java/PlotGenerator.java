import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberTickUnitSource;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

public class PlotGenerator {

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);
        File plotDir = new File("results/plots");
        if (!plotDir.exists()) plotDir.mkdirs();

        renderSideBySide("W1", "-", "W1: Random Access (10k gets)", "w1_random_access.png");
        renderSideBySide("W2", "-", "W2: Linear Search (1k contains)", "w2_search.png");
        renderSideBySide("W3", "head", "W3: Insert/Remove (Head)", "w3_insert_remove_head.png");
        renderSideBySide("W3", "middle", "W3: Insert/Remove (Middle)", "w3_insert_remove_middle.png");
        renderSideBySide("W4", "-", "W4: MinHeap Priority Processing", "w4_priority_processing.png");

        System.out.println("All plots successfully re-rendered with professional side-by-side layout!");
    }

    private static void renderSideBySide(String targetWorkload, String targetVariant, String mainTitle, String filename) throws IOException {
        XYSeriesCollection timeData = new XYSeriesCollection();
        XYSeriesCollection opsData = new XYSeriesCollection();

        try (BufferedReader br = new BufferedReader(new FileReader("results/results.csv"))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length < 8) continue;

                String workload = p[0].trim();
                String variant = p[1].trim();
                String struct = p[2].trim();
                double n = Double.parseDouble(p[3].trim());
                double time = Double.parseDouble(p[4].trim());
                double steps = Double.parseDouble(p[5].trim());
                double moves = Double.parseDouble(p[6].trim());
                double comps = Double.parseDouble(p[7].trim());

                if (workload.equalsIgnoreCase(targetWorkload) && variant.equalsIgnoreCase(targetVariant)) {
                    getOrAdd(timeData, struct).add(n, Math.max(time, 0.0001));
                    getOrAdd(opsData, struct).add(n, Math.max(steps + moves + comps, 1.0));
                }
            }
        }

        JFreeChart timeChart = createSubChart("Execution Time vs n", "Time (ms)", timeData);
        JFreeChart opsChart = createSubChart("Physical Operations vs n", "Steps + Moves + Comparisons", opsData);

        // Combine side-by-side into a single 1200x500 image
        BufferedImage image = new BufferedImage(1200, 500, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, 1200, 500);

        // Header Title
        g2.setColor(new Color(30, 41, 59));
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        int titleWidth = fm.stringWidth(mainTitle);
        g2.drawString(mainTitle, (1200 - titleWidth) / 2, 28);

        // Draw left and right charts
        timeChart.draw(g2, new Rectangle(10, 35, 580, 455));
        opsChart.draw(g2, new Rectangle(610, 35, 580, 455));
        g2.dispose();

        ChartUtils.writeBufferedImageAsPNG(new java.io.FileOutputStream("results/plots/" + filename), image);
    }

    private static JFreeChart createSubChart(String subTitle, String yLabel, XYSeriesCollection dataset) {
        LogarithmicAxis xAxis = new LogarithmicAxis("n (Input Size)");
        xAxis.setStrictValuesFlag(false);

        LogarithmicAxis yAxis = new LogarithmicAxis(yLabel);
        yAxis.setStrictValuesFlag(false);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
        Color[] colors = {new Color(37, 99, 235), new Color(220, 38, 38)}; // Blue, Red
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesPaint(i, colors[i % colors.length]);
            renderer.setSeriesStroke(i, new BasicStroke(2.0f));
            renderer.setSeriesShape(i, new Ellipse2D.Double(-4, -4, 8, 8));
        }

        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        plot.setBackgroundPaint(new Color(248, 250, 252));
        plot.setDomainGridlinePaint(new Color(203, 213, 225));
        plot.setRangeGridlinePaint(new Color(203, 213, 225));

        JFreeChart chart = new JFreeChart(subTitle, new Font("SansSerif", Font.BOLD, 13), plot, true);
        chart.setBackgroundPaint(Color.WHITE);
        return chart;
    }

    private static XYSeries getOrAdd(XYSeriesCollection coll, String key) {
        for (int i = 0; i < coll.getSeriesCount(); i++) {
            if (coll.getSeriesKey(i).equals(key)) return coll.getSeries(i);
        }
        XYSeries s = new XYSeries(key);
        coll.addSeries(s);
        return s;
    }
}