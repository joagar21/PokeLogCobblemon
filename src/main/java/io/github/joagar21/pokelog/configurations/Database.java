package io.github.joagar21.pokelog.configurations;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;

import com.google.common.collect.Lists;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.joagar21.pokelog.PokeLog;
import io.github.joagar21.pokelog.utilities.LogFormat.CaptureFormat;
import io.github.joagar21.pokelog.utilities.LogFormat.HatchFormat;
import io.github.joagar21.pokelog.utilities.LogFormat.ReleaseFormat;
import io.github.joagar21.pokelog.utilities.LogFormat.TradeFormat;
import io.github.joagar21.pokelog.utilities.Utilities;

public class Database {

    private final String PATH = "config/PokeLog/data.db";
    private HikariDataSource hikari;
    private boolean useMaria;

    public Database() throws SQLException {
        useMaria = MainConfig.INSTANCE.useMariaDB;
        if (useMaria) {
            setupMariaDB();
        } else {
            setupSQLite();
        }
        createTables();
    }

    private void setupSQLite() {
        File file = new File(PATH);
        file.getParentFile().mkdirs();

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                PokeLog.getLogger().error("Failed to create database file: " + e);
            }
        }

        hikari = new HikariDataSource();
        hikari.setJdbcUrl("jdbc:sqlite:" + PATH);
        PokeLog.getLogger().info("Connected to SQLite database.");
    }

    private void setupMariaDB() {
        try {
            HikariConfig config = new HikariConfig();

            config.setJdbcUrl("jdbc:mariadb://" +
                    MainConfig.INSTANCE.dbHost + ":" +
                    MainConfig.INSTANCE.dbPort + "/" +
                    MainConfig.INSTANCE.dbName);
            config.setUsername(MainConfig.INSTANCE.dbUser);
            config.setPassword(MainConfig.INSTANCE.dbPassword);

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTestQuery("SELECT 1");
            config.setPoolName("PokeLog");
            config.setIdleTimeout(300_000);
            config.setMaxLifetime(1_800_000);
            config.setLeakDetectionThreshold(15_000);

            hikari = new HikariDataSource(config);

            PokeLog.getLogger().info("Connected to MariaDB database.");
        } catch (Exception e) {
            PokeLog.getLogger().error("Unexpected error during MariaDB setup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTables() {
        boolean maria = MainConfig.INSTANCE.useMariaDB;

        try (Connection connection = hikari.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS capture (" +
                    "time BIGINT, " +
                    "player VARCHAR(255), " +
                    "nbt TEXT, " +
                    "PRIMARY KEY (time, player, " + (maria ? "nbt(255)" : "nbt") + "))");
        } catch (SQLException e) {
            PokeLog.getLogger().error("Failed to create capture table: " + e);
        }


        try (Connection connection = hikari.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS hatch (" +
                    "time BIGINT, " +
                    "player VARCHAR(255), " +
                    "nbt TEXT, " +
                    "PRIMARY KEY (time, player, " + (maria ? "nbt(255)" : "nbt") + "))");
        } catch (SQLException e) {
            PokeLog.getLogger().error("Failed to create hatch table: " + e);
        }

        try (Connection connection = hikari.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS trade (" +
                    "time BIGINT, " +
                    "player VARCHAR(255), " +
                    "traded_to VARCHAR(255), " +
                    "nbt TEXT, " +
                    "PRIMARY KEY (time, player, traded_to, " + (maria ? "nbt(255)" : "nbt") + "))");
        } catch (SQLException e) {
            PokeLog.getLogger().error("Failed to create trade table: " + e);
        }

        try (Connection connection = hikari.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS `release` (" +
                    "time BIGINT, " +
                    "player VARCHAR(255), " +
                    "nbt TEXT, " +
                    "PRIMARY KEY (time, player, " + (maria ? "nbt(255)" : "nbt") + "))");
        } catch (SQLException e) {
            PokeLog.getLogger().error("Failed to create release table: " + e);
        }
    }

    public void addCaptureLog(String player, String nbt) {
        try (Connection connection = hikari.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO capture (time, player, nbt) VALUES (?, ?, ?)")) {
            preparedStatement.setLong(1, System.currentTimeMillis());
            preparedStatement.setString(2, player);
            preparedStatement.setString(3, nbt);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            PokeLog.getLogger().error("Failed to add capture log for the player " + player + ": " + e);
        }
    }

    public void addHatchLog(String player, String nbt) {

        try (Connection connection = hikari.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO hatch (time, player, nbt) VALUES (?, ?, ?)")) {
        preparedStatement.setLong(1, System.currentTimeMillis());
        preparedStatement.setString(2, player);
        preparedStatement.setString(3, nbt);
        preparedStatement.executeUpdate();
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to add hatch log for the player "+ player +": "+ e);
    }
  }
  public void addTradeLog(String player, String tradedTo, String nbt) {

      try (Connection connection = hikari.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO trade (time, player, traded_to, nbt) VALUES (?, ?, ?, ?)")) {
        preparedStatement.setLong(1, System.currentTimeMillis());
        preparedStatement.setString(2, player);
        preparedStatement.setString(3, tradedTo);
        preparedStatement.setString(4, nbt);
        preparedStatement.executeUpdate();
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to add trade log for the player "+ player +": "+ e);
    }
  }
  public void addReleaseLog(String player, String nbt) {

      try (Connection connection = hikari.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `release` (time, player, nbt) VALUES (?, ?, ?)")) {
        preparedStatement.setLong(1, System.currentTimeMillis());
        preparedStatement.setString(2, player);
        preparedStatement.setString(3, nbt);
        preparedStatement.executeUpdate();
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to add release log for the player "+ player +": "+ e);
    }
  }
  public List<CaptureFormat> getCaptureLogs(String filterPlayer, PokemonProperties filterProperties) {
    
    String statement = "SELECT * FROM capture ORDER BY time DESC";
    if (!filterPlayer.equals("all")) statement = "SELECT * FROM capture WHERE player = ? ORDER BY time DESC";
    
    List<CaptureFormat> list = Lists.newArrayList();

      try (Connection connection = hikari.getConnection();
           PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
        if (!filterPlayer.equals("all")) preparedStatement.setString(1, filterPlayer);
        ResultSet resultSet = preparedStatement.executeQuery();
        
        while (resultSet.next()) {
          if (filterProperties != null) {
             String nbt = resultSet.getString("nbt");
             if (filterProperties.matches(Utilities.getPokemonFromNbt(nbt))) list.add(new CaptureFormat(resultSet.getLong("time"), resultSet.getString("player"), nbt));
          } else {
             list.add(new CaptureFormat(resultSet.getLong("time"), resultSet.getString("player"), resultSet.getString("nbt"))); 
          }
        }
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to get capture logs: "+ e);
    }
    return list;
  }
  public List<HatchFormat> getHatchLogs(String filterPlayer, PokemonProperties filterProperties) {
    
    String statement = "SELECT * FROM hatch ORDER BY time DESC";
    if (!filterPlayer.equals("all")) statement = "SELECT * FROM hatch WHERE player = ? ORDER BY time DESC";
    
    List<HatchFormat> list = Lists.newArrayList();

      try (Connection connection = hikari.getConnection();
           PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
        if (!filterPlayer.equals("all")) preparedStatement.setString(1, filterPlayer);
        ResultSet resultSet = preparedStatement.executeQuery();
        
        while (resultSet.next()) {
          if (filterProperties != null) {
             String nbt = resultSet.getString("nbt");
             if (filterProperties.matches(Utilities.getPokemonFromNbt(nbt))) list.add(new HatchFormat(resultSet.getLong("time"), resultSet.getString("player"), nbt));
          } else {
             list.add(new HatchFormat(resultSet.getLong("time"), resultSet.getString("player"), resultSet.getString("nbt"))); 
          }
        }
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to get hatch logs: "+ e);
    }
    return list;
  }
  public List<TradeFormat> getTradeLogs(String filterPlayer, PokemonProperties filterProperties) {
    
    String statement = "SELECT * FROM trade ORDER BY time DESC";
    if (!filterPlayer.equals("all")) statement = "SELECT * FROM trade WHERE player = ? ORDER BY time DESC";
    
    List<TradeFormat> list = Lists.newArrayList();

      try (Connection connection = hikari.getConnection();
           PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
        if (!filterPlayer.equals("all")) preparedStatement.setString(1, filterPlayer);
        ResultSet resultSet = preparedStatement.executeQuery();
        
        while (resultSet.next()) {
          if (filterProperties != null) {
             String nbt = resultSet.getString("nbt");
             if (filterProperties.matches(Utilities.getPokemonFromNbt(nbt))) list.add(new TradeFormat(resultSet.getLong("time"), resultSet.getString("player"), resultSet.getString("traded_to"), nbt)); 
          } else {
             list.add(new TradeFormat(resultSet.getLong("time"), resultSet.getString("player"), resultSet.getString("traded_to"), resultSet.getString("nbt"))); 
          }
        }
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to get trade logs: "+ e);
    }
    return list;
  }
  public List<ReleaseFormat> getReleaseLogs(String filterPlayer, PokemonProperties filterProperties) {
    
    String statement = "SELECT * FROM `release` ORDER BY time DESC";
    if (!filterPlayer.equals("all")) statement = "SELECT * FROM `release` WHERE player = ? ORDER BY time DESC";
    
    List<ReleaseFormat> list = Lists.newArrayList();

      try (Connection connection = hikari.getConnection();
           PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
        if (!filterPlayer.equals("all")) preparedStatement.setString(1, filterPlayer);
        ResultSet resultSet = preparedStatement.executeQuery();
        
        while (resultSet.next()) {
          if (filterProperties != null) {
             String nbt = resultSet.getString("nbt");
             if (filterProperties.matches(Utilities.getPokemonFromNbt(nbt))) list.add(new ReleaseFormat(resultSet.getLong("time"), resultSet.getString("player"), nbt));
          } else {
             list.add(new ReleaseFormat(resultSet.getLong("time"), resultSet.getString("player"), resultSet.getString("nbt"))); 
          }
        }
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to get release logs: "+ e);
    }
    return list;
  }
  public void clearLog(String type) {

      try (Connection connection = hikari.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM " + type)) {
        preparedStatement.executeUpdate();
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to clear "+ type +" log: "+ e);
    }
  }
  public void closeConnection() {
        if (!hikari.isClosed()) hikari.close();
  }
}