package io.github.joagar21.pokelog.utilities;

import java.util.Optional;
import java.util.UUID;

import com.cobblemon.mod.common.pokemon.Pokemon;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import io.github.joagar21.pokelog.PokeLog;

import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class Utilities {
  
  public static void sendMessage(ServerPlayerEntity player, String message) {
    if (!message.isBlank()) player.sendMessage(Texts.color(message));
  }
  public static void sendMessage(ServerCommandSource source, String message) {
    if (!message.isBlank()) source.sendMessage(Texts.color(message));
  }
  public static String capitalizeFirstLetter(String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }
  public static String getTranslatedText(String translationKey) {
    return Text.translatable(translationKey).getString();
  }
  public static String getTranslatedText(String type, Identifier identifier) {
    return getTranslatedText(Util.createTranslationKey(type, identifier));
  }
  public static Pokemon getPokemonFromNbt(String nbt) {
    Pokemon pokemon = new Pokemon();
    try {
        pokemon.loadFromNBT(PokeLog.getServer().getRegistryManager(), NbtHelper.fromNbtProviderString(nbt));
    } catch (CommandSyntaxException e) {}
    return pokemon;
  }
  public static String getPlayerNameByUUID(String uuid) {
    
    MinecraftServer server = PokeLog.getServer();
    ServerPlayerEntity player = server.getPlayerManager().getPlayer(UUID.fromString(uuid));
    
    if (player != null) {
       return player.getName().getString();
    } else {
       Optional<GameProfile> profile = server.getUserCache().getByUuid(UUID.fromString(uuid));
       
       if (profile.isPresent()) {
          return profile.get().getName();
       } else {
          return "Unknown Player";
       }
    }
  }
}