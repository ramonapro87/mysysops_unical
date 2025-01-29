package edu.boun.edgecloudsim.simulationvisualizer;

import edu.boun.edgecloudsim.utils.Coordinates;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChartGenerator implements IDiagrams {

    // Metodo per generare il diagramma
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
            JFreeChart chart = ChartFactory.createScatterPlot(
                    diagramType.name(), xAxisLabel, yAxisLabel, dataset,
                    PlotOrientation.VERTICAL, true, true, false);

            // Rimuovi la legenda per evitare conflitti
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

            // Cartella di destinazione per il salvataggio dei grafici
            String folder = "sim_results/diagram_result";

            // Crea la cartella se non esiste
            File directory = new File(folder);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Salva il grafico come immagine
            saveChartAsImage(chart, folder, 800, 600);

            System.out.println("Grafico generato con successo.");
        } catch (Exception e) {
            System.out.println("Si è verificato un errore:");
            e.printStackTrace();
        }
    }

    // Metodo per salvare il grafico come immagine
    private void saveChartAsImage(JFreeChart chart, String filePath, int width, int height) {
        String uuid = UUID.randomUUID().toString();
        String fileName = filePath + "/File_" + uuid + ".png";

        File outputFile = new File(fileName);
        BufferedImage chartImage = chart.createBufferedImage(width, height);
        try {
            // Salva l'immagine come PNG
            ImageIO.write(chartImage, "png", outputFile);
            System.out.println("File salvato come immagine: " + fileName);
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio del file: " + e.getMessage());
        }
    }

    // Metodo per generare i grafici di tipo ENERGY_VS_TIME
    @Override
    public void generateEnergyCharts(Map<Integer, List<Coordinates>> coordinatesById, String scenarioName, String orchestretorPolicy) {
        generateDiagram(coordinatesById, scenarioName, orchestretorPolicy, DiagramType.ENERGY_VS_TIME);
    }

    // Metodo per generare i grafici di tipo MAPCHART_LOCALIZATION
    @Override
    public void generateMapChart(Map<Integer, List<Coordinates>> coordinatesById, String scenarioName, String orchestretorPolicy) {
        generateDiagram(coordinatesById, scenarioName, orchestretorPolicy, DiagramType.MAPCHART_LOCALIZATION);
    }
}
