package edu.boun.edgecloudsim.simulationvisualizer;

public class ServiceTimeDiagram {
    private String scenarioName;
    private Double serviceTime;
    private int numDevice;

    public ServiceTimeDiagram() {
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
                '}';
    }
}
