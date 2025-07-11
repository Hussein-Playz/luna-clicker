package uk.infy.luna;

public class Upgrade {
    private String name;
    private double cost;
    private double multiplier;
    private int purchased;
    private double baseCost;

    private String effectType = "";
    private double effectValue = 0;

    public Upgrade(String name, double cost, double multiplier) {
        this.name = name;
        this.cost = cost;
        this.baseCost = cost;
        this.multiplier = multiplier;
        this.purchased = 0;
    }

    public void setEffect(String effectType, double effectValue) {
        this.effectType = effectType;
        this.effectValue = effectValue;
    }

    public double getEffectValue() {
        return effectValue;
    }

    public void setEffectValue(double effectValue) {
        this.effectValue = effectValue;
    }

    public void purchase(Game game) {
        if (game.getCurrency() >= cost) {
            game.subtractCurrency(cost);
            purchased++;
            recalculateCost();

            switch (effectType) {
                case "clickMultiplier":
                    game.increaseClickValue(game.getClickValue() * (effectValue - 1));
                    break;
                case "generatorMultiplier":
                    for (AutoGenerator ag : game.getAutoGenerators()) {
                        ag.setIncomePerSecond(ag.getIncomePerSecond() * (effectValue / 2) + 50);
                    }
                    break;
                default:
                    game.increaseClickValue(multiplier);
            }
        }
    }

    public void recalculateCost() {
        cost = baseCost * Math.pow(1.15, purchased);
    }

    public String getName() { return name; }
    public double getCost() { return cost; }
    public double getMultiplier() { return multiplier; }
    public int getPurchased() { return purchased; }

    public void reset() {
        this.purchased = 0;
        this.cost = baseCost;
    }

    public void setPurchased(int purchased) {
        this.purchased = purchased;
        recalculateCost();
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
    public double getEffectiveness() {
        return effectValue;
    }

    public void setEffectiveness(double newEffectValue) {
        this.effectValue = newEffectValue;
    }
}
