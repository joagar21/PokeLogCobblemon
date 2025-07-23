package io.github.joagar21.pokelog.utilities;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class Permissions {
  
  public static final String RELOAD = "pokelog.command.reload";
  public static final String CLEAR = "pokelog.command.clear";
  public static final String OPEN = "pokelog.command.open";
  public static final String RETRIEVE_POKEMON = "pokelog.pokemon.retrieve";
  
  public static boolean hasPermission(String permission, ServerPlayerEntity player) {
    return me.lucko.fabric.api.permissions.v0.Permissions.check(player, permission, 4);
  }
  public static boolean hasPermission(String permission, ServerCommandSource source) {
    return !source.isExecutedByPlayer() || hasPermission(permission, source.getPlayer());
  }
}