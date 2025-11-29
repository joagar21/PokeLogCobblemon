package io.github.joagar21.pokelog.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;

import io.github.joagar21.pokelog.PokeLog;
import io.github.joagar21.pokelog.configurations.Main;
import io.github.joagar21.pokelog.utilities.Permissions;
import io.github.joagar21.pokelog.utilities.Texts;
import io.github.joagar21.pokelog.utilities.Utilities;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.ClickEvent.Action;

public class Info {
  
  public static int execute(CommandContext<ServerCommandSource> command) { 
    
    ServerCommandSource source = command.getSource();
    Utilities.sendMessage(source, "&6====================[&a&l Info &6]=======================");
    Utilities.sendMessage(source, "&eVersion: "+ PokeLog.MODVERSION);
    Utilities.sendMessage(source, "&eDeveloped by: joagar");
    source.sendMessage(Texts.color("&eDiscord Server: https://discord.gg/eDsKXU4AHJ").copy()
    .styled(style -> style.withClickEvent(new ClickEvent(Action.OPEN_URL, "https://discord.gg/eDsKXU4AHJ"))));
    Utilities.sendMessage(source,"&6==================================================");
    
    Utilities.sendMessage(source, " ");
    String commandName = Main.INSTANCE.CommandAlias.getFirst();
    
    Utilities.sendMessage(source, "&6==================[&a&l Commands &6]====================");
    Utilities.sendMessage(source, "&e/"+ commandName +" info");
    if (Permissions.hasPermission(Permissions.RELOAD, source)) Utilities.sendMessage(source, "&e/"+ commandName +" reload");
    if (Permissions.hasPermission(Permissions.CLEAR, source)) Utilities.sendMessage(source, "&e/"+ commandName +" clear <type>");
    if (Permissions.hasPermission(Permissions.OPEN, source)) Utilities.sendMessage(source, "&e/"+ commandName +" open <type> <player> <pokemonProperties>");
    Utilities.sendMessage(source, "&6==================================================");
    
    return Command.SINGLE_SUCCESS;
  }
}