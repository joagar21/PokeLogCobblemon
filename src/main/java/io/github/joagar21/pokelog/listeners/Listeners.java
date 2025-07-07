package io.github.joagar21.pokelog.listeners;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.pokemon.PokemonPropertyExtractor;

import io.github.joagar21.pokelog.PokeLog;
import io.github.joagar21.pokelog.configurations.Main;
import io.github.joagar21.pokelog.utilities.Concurrency;

import kotlin.Unit;

public class Listeners {

  public static void register() {
    
    CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.NORMAL, event -> {
      String player = event.getPlayer().getUuidAsString();
      
      if (!Main.INSTANCE.PlayerWhitelist.contains(player)) {
         String properties = event.getPokemon().createPokemonProperties(PokemonPropertyExtractor.ALL).asString(" ");
         Concurrency.runAsync(() -> PokeLog.getDatabase().addCaptureLog(player, properties));
      }
      return Unit.INSTANCE;
    });
    
    CobblemonEvents.HATCH_EGG_POST.subscribe(Priority.NORMAL, event -> {
      String player = event.getPlayer().getUuidAsString();
      
      if (!Main.INSTANCE.PlayerWhitelist.contains(player)) {
         String properties = event.getEgg().asString(" ");
         Concurrency.runAsync(() -> PokeLog.getDatabase().addHatchLog(player, properties));
      }
      return Unit.INSTANCE;
    });
    
    CobblemonEvents.TRADE_COMPLETED.subscribe(Priority.NORMAL, event -> {
      String participant1 = event.getTradeParticipant1().getUuid().toString();
      String participant2 = event.getTradeParticipant2().getUuid().toString();
      
      if (Main.INSTANCE.PlayerWhitelist.contains(participant1) || Main.INSTANCE.PlayerWhitelist.contains(participant2)) {
         return Unit.INSTANCE;
      }
      String participant1SentPokemon = event.getTradeParticipant2Pokemon().createPokemonProperties(PokemonPropertyExtractor.ALL).asString(" ");
      String participant2SentPokemon = event.getTradeParticipant1Pokemon().createPokemonProperties(PokemonPropertyExtractor.ALL).asString(" ");
      
      Concurrency.runAsync(() -> {
        PokeLog.getDatabase().addTradeLog(participant1, participant2, participant1SentPokemon);
        PokeLog.getDatabase().addTradeLog(participant2, participant1, participant2SentPokemon);
      });
      return Unit.INSTANCE;
    });
  }
}
