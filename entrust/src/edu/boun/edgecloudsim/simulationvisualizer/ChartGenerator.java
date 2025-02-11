package edu.boun.edgecloudsim.simulationvisualizer;

import edu.boun.edgecloudsim.utils.Coordinates;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ChartGenerator implements IDiagrams {
    // Cartella di destinazione per il salvataggio dei grafici
    String folder = "sim_results/diagram_result";
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
                yAxisLabel = "Energy Consumed Wh";
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

    @Override
    public void generateServiceTimeChart(LinkedList<ServiceTimeDiagram> dataList) {
        // Raggruppiamo i dati per scenario
        Map<String, List<ServiceTimeDiagram>> groupedByScenario = dataList.stream()
                .collect(Collectors.groupingBy(ServiceTimeDiagram::getScenarioName));

        XYSeriesCollection dataset = new XYSeriesCollection();

        for (Map.Entry<String, List<ServiceTimeDiagram>> entry : groupedByScenario.entrySet()) {
            XYSeries series = new XYSeries(entry.getKey());

            for (ServiceTimeDiagram data : entry.getValue()) {
                series.add(data.getNumDevice(), data.getServiceTime());
            }

            dataset.addSeries(series);
        }

        JFreeChart lineChart = ChartFactory.createXYLineChart(
                "Service Time vs Number of Devices",
                "Number of Devices",
                "Service Time [s]",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);



        XYPlot plot = lineChart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // Assegna colori diversi a ogni serie
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesPaint(i, getColor(i));
            renderer.setSeriesStroke(i, new BasicStroke(2.0f));
        }

        plot.setRenderer(renderer);

        // Creiamo la finestra per mostrare il grafico
        JFrame frame = new JFrame("Avg service time plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(lineChart));
        frame.pack();
        frame.setVisible(true);


        saveChartAsImage(lineChart, folder, 800, 600);

    }
    public void  createHistogramFailedTask(HashMap<String, Double> data, int networkStability) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Aggiungi i dati all'istogramma
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), "Failed Task", entry.getKey());
        }



        // Crea il grafico con il titolo, le etichette degli assi e i dati
        JFreeChart chart = ChartFactory.createBarChart(
                "Network Stability: " + networkStability,  // Titolo
                "Scenario",                               // Etichetta asse X
                "Failed Task %",                            // Etichetta asse Y
                dataset,                                  // Dataset
                org.jfree.chart.plot.PlotOrientation.VERTICAL, // Orientamento
                false,                                    // Disabilita la legenda
                true,                                     // Include tooltips
                false                                     // Include URL
        );
            chart.removeLegend();

        // Ottieni il plot e il renderer per modificare il colore delle barre
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        // Imposta il colore delle barre su grigio
        renderer.setSeriesPaint(0, Color.GRAY); // Modifica il colore della serie 0 (la prima serie)
        // Imposta la larghezza delle barre
        renderer.setItemMargin(0.5); // Aumenta il margine tra le barre per farle più sottili


        saveChartAsImage(chart, folder, 800, 600);

    }


    public void generateEnergyForApp(LinkedList<ServiceTimeDiagram> data) {
        // Creazione del dataset per l'istogramma
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Aggiungiamo i valori di energia media spesa al dataset
        for (ServiceTimeDiagram diagram : data) {
            dataset.addValue(diagram.getAvgSpentEnergy(), "Energy Spent Wh", diagram.getScenarioName());
        }

        // Creazione del grafico
        JFreeChart chart = ChartFactory.createBarChart(
                data.getFirst().getNameApp(),               // Titolo del grafico
                "Scenario",                                // Etichetta dell'asse delle X (scenari)
                "Average Spent Energy [Wh]",                    // Etichetta dell'asse delle Y (energia spesa media)
                dataset,                                   // Dataset
                PlotOrientation.VERTICAL,                  // Orientamento del grafico
                false,                                     // Legenda
                true,                                      // Tooltips
                false                                      // URLs
        );

        // Mostra il grafico in una finestra
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        JFrame frame = new JFrame("Energy for App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(chartPanel);
        frame.pack();
        frame.setVisible(true);

        // Salva il grafico come immagine
        saveChartAsImage(chart, folder, 800, 600);
    }











    private static Color getColor(int index) {
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.CYAN};
        return colors[index % colors.length]; // Cicla i colori se ci sono più serie
    }
    }








