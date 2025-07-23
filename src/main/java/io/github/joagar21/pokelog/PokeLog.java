package io.github.joagar21.pokelog;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.joagar21.pokelog.listeners.Listeners;
import io.github.joagar21.pokelog.commands.Base;
import io.github.joagar21.pokelog.configurations.Database;
import io.github.joagar21.pokelog.configurations.MainConfig;
import io.github.joagar21.pokelog.configurations.UIConfiguration;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.minecraft.server.MinecraftServer;

public class PokeLog implements ModInitializer {
  
  public static final String MODID = "pokelog";
  public static final String MODNAME = "PokeLog";
  public static final String MODVERSION = "1.0.2";
  
  private static PokeLog instance;
  private Database database;
  private MinecraftServer server;
  private Logger logger = LoggerFactory.getLogger(MODNAME);
  
  @Override
  public void onInitialize() {
    instance = this;
    
    logger.info(MODNAME + " is now loading database and configurations.");
    MainConfig.load();
    UIConfiguration.load();
    loadDatabase();
    
    logger.info(MODNAME + " is now registering events and commands.");
    Listeners.register();
    CommandRegistrationCallback.EVENT.register(Base::register);
    
    ServerLifecycleEvents.SERVER_STARTING.register(server -> {
      this.server = server;
      if (database == null) server.stop(false);
    });
    
    ServerLifecycleEvents.SERVER_STARTED.register(server -> {
      logger.info(MODNAME + " is now online.");
    });
    
    ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
      database.closeConnection();
    });
  }
  public void loadDatabase() {
    try {
        database = new Database();
    } catch (SQLException e) {
        logger.error("Establishing connection to the database failed: "+ e);
    }
  }
  public static PokeLog getInstance() {
    return instance;
  }
  public static Database getDatabase() {
    return instance.database;
  }
  public static MinecraftServer getServer() {
    return instance.server;
  }
  public static Logger getLogger() {
    return instance.logger;
  }
}