package io.github.joagar21.pokelog.commands;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import io.github.joagar21.pokelog.utilities.Concurrency;
import io.github.joagar21.pokelog.utilities.UserInterface;
import io.github.joagar21.pokelog.utilities.Utilities;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class Open {
  
  public static int execute(CommandContext<ServerCommandSource> command) { 
    
    String type = StringArgumentType.getString(command, "type");
    ServerPlayerEntity player = command.getSource().getPlayer();
    
    if (!type.equals("capture") && !type.equals("hatch") && !type.equals("trade") && !type.equals("release")) {
       Utilities.sendMessage(player, "&cPlease enter a valid log type.");
    } else {
       String targetPlayer = StringArgumentType.getString(command, "player");
       String pokemonProperties = StringArgumentType.getString(command, "pokemonProperties");
       
       Utilities.sendMessage(player, "&aFetching "+ type +" logs from player "+ targetPlayer +" with "+ pokemonProperties +" pokemon properties...");
       Concurrency.runAsync(() -> UserInterface.open(type, targetPlayer, PokemonProperties.Companion.parse(pokemonProperties), player));
    }
    return Command.SINGLE_SUCCESS;
  }
}
