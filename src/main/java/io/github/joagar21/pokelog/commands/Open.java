package io.github.joagar21.pokelog.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import io.github.joagar21.pokelog.utilities.Concurrency;
import io.github.joagar21.pokelog.utilities.UserInterface;
import io.github.joagar21.pokelog.utilities.Utilities;

import net.minecraft.server.command.ServerCommandSource;

public class Open {
  
  public static int execute(CommandContext<ServerCommandSource> command) { 
    
    String type = StringArgumentType.getString(command, "type");
    
    if (!type.equals("capture") && !type.equals("hatch") && !type.equals("trade")) {
       Utilities.sendMessage(command.getSource().getPlayer(), "&cPlease enter capture, hatch, or trade only.");
    } else {
       Concurrency.runAsync(() -> UserInterface.open(type, StringArgumentType.getString(command, "filterPlayer"), StringArgumentType.getString(command, "filterProperties"), command.getSource().getPlayer()));
    }
    return Command.SINGLE_SUCCESS;
  }
}
