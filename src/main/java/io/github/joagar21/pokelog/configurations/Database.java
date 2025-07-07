package io.github.joagar21.pokelog.configurations;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.google.common.collect.Lists;

import io.github.joagar21.pokelog.PokeLog;
import io.github.joagar21.pokelog.utilities.LogFormat.CaptureFormat;
import io.github.joagar21.pokelog.utilities.LogFormat.HatchFormat;
import io.github.joagar21.pokelog.utilities.LogFormat.TradeFormat;
import io.github.joagar21.pokelog.utilities.Utilities;

public class Database {
  
  private final Connection connection;
  private final String PATH = "config/PokeLog/data.db";
  
  public Database() throws SQLException {
    
    File file = new File(PATH);
    file.getParentFile().mkdirs();
    
    if (!file.exists()) {
       try {
           file.createNewFile();
       } catch (IOException e) {
           PokeLog.getLogger().error("Failed to create database file: "+ e);
       }
    }
    connection = DriverManager.getConnection("jdbc:sqlite:"+ PATH);
    createTables();
  }
  private void createTables() {
    
    try (Statement statement = connection.createStatement()) {
        statement.execute("CREATE TABLE IF NOT EXISTS capture (" +
        "time INTEGER, " +
        "player TEXT, " +
        "properties TEXT, " +
        "PRIMARY KEY (time, player, properties))");
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to create capture table: "+ e);
    }
    
    try (Statement statement = connection.createStatement()) {
        statement.execute("CREATE TABLE IF NOT EXISTS hatch (" +
        "time INTEGER, " +
        "player TEXT, " +
        "properties TEXT, " +
        "PRIMARY KEY (time, player, properties))");
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to create hatch table: "+ e);
    }
    
    try (Statement statement = connection.createStatement()) {
        statement.execute("CREATE TABLE IF NOT EXISTS trade (" +
        "time INTEGER, " +
        "player TEXT, " +
        "traded_to TEXT, " +
        "properties TEXT, " +
        "PRIMARY KEY (time, player, traded_to, properties))");
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to create trade table: "+ e);
    }
  }
  public void addCaptureLog(String player, String properties) {
    
    try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO capture (time, player, properties) VALUES (?, ?, ?)")) {
        preparedStatement.setLong(1, System.currentTimeMillis());
        preparedStatement.setString(2, player);
        preparedStatement.setString(3, properties);
        preparedStatement.executeUpdate();
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to add capture log for the player "+ player +": "+ e);
    }
  }
  public void addHatchLog(String player, String properties) {
    
    try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO hatch (time, player, properties) VALUES (?, ?, ?)")) {
        preparedStatement.setLong(1, System.currentTimeMillis());
        preparedStatement.setString(2, player);
        preparedStatement.setString(3, properties);
        preparedStatement.executeUpdate();
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to add hatch log for the player "+ player +": "+ e);
    }
  }
  public void addTradeLog(String player, String tradedTo, String properties) {
    
    try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO trade (time, player, traded_to, properties) VALUES (?, ?, ?, ?)")) {
        preparedStatement.setLong(1, System.currentTimeMillis());
        preparedStatement.setString(2, player);
        preparedStatement.setString(3, tradedTo);
        preparedStatement.setString(4, properties);
        preparedStatement.executeUpdate();
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to add trade log for the player "+ player +": "+ e);
    }
  }
  public List<CaptureFormat> getCaptureLogs(String filterPlayer, String[] filterProperties) {
    
    String statement = "SELECT * FROM capture ORDER BY time DESC";
    if (!filterPlayer.equals("all")) statement = "SELECT * FROM capture WHERE player = ? ORDER BY time DESC";
    
    List<CaptureFormat> list = Lists.newArrayList();
    boolean checkProperties = filterProperties[0].equals("all") ? false : true;
    
    try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
        if (!filterPlayer.equals("all")) preparedStatement.setString(1, filterPlayer);
        ResultSet resultSet = preparedStatement.executeQuery();
        
        while (resultSet.next()) {
          String properties = resultSet.getString("properties");
          if (checkProperties && !Utilities.hasProperties(properties, filterProperties)) continue;
          list.add(new CaptureFormat(resultSet.getLong("time"), resultSet.getString("player"), properties)); 
        }
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to get capture logs: "+ e);
    }
    return list;
  }
  public List<HatchFormat> getHatchLogs(String filterPlayer, String[] filterProperties) {
    
    String statement = "SELECT * FROM hatch ORDER BY time DESC";
    if (!filterPlayer.equals("all")) statement = "SELECT * FROM hatch WHERE player = ? ORDER BY time DESC";
    
    List<HatchFormat> list = Lists.newArrayList();
    boolean checkProperties = filterProperties[0].equals("all") ? false : true;
    
    try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
        if (!filterPlayer.equals("all")) preparedStatement.setString(1, filterPlayer);
        ResultSet resultSet = preparedStatement.executeQuery();
        
        while (resultSet.next()) {
          String properties = resultSet.getString("properties");
          if (checkProperties && !Utilities.hasProperties(properties, filterProperties)) continue;
          list.add(new HatchFormat(resultSet.getLong("time"), resultSet.getString("player"), properties)); 
        }
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to get hatch logs: "+ e);
    }
    return list;
  }
  public List<TradeFormat> getTradeLogs(String filterPlayer, String[] filterProperties) {
    
    String statement = "SELECT * FROM trade ORDER BY time DESC";
    if (!filterPlayer.equals("all")) statement = "SELECT * FROM trade WHERE player = ? ORDER BY time DESC";
    
    List<TradeFormat> list = Lists.newArrayList();
    boolean checkProperties = filterProperties[0].equals("all") ? false : true;
    
    try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
        if (!filterPlayer.equals("all")) preparedStatement.setString(1, filterPlayer);
        ResultSet resultSet = preparedStatement.executeQuery();
        
        while (resultSet.next()) {
          String properties = resultSet.getString("properties");
          if (checkProperties && !Utilities.hasProperties(properties, filterProperties)) continue;
          list.add(new TradeFormat(resultSet.getLong("time"), resultSet.getString("player"), resultSet.getString("traded_to"), properties)); 
        }
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to get trade logs: "+ e);
    }
    return list;
  }
  public void clearLog(String type) {
    
    try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM " + type)) {
        preparedStatement.executeUpdate();
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to clear "+ type +" log: "+ e);
    }
  }
  public void closeConnection() {
    
    try {
        if (!connection.isClosed()) connection.close();
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to close database connection: "+ e);
    }
  }
}