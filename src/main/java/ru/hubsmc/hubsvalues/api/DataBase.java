package ru.hubsmc.hubsvalues.api;

import ru.hubsmc.hubsvalues.HubsValues;

import java.sql.*;
import java.util.ArrayList;
import org.mariadb.jdbc.Driver;

public class DataBase {
    public static class DataBaseInfo {
        public String url;
        public String user;
        public String password;

        DataBaseInfo(String url, String user, String password) {
            this.url = url;
            this.user = user;
            this.password = password;
        }
    }

    private String db_url;
    private String db_user;
    private String db_password;

    public DataBase(String url, String user, String password) {
        this.db_url = url;
        this.db_user = user;
        this.db_password = password;

        try {
            DriverManager.registerDriver(new Driver());
        } catch (SQLException E) {
            E.printStackTrace();
        }

        Connection connection = Connect();

        if ( connection == null )
            HubsValues.getInstance().logConsole("WARNING! Can't connect to MySQL:\n- url: \"" + this.db_url + "\"");
        else {
            HubsValues.getInstance().logConsole("Connected to DB: \"" + this.db_url + "\"!");
            Disconnect(connection);
        }
    }

    DataBase(DataBaseInfo db_info) {
        new DataBase(db_info.url, db_info.user, db_info.password);
    }

    private Connection Connect() {
        Connection connection;
        try {
            connection = DriverManager.getConnection(this.db_url, this.db_user, this.db_password);
            return !connection.isClosed() ? connection : null;
        } catch (SQLException E) {
            E.printStackTrace();
        }
        return null;
    }

    private void Disconnect(Connection connection) {
        try {
            connection.close();
        } catch (SQLException E) {
            E.printStackTrace();
        }
    }

    public Manager GetManager() {
        Connection connection = Connect();
        return connection != null ? new Manager(connection) : null;
    }

    /*
     *  Requests manager
     */
    public class Manager {
        private Connection connection;
        private ArrayList<PreparedStatement> UnClosedStatements;
        private ArrayList<ResultSet> UnClosedResultSets;

        Manager(Connection connection){
            this.connection = connection;
            this.UnClosedStatements = new ArrayList<>();
            this.UnClosedResultSets = new ArrayList<>();
        }

        public void Free() {
            synchronized (this.UnClosedResultSets) {
                for (ResultSet resultSet : this.UnClosedResultSets) {
                    if (resultSet != null)
                        try {
                            resultSet.close();
                        } catch (SQLException E) {
                            E.printStackTrace();
                        }
                }
            }

            synchronized (this.UnClosedStatements) {
                for (PreparedStatement statement : this.UnClosedStatements) {
                    if (statement != null)
                        try {
                            statement.close();
                        } catch (SQLException E) {
                            E.printStackTrace();
                        }
                }
            }

            try {
                this.connection.close();
            } catch (SQLException E) {
                E.printStackTrace();
            }
        }

        private void PushStatement(PreparedStatement statement) {
            synchronized (this.UnClosedStatements) {
                this.UnClosedStatements.add(statement);
            }
        }

        private void PushResultSet(ResultSet resultSet) {
            synchronized (this.UnClosedResultSets) {
                this.UnClosedResultSets.add(resultSet);
            }
        }

        public ResultSet Request(String sql) {
            PreparedStatement statement = null;
            ResultSet result = null;

            try {
                statement = this.connection.prepareStatement(sql);
                PushStatement(statement);

                result = statement.executeQuery();
                PushResultSet(result);
            } catch (SQLException E) {
                E.printStackTrace();
            }

            return result;
        }

        public boolean Execute(String sql) {
            PreparedStatement statement = null;
            boolean result = false;

            try {
                statement = this.connection.prepareStatement(sql);
                statement.execute();
                result = true;
            } catch (SQLException E) {
                E.printStackTrace();
            } finally {
                try {
                    if (statement != null)
                        statement.close();
                } catch (Exception E) {
                    E.printStackTrace();
                }
            }

            return result;
        }
    }
}
