package uk.infy.luna;

import javax.swing.*;
import java.io.*;
import java.util.Base64;
import java.util.Properties;

public class GameSaveManager {
    private static final String SAVE_FILE = "luna_clicker_save.dat";
    private static final String ENCRYPTION_KEY = "LunaClickerEncryptionKey";

    public static void save(Game game) {
        Properties props = new Properties();

        props.setProperty("currency", String.valueOf(game.getCurrency()));
        props.setProperty("clickValue", String.valueOf(game.getClickValue()));
        props.setProperty("prestigeMultiplier", String.valueOf(game.getPrestigeMultiplier()));
        props.setProperty("prestigeCount", String.valueOf(game.getPrestigeCount()));
        props.setProperty("prestigePoints", String.valueOf(game.getPrestigePoints()));
        props.setProperty("prestigeCost", String.valueOf(game.getPrestigeCost()));
        props.setProperty("currentTheme", String.valueOf(game.getTheme()));
        props.setProperty("rebirthCount", String.valueOf(game.getRebirthCount()));
        props.setProperty("rebirthCost", String.valueOf(game.getRebirthCost()));
        props.setProperty("autoPrestigeEnabled", String.valueOf(game.isAutoPrestigeEnabled()));
        props.setProperty("autoPrestigePurchased", String.valueOf(game.isAutoPrestigePurchased()));
        props.setProperty("lastTimeTravelUse", String.valueOf(game.getLastTimeTravelUse()));
        props.setProperty("autoBuyBestUpgradeUnlocked", String.valueOf(game.isAutoBuyBestUpgradeUnlocked()));
        props.setProperty("autoBuyBestGeneratorUnlocked", String.valueOf(game.isAutoBuyBestGeneratorUnlocked()));
        props.setProperty("autoBuyBestUpgradeEnabled", String.valueOf(game.isAutoBuyBestUpgradeEnabled()));
        props.setProperty("autoBuyBestGeneratorEnabled", String.valueOf(game.isAutoBuyBestGeneratorEnabled()));

        // Save upgrades purchased count
        for (Upgrade upg : game.getUpgrades()) {
            props.setProperty("upgrade_" + upg.getName() + "_purchased", String.valueOf(upg.getPurchased()));
        }

        // Save auto-generators purchased count
        for (AutoGenerator ag : game.getAutoGenerators()) {
            props.setProperty("generator_" + ag.getName() + "_purchased", String.valueOf(ag.getPurchased()));
        }

        // Save achievements dynamically
        for (Achievement ach : game.getAchievements()) {
            props.setProperty("achievement_" + ach.getName(), String.valueOf(ach.isUnlocked()));
        }

        // Save last save time for offline progress
        props.setProperty("lastSaveTime", String.valueOf(System.currentTimeMillis()));

        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
            props.store(byteOut, "Luna Clicker Save Data");
            byte[] data = byteOut.toByteArray();
            byte[] encrypted = xorEncrypt(data, ENCRYPTION_KEY.getBytes());
            String encoded = Base64.getEncoder().encodeToString(encrypted);

            try (FileWriter writer = new FileWriter(SAVE_FILE)) {
                writer.write(encoded);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load(Game game) {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            System.out.println("No save file found. Starting new game.");
            return;
        }

        try {
            String encoded = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            byte[] encrypted = Base64.getDecoder().decode(encoded);
            byte[] decrypted = xorEncrypt(encrypted, ENCRYPTION_KEY.getBytes());

            Properties props = new Properties();
            props.load(new ByteArrayInputStream(decrypted));

            game.setCurrency(Double.parseDouble(props.getProperty("currency", "0")));
            game.setClickValue(Double.parseDouble(props.getProperty("clickValue", "1")));
            game.setPrestigeMultiplier(Double.parseDouble(props.getProperty("prestigeMultiplier", "1")));
            game.setPrestigeCount(Integer.parseInt(props.getProperty("prestigeCount", "0")));
            game.setPrestigePoints(Double.parseDouble(props.getProperty("prestigePoints", "0")));
            game.setPrestigeCost(Double.parseDouble(props.getProperty("prestigeCost", "1_000_000")));
            game.setRebirthCount((int) Double.parseDouble(props.getProperty("rebirthCount", "0")));
            game.setRebirthCost(Double.parseDouble(props.getProperty("rebirthCost", "10")));
            game.setAutoPrestigeEnabled(Boolean.parseBoolean(props.getProperty("autoPrestigeEnabled", "false")));
            game.setAutoPrestigePurchased(Boolean.parseBoolean(props.getProperty("autoPrestigePurchased", "false")));
            game.setLastTimeTravelUse(Long.parseLong(props.getProperty("lastTimeTravelUse", "0")));
            game.setAutoBuyBestUpgradeUnlocked(Boolean.parseBoolean(props.getProperty("autoBuyBestUpgradeUnlocked", "false")));
            game.setAutoBuyBestGeneratorUnlocked(Boolean.parseBoolean(props.getProperty("autoBuyBestGeneratorUnlocked", "false")));
            game.setAutoBuyBestUpgradeEnabled(Boolean.parseBoolean(props.getProperty("autoBuyBestUpgradeEnabled", "true")));
            game.setAutoBuyBestGeneratorEnabled(Boolean.parseBoolean(props.getProperty("autoBuyBestGeneratorEnabled", "true")));

            // Load upgrades purchased
            for (Upgrade upg : game.getUpgrades()) {
                int purchased = Integer.parseInt(props.getProperty("upgrade_" + upg.getName() + "_purchased", "0"));
                upg.setPurchased(purchased);
                upg.recalculateCost();
            }

            // Load auto-generators purchased
            for (AutoGenerator ag : game.getAutoGenerators()) {
                int purchased = Integer.parseInt(props.getProperty("generator_" + ag.getName() + "_purchased", "0"));
                ag.setPurchased(purchased);
                ag.recalculateCost();
            }

            // Load achievements dynamically
            for (Achievement ach : game.getAchievements()) {
                boolean unlocked = Boolean.parseBoolean(props.getProperty("achievement_" + ach.getName(), "false"));
                if (unlocked) {
                    ach.unlock();
                    game.grantAchievementReward(ach); // reapplies rewards if needed
                }
            }

            // Load and apply theme
            String savedTheme = props.getProperty("currentTheme", "Light (Default)");
            game.setTheme(savedTheme);

            // Refresh GUI
            for (Upgrade upg : game.getUpgrades()) {
                JButton button = game.getUpgradeButton(upg);
                if (button != null) {
                    button.setText(upg.getName() + " ($" + String.format("%,.2f", upg.getCost()) + ")");
                }
            }

            for (AutoGenerator ag : game.getAutoGenerators()) {
                JButton button = game.getGeneratorButton(ag);
                if (button != null) {
                    button.setText(ag.getName() + " ($" + String.format("%,.2f", ag.getCost()) + ")");
                }
            }

            game.updateCurrencyLabel();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static long getLastSaveTime() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return 0;

        try {
            String encoded = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            byte[] encrypted = Base64.getDecoder().decode(encoded);
            byte[] decrypted = xorEncrypt(encrypted, ENCRYPTION_KEY.getBytes());

            Properties props = new Properties();
            props.load(new ByteArrayInputStream(decrypted));

            return Long.parseLong(props.getProperty("lastSaveTime", "0"));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static byte[] xorEncrypt(byte[] data, byte[] key) {
        byte[] output = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            output[i] = (byte) (data[i] ^ key[i % key.length]);
        }
        return output;
    }
}
