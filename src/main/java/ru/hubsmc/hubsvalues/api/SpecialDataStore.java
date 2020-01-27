package ru.hubsmc.hubsvalues.api;

import org.bukkit.entity.Player;
import ru.hubsmc.hubsvalues.HubsValues;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SpecialDataStore extends DataStore {

    private static final String TABLE_NAME = "hubsval";
    private static final String C_PLAYER = "player";
    private static final String C_DOLLARS = "dollars";
    private static final String C_HUBIXES = "hubixes";
    private static final String C_MANA = "mana";
    private static final String C_MAX = "max";
    private static final String C_REGEN = "regen";

    private static Map<Player, Integer> manaMap;
    private static Map<Player, Integer> maxManaMap;
    private static Map<Player, Integer> regenManaMap;
    private static Map<Player, Integer> dollarMap;

    public SpecialDataStore() {
        super(TABLE_NAME, C_PLAYER, C_DOLLARS, C_HUBIXES, C_MANA, C_MAX, C_REGEN);
        manaMap = new HashMap<>();
        maxManaMap = new HashMap<>();
        regenManaMap = new HashMap<>();
        dollarMap = new HashMap<>();
    }

    public void prepareToWork(String url, String user, String password) {
        super.prepareToWork(url, user, password, new String[] {"null"}, new int[] {0, 0, HubsValues.START_MANA, HubsValues.START_MANA, HubsValues.START_REGEN}, new double[0]);
    }

    static boolean isPlayerOnline(Player player) {
        return dollarMap.containsKey(player);
    }

    static int getManaFromMap(Player player) {
        return manaMap.get(player);
    }

    static int getMaxManaFromMap(Player player) {
        return maxManaMap.get(player);
    }

    static int getRegenManaFromMap(Player player) {
        return regenManaMap.get(player);
    }

    static int getDollarsFromMap(Player player) {
        return dollarMap.get(player);
    }

    static void setManaToMap(Player player, int amount) {
        manaMap.put(player, amount);
    }

    static void setMaxManaToMap(Player player, int amount) {
        maxManaMap.put(player, amount);
    }

    static void setRegenManaToMap(Player player, int amount) {
        regenManaMap.put(player, amount);
    }

    static void setDollarsToMap(Player player, int amount) {
        dollarMap.put(player, amount);
    }


    static int loadValue(String UUID, String valueType) {
        return selectIntValue(UUID, valueType);
    }

    static void saveAllMapValues(String UUID, int manaAmount, int maxAmount, int regenAmount, int dollarsAmount) {
        update(UUID, C_MANA, manaAmount);
        update(UUID, C_MAX, maxAmount);
        update(UUID, C_REGEN, regenAmount);
        update(UUID, C_DOLLARS, dollarsAmount);
    }

    static void increaseManaForAll() {
        updateIncreaseAll();
    }

    static boolean checkDataExist(String UUID) {
        return selectExist(UUID);
    }

    static void createAccount(String UUID, String playerName) {
        createAccount(UUID);
        update(UUID, C_PLAYER, playerName);
    }

    private static void updateIncreaseAll() {
        try {
            ResultSet rs = manager.Request("SELECT " + C_UUID + ", " + C_MANA + ", " + C_REGEN + " FROM " + TABLE_NAME);
            while (rs.next()) {
                update(rs.getString(C_UUID), C_MANA, rs.getInt(C_MANA) + rs.getInt(C_REGEN));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
