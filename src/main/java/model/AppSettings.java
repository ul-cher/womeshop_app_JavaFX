package model;

public class AppSettings {
    private int id;
    private double initialCapital;
    private double currentCapital;
    private double totalIncome;
    private double totalCost;

    public AppSettings() {}

    public AppSettings(double initialCapital, double currentCapital,
                       double totalIncome, double totalCost) {
        this.initialCapital = initialCapital;
        this.currentCapital = currentCapital;
        this.totalIncome = totalIncome;
        this.totalCost = totalCost;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getInitialCapital() { return initialCapital; }
    public void setInitialCapital(double initialCapital) {
        this.initialCapital = initialCapital;
    }

    public double getCurrentCapital() { return currentCapital; }
    public void setCurrentCapital(double currentCapital) {
        this.currentCapital = currentCapital;
    }

    public double getTotalIncome() { return totalIncome; }
    public void setTotalIncome(double totalIncome) { this.totalIncome = totalIncome; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
}
