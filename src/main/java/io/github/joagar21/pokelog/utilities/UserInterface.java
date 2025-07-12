package io.github.joagar21.pokelog.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.Optional;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.moves.MoveSet;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.EVs;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.abilities.HiddenAbilityType;

import com.google.common.collect.Lists;

import com.mojang.authlib.GameProfile;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.button.linked.LinkType;
import ca.landonjw.gooeylibs2.api.button.linked.LinkedPageButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.page.LinkedPage.Builder;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;

import io.github.joagar21.pokelog.PokeLog;
import io.github.joagar21.pokelog.configurations.Main;
import io.github.joagar21.pokelog.configurations.UIConfiguration;
import io.github.joagar21.pokelog.configurations.UIConfiguration.UserInterfaceFormat;
import io.github.joagar21.pokelog.utilities.LogFormat.CaptureFormat;
import io.github.joagar21.pokelog.utilities.LogFormat.HatchFormat;
import io.github.joagar21.pokelog.utilities.LogFormat.ReleaseFormat;
import io.github.joagar21.pokelog.utilities.LogFormat.TradeFormat;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class UserInterface {
  
  public static void open(String type, String filterPlayer, String filterProperties, ServerPlayerEntity player) {
    
    ChestTemplate template = ChestTemplate.builder(UIConfiguration.INSTANCE.UserInterfaceRows).fill(new PlaceholderButton()).build();
    
    for (Entry<Integer, UserInterfaceFormat> entry : UIConfiguration.INSTANCE.UserInterface.entrySet()) {
        UserInterfaceFormat format = entry.getValue();
        
        switch (format.getType() == null ? "none" : format.getType()) {
          
          case "previous":
            template.set(entry.getKey(), LinkedPageButton.builder()
            .linkType(LinkType.Previous)
            .display(getItemStack(format))
            .build());
          break;
          
          case "next":
            template.set(entry.getKey(), LinkedPageButton.builder()
            .linkType(LinkType.Next)
            .display(getItemStack(format))
            .build());
          break;
          
          default:
            template.set(entry.getKey(), GooeyButton.of(getItemStack(format)));
          break;
        }
    }
    Builder builder = LinkedPage.builder().title(Texts.color(UIConfiguration.INSTANCE.UserInterfaceTitle.replace("%type%", Utilities.capitalizeFirstLetter(type))));
    LinkedPage page = PaginationHelper.createPagesFromPlaceholders(template, getLogButtons(type, filterPlayer, filterProperties, player), builder);
    PokeLog.getServer().execute(() -> UIManager.openUIForcefully(player, page));
  }
  private static List<Button> getLogButtons(String type, String filterPlayer, String filterProperties, ServerPlayerEntity player) {
    
    List<Button> buttons = Lists.newArrayList();
    SimpleDateFormat dateFormat = new SimpleDateFormat(Main.INSTANCE.TimeFormat);
    dateFormat.setTimeZone(TimeZone.getTimeZone(Main.INSTANCE.TimeZone));
    
    if (!filterPlayer.equals("all")) {
       Optional<GameProfile> profile = PokeLog.getServer().getUserCache().findByName(filterPlayer);
       if (profile.isPresent()) filterPlayer = profile.get().getId().toString();
    }
    if (type.equals("capture")) {
       for (CaptureFormat format : PokeLog.getDatabase().getCaptureLogs(filterPlayer, filterProperties.split(" "))) {
           Pokemon pokemon = PokemonProperties.Companion.parse(format.getProperties()).create();
           
           buttons.add(GooeyButton.builder()
           .display(ItemStackBuilder.builder()
             .stack(PokemonItem.from(pokemon))
             .name(UIConfiguration.INSTANCE.LogPokemonSpriteTitle.replace("%player%", Utilities.getPlayerNameByUUID(format.getPlayer())).replace("%date%", dateFormat.format(new Date(format.getTime()))))
             .lore(UIConfiguration.INSTANCE.PokemonSpriteLore.stream().map(s -> parsePokemonPlaceholders(s, pokemon)).toList())
             .build())
           .onClick(() -> {
             UIManager.closeUI(player);
             Cobblemon.INSTANCE.getStorage().getParty(player).add(pokemon);
           })
           .build());
       }
    }
    else if (type.equals("hatch")) {
       for (HatchFormat format : PokeLog.getDatabase().getHatchLogs(filterPlayer, filterProperties.split(" "))) {
           Pokemon pokemon = PokemonProperties.Companion.parse(format.getProperties()).create();
           
           buttons.add(GooeyButton.builder()
           .display(ItemStackBuilder.builder()
             .stack(PokemonItem.from(pokemon))
             .name(UIConfiguration.INSTANCE.LogPokemonSpriteTitle.replace("%player%", Utilities.getPlayerNameByUUID(format.getPlayer())).replace("%date%", dateFormat.format(new Date(format.getTime()))))
             .lore(UIConfiguration.INSTANCE.PokemonSpriteLore.stream().map(s -> parsePokemonPlaceholders(s, pokemon)).toList())
             .build())
           .onClick(() -> {
             UIManager.closeUI(player);
             Cobblemon.INSTANCE.getStorage().getParty(player).add(pokemon);
           })
           .build());
       }
    }
    else if (type.equals("trade")) {
       for (TradeFormat format : PokeLog.getDatabase().getTradeLogs(filterPlayer, filterProperties.split(" "))) {
           Pokemon pokemon = PokemonProperties.Companion.parse(format.getProperties()).create();
           
           buttons.add(GooeyButton.builder()
           .display(ItemStackBuilder.builder()
             .stack(PokemonItem.from(pokemon))
             .name(UIConfiguration.INSTANCE.TradeLogPokemonSpriteTitle.replace("%player%", Utilities.getPlayerNameByUUID(format.getPlayer())).replace("%traded_to%", Utilities.getPlayerNameByUUID(format.getTradedTo())).replace("%date%", dateFormat.format(new Date(format.getTime()))))
             .lore(UIConfiguration.INSTANCE.PokemonSpriteLore.stream().map(s -> parsePokemonPlaceholders(s, pokemon)).toList())
             .build())
           .onClick(() -> {
             UIManager.closeUI(player);
             Cobblemon.INSTANCE.getStorage().getParty(player).add(pokemon);
           })
           .build());
       }
    }
    else if (type.equals("release")) {
       for (ReleaseFormat format : PokeLog.getDatabase().getReleaseLogs(filterPlayer, filterProperties.split(" "))) {
           Pokemon pokemon = PokemonProperties.Companion.parse(format.getProperties()).create();
           
           buttons.add(GooeyButton.builder()
           .display(ItemStackBuilder.builder()
             .stack(PokemonItem.from(pokemon))
             .name(UIConfiguration.INSTANCE.LogPokemonSpriteTitle.replace("%player%", Utilities.getPlayerNameByUUID(format.getPlayer())).replace("%date%", dateFormat.format(new Date(format.getTime()))))
             .lore(UIConfiguration.INSTANCE.PokemonSpriteLore.stream().map(s -> parsePokemonPlaceholders(s, pokemon)).toList())
             .build())
           .onClick(() -> {
             UIManager.closeUI(player);
             Cobblemon.INSTANCE.getStorage().getParty(player).add(pokemon);
           })
           .build());
       }
    }
    return buttons;
  }
  private static ItemStack getItemStack(UserInterfaceFormat format) {
    return ItemStackBuilder.builder()
    .hideDetails()
    .enchanted(format.isDisplayEnchanted())
    .count(format.getDisplayCount())
    .item(format.getDisplayItem())
    .name(format.getDisplayName())
    .lore(format.getDisplayLore())
    .nbt(format.getDisplayNbt())
    .build();
  }
  private static int getTotalEvs(Pokemon pokemon) {
    EVs evs = pokemon.getEvs();
    return evs.get(Stats.HP) + evs.get(Stats.ATTACK) + evs.get(Stats.DEFENCE) + evs.get(Stats.SPEED) + evs.get(Stats.SPECIAL_ATTACK) + evs.get(Stats.SPECIAL_DEFENCE);
  }
  private static int getIvsPercentage(Pokemon pokemon) {
    IVs ivs = pokemon.getIvs();
    int totalIVs = ivs.get(Stats.HP) + ivs.get(Stats.ATTACK) + ivs.get(Stats.DEFENCE) + ivs.get(Stats.SPECIAL_ATTACK) + ivs.get(Stats.SPECIAL_DEFENCE) + ivs.get(Stats.SPEED);
    double ivAverage = totalIVs / 186.0;
    return (int) Math.round(ivAverage * 10000) / 100;
  }
  private static boolean hasHiddenAbility(Pokemon pokemon) {
    return pokemon.getForm().getAbilities().getMapping().values().stream()
    .flatMap(List::stream)
    .filter(potentialAbility -> potentialAbility.getType() == HiddenAbilityType.INSTANCE)
    .anyMatch(potentialAbility -> potentialAbility.getTemplate() == pokemon.getAbility().getTemplate());
  }
  private static String parsePokemonPlaceholders(String str, Pokemon pokemon) {
    
    IVs ivs = pokemon.getIvs();
    EVs evs = pokemon.getEvs();
    MoveSet moves = pokemon.getMoveSet();
    
    return str
    .replace("%pokemon%", pokemon.getSpecies().getTranslatedName().getString())
    .replace("%nickname%", pokemon.getNickname() == null ? "" : pokemon.getNickname().getString())
    .replace("%ability%", hasHiddenAbility(pokemon) ? Utilities.getTranslatedText(pokemon.getAbility().getDisplayName()) + UIConfiguration.INSTANCE.HiddenAbilityText : Utilities.getTranslatedText(pokemon.getAbility().getDisplayName()))
    .replace("%size%", String.valueOf(pokemon.getScaleModifier()))
    .replace("%nature%", Utilities.getTranslatedText(pokemon.getNature().getDisplayName()))
    .replace("%original_trainer%", pokemon.getOriginalTrainerName() == null ? UIConfiguration.INSTANCE.NoneText : pokemon.getOriginalTrainerName())
    .replace("%ball%", pokemon.getCaughtBall().item().getName().getString())
    .replace("%level%", String.valueOf(pokemon.getLevel()))
    .replace("%ev_hp%", String.valueOf(evs.getOrDefault(Stats.HP)))
    .replace("%ev_atk%", String.valueOf(evs.getOrDefault(Stats.ATTACK)))
    .replace("%ev_def%", String.valueOf(evs.getOrDefault(Stats.DEFENCE)))
    .replace("%ev_spe%", String.valueOf(evs.getOrDefault(Stats.SPEED)))
    .replace("%ev_spa%", String.valueOf(evs.getOrDefault(Stats.SPECIAL_ATTACK)))
    .replace("%ev_spd%", String.valueOf(evs.getOrDefault(Stats.SPECIAL_DEFENCE)))
    .replace("%ev_total%", String.valueOf(getTotalEvs(pokemon) +"/"+ EVs.MAX_TOTAL_VALUE))
    .replace("%iv_hp%", String.valueOf(ivs.getOrDefault(Stats.HP)))
    .replace("%iv_atk%", String.valueOf(ivs.getOrDefault(Stats.ATTACK)))
    .replace("%iv_def%", String.valueOf(ivs.getOrDefault(Stats.DEFENCE)))
    .replace("%iv_spe%", String.valueOf(ivs.getOrDefault(Stats.SPEED)))
    .replace("%iv_spa%", String.valueOf(ivs.getOrDefault(Stats.SPECIAL_ATTACK)))
    .replace("%iv_spd%", String.valueOf(ivs.getOrDefault(Stats.SPECIAL_DEFENCE)))
    .replace("%iv_percentage%", String.valueOf(getIvsPercentage(pokemon)))
    .replace("%aspects%", pokemon.getAspects().toString().replace("[", "").replace("]", ""))
    .replace("%friendship%", String.valueOf(pokemon.getFriendship()))
    .replace("%dynamax_level%", String.valueOf(pokemon.getDmaxLevel()))
    .replace("%held_item%", pokemon.heldItem().isEmpty() ? UIConfiguration.INSTANCE.NoneText : pokemon.heldItem().getName().getString())
    .replace("%move_1%", moves.get(0) == null ? UIConfiguration.INSTANCE.NoneText : moves.get(0).getDisplayName().getString())
    .replace("%move_2%", moves.get(1) == null ? UIConfiguration.INSTANCE.NoneText : moves.get(1).getDisplayName().getString())
    .replace("%move_3%", moves.get(2) == null ? UIConfiguration.INSTANCE.NoneText : moves.get(2).getDisplayName().getString())
    .replace("%move_4%", moves.get(3) == null ? UIConfiguration.INSTANCE.NoneText : moves.get(3).getDisplayName().getString());
  }
}