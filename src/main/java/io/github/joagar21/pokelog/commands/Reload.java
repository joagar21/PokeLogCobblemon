package io.github.joagar21.pokelog.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import io.github.joagar21.pokelog.configurations.Main;
import io.github.joagar21.pokelog.configurations.UIConfiguration;
import io.github.joagar21.pokelog.utilities.Utilities;

import net.minecraft.server.command.ServerCommandSource;

public class Reload {
  
  public static int execute(CommandContext<ServerCommandSource> command) { 
    
    Main.load();
    UIConfiguration.load();
    Utilities.sendMessage(command.getSource(), "&aPokeLog has been reloaded.");
    return Command.SINGLE_SUCCESS;
  }
}