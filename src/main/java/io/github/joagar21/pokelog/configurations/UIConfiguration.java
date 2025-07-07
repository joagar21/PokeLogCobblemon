package io.github.joagar21.pokelog.configurations;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;

public class UIConfiguration {
  
  public static final String PATH = "config/PokeLog/user-interface.json";
  public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
  public static UIConfiguration INSTANCE = new UIConfiguration();
  
  public String NoneText = "&bNone";
  public String HiddenAbilityText = " &b(Hidden Ability)";
  public String LogPokemonSpriteTitle = "&a%player% (%date%)";
  public String TradeLogPokemonSpriteTitle = "&a%player% traded to %traded_to% (%date%)";
  public List<String> PokemonSpriteLore = Lists.newArrayList(
  "&e%pokemon% %nickname%",
  " ",
  "&6Level: &b%level%",
  "&6Dynamax Level: &b%dynamax_level%",
  "&6Friendship: &b%friendship%",
  "&6Nature: &b%nature%",
  "&6Ability: &b%ability%",
  " ",
  "&6Size: &b%size%",
  "&6Poké Ball: &b%ball%",
  "&6Held Item: &b%held_item%",
  "&6Original Trainer: &b%original_trainer%",
  "&6Aspects: &b%aspects%",
  " ",
  "&6IVs: &b%iv_hp%&7/&b%iv_atk%&7/&b%iv_def%&7/&b%iv_spe%&7/&b%iv_spa%&7/&b%iv_spd% &b(%iv_percentage%%)",
  "&6EVs: &b%ev_hp%&7/&b%ev_atk%&7/&b%ev_def%&7/&b%ev_spe%&7/&b%ev_spa%&7/&b%ev_spd% &b(%ev_total%)",
  " ",
  "&6Moves: &b%move_1%&7/&b%move_2%&7/&b%move_3%&7/&b%move_4%",
  " ",
  "&7Click to retrieve the pokemon.");
  
  public int UserInterfaceRows = 6;
  public String UserInterfaceTitle = "&#404040%type% Logs";
  public LinkedHashMap<Integer, UserInterfaceFormat> UserInterface = new LinkedHashMap<>();
  
  public static void save() {
    
    File file = new File(PATH);
    file.getParentFile().mkdirs();
    
    try {
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        osw.write(GSON.toJson(INSTANCE));
        osw.flush();
        osw.close();
    } catch (IOException ioe) {
        ioe.printStackTrace();
    }
  }
  public static void load() {
  
    File file = new File(PATH);
    
    try {
        if (!file.exists()) { 
           INSTANCE.loadDefaultValues();
           save();
        } else {
           InputStreamReader isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
           INSTANCE = GSON.fromJson(isr, UIConfiguration.class);
           isr.close();
        } 
    } catch (IOException ioe) {
        ioe.printStackTrace();
    }
  }
  private void loadDefaultValues() {
    
    UserInterfaceFormat redPane = new UserInterfaceFormat();
    redPane.setDisplayItem("minecraft:red_stained_glass_pane");
    
    UserInterfaceFormat blackPane = new UserInterfaceFormat();
    blackPane.setDisplayItem("minecraft:black_stained_glass_pane");
    
    UserInterfaceFormat whitePane = new UserInterfaceFormat();
    whitePane.setDisplayItem("minecraft:white_stained_glass_pane");
    
    UserInterfaceFormat previous = new UserInterfaceFormat();
    previous.setType("previous");
    previous.setDisplayItem("minecraft:spectral_arrow");
    previous.setDisplayName("&6Previous");
    
    UserInterfaceFormat next = new UserInterfaceFormat();
    next.setType("next");
    next.setDisplayItem("minecraft:spectral_arrow");
    next.setDisplayName("&6Next");
    
    UserInterface.put(0, redPane);
    UserInterface.put(1, redPane);
    UserInterface.put(2, redPane);
    UserInterface.put(3, redPane);
    UserInterface.put(4, redPane);
    UserInterface.put(5, redPane);
    UserInterface.put(6, redPane);
    UserInterface.put(7, redPane);
    UserInterface.put(8, redPane);
    UserInterface.put(9, redPane);
    UserInterface.put(17, redPane);
    UserInterface.put(18, blackPane);
    UserInterface.put(26, blackPane);
    UserInterface.put(27, blackPane);
    UserInterface.put(35, blackPane);
    UserInterface.put(36, whitePane);
    UserInterface.put(44, whitePane);
    UserInterface.put(45, whitePane);
    UserInterface.put(46, whitePane);
    UserInterface.put(47, whitePane);
    UserInterface.put(48, previous);
    UserInterface.put(49, whitePane);
    UserInterface.put(50, next);
    UserInterface.put(51, whitePane);
    UserInterface.put(52, whitePane);
    UserInterface.put(53, whitePane);
  }
  public static class UserInterfaceFormat {
    
    private String Type;
    private Boolean DisplayEnchanted;
    private Integer DisplayCount;
    private String DisplayItem;
    private String DisplayName;
    private List<String> DisplayLore;
    private List<String> DisplayNbt;
    private List<String> Commands;
    
    public String getType() {
      return Type;
    }
    public void setType(String type) {
      Type = type;
    }
    public Boolean isDisplayEnchanted() {
      return DisplayEnchanted;
    }
    public void setDisplayEnchanted(Boolean displayEnchanted) {
      DisplayEnchanted = displayEnchanted;
    }
    public Integer getDisplayCount() {
      return DisplayCount;
    }
    public void setDisplayCount(Integer displayCount) {
      DisplayCount = displayCount;
    }
    public String getDisplayItem() {
      return DisplayItem;
    }
    public void setDisplayItem(String displayItem) {
      DisplayItem = displayItem;
    }
    public String getDisplayName() {
      return DisplayName;
    }
    public void setDisplayName(String displayName) {
      DisplayName = displayName;
    }
    public List<String> getDisplayLore() {
      return DisplayLore;
    }
    public void setDisplayLore(List<String> displayLore) {
      DisplayLore = displayLore;
    }
    public List<String> getDisplayNbt() {
      return DisplayNbt;
    }
    public void setDisplayNbt(List<String> displayNbt) {
      DisplayNbt = displayNbt;
    }
    public List<String> getCommands() {
      return Commands;
    }
    public void setCommands(List<String> commands) {
      Commands = commands;
    }
  }
}