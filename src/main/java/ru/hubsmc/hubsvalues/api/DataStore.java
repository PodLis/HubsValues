package ru.hubsmc.hubsvalues.api;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DataStore {

    private static String TABLE_NAME;
    protected static final String C_UUID = "uuid";
    private static String[] COLUMNS;
    private static String[] stringPrimalValues;
    private static int[] intPrimalValues;
    private static double[] doublePrimalValues;

    private static DataBase dataBase;
    protected static DataBase.Manager manager;

    public DataStore(String table, String... columns) {
        TABLE_NAME = table;
        COLUMNS = columns;
    }

    public void prepareToWork(String url, String user, String password, String[] stringPrimal, int[] intPrimal, double[] doublePrimal) {
        if (stringPrimal.length + intPrimal.length + doublePrimal.length != COLUMNS.length) {
            throw new IllegalArgumentException();
        }
        stringPrimalValues = stringPrimal;
        intPrimalValues = intPrimal;
        doublePrimalValues = doublePrimal;
        try {
            dataBase = new DataBase(url, user, password);
            manager = dataBase.GetManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnections() {
        manager.Free();
    }


    public static void saveValue(String UUID, String valueType, String valueAmount) {
        update(UUID, valueType, valueAmount);
    }

    public static void saveValue(String UUID, String valueType, int valueAmount) {
        update(UUID, valueType, valueAmount);
    }

    public static void saveValue(String UUID, String valueType, double valueAmount) {
        update(UUID, valueType, valueAmount);
    }

    public static void saveAllValues(String UUID, String[] strings, int[] integers, double[] doubles) {
        int i = 0;
        for (String value : strings) {
            update(UUID, COLUMNS[i], value);
            i++;
        }
        for (int value : integers) {
            update(UUID, COLUMNS[i], value);
            i++;
        }
        for (double value : doubles) {
            update(UUID, COLUMNS[i], value);
            i++;
        }
    }

    public static void createAccount(String UUID) {
        insert(UUID, stringPrimalValues, intPrimalValues, doublePrimalValues);
    }

    public static void deleteAccount(String UUID) {
        delete(UUID);
    }


    // data-safe requests

    public static String selectStringValue(String uuid, String column) {
        try {
            ResultSet rs = manager.Request("SELECT " + column + " FROM " + TABLE_NAME + " WHERE " + C_UUID + " = '" + uuid + "'");
            rs.next();
            return rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int selectIntValue(String uuid, String column) {
        try {
            ResultSet rs = manager.Request("SELECT " + column + " FROM " + TABLE_NAME + " WHERE " + C_UUID + " = '" + uuid + "'");
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double selectDoubleValue(String uuid, String column) {
        try {
            ResultSet rs = manager.Request("SELECT " + column + " FROM " + TABLE_NAME + " WHERE " + C_UUID + " = '" + uuid + "'");
            rs.next();
            return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean selectExist(String uuid) {
        try {
            ResultSet rs = manager.Request("SELECT * FROM " + TABLE_NAME + " WHERE " + C_UUID + " = '" + uuid + "'");
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // data-unsafe requests

    protected static void insert(String uuid, String[] strings, int[] integers, double[] doubles) {
        StringBuilder sql = new StringBuilder("insert into " + TABLE_NAME + "(" + C_UUID);
        for (String column : COLUMNS) {
            sql.append(", ").append(column);
        }

        sql.append(") values (" + "'").append(uuid).append("'");
        for (String value : strings) {
            sql.append(", " + "'").append(value).append("'");
        }
        for (int value : integers) {
            sql.append(", ").append(value);
        }
        for (double value : doubles) {
            sql.append(", ").append(value);
        }
        sql.append(")");

        manager.Execute(sql.toString());
    }

    protected static void update(String uuid, String column, String value) {
        manager.Execute("update " + TABLE_NAME + " set " + column + " = '" + value + "' where " + C_UUID + " = '" + uuid + "'");
    }

    protected static void update(String uuid, String column, int value) {
        manager.Execute("update " + TABLE_NAME + " set " + column + " = " + value + " where " + C_UUID + " = '" + uuid + "'");
    }

    protected static void update(String uuid, String column, double value) {
        manager.Execute("update " + TABLE_NAME + " set " + column + " = " + value + " where " + C_UUID + " = '" + uuid + "'");
    }

    protected static void delete(String uuid) {
        manager.Execute("delete from " + TABLE_NAME + " where " + C_UUID + " = '" + uuid + "'");
    }

}
