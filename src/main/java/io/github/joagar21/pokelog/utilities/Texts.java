package io.github.joagar21.pokelog.utilities;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.joagar21.pokelog.PokeLog;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class Texts {
  
  private static final Pattern COLOUR_PATTERN = Pattern.compile("&(#\\w{6}|[\\da-zA-Z])");
  
  public static MutableText color(String text) {
    
    if (text.contains("{")) {
       try {
           return Text.Serialization.fromJson(text, PokeLog.getServer().getRegistryManager());
       } catch (Exception ignored) {}
    }
    int lastEnd = 0;
    TextColor lastColor = null;
    Formatting nextApply = null;
    Matcher matcher = COLOUR_PATTERN.matcher(text);
    MutableText textLiteral = Text.literal("");
    
    while (matcher.find()) {
      int start = matcher.start();
      String segment = text.substring(lastEnd, start);
      MutableText iFormattableTextComponent = attemptAppend(textLiteral, segment, lastColor);
      
      if (nextApply != null && iFormattableTextComponent != null) {
         iFormattableTextComponent.setStyle(Style.EMPTY.withColor(lastColor).withFormatting(nextApply));
      }
      lastEnd = matcher.end();
      String colourCode = matcher.group(1);
      Optional<TextColor> colour = parseColour(colourCode);
      
      if (colour.isPresent()) {
         lastColor = colour.get();
         nextApply = null;
      } else {
         Formatting byCode = getByCode(colourCode.toCharArray()[0]);
        
         if (byCode != null) {
            nextApply = byCode;
         } else {
            textLiteral.append(Text.literal("&" + colourCode));
         }
      }
    }
    String segment = text.substring(lastEnd);
    MutableText iFormattableTextComponent = attemptAppend(textLiteral, segment, lastColor);
    
    if (nextApply != null && iFormattableTextComponent != null) {
       iFormattableTextComponent.setStyle(Style.EMPTY.withColor(lastColor).withFormatting(nextApply));
    }
    return textLiteral;
  }
  private static Optional<TextColor> parseColour(String colourCode) {
    
    TextColor colour = TextColor.parse(colourCode).result().orElse(null);
    
    if (colour != null) {
       return Optional.of(colour);
    }
    if (colourCode.length() > 1) {
       return Optional.empty();
    }
    Formatting byCode = getByCode(colourCode.toCharArray()[0]);
    
    if (byCode == null) {
       return Optional.empty();
    }
    return Optional.ofNullable(TextColor.fromFormatting(byCode));
  }
  private static MutableText attemptAppend(MutableText textLiteral, String segment, TextColor lastColour) {
    
    if (segment.isEmpty()) {
       return null;
    }
    MutableText appended = Text.literal(segment);
    
    if (lastColour != null) {
       appended.setStyle(Style.EMPTY.withColor(lastColour));
    }
    textLiteral.append(appended);
    return appended;
  }
  private static Formatting getByCode(char p_211165_0_) {
    
    char c0 = Character.toString(p_211165_0_).toLowerCase(Locale.ROOT).charAt(0);
    
    switch (c0) {
      case '0': return Formatting.BLACK;
      case '1': return Formatting.DARK_BLUE;
      case '2': return Formatting.DARK_GREEN;
      case '3': return Formatting.DARK_AQUA;
      case '4': return Formatting.DARK_RED;
      case '5': return Formatting.DARK_PURPLE;
      case '6': return Formatting.GOLD;
      case '7': return Formatting.GRAY;
      case '8': return Formatting.DARK_GRAY;
      case '9': return Formatting.BLUE;
      case 'a': return Formatting.GREEN;
      case 'b': return Formatting.AQUA;
      case 'c': return Formatting.RED;
      case 'd': return Formatting.LIGHT_PURPLE;
      case 'e': return Formatting.YELLOW;
      case 'f': return Formatting.WHITE;
      case 'k': return Formatting.OBFUSCATED;
      case 'l': return Formatting.BOLD;
      case 'm': return Formatting.STRIKETHROUGH;
      case 'n': return Formatting.UNDERLINE;
      case 'o': return Formatting.ITALIC;
      case 'r': return Formatting.RESET;
    }
    return null;
  }
}