package uk.infy.luna;

public class AutoGenerator {
    private String name;
    private double cost;
    private int purchased;
    private double incomePerSecond;
    private double baseCost;
    private double baseIncomePerSecond;

    public AutoGenerator(String name, double cost, double incomePerSecond) {
        this.name = name;
        this.cost = cost;
        this.baseCost = cost;
        this.incomePerSecond = incomePerSecond;
        this.baseIncomePerSecond = incomePerSecond;
        this.purchased = 0;
    }

    public void purchase(Game game) {
        if (game.getCurrency() >= cost) {
            game.subtractCurrency(cost);
            purchased++;
            recalculateCost();
        }
    }

    public void recalculateCost() {
        cost = baseCost * Math.pow(1.15, purchased);
    }

    public void reset() {
        purchased = 0;
        cost = baseCost;
        incomePerSecond = baseIncomePerSecond;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public int getPurchased() {
        return purchased;
    }

    public double getIncomePerSecond() {
        return incomePerSecond;
    }

    public void setIncomePerSecond(double incomePerSecond) {
        this.incomePerSecond = incomePerSecond;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setPurchased(int purchased) {
        this.purchased = purchased;
        recalculateCost();
    }
}
