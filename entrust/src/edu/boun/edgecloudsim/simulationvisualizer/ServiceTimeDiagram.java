package edu.boun.edgecloudsim.simulationvisualizer;

import edu.boun.edgecloudsim.core.SimSettings;

public class ServiceTimeDiagram {
    private String scenarioName;
    private Double serviceTime;
    private int numDevice;


   // private double failedTask;
    private String nameApp;
    private Double avgSpentEnergy;

    public Double getAvgSpentEnergy() {
        return avgSpentEnergy;
    }

    public void setAvgSpentEnergy(Double avgSpentEnergy) {
        this.avgSpentEnergy = avgSpentEnergy;
    }

    public ServiceTimeDiagram() {
    }



    public String getNameApp() {
        return nameApp;
    }

    public void setNameApp(String nameApp) {
        this.nameApp = nameApp;
    }







    public String getScenarioName() {
        return scenarioName;
    }

    public int getNumDevice() {
        return numDevice;
    }

    public Double getServiceTime() {
        return serviceTime;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public void setServiceTime(Double serviceTime) {
        this.serviceTime = serviceTime;
    }

    public void setNumDevice(int numDevice) {
        this.numDevice = numDevice;
    }

    @Override
    public String toString() {
        return "ServiceTimeDiagram{" +
                "scenarioName='" + scenarioName + '\'' +
                ", serviceTime=" + serviceTime +
                ", numDevice=" + numDevice +
                ", nameApp='" + nameApp + '\'' +
                ", avgSpentEnergy=" + avgSpentEnergy +
                '}';
    }
}
