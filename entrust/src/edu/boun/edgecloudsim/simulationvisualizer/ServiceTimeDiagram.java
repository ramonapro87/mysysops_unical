package edu.boun.edgecloudsim.simulationvisualizer;

public class ServiceTimeDiagram {
    private String scenarioName;
    private Double serviceTime;
    private int numDevice;

    private String nameApp;
    private Double avgSpentEnergy;
    private int  numtaskonedge;
    private int numtaskonMobile;
    private int numtaskoncloud;

    public int getNumtaskoncloud() {
        return numtaskoncloud;
    }

    public void setNumtaskoncloud(int numtaskoncloud) {
        this.numtaskoncloud = numtaskoncloud;
    }

    public int getNumtaskonMobile() {
        return numtaskonMobile;
    }

    public void setNumtaskonedge(int numtaskonedge) {
        this.numtaskonedge = numtaskonedge;
    }

    public void setNumtaskonMobile(int numtaskonMobile) {
        this.numtaskonMobile = numtaskonMobile;
    }

    public int getNumtaskonedge() {
        return numtaskonedge;
    }

    public Double getAvgSpentEnergy() {
        return  avgSpentEnergy;
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
                ", numtaskonedge=" + numtaskonedge +
                ", numtaskonMobile=" + numtaskonMobile +
                ", numtaskoncloud=" + numtaskoncloud +
                '}';
    }
}
