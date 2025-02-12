package edu.boun.edgecloudsim.simulationvisualizer;

import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import edu.boun.edgecloudsim.utils.Coordinates;
import org.jfree.chart.*;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.border.LineBorder;
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
        if (dataList == null || dataList.isEmpty()) {
            System.out.println("No data available to plot.");
            return;
        }

        // Prendiamo il nome dell'applicazione dalla prima entry
        String appname = dataList.getFirst().getNameApp();

        // Raggruppiamo i dati per scenario
        Map<String, List<ServiceTimeDiagram>> groupedByScenario = dataList.stream()
                .collect(Collectors.groupingBy(ServiceTimeDiagram::getScenarioName));

        // Creiamo il grafico
        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title(appname)
                .xAxisTitle("Number of Devices")
                .yAxisTitle("Service Time [s]")
                .build();

        chart.getStyler().setLegendPosition(org.knowm.xchart.style.Styler.LegendPosition.OutsideS); // Legenda fuori
        chart.getStyler().setMarkerSize(6);

        System.out.println("DEBUG: Generazione dataset:");

        // Aggiungiamo le serie al grafico
        for (Map.Entry<String, List<ServiceTimeDiagram>> entry : groupedByScenario.entrySet()) {
            String scenarioName = entry.getKey();
            List<ServiceTimeDiagram> values = entry.getValue();

            double[] xData = values.stream().mapToDouble(ServiceTimeDiagram::getNumDevice).toArray();
            double[] yData = values.stream().mapToDouble(ServiceTimeDiagram::getServiceTime).toArray();

            org.knowm.xchart.XYSeries series = chart.addSeries(scenarioName, xData, yData);
            series.setMarker(SeriesMarkers.CIRCLE);
        }

        // Mostriamo il grafico in una finestra
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Avg Service Time Plot");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            JPanel chartPanel = new XChartPanel<>(chart);
            frame.add(chartPanel);
            frame.pack();
            frame.setVisible(true);
        });

        // Salviamo il grafico come immagine
        saveChartAsImage(chart, folder, "chartservicetime.png");
    }

    private void saveChartAsImage(XYChart chart, String folder, String filename) {
        try {
            // Generiamo una stringa casuale (UUID)
            String randomString = UUID.randomUUID().toString().substring(0, 8); // Prendiamo solo i primi 8 caratteri

            // Creiamo il nuovo nome file con la stringa casuale
            String newFilename = filename.replace(".png", "") + "_" + randomString + ".png";

            // Percorso completo del file
            Path outputPath = Paths.get(folder, newFilename);

            // Salviamo l'immagine
            BitmapEncoder.saveBitmap(chart, outputPath.toString(), BitmapEncoder.BitmapFormat.PNG);

            System.out.println("Grafico salvato in: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio dell'immagine: " + e.getMessage());
        }
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








