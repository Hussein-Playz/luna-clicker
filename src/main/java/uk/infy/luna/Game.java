package uk.infy.luna;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.intellijthemes.*;

public class Game {
    private JFrame frame;
    private JLabel currencyLabel;
    private double currency;
    private double clickValue;
    private double prestigeMultiplier = 1;
    private double prestigeCost = 1_000_000;
    private int prestigeCount = 0;
    private double prestigePoints = 0;
    private String currentTheme = "Light (Default)";

    public int rebirthCount = 0;
    private JButton rebirthButton;

    private boolean autoPrestigeEnabled = false;
    private boolean autoPrestigePurchased = false;

    public long lastTimeTravelUse = 0; // stores last usage timestamp
    private final long timeTravelCooldown = 10 * 60 * 1000; // 10 min cooldown in ms

    private boolean autoBuyBestUpgradeUnlocked = false;
    private boolean autoBuyBestGeneratorUnlocked = false;
    private boolean autoBuyBestUpgradeEnabled = true;
    private boolean autoBuyBestGeneratorEnabled = true;


    private List<Upgrade> upgrades = new ArrayList<>();
    private List<AutoGenerator> autoGenerators = new ArrayList<>();
    private List<Achievement> achievements = new ArrayList<>();
    private Achievement firstClick = new Achievement(
            "First Click",
            "You clicked the button for the first time!",
            "Click for the first time! (Welcome)",
            "clickValue", 0.10 // +10% click value
    );

    private Achievement rich = new Achievement(
            "Data Millionaire",
            "You reached 1,000,000 Data Points!",
            "Reach 1,000,000 Data Points.",
            "prestigePoints", 0
    );

    private Achievement powerfulClicker = new Achievement(
            "Power Clicker",
            "Your click value reached 100!",
            "Make your click value reach 100!",
            "production", 0.01 // +1% production
    );

    private Achievement mightyClicker = new Achievement(
            "Mighty Clicker",
            "Your click value reached 1,000!",
            "Make your click value reach 1,000!",
            "production", 0.02 // +2% production
    );

    private Achievement grinderClicker = new Achievement(
            "Grinding Clicker",
            "Your click value reached 10,000!",
            "Make your click value reach 10,000!",
            "production", 0.05 // +5% production
    );

    private Achievement insaneClicker = new Achievement(
            "Insane Clicker",
            "Your click value reached 100,000!",
            "Make your click value reach 100,000!",
            "clickValue", 0.20 // +20% click value
    );

    private Achievement godlyClicker = new Achievement(
            "Godly Clicker",
            "Your click value reached 1,000,000!",
            "Make your click value reach 1,000,000!",
            "clickValue", 0.50 // +50% click value
    );

    private Achievement firstPrestige = new Achievement(
            "First Prestige",
            "You've prestiged for the first time!",
            "Prestige for the first time.",
            "prestigePoints", 2
    );
    private Map<Upgrade, JButton> upgradeButtons = new HashMap<>();
    private Map<AutoGenerator, JButton> generatorButtons = new HashMap<>();
    private JButton prestigeButton;
    public double baseCost = 10;

    public List<Upgrade> getUpgrades() { return upgrades; }
    public List<AutoGenerator> getAutoGenerators() { return autoGenerators; }

    public double getPrestigePoints() { return prestigePoints; }
    public void setPrestigePoints(double prestigePoints) { this.prestigePoints = prestigePoints; }
    public void addPrestigePoints(double amt) { prestigePoints += amt; updateCurrencyLabel(); }

    public void setCurrency(double currency) { this.currency = currency; updateCurrencyLabel(); }
    public void setClickValue(double clickValue) { this.clickValue = clickValue; }

    public double getPrestigeMultiplier() { return prestigeMultiplier; }
    public void setPrestigeMultiplier(double prestigeMultiplier) { this.prestigeMultiplier = prestigeMultiplier; }

    public int getPrestigeCount() { return prestigeCount; }
    public void setPrestigeCount(int prestigeCount) { this.prestigeCount = prestigeCount; }

    public double getPrestigeCost() { return prestigeCost; }
    public void setPrestigeCost(double prestigeCost) { this.prestigeCost = prestigeCost;}

    public String getTheme() { return currentTheme; }
    public void setTheme(String currentTheme) { try {switch (currentTheme) {case "Light (Default)": UIManager.setLookAndFeel(new FlatLightLaf());break; case "Dark": UIManager.setLookAndFeel(new FlatDarkLaf());break; case "Arc Dark": UIManager.setLookAndFeel(new FlatArcDarkIJTheme());break; case "Solarized Light": UIManager.setLookAndFeel(new FlatSolarizedLightIJTheme());break; case "Solarized Dark": UIManager.setLookAndFeel(new FlatSolarizedDarkIJTheme());break; default: UIManager.setLookAndFeel(new FlatLightLaf());}this.currentTheme = currentTheme;SwingUtilities.updateComponentTreeUI(frame);} catch (Exception ex) {ex.printStackTrace();JOptionPane.showMessageDialog(frame, "Failed to apply theme: " + currentTheme);}}

    public double getRebirthCount() { return rebirthCount; }
    public void setRebirthCount(int rebirthCount) { this.rebirthCount = rebirthCount; }

    public double getRebirthCost() {return baseCost * Math.pow(1.10, rebirthCount);}
    public void setRebirthCost(double baseCost) { this.baseCost = baseCost; }

    public boolean isAutoPrestigeEnabled() { return autoPrestigeEnabled; }
    public void setAutoPrestigeEnabled(boolean enabled) { autoPrestigeEnabled = enabled; }

    public boolean isAutoPrestigePurchased() { return autoPrestigePurchased; }
    public void setAutoPrestigePurchased(boolean purchased) { autoPrestigePurchased = purchased; }

    public long getLastTimeTravelUse() { return lastTimeTravelUse; }
    public void setLastTimeTravelUse(long lastUse) { this.lastTimeTravelUse = lastUse; }

    public boolean isAutoBuyBestUpgradeUnlocked() { return autoBuyBestUpgradeUnlocked; }
    public void setAutoBuyBestUpgradeUnlocked(boolean unlocked) { this.autoBuyBestUpgradeUnlocked = unlocked; }

    public boolean isAutoBuyBestGeneratorUnlocked() { return autoBuyBestGeneratorUnlocked; }
    public void setAutoBuyBestGeneratorUnlocked(boolean unlocked) { this.autoBuyBestGeneratorUnlocked = unlocked; }

    public boolean isAutoBuyBestUpgradeEnabled() { return autoBuyBestUpgradeEnabled; }
    public void setAutoBuyBestUpgradeEnabled(boolean enabled) { this.autoBuyBestUpgradeEnabled = enabled; }

    public boolean isAutoBuyBestGeneratorEnabled() { return autoBuyBestGeneratorEnabled; }
    public void setAutoBuyBestGeneratorEnabled(boolean enabled) { this.autoBuyBestGeneratorEnabled = enabled; }

    public void init() {
        currency = 0;
        clickValue = 1;
        achievements.add(firstClick);
        achievements.add(rich);
        achievements.add(firstPrestige);
        achievements.add(powerfulClicker);
        achievements.add(mightyClicker);
        achievements.add(grinderClicker);
        achievements.add(insaneClicker);
        achievements.add(godlyClicker);

        frame = new JFrame("Luna Clicker");
        frame.setLayout(new BorderLayout());

        currencyLabel = new JLabel("Data Points: 0 | Prestige Points: 0");
        currencyLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        currencyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(currencyLabel, BorderLayout.NORTH);

        ClickerButton clickerButton = new ClickerButton(this);
        frame.add(clickerButton.getButton(), BorderLayout.CENTER);

        // Auto-generators panel (LEFT)
        JPanel generatorPanel = new JPanel();
        generatorPanel.setLayout(new BoxLayout(generatorPanel, BoxLayout.Y_AXIS));
        generatorPanel.setBorder(BorderFactory.createTitledBorder("Auto-Generators"));
        JLabel generatorMultiplier = new JLabel("Auto-Generator Multipliers:");

        AutoGenerator botFarm = new AutoGenerator("Bot Farm", 50, 1);
        AutoGenerator aiCluster = new AutoGenerator("AI Cluster", 500, 10);
        AutoGenerator dataCenter = new AutoGenerator("Data Center", 5000, 50);
        AutoGenerator quantumLab = new AutoGenerator("Quantum Lab", 25000, 200);
        AutoGenerator lunarServer = new AutoGenerator("Lunar Server Array", 100000, 1000);
        

        autoGenerators.add(botFarm);
        autoGenerators.add(aiCluster);
        autoGenerators.add(dataCenter);
        autoGenerators.add(quantumLab);
        autoGenerators.add(lunarServer);

        for (AutoGenerator ag : autoGenerators) {
            JButton agButton = new JButton(ag.getName() + " ($" + formatNumber(ag.getCost()) + ")");
            agButton.addActionListener(e -> {
                ag.purchase(this);
                agButton.setText(ag.getName() + " ($" + formatNumber(ag.getCost()) + ")");
            });
            generatorPanel.add(agButton);
            generatorButtons.put(ag, agButton);
        }
        JButton timeTravelButton = new JButton("Time Travel (cost 2 Prestige Point)");
        timeTravelButton.addActionListener( e -> {
            if (prestigePoints < 2) {
                JOptionPane.showMessageDialog(frame, "You need atleast 2 prestige points to purchase this");
            } else if (prestigePoints >= 2) {
                subtractPrestigePoints(2);
                performTimeTravel(30);
            }
        });
        generatorPanel.add(timeTravelButton);
        frame.add(new JScrollPane(generatorPanel), BorderLayout.WEST);

        // Upgrades panel (RIGHT)
        JPanel upgradePanel = new JPanel();
        upgradePanel.setLayout(new BoxLayout(upgradePanel, BoxLayout.Y_AXIS));
        upgradePanel.setBorder(BorderFactory.createTitledBorder("Upgrades"));

        Upgrade cpuUpgrade = new Upgrade("CPU Upgrade", 10, 1);
        cpuUpgrade.setEffect("clickMultiplier", 1.5);

        Upgrade gpuUpgrade = new Upgrade("GPU Overclock", 100, 5);
        gpuUpgrade.setEffect("generatorMultiplier", 1.5);

        Upgrade quantumUpgrade = new Upgrade("Quantum Processor", 1000, 20);
        quantumUpgrade.setEffect("clickMultiplier", 2.0);

        Upgrade superClusterUpgrade = new Upgrade("Super Cluster", 100000, 1000);
        superClusterUpgrade.setEffect("clickMultiplier", 3.0);

        upgrades.add(cpuUpgrade);
        upgrades.add(gpuUpgrade);
        upgrades.add(quantumUpgrade);
        upgrades.add(superClusterUpgrade);

        for (Upgrade upg : upgrades) {
            JButton upgradeButton = new JButton(upg.getName() + " ($" + formatNumber(upg.getCost()) + ")");
            upgradeButton.addActionListener(e -> {
                if (currency >= upg.getCost()) {
                    upg.purchase(this);
                    updateCurrencyLabel();
                    upgradeButton.setText(upg.getName() + " ($" + formatNumber(upg.getCost()) + ")");
                }
            });
            upgradePanel.add(upgradeButton);
            upgradeButtons.put(upg, upgradeButton);
        }
        frame.add(new JScrollPane(upgradePanel), BorderLayout.EAST);

        // Prestige button (BOTTOM)
        prestigeButton = new JButton(getPrestigeButtonText());
        prestigeButton.addActionListener(e -> {
            if (currency >= prestigeCost) {
                prestige();
            } else {
                JOptionPane.showMessageDialog(frame, "You need at least " + formatNumber(prestigeCost) + " Data Points to prestige.");
            }
        });

        // Prestige Shop button
        JButton prestigeShopButton = new JButton("Prestige Shop");
        prestigeShopButton.addActionListener(e -> showPrestigeShop());

        JButton rebirthButton = new JButton(getRebirthButtonText());
        rebirthButton.addActionListener(e -> {
            if (prestigeCount < getRebirthCost() || currency < (prestigeCost * 2)) {
                JOptionPane.showMessageDialog(frame, "You need at least Prestige level " + getRebirthCost() +
                        " and " + formatNumber(prestigeCost * 2) + " Data Points to Rebirth.");
                return;
            } else {
                prestigeCount -= getRebirthCost();
                performRebirth();
                updateCurrencyLabel();
                rebirthButton.setText(getRebirthButtonText());
            }
        });

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(prestigeButton, BorderLayout.CENTER);
        southPanel.add(prestigeShopButton, BorderLayout.EAST);
        southPanel.add(rebirthButton, BorderLayout.WEST);
        frame.add(southPanel, BorderLayout.SOUTH);

        // Achievements Panel button (top-right)
        JButton achievementsButton = new JButton("Achievements");
        achievementsButton.addActionListener(e -> showAchievementsPanel());

        JButton settingsButton = new JButton("Settings");
        settingsButton.addActionListener(e -> showSettings());

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(currencyLabel, BorderLayout.CENTER);
        northPanel.add(achievementsButton, BorderLayout.EAST);
        northPanel.add(settingsButton, BorderLayout.WEST);
        frame.add(northPanel, BorderLayout.NORTH);

        GameSaveManager.load(this);

        // Offline progress reward
        awardOfflineProgress();

        Timer timer = new Timer(1000, e -> {
            for (AutoGenerator ag : autoGenerators) {
                currency += ag.getIncomePerSecond() * ag.getPurchased() * prestigeMultiplier;
            }
            if (autoBuyBestUpgradeUnlocked && autoBuyBestUpgradeEnabled) {
                autoBuyBestUpgrade();
            }

            if (autoBuyBestGeneratorUnlocked && autoBuyBestGeneratorEnabled) {
                autoBuyBestGenerator();
            }

            updateCurrencyLabel();
        });
        timer.start();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1024, 768));
        frame.setIconImage(new ImageIcon(Objects.requireNonNull(Main.class.getResource("/buttonlogo.png"))).getImage());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                GameSaveManager.save(Game.this);
            }
        });

        Timer randomEventTimer = new Timer(30 * 60 * 1000, e -> triggerRandomEvent());
        randomEventTimer.start();

        // Dev Panel, Ctrl + D
        frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK),
                "openDevConsole"
        );

        frame.getRootPane().getActionMap().put("openDevConsole", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String cmd = JOptionPane.showInputDialog(frame, "Enter special code:");
                if (cmd != null) {
                    if (cmd.startsWith("add ")) {
                        try {
                            double amt = Double.parseDouble(cmd.split(" ")[1]);
                            addCurrency(amt);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, "Invalid amount.");
                        }
                    } else if (cmd.equalsIgnoreCase("prestige")) {
                        setCurrency(prestigeCost);
                        rich.unlock();
                        firstClick.unlock();
                        prestige();
                    } else if (cmd.equalsIgnoreCase("rebirth")) {
                        setPrestigeCount(1000);
                        setCurrency(prestigeCost*2);
                        performRebirth();
                        updateCurrencyLabel();
                        rebirthButton.setText(getRebirthButtonText());
                    } else if (cmd.startsWith("addpp")) {
                        try {
                            double amt = Double.parseDouble(cmd.split(" ")[1]);
                            addPrestigePoints(amt);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(frame, "invalid amount.");
                        }
                    }
                }
            }
        });

    }

    // Award offline currency based on time since last save
    private void awardOfflineProgress() {
        long now = System.currentTimeMillis();
        long lastSave = GameSaveManager.getLastSaveTime();
        if (lastSave == 0) return; // No previous save

        long diffSeconds = (now - lastSave) / 1000;
        if (diffSeconds <= 0) return;

        double offlineIncome = 0;
        for (AutoGenerator ag : autoGenerators) {
            offlineIncome += ag.getIncomePerSecond() * ag.getPurchased() * prestigeMultiplier;
        }
        offlineIncome *= diffSeconds;

        if (offlineIncome > 0) {
            addCurrency(offlineIncome);
            JOptionPane.showMessageDialog(frame, "Welcome back! You earned " + formatNumber(offlineIncome) + " Data Points while away (" + diffSeconds + " seconds).");
        }
    }

    public void addCurrency(double amount) {
        currency += amount * prestigeMultiplier;
        updateCurrencyLabel();
        checkAchievements();
    }

    public void increaseClickValue(double multiplier) {
        clickValue += multiplier;
    }

    public double getClickValue() {
        return clickValue;
    }

    public double getCurrency() {
        return currency;
    }

    public void subtractCurrency(double amount) {
        currency -= amount;
        updateCurrencyLabel();
    }
    public void subtractPrestigePoints(double amount) {
        prestigePoints -= amount;
        if (prestigePoints < 0) prestigePoints = 0;
        updateCurrencyLabel();
    }


    public void updateCurrencyLabel() {
        currencyLabel.setText("Data Points: " + formatNumber(currency) + " | Prestige Points: " + formatNumber(prestigePoints));
        if (prestigeButton != null) {
            prestigeButton.setText(getPrestigeButtonText());
        }
        if (rebirthButton != null) {
            rebirthButton.setText(getRebirthButtonText());
        }
        if (autoPrestigeEnabled == true)
            if (currency >= prestigeCost) {
                prestige();
            }
    }



    public void checkAchievements() {
        if (!firstClick.isUnlocked() && currency >= 1) {
            firstClick.unlock();
            grantAchievementReward(firstClick);
            JOptionPane.showMessageDialog(frame, "Achievement Unlocked: " + firstClick.getName());
        }
        if (!rich.isUnlocked() && currency >= 1_000_000) {
            rich.unlock();
            JOptionPane.showMessageDialog(frame, "Achievement Unlocked: " + rich.getName());
        }
        if (!powerfulClicker.isUnlocked() && clickValue >= 100) {
            powerfulClicker.unlock();
            grantAchievementReward(powerfulClicker);
            JOptionPane.showMessageDialog(frame, "Achievement Unlocked: " + powerfulClicker.getName());
        }
        if (!mightyClicker.isUnlocked() && clickValue >= 1000) {
            mightyClicker.unlock();
            grantAchievementReward(mightyClicker);
            JOptionPane.showMessageDialog(frame, "Achievement Unlocked: " + mightyClicker.getName());
        }
        if (!grinderClicker.isUnlocked() && clickValue >= 10000) {
            grinderClicker.unlock();
            grantAchievementReward(grinderClicker);
            JOptionPane.showMessageDialog(frame, "Achievement Unlocked: " + grinderClicker.getName());
        }
        if (!insaneClicker.isUnlocked() && clickValue >= 100000) {
            insaneClicker.unlock();
            grantAchievementReward(insaneClicker);
            JOptionPane.showMessageDialog(frame, "Achievement Unlocked: " + insaneClicker.getName());
        }
        if (!godlyClicker.isUnlocked() && clickValue >= 1000000) {
            godlyClicker.unlock();
            grantAchievementReward(godlyClicker);
            JOptionPane.showMessageDialog(frame, "Achievement Unlocked: " + godlyClicker.getName());
        }
    }

    public void grantAchievementReward(Achievement ach) {
        switch (ach.getRewardType()) {
            case "production":
                for (AutoGenerator ag : autoGenerators) {
                    ag.setIncomePerSecond(ag.getIncomePerSecond() * (1 + ach.getRewardValue()));
                }
                break;
            case "prestigePoints":
                prestigePoints += ach.getRewardValue();
                break;
            case "clickValue":
                clickValue *= (1 + ach.getRewardValue());
                break;
        }
    }

    public void prestige() {
        if (currency < prestigeCost) {
            JOptionPane.showMessageDialog(frame, "Not enough currency to prestige.");
            return;
        }
        currency = 0;
        prestigeCount++;
        clickValue = prestigeCount/prestigeCount;
        double earnedPoints = 1;
        addPrestigePoints(earnedPoints);

        for (Upgrade upg : upgrades) {
            upg.reset();
            JButton button = upgradeButtons.get(upg);
            if (button != null) {
                button.setText(upg.getName() + " ($" + formatNumber(upg.getCost()) + ")");
            }
        }

        for (AutoGenerator ag : autoGenerators) {
            ag.reset();
            JButton button = generatorButtons.get(ag);
            if (button != null) {
                button.setText(ag.getName() + " ($" + formatNumber(ag.getCost()) + ")");
            }
        }

        prestigeMultiplier *= 2;
        prestigeCost *= 10;

        updateCurrencyLabel();

        // Bonus milestone every 10 prestiges: +50% production permanently
        if (prestigeCount % 10 == 0) {
            prestigeMultiplier *= 1.5;
            JOptionPane.showMessageDialog(frame, "Milestone reached! Production permanently boosted by 50%!");
        }

        JOptionPane.showMessageDialog(frame, "You prestiged! Prestige level: " + prestigeCount +
                ". Click power is now " + formatNumber(Math.pow(2, prestigeCount)) + " and production x" + prestigeMultiplier +
                ". You earned " + formatNumber(earnedPoints) + " prestige points.");
        if (prestigeCount == 1) {
            firstPrestige.unlock();
            grantAchievementReward(firstPrestige);
            JOptionPane.showMessageDialog(frame, "Achievement Unlocked: " + firstPrestige.getName());
        }
    }
    private void timeTravel(int seconds) {
        for (AutoGenerator ag : autoGenerators) {
            currency += ag.getIncomePerSecond() * ag.getPurchased() * prestigeMultiplier * seconds;
        }
        updateCurrencyLabel();
        JOptionPane.showMessageDialog(frame, "You time travelled " + seconds + " seconds ahead!");
    }

    private void toggleAutoPrestige() {
        autoPrestigeEnabled = !autoPrestigeEnabled;
        JOptionPane.showMessageDialog(frame, "Auto-Prestige is now " + (autoPrestigeEnabled ? "Enabled" : "Disabled"));
    }

    private String formatNumber(double value) {
        return String.format("%,.2f", value);
    }

    private String getPrestigeButtonText() {
        return "Prestige (Requires " + formatNumber(prestigeCost) + " Data Points) | Level: " + prestigeCount;
    }

    private String getRebirthButtonText() {
        return "Rebirth (Requires " + formatNumber(getRebirthCost()) + " Prestige and " + formatNumber(prestigeCost*2) + " Data points)| Level: " + rebirthCount;
    }

    public JButton getUpgradeButton(Upgrade upg) {
        return upgradeButtons.get(upg);
    }

    public JButton getGeneratorButton(AutoGenerator ag) {
        return generatorButtons.get(ag);
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    // Achievement display panel
    public void showAchievementsPanel() {
        JFrame achFrame = new JFrame("Achievements");
        achFrame.setSize(400, 600);
        achFrame.setLayout(new GridLayout(0, 1));

        for (Achievement a : achievements) {
            String status = a.isUnlocked() ? "Unlocked" : "Locked";
            String hwunlock = a.getHwunlock();
            JLabel label = new JLabel(a.getName() + " - " + status + " - " + hwunlock);
            achFrame.add(label);
        }

        achFrame.setVisible(true);
    }

    // Prestige Shop GUI
    public void showPrestigeShop() {
        JFrame shopFrame = new JFrame("Prestige Shop");
        shopFrame.setSize(400, 200);
        shopFrame.setLayout(new GridLayout(0, 1));

        JButton prodBoost = new JButton("+1% production (cost 1 prestige point)");
        prodBoost.addActionListener(e -> {
            if (prestigePoints >= 1) {
                prestigePoints -= 1;
                prestigeMultiplier *= 1.01;
                updateCurrencyLabel();
                JOptionPane.showMessageDialog(shopFrame, "Production increased by +1%");
            } else {
                JOptionPane.showMessageDialog(shopFrame, "Not enough prestige points.");
            }
        });
        shopFrame.add(prodBoost);

        JButton clickBoost = new JButton("+10% click power (cost 1 prestige point)");
        clickBoost.addActionListener(e -> {
            if (prestigePoints >= 1) {
                prestigePoints -= 1;
                clickValue *= 1.10;
                updateCurrencyLabel();
                JOptionPane.showMessageDialog(shopFrame, "Click power increased by +10%");
            } else {
                JOptionPane.showMessageDialog(shopFrame, "Not enough prestige points.");
            }
        });
        shopFrame.add(clickBoost);
        JButton autoBuyUpgrades = new JButton(autoBuyBestUpgradeUnlocked ? "Auto-Buy Best Upgrade (Bought)" : "Unlock Auto-Buy Best Upgrade (cost 5 prestige points)");
        autoBuyUpgrades.setEnabled(!autoBuyBestUpgradeUnlocked);
        autoBuyUpgrades.addActionListener(e -> {
            if (prestigePoints >= 5) {
                prestigePoints -= 5;
                autoBuyBestUpgradeUnlocked = true;
                autoBuyUpgrades.setText("Auto-Buy Best Upgrade (Bought)");
                autoBuyUpgrades.setEnabled(false);
                JOptionPane.showMessageDialog(shopFrame, "Auto-buy best upgrade unlocked!");
            } else {
                JOptionPane.showMessageDialog(shopFrame, "Not enough prestige points.");
            }
        });
        shopFrame.add(autoBuyUpgrades);

        JButton autoBuyGenerators = new JButton(autoBuyBestGeneratorUnlocked ? "Auto-Buy Best Generator (Bought)" : "Unlock Auto-Buy Best Generator (cost 5 prestige points)");
        autoBuyGenerators.setEnabled(!autoBuyBestGeneratorUnlocked);
        autoBuyGenerators.addActionListener(e -> {
            if (prestigePoints >= 5) {
                prestigePoints -= 5;
                autoBuyBestGeneratorUnlocked = true;
                autoBuyGenerators.setText("Auto-Buy Best Generator (Bought)");
                autoBuyGenerators.setEnabled(false);
                JOptionPane.showMessageDialog(shopFrame, "Auto-buy best generator unlocked!");
            } else {
                JOptionPane.showMessageDialog(shopFrame, "Not enough prestige points.");
            }
        });
        shopFrame.add(autoBuyGenerators);
        shopFrame.setVisible(true);
    }

    public void showSettings() {
        JFrame sFrame = new JFrame("Settings");
        sFrame.setSize(400, 600);
        sFrame.setLayout(new GridLayout(0, 1));
        JButton themeButton = new JButton("Select Theme");
        themeButton.addActionListener(e -> {
            // List of themes
            UIManager.LookAndFeelInfo[] themes = {
                    new UIManager.LookAndFeelInfo("Light (Default)", FlatLightLaf.class.getName()),
                    new UIManager.LookAndFeelInfo("Dark", FlatDarkLaf.class.getName()),
                    new UIManager.LookAndFeelInfo("Arc Dark", FlatArcDarkIJTheme.class.getName()),
                    new UIManager.LookAndFeelInfo("Solarized Light", FlatSolarizedLightIJTheme.class.getName()),
                    new UIManager.LookAndFeelInfo("Solarized Dark", FlatSolarizedDarkIJTheme.class.getName()),
            };

            // Show options in dialog
            String[] themeNames = new String[themes.length];
            for (int i = 0; i < themes.length; i++) {
                themeNames[i] = themes[i].getName();
            }

            String selected = (String) JOptionPane.showInputDialog(
                    sFrame,
                    "Select a theme:",
                    "Theme Selector",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    themeNames,
                    themeNames[0]);

            if (selected != null) {
                try {
                    for (UIManager.LookAndFeelInfo info : themes) {
                        if (info.getName().equals(selected)) {
                            UIManager.setLookAndFeel(info.getClassName());
                            SwingUtilities.updateComponentTreeUI(frame);
                            SwingUtilities.updateComponentTreeUI(sFrame);
                            currentTheme = selected;
                            break;
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(sFrame, "Failed to apply theme.");
                }
            }
        });
        JButton resetButton = new JButton("Reset Progress?");
        resetButton.addActionListener(e -> {
            currency = 0;
            prestigeMultiplier = 1;
            prestigeCost = 1_000_000;
            prestigeCount = 0;
            prestigePoints = 0;
            rebirthCount = 0;
            setRebirthCost(0);
            for (Upgrade upg : upgrades) {
                upg.reset();
                JButton button = upgradeButtons.get(upg);
                if (button != null) {
                    button.setText(upg.getName() + " ($" + formatNumber(upg.getCost()) + ")");
                }
            }

            for (AutoGenerator ag : autoGenerators) {
                ag.reset();
                JButton button = generatorButtons.get(ag);
                if (button != null) {
                    button.setText(ag.getName() + " ($" + formatNumber(ag.getCost()) + ")");
                }
            }
            clickValue = 1;
            for (Achievement ach : getAchievements()) {
                ach.remove();
            }
            JOptionPane.showMessageDialog(frame, "Reset Complete. But why?");
        });
        JButton autoPrestigeButton = new JButton("Purchase Auto-Prestige?");
        autoPrestigeButton.setVisible(false);
        autoPrestigeButton.addActionListener(e -> {
            if (!autoPrestigePurchased) {
                if (prestigePoints >= 5) {
                    subtractPrestigePoints(5);
                    autoPrestigePurchased = true;
                    JOptionPane.showMessageDialog(frame, "Auto-Prestige purchased! You can now enable or disable it.");
                    autoPrestigeButton.setText("Auto-Prestige: OFF");
                } else {
                    JOptionPane.showMessageDialog(frame, "You need 5 prestige points to buy Auto-Prestige.");
                }
            } else {
                toggleAutoPrestige();
                autoPrestigeButton.setText("Auto-Prestige: " + (autoPrestigeEnabled ? "ON" : "OFF"));
            }
        });
        if (rebirthCount >= 2) {
            autoPrestigeButton.setVisible(true);
            if (autoPrestigePurchased) {
                autoPrestigeButton.setText("Auto-Prestige: " + (autoPrestigeEnabled ? "ON" : "OFF"));
            }
        }
        JCheckBox toggleAutoBuyUpgrades = new JCheckBox("Auto-Buy Best Upgrade Enabled", autoBuyBestUpgradeEnabled);
        toggleAutoBuyUpgrades.addActionListener(e -> {
            autoBuyBestUpgradeEnabled = toggleAutoBuyUpgrades.isSelected();
            JOptionPane.showMessageDialog(frame, "Auto-Buy Best Upgrade is now " + (autoBuyBestUpgradeEnabled ? "Enabled" : "Disabled"));
        });

        JCheckBox toggleAutoBuyGenerators = new JCheckBox("Auto-Buy Best Generator Enabled", autoBuyBestGeneratorEnabled);
        toggleAutoBuyGenerators.addActionListener(e -> {
            autoBuyBestGeneratorEnabled = toggleAutoBuyGenerators.isSelected();
            JOptionPane.showMessageDialog(frame, "Auto-Buy Best Generator is now " + (autoBuyBestGeneratorEnabled ? "Enabled" : "Disabled"));
        });
        if (autoBuyBestUpgradeUnlocked) {
            sFrame.add(toggleAutoBuyUpgrades);
        }
        if (autoBuyBestGeneratorUnlocked) {
            sFrame.add(toggleAutoBuyGenerators);
        }

        sFrame.add(themeButton);
        sFrame.add(resetButton);
        sFrame.add(autoPrestigeButton);
        sFrame.setVisible(true);
    }

    private void triggerRandomEvent() {
        String[] events = {"Golden Click", "Generators x2", "Free Upgrade"};
        String event = events[new Random().nextInt(events.length)];

        switch (event) {
            case "Golden Click":
                double prestigeBonus = Math.max(1, prestigeCount * 0.10);
                double goldenReward = (1000 + new Random().nextInt(1_000_000 - 1000)) * prestigeBonus;
                addCurrency(goldenReward);
                JOptionPane.showMessageDialog(frame, "Golden Click Event! You gained " + formatNumber(goldenReward) + " Data Points.");
                break;

            case "Generators x2":
                for (AutoGenerator ag : autoGenerators) {
                    ag.setIncomePerSecond(ag.getIncomePerSecond() * 2);
                }
                JOptionPane.showMessageDialog(frame, "Generators x2 Event! All generators produce double for 60 seconds.");

                // Schedule to revert after 60s
                new Timer(60 * 1000, e -> {
                    for (AutoGenerator ag : autoGenerators) {
                        ag.setIncomePerSecond(ag.getIncomePerSecond() / 2);
                    }
                    JOptionPane.showMessageDialog(frame, "Generators x2 has ended. Production returns to normal.");
                }).start();
                break;

            case "Free Upgrade":
                if (!upgrades.isEmpty()) {
                    Upgrade randomUpgrade = upgrades.get(new Random().nextInt(upgrades.size()));
                    randomUpgrade.purchase(this); // grant upgrade for free
                    JButton button = upgradeButtons.get(randomUpgrade);
                    if (button != null) {
                        button.setText(randomUpgrade.getName() + " ($" + formatNumber(randomUpgrade.getCost()) + ")");
                    }
                    JOptionPane.showMessageDialog(frame, "Free Upgrade Event! You gained: " + randomUpgrade.getName());
                }
                break;
        }
    }
    private void performRebirth() {
        if (prestigeCount < getRebirthCost() || currency < (prestigeCost * 2)) {
            JOptionPane.showMessageDialog(frame, "You need at least Prestige level " + getRebirthCost() +
                    " and " + formatNumber(prestigeCost * 2) + " Data Points to Rebirth.");
            return;
        }

        rebirthCount++;

        // Reset everything
        currency = 0;
        prestigePoints = 0;
        prestigeCount = 0;
        prestigeCost = 1_000_000;
        prestigeMultiplier = 1;
        clickValue = 1;

        // Reset upgrades and auto-generators
        for (Upgrade upg : upgrades) {
            upg.reset();
            upg.setEffectiveness(upg.getEffectiveness() * (1 + 0.15 * rebirthCount)); // +15% effective per rebirth
            JButton button = upgradeButtons.get(upg);
            if (button != null) {
                button.setText(upg.getName() + " ($" + formatNumber(upg.getCost()) + ")");
            }
        }

        for (AutoGenerator ag : autoGenerators) {
            ag.reset();
            ag.setIncomePerSecond(ag.getIncomePerSecond() * Math.pow(3, rebirthCount)); // x3 per rebirth
            JButton button = generatorButtons.get(ag);
            if (button != null) {
                button.setText(ag.getName() + " ($" + formatNumber(ag.getCost()) + ")");
            }
        }

        // Reset theme to default
        setTheme("Light (Default)");

        // Remove all achievements
        for (Achievement ach : getAchievements()) {
            ach.remove();
        }

        JOptionPane.showMessageDialog(frame, "You have REBIRTHED!\nRebirth count: " + rebirthCount + "\nAuto-generators x" + Math.pow(3, rebirthCount) + "\nUpgrades +"
                + (15 * rebirthCount) + "% effective.\nEverything has been reset for a fresh universe.");
        updateCurrencyLabel();
    }
    public ActionListener performTimeTravel(int minutes) {
        long now = System.currentTimeMillis();
        if (now - lastTimeTravelUse < timeTravelCooldown) {
            long remaining = (timeTravelCooldown - (now - lastTimeTravelUse)) / 1000;
            JOptionPane.showMessageDialog(frame, "Time Travel is on cooldown. Please wait " + remaining + " seconds.");
            return null;
        }

        double totalIncome = 0;
        for (AutoGenerator ag : autoGenerators) {
            totalIncome += ag.getIncomePerSecond() * ag.getPurchased() * prestigeMultiplier;
        }

        double reward = totalIncome * 60 * minutes;
        addCurrency(reward);

        lastTimeTravelUse = now;

        JOptionPane.showMessageDialog(frame, "You travelled " + minutes + " minutes into the future!\nGained: " + formatNumber(reward) + " Data Points.");
        return null;
    }
    private void autoBuyBestUpgrade() {
        Upgrade best = null;
        double bestEfficiency = 0;

        for (Upgrade upg : upgrades) {
            double efficiency = upg.getMultiplier() / upg.getCost();
            if (currency >= upg.getCost() && efficiency > bestEfficiency) {
                best = upg;
                bestEfficiency = efficiency;
            }
        }

        if (best != null) {
            best.purchase(this);
            JButton button = upgradeButtons.get(best);
            if (button != null) {
                button.setText(best.getName() + " ($" + formatNumber(best.getCost()) + ")");
            }
        }
    }

    private void autoBuyBestGenerator() {
        AutoGenerator best = null;
        double bestEfficiency = 0;

        for (AutoGenerator ag : autoGenerators) {
            double efficiency = ag.getIncomePerSecond() / ag.getCost();
            if (currency >= ag.getCost() && efficiency > bestEfficiency) {
                best = ag;
                bestEfficiency = efficiency;
            }
        }

        if (best != null) {
            best.purchase(this);
            JButton button = generatorButtons.get(best);
            if (button != null) {
                button.setText(best.getName() + " ($" + formatNumber(best.getCost()) + ")");
            }
        }
    }

}
