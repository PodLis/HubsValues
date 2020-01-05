package ru.hubsmc.hubsvalues.api;

import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DataStore {

    private static final String TABLE_NAME = "hubsval";
    public static final String C_UUID = "uuid";
    public static final String C_PLAYER = "player";
    public static final String C_DOLLARS = "dollars";
    public static final String C_HUBIXES = "hubixes";
    public static final String C_MANA = "mana";
    public static final String C_MAX = "max";
    public static final String C_REGEN = "regen";

    private static Map<Player, Integer> manaMap;
    private static Map<Player, Integer> maxManaMap;
    private static Map<Player, Integer> regenManaMap;
    private static Map<Player, Integer> dollarMap;

    private static DataBase dataBase;
    private static DataBase.Manager manager;

    public DataStore() {
    }

    public void prepareToWork(String url, String user, String password) {
        try {
            manaMap = new HashMap<>();
            maxManaMap = new HashMap<>();
            regenManaMap = new HashMap<>();
            dollarMap = new HashMap<>();

            dataBase = new DataBase(url, user, password);
            manager = dataBase.GetManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnections() {
        manager.Free();
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
        return selectValue(UUID, valueType);
    }

    static void saveValue(String UUID, String valueType, int valueAmount) {
        update(UUID, valueType, valueAmount);
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

    static void createAccount(String UUID, String playerName, int manaAmount, int regenAmount) {
        insert(UUID, playerName, 0, 0, manaAmount, manaAmount, regenAmount);
    }

    private static void insert(String uuid, String player, int dollars, int hubixes, int mana, int max, int regen) {
        manager.Execute("insert into " + TABLE_NAME + "(" +
                C_UUID + ", " + C_PLAYER + ", " +
                C_DOLLARS + ", " + C_HUBIXES + ", " + C_MANA + ", " + C_MAX + ", " + C_REGEN +
                ") values (" +
                "'" + uuid + "'" + ", " + "'" + player + "'" + ", " +
                dollars + ", " + hubixes + ", " + mana + ", " + max + ", " + regen +
                ")");
    }

    private static void update(String uuid, String column, int amount) {
        manager.Execute("update " + TABLE_NAME + " set " + column + " = " + amount + " where " + C_UUID + " = '" + uuid + "'");
    }

    private static void delete(String uuid) {
        manager.Execute("delete from " + TABLE_NAME + " where " + C_UUID + " = '" + uuid + "'");
    }

    private static int selectValue(String uuid, String column) {
        try {
            ResultSet rs = manager.Request("SELECT " + column + " FROM " + TABLE_NAME + " WHERE " + C_UUID + " = '" + uuid + "'");
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static boolean selectExist(String uuid) {
        try {
            ResultSet rs = manager.Request("SELECT * FROM " + TABLE_NAME + " WHERE " + C_UUID + " = '" + uuid + "'");
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
