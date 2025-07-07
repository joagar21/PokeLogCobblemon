package io.github.joagar21.pokelog.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import io.github.joagar21.pokelog.PokeLog;
import io.github.joagar21.pokelog.utilities.Concurrency;
import io.github.joagar21.pokelog.utilities.Utilities;

import net.minecraft.server.command.ServerCommandSource;

public class Clear {

  public static int execute(CommandContext<ServerCommandSource> command) { 
    
    String type = StringArgumentType.getString(command, "type");
    
    if (!type.equals("capture") && !type.equals("hatch") && !type.equals("trade")) {
       Utilities.sendMessage(command.getSource(), "&cPlease enter capture, hatch, or trade only.");
    } else {
       Concurrency.runAsync(() -> PokeLog.getDatabase().clearLog(type));
       Utilities.sendMessage(command.getSource(), "&aThe logs for "+ type +" has been cleared.");
    }
    return Command.SINGLE_SUCCESS;
  }
}
