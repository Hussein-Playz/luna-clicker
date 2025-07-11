package uk.infy.luna;

public class Achievement {
    private String name;
    private String description;
    private String hwunlock;
    private boolean unlocked;
    private String rewardType; // e.g. "production", "prestigePoints", "clickValue"
    private double rewardValue;


    public Achievement(String name, String description, String hwunlock, String rewardType, double rewardValue) {
        this.name = name;
        this.description = description;
        this.hwunlock = hwunlock;
        this.unlocked = false;
        this.rewardType = rewardType;
        this.rewardValue = rewardValue;
    }

    public void unlock() {
        this.unlocked = true;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    public String getHwunlock() {
        return hwunlock;
    }
    public void remove() {
        this.unlocked = false;
    }
    public String getRewardType() { return rewardType; }
    public double getRewardValue() { return rewardValue; }
}
