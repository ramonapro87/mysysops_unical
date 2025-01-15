package edu.boun.edgecloudsim.simulationvisualizer;

import edu.boun.edgecloudsim.utils.Coordinates;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class ChartGenerator implements IDiagrams {

    boolean firstTime = true;




    public void generateDiagram(Map<Integer, List<Coordinates>> coordinatesById, String scenarioName, String orchestretorPolicy, DiagramType diagramType) {
        try {
            // Crea un dataset
            XYSeriesCollection dataset = new XYSeriesCollection();

            // Crea una serie di dati per ciascun id
            for (Map.Entry<Integer, List<Coordinates>> entry : coordinatesById.entrySet()) {
                XYSeries series = new XYSeries("Host" + entry.getKey());
                for (Coordinates coord : entry.getValue()) {
                    if (diagramType == DiagramType.ENERGY_VS_TIME) {
                        // Per ENERGY_VS_TIME, l'asse X è il tempo, l'asse Y è l'energia consumata
                        series.add(coord.getTime(), coord.getEnergyConsumed());
                    } else if (diagramType == DiagramType.MAPCHART_LOCALIZATION) {
                        // Per MAPCHART_LOCALIZATION, l'asse X e Y sono le coordinate
                        double x = coord.getX() + Math.random() * 0.5;
                        double y = coord.getY() + Math.random() * 0.5;
                        series.add(x, y);
                    }
                }
                dataset.addSeries(series);
            }

            // Stampa di debug per verificare il dataset
            for (int i = 0; i < dataset.getSeriesCount(); i++) {
                XYSeries series = dataset.getSeries(i);
                System.out.println("Series " + i + ": " + series.getKey() + ", Item Count: " + series.getItemCount());
            }

            // Variabili per i titoli degli assi X e Y
            String xAxisLabel = "";
            String yAxisLabel = "";

            // Imposta i titoli degli assi in base al tipo di diagramma
            if (diagramType == DiagramType.ENERGY_VS_TIME) {
                xAxisLabel = "Time";
                yAxisLabel = "Energy Consumed";
            } else if (diagramType == DiagramType.MAPCHART_LOCALIZATION) {
                xAxisLabel = "Coordinate X";
                yAxisLabel = "Coordinate Y";
            }

            // Crea il grafico scatterplot con i titoli degli assi
            JFreeChart chart = ChartFactory.createScatterPlot(diagramType.name(), xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);

            // Rimuovi la legenda per verificare se è la causa dell'errore
            chart.removeLegend();

            // Mostra il grafico in una finestra
            SwingUtilities.invokeLater(() -> {
                String title = scenarioName + " - " + orchestretorPolicy;
                JFrame frame = new JFrame(title);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new ChartPanel(chart));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });

            String folder = "sim_results/diagram_result";

            if (firstTime) {
                firstTime = false;
                // Pulizia della cartella
                File directory = new File(folder);
                if (directory.exists()) {
                    File[] files = directory.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            file.delete();
                        }
                    }
                }
            }
            // Salva il grafico come immagine
            saveChartAsImage(chart, folder, 800, 600);

            System.out.println("Grafico generato con successo.");
        } catch (Exception e) {
            System.out.println("Si è verificato un errore:");
            e.printStackTrace();
        }
    }




    private void saveChartAsImage(JFreeChart chart, String filePath, int width, int height) {
        String uuid = UUID.randomUUID().toString();
        String fileName = filePath + "/File_" + uuid + ".png";

        File outputFile = new File(fileName);
        outputFile.getParentFile().mkdirs(); // Crea le directory se non esistono
        BufferedImage chartImage = chart.createBufferedImage(width, height);
        try {
            ImageIO.write(chartImage, "png", outputFile);
        } catch (IOException e) {
            System.err.println("Error saving chart: " + e.getMessage());
        }
    }

    @Override
    public void generateEnergyCharts(Map<Integer, List<Coordinates>> coordinatesById, String scenarioName, String orchestretorPolicy) {
        generateDiagram(coordinatesById, scenarioName, orchestretorPolicy, DiagramType.ENERGY_VS_TIME);
    }

    @Override
    public void generateMapChart(Map<Integer, List<Coordinates>> coordinatesById, String scenarioName, String orchestretorPolicy) {
        generateDiagram(coordinatesById, scenarioName, orchestretorPolicy, DiagramType.MAPCHART_LOCALIZATION);
    }
}
