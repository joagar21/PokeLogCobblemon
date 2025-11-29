package io.github.joagar21.pokelog.configurations;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;

import com.google.common.collect.Lists;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.github.joagar21.pokelog.PokeLog;
import io.github.joagar21.pokelog.utilities.LogFormat.BaseFormat;
import io.github.joagar21.pokelog.utilities.LogFormat.TradeFormat;
import io.github.joagar21.pokelog.utilities.Utilities;

public class Database {
  
  private boolean UseSqlite;
  private HikariDataSource dataSource;
  private final String PATH = "config/PokeLog/data.db";
  
  public Database() throws SQLException {
    
    UseSqlite = Main.INSTANCE.UseSqlite;
    
    if (UseSqlite) {
       File file = new File(PATH);
       file.getParentFile().mkdirs();
       
       if (!file.exists()) {
          try {
              file.createNewFile();
          } catch (IOException e) {
              PokeLog.getLogger().error("Failed to create database file: "+ e);
          }
       }
       dataSource = new HikariDataSource();
       dataSource.setJdbcUrl("jdbc:sqlite:" + PATH);
    }
    else {
       try {
           HikariConfig config = new HikariConfig();
           config.setJdbcUrl("jdbc:mariadb://" + Main.INSTANCE.DatabaseAddress + "/" + Main.INSTANCE.DatabaseName);
           config.setUsername(Main.INSTANCE.DatabaseUserName);
           config.setPassword(Main.INSTANCE.DatabasePassword);
           config.setMaximumPoolSize(10);
           config.setMinimumIdle(2);
           config.setIdleTimeout(300_000);
           config.setMaxLifetime(1_200_000);
           config.setLeakDetectionThreshold(20_000);
           
           dataSource = new HikariDataSource(config);
       } catch (Exception e) {
           PokeLog.getLogger().error("Establishing connection to the database failed: "+ e);
       }
    }
    createTables();
  }
  private void createTables() {
    
    executeStatement(
      "CREATE TABLE IF NOT EXISTS capture_logs (" +
      "time BIGINT, " +
      "player VARCHAR(36), " +
      "nbt TEXT, " +
      "extra TEXT, " +
      "PRIMARY KEY (time, player, "+ (UseSqlite ? "nbt, extra" : "nbt(255), extra(255)") +"))"
    );
    
    executeStatement(
      "CREATE TABLE IF NOT EXISTS hatch_logs (" +
      "time BIGINT, " +
      "player VARCHAR(36), " +
      "nbt TEXT, " +
      "extra TEXT, " +
      "PRIMARY KEY (time, player, "+ (UseSqlite ? "nbt, extra" : "nbt(255), extra(255)") +"))"
    );
    
    executeStatement(
      "CREATE TABLE IF NOT EXISTS trade_logs (" +
      "time BIGINT, " +
      "player VARCHAR(36), " +
      "traded_to VARCHAR(36), " +
      "nbt TEXT, " +
      "extra TEXT, " +
      "PRIMARY KEY (time, player, traded_to, "+ (UseSqlite ? "nbt, extra" : "nbt(255), extra(255)") +"))"
    );
    
    executeStatement(
      "CREATE TABLE IF NOT EXISTS release_logs (" +
      "time BIGINT, " +
      "player VARCHAR(36), " +
      "nbt TEXT, " +
      "extra TEXT, " +
      "PRIMARY KEY (time, player, "+ (UseSqlite ? "nbt, extra" : "nbt(255), extra(255)") +"))"
    );
  }
  public void addBaseLog(String type, String playerUUID, String nbt, String extra) {
    
    executeStatement(
      "INSERT INTO "+ type +"_logs (time, player, nbt, extra) VALUES (?, ?, ?, ?)",
      
      preparedStatement -> {
        preparedStatement.setLong(1, System.currentTimeMillis());
        preparedStatement.setString(2, playerUUID);
        preparedStatement.setString(3, nbt);
        preparedStatement.setString(4, extra);
      },
      
      false
    );
  }
  public void addTradeLog(String playerUUID, String tradedToUUID, String nbt, String extra) {
    
    executeStatement(
      "INSERT INTO trade_logs (time, player, traded_to, nbt, extra) VALUES (?, ?, ?, ?, ?)",
      
      preparedStatement -> {
        preparedStatement.setLong(1, System.currentTimeMillis());
        preparedStatement.setString(2, playerUUID);
        preparedStatement.setString(3, tradedToUUID);
        preparedStatement.setString(4, nbt);
        preparedStatement.setString(5, extra);
      },
      
      false
    );
  }
  public List<BaseFormat> getLogs(String type, String targetPlayer, PokemonProperties pokemonProperties) {
    
    String statement = "SELECT * FROM "+ type +"_logs ORDER BY time DESC";
    if (!targetPlayer.equals("all")) statement = "SELECT * FROM "+ type +"_logs WHERE player = ? ORDER BY time DESC";
    
    return query(
      statement,
      
      preparedStatement -> {if (!targetPlayer.equals("all")) preparedStatement.setString(1, targetPlayer);},
      
      resultSet -> {
        List<BaseFormat> list = Lists.newArrayList();
        
        while (resultSet.next()) {
          String nbt = resultSet.getString("nbt");
          if (pokemonProperties.matches(Utilities.getPokemonFromNbt(nbt))) list.add(new BaseFormat(resultSet.getLong("time"), resultSet.getString("player"), nbt, resultSet.getString("extra")));
        }
        return list;
      }
    );
  }
  public List<TradeFormat> getTradeLogs(String targetPlayer, PokemonProperties pokemonProperties) {
    
    String statement = "SELECT * FROM trade_logs ORDER BY time DESC";
    if (!targetPlayer.equals("all")) statement = "SELECT * FROM trade_logs WHERE player = ? ORDER BY time DESC";
    
    return query(
      statement,
      
      preparedStatement -> {if (!targetPlayer.equals("all")) preparedStatement.setString(1, targetPlayer);},
      
      resultSet -> {
        List<TradeFormat> list = Lists.newArrayList();
        
        while (resultSet.next()) {
          String nbt = resultSet.getString("nbt");
          if (pokemonProperties.matches(Utilities.getPokemonFromNbt(nbt))) list.add(new TradeFormat(resultSet.getLong("time"), resultSet.getString("player"), resultSet.getString("traded_to"), nbt, resultSet.getString("extra"))); 
        }
        return list;
      }
    );
  }
  public void clearLog(String type) {
    
    executeStatement(
      "DELETE FROM "+ type +"_logs",
      
      preparedStatement -> {},
      
      false
    );
  }
  public void closeConnection() {
    if (!dataSource.isClosed()) dataSource.close();
  }
  private void executeStatement(String statement) {
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
        preparedStatement.execute();
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to execute statement: " + e);
    }
  }
  private void executeStatement(String statement, StatementExecutor statementExecutor, boolean batchExecution) {
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
        statementExecutor.accept(preparedStatement);
        
        if (batchExecution) {
           preparedStatement.executeBatch();
        } else {
           preparedStatement.execute();
        }
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to execute statement: " + e);
    }
  }
  private <T> T query(String statement, StatementExecutor statementExecutor, Query<T> query) {
    try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
        statementExecutor.accept(preparedStatement);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            return query.apply(resultSet);
        }
    } catch (SQLException e) {
        PokeLog.getLogger().error("Failed to query statement: " + e);
    }
    return null;
  }
  @FunctionalInterface
  public interface StatementExecutor {
    void accept(PreparedStatement statement) throws SQLException;
  }
  @FunctionalInterface
  public interface Query<T> {
    T apply(ResultSet resultSet) throws SQLException;
  }
}