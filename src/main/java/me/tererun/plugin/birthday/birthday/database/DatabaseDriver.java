package me.tererun.plugin.birthday.birthday.database;

import me.tererun.plugin.birthday.birthday.Birthday;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class DatabaseDriver {

    private static Connection connection;
    private static PreparedStatement ps;
    private static String url;

    public DatabaseDriver(String fileName, String tableName) {
        connection = null;
        String filePath = Birthday.plugin.getDataFolder().getAbsolutePath() + File.separator + fileName;
        url = "jdbc:sqlite:" + filePath;
        File file = new File(filePath);
        if (!file.exists()) {
            createNewDatabase(fileName);
            createTable(tableName);
        }

    }

    /**
     * Connect to a sample database
     *
     * @param fileName the database file name
     */
    public void createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:" + Birthday.plugin.getDataFolder().getAbsolutePath() + File.separator + fileName;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.err.println("Create database error");
            System.out.println(e.getMessage());
        }
    }

    /**
     * SELECT文
     */
    public List<String> loadData(String tableName, String uuid) {
        String sql;
        if (uuid == null) {
            sql = "SELECT * FROM " + tableName;
        } else {
            sql = "SELECT * FROM " + tableName + " WHERE uuid = '" + uuid + "'";
        }
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet rs = statement.executeQuery(sql);
            List<String> stringList = new ArrayList<>();
            while (rs.next()) {
                stringList.add(0, rs.getString("uuid"));
                stringList.add(1, rs.getString("year"));
                stringList.add(2, rs.getString("day"));
            }
            return stringList;
        } catch(SQLException e) {
            System.err.println("select error");
            System.err.println(e.getMessage());
        } finally {
            try {
                if(connection != null) connection.close();
            } catch(SQLException e) {
                System.err.println(e);
            }
        }
        return null;
    }

    public void loadAllData(String tableName, String uuid) {
        String sql;
        if (uuid == null) {
            sql = "SELECT * FROM " + tableName;
        } else {
            sql = "SELECT * FROM " + tableName + " WHERE uuid = '" + uuid + "'";
        }
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet resultSet = statement.executeQuery(sql);
            Calendar calendar = Calendar.getInstance();

            try {
                while (resultSet.next()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
                    sdf.setLenient(false);
                    System.out.println("loading " + resultSet.getString("uuid") + ": " + resultSet.getString("year") + " / " + resultSet.getString("day"));
                    try {
                        Date parsedDate = sdf.parse(resultSet.getString("day"));
                        Calendar birthCalender = Calendar.getInstance();
                        birthCalender.setTime(parsedDate);
                        if ((calendar.get(Calendar.MONTH) == birthCalender.get(Calendar.MONTH)) && (calendar.get(Calendar.DAY_OF_MONTH) == birthCalender.get(Calendar.DAY_OF_MONTH))) {
                            UUID uuid1 = UUID.fromString(resultSet.getString("uuid"));
                            Bukkit.broadcastMessage(Birthday.prefix + "§e本日は " + Bukkit.getOfflinePlayer(uuid1).getName() + " さんの誕生日です！");
                            Birthday.birthdayers.add(uuid1);
                        }
                    } catch (ParseException e) { Birthday.plugin.getLogger().warning("誕生日の照会でエラーが発生しました"); }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        } catch(SQLException e) {
            System.err.println("SelectAll error");
            System.err.println(e.getMessage());
        } finally {
            try {
                if(connection != null) connection.close();
            } catch(SQLException e) {
                System.err.println(e);
            }
        }
    }

    /**
     * SELECT文
     */
    public int getCount(String tableName, String uuid) {
        String sql;
        if (uuid == null) {
            sql = "SELECT COUNT(*) FROM " + tableName;
        } else {
            sql = "SELECT COUNT(*) FROM " + tableName + " WHERE uuid = '" + uuid + "'";
        }
        int i = 0;
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                i = rs.getInt(1);
            }
        } catch(SQLException e) {
            System.err.println("Count error");
            System.err.println(e.getMessage());
            return 0;
        } finally {
            try {
                if(connection != null) connection.close();
            } catch(SQLException e) {
                System.err.println(e);
            }
        }
        return i;
    }

    public void addData(String tableName, String uuid, String year, String day) {
        if (getCount(tableName, uuid) == 0) {
            System.out.println("addData to insert: " + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
            insertData(tableName, uuid, year, day);
        } else {
            System.out.println("addData to update: " + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
            updateData(tableName, uuid, year, day);
        }
    }

    /**
     * INSERT文
     */
    public void insertData(String tableName, String uuid, String year, String day) {
        String sql = "INSERT INTO " + tableName + " (uuid, year, day) VALUES('" + uuid + "', '" + year + "', '" + day + "')";

        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            statement.executeUpdate(sql);
            Birthday.plugin.getLogger().severe(Birthday.prefix + "Insert data: uuid = " + uuid + ", year = " + year + ", day = " + day);
        } catch(SQLException e) {
            System.err.println("Insert error");
            System.err.println(e.getMessage());
        } finally {
            try {
                if(connection != null) connection.close();
            } catch(SQLException e) {
                System.err.println(e);
            }
        }
    }

    /**
     * UPDATE文
     */
    public void updateData(String tableName, String uuid, String year, String day) {
        try {
            String sql = "UPDATE " + tableName + " SET year = '" + year + "', day = '" + day + "' WHERE uuid = '" + uuid + "'";
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            statement.executeUpdate(sql);
            Birthday.plugin.getLogger().severe(Birthday.prefix + "Update data: uuid = " + uuid + ", year = " + year + ", day = " + day);
        } catch(SQLException e) {
            System.err.println("Update error");
            System.err.println(e.getMessage());
        } finally {
            try {
                if(connection != null) connection.close();
            } catch(SQLException e) {
                System.err.println(e);
            }
        }
    }

    /**
     * DELETE文
     */
    public void deleteData(String tableName, String uuid) {
        try {
            String sql = "DELETE FROM " + tableName + " WHERE uuid = '" + uuid + "'";
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            statement.executeUpdate(sql);
        } catch(SQLException e) {
            System.err.println("Delete error");
            System.err.println(e.getMessage());
        } finally {
            try {
                if(connection != null) connection.close();
            } catch(SQLException e) {
                System.err.println(e);
            }
        }
    }

    /**
     * テーブル作成
     */
    public void createTable(String tableName) {
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            statement.executeUpdate("CREATE TABLE " + tableName + " (uuid text, year text, day text)");
        } catch(SQLException e) {
            System.err.println("Create table error");
            System.err.println(e.getMessage());
        } finally {
            try {
                if(connection != null)
                    connection.close();
            } catch(SQLException e) {
                System.err.println(e);
            }
        }
    }

    /**
     * テーブル削除
     */
    public void dropTable(String tableName) {
        try {
            connection = DriverManager.getConnection(url);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate("DROP TABLE IF EXISTS " + tableName);
        } catch(SQLException e) {
            System.err.println("Drop table error");
            System.err.println(e.getMessage());
        } finally {
            try {
                if(connection != null) connection.close();
            } catch(SQLException e) {
                System.err.println(e);
            }
        }
    }

}
