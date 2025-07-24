package io.github.joagar21.pokelog.listeners;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;

import io.github.joagar21.pokelog.PokeLog;
import io.github.joagar21.pokelog.configurations.MainConfig;
import io.github.joagar21.pokelog.utilities.Concurrency;

import kotlin.Unit;

import net.minecraft.nbt.NbtCompound;

public class Listeners {

  public static void register() {
    
    CobblemonEvents.POKEMON_RELEASED_EVENT_POST.subscribe(Priority.NORMAL, event -> {
      String player = event.getPlayer().getUuidAsString();
      
      if (!MainConfig.INSTANCE.PlayerWhitelist.contains(player)) {
         NbtCompound nbt = event.getPokemon().saveToNBT(PokeLog.getServer().getRegistryManager(), new NbtCompound());
         Concurrency.runAsync(() -> PokeLog.getDatabase().addReleaseLog(player, nbt.toString()));
      }
      return Unit.INSTANCE;
    });
    
    CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.NORMAL, event -> {
      String player = event.getPlayer().getUuidAsString();
      
      if (!MainConfig.INSTANCE.PlayerWhitelist.contains(player)) {
         NbtCompound nbt = event.getPokemon().saveToNBT(PokeLog.getServer().getRegistryManager(), new NbtCompound());
         Concurrency.runAsync(() -> PokeLog.getDatabase().addCaptureLog(player, nbt.toString()));
      }
      return Unit.INSTANCE;
    });
    
    CobblemonEvents.HATCH_EGG_POST.subscribe(Priority.NORMAL, event -> {
      String player = event.getPlayer().getUuidAsString();
      
      if (!MainConfig.INSTANCE.PlayerWhitelist.contains(player)) {
         NbtCompound nbt = event.getEgg().saveToNBT(PokeLog.getServer().getRegistryManager());
         Concurrency.runAsync(() -> PokeLog.getDatabase().addHatchLog(player, nbt.toString()));
      }
      return Unit.INSTANCE;
    });
    
    CobblemonEvents.TRADE_COMPLETED.subscribe(Priority.NORMAL, event -> {
      String participant1 = event.getTradeParticipant1().getUuid().toString();
      String participant2 = event.getTradeParticipant2().getUuid().toString();
      
      if (MainConfig.INSTANCE.PlayerWhitelist.contains(participant1) || MainConfig.INSTANCE.PlayerWhitelist.contains(participant2)) {
         return Unit.INSTANCE;
      }
      NbtCompound participant1SentPokemon = event.getTradeParticipant2Pokemon().saveToNBT(PokeLog.getServer().getRegistryManager(), new NbtCompound());
      NbtCompound participant2SentPokemon = event.getTradeParticipant1Pokemon().saveToNBT(PokeLog.getServer().getRegistryManager(), new NbtCompound());
      
      Concurrency.runAsync(() -> {
        PokeLog.getDatabase().addTradeLog(participant1, participant2, participant1SentPokemon.toString());
        PokeLog.getDatabase().addTradeLog(participant2, participant1, participant2SentPokemon.toString());
      });
      return Unit.INSTANCE;
    });
  }
}
