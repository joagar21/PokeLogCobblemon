package io.github.joagar21.pokelog.commands;

import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import io.github.joagar21.pokelog.PokeLog;
import io.github.joagar21.pokelog.configurations.Main;
import io.github.joagar21.pokelog.utilities.Permissions;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class Base {
  
  public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registry, CommandManager.RegistrationEnvironment environment) {
    
    for (String commandAlias : Main.INSTANCE.CommandAlias) {
        dispatcher.register(
          CommandManager.literal(commandAlias)
          .executes(command -> execute(command))
          
          .then(
            CommandManager.literal("info")
            .executes(Info::execute)
          )
          
          .then(
            CommandManager.literal("reload")
            .requires(source -> Permissions.hasPermission(Permissions.RELOAD, source))
            .executes(Reload::execute)
          )
          
          .then(
            CommandManager.literal("clear")
            .requires(source -> Permissions.hasPermission(Permissions.CLEAR, source))
            .then(CommandManager.argument("type", StringArgumentType.string())
              .suggests((command, builder) -> {
                builder.suggest("capture");
                builder.suggest("hatch");
                builder.suggest("trade");
                builder.suggest("release");
                return builder.buildFuture();
              })
            .executes(Clear::execute)))
          
          .then(
            CommandManager.literal("open")
            .requires(source -> source.isExecutedByPlayer() && Permissions.hasPermission(Permissions.OPEN, source))
            .then(CommandManager.argument("type", StringArgumentType.string())
              .suggests((command, builder) -> {
                builder.suggest("capture");
                builder.suggest("hatch");
                builder.suggest("trade");
                builder.suggest("release");
                return builder.buildFuture();
              })
            .then(CommandManager.argument("filterPlayer", StringArgumentType.string())
              .suggests((command, builder) -> {
                builder.suggest("all");
                PokeLog.getServer().getPlayerManager().getPlayerList().forEach(player -> builder.suggest(player.getName().getString()));
                return builder.buildFuture();
              })
            .then(CommandManager.argument("filterProperties", StringArgumentType.greedyString())
              .suggests((command, builder) -> {
                builder.suggest("all");
                PokemonPropertiesArgumentType.Companion.properties().listSuggestions(command, builder);
                return builder.buildFuture();
              })
            .executes(Open::execute))))
          )
        );
    }
  }
  private static int execute(CommandContext<ServerCommandSource> command) {
    Info.execute(command);
    return Command.SINGLE_SUCCESS;
  }
}