package io.github.joagar21.pokelog.listeners;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;

import io.github.joagar21.pokelog.PokeLog;
import io.github.joagar21.pokelog.configurations.Main;
import io.github.joagar21.pokelog.utilities.Concurrency;

import kotlin.Unit;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

public class Listeners {

  public static void register() {
    
    CobblemonEvents.POKEMON_RELEASED_EVENT_POST.subscribe(Priority.NORMAL, event -> {
      ServerPlayerEntity player = event.getPlayer();
      
      if (!Main.INSTANCE.PlayerWhitelist.contains(player.getUuidAsString())) {
         NbtCompound nbt = event.getPokemon().saveToNBT(PokeLog.getServer().getRegistryManager(), new NbtCompound());
         Concurrency.runAsync(() -> PokeLog.getDatabase().addBaseLog("release", player.getUuidAsString(), nbt.toString(), getExtraInfo(player)));
      }
      return Unit.INSTANCE;
    });
    
    CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.NORMAL, event -> {
      ServerPlayerEntity player = event.getPlayer();
      
      if (!Main.INSTANCE.PlayerWhitelist.contains(player.getUuidAsString())) {
         NbtCompound nbt = event.getPokemon().saveToNBT(PokeLog.getServer().getRegistryManager(), new NbtCompound());
         Concurrency.runAsync(() -> PokeLog.getDatabase().addBaseLog("capture", player.getUuidAsString(), nbt.toString(), getExtraInfo(player)));
      }
      return Unit.INSTANCE;
    });
    
    CobblemonEvents.HATCH_EGG_POST.subscribe(Priority.NORMAL, event -> {
      ServerPlayerEntity player = event.getPlayer();
      
      if (!Main.INSTANCE.PlayerWhitelist.contains(player.getUuidAsString())) {
         NbtCompound nbt = event.getPokemon().saveToNBT(PokeLog.getServer().getRegistryManager(), new NbtCompound());
         Concurrency.runAsync(() -> PokeLog.getDatabase().addBaseLog("hatch", player.getUuidAsString(), nbt.toString(), getExtraInfo(player)));
      }
      return Unit.INSTANCE;
    });
    
    CobblemonEvents.TRADE_EVENT_POST.subscribe(Priority.NORMAL, event -> {
      Pokemon pokemon1 = event.getTradeParticipant1Pokemon();
      Pokemon pokemon2 = event.getTradeParticipant2Pokemon();
      
      ServerPlayerEntity player1 = pokemon1.getOwnerPlayer();
      ServerPlayerEntity player2 = pokemon2.getOwnerPlayer();
      
      if (Main.INSTANCE.PlayerWhitelist.contains(player1.getUuidAsString()) || Main.INSTANCE.PlayerWhitelist.contains(player2.getUuidAsString())) {
         return Unit.INSTANCE;
      }
      Concurrency.runAsync(() -> {
        PokeLog.getDatabase().addTradeLog(player1.getUuidAsString(), player2.getUuidAsString(), pokemon2.saveToNBT(PokeLog.getServer().getRegistryManager(), new NbtCompound()).toString(), getExtraInfo(player1));
        PokeLog.getDatabase().addTradeLog(player2.getUuidAsString(), player1.getUuidAsString(), pokemon1.saveToNBT(PokeLog.getServer().getRegistryManager(), new NbtCompound()).toString(), getExtraInfo(player2));
      });
      return Unit.INSTANCE;
    });
  }
  private static String getExtraInfo(ServerPlayerEntity player) {
    return player.getServerWorld().getRegistryKey().getValue().toString() +"||"+ player.getX() +"||"+ player.getY() +"||"+ player.getZ();
  }
}