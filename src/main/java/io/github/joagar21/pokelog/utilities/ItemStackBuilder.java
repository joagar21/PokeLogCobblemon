package io.github.joagar21.pokelog.utilities;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ItemStackBuilder {
  
  public static Builder builder() {
    return new Builder();
  }
  public static ItemStack getItemStack(Item item) {
    return builder().item(item).hideDetails().build();
  }
  public static ItemStack getItemStack(String item) {
    return builder().item(item).hideDetails().build();
  }
  public static ItemStack getItemStack(ItemStack stack) {
    return builder().stack(stack).hideDetails().build();
  }
  public static class Builder {
    
    private boolean hideDetails = false;
    private boolean enchanted = false;
    private int count = 1;
    private Item item = Items.AIR;
    private ItemStack stack = ItemStack.EMPTY;
    private MutableText name = Text.empty();
    private List<MutableText> lore = Lists.newArrayList();
    private List<String> nbt = Lists.newArrayList();
    
    public Builder hideDetails() {
      hideDetails = true;
      return this;
    }
    public Builder enchanted(Boolean enchanted) {
      if (enchanted != null) this.enchanted = enchanted;
      return this;
    }
    public Builder count(Integer count) {
      if (count != null) this.count = count;
      return this;
    }
    public Builder item(Item item) {
      if (item != null) this.item = item;
      return this;
    }
    public Builder item(String item) {
      if (item != null) this.item =  Registries.ITEM.get(Identifier.of(item));
      return this;
    }
    public Builder stack(ItemStack stack) {
      if (stack != null) this.stack = stack;
      return this;
    }
    public Builder name(String name) {
      if (name != null && !name.isBlank()) this.name = Texts.color(name);
      return this;
    }
    public Builder lore(List<String> lore) {
      if (lore != null && !lore.isEmpty()) this.lore = lore.stream().map(str -> Texts.color(str)).collect(Collectors.toList());
      return this;
    }
    public Builder nbt(List<String> nbt) {
      if (nbt != null && !nbt.isEmpty()) this.nbt = nbt;
      return this;
    }
    public ItemStack build() {
      
      ItemStack stack = new ItemStack(item, count);
      if (!this.stack.isEmpty()) stack = this.stack;
      
      if (hideDetails) {
         stack.set(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
      }
      if (enchanted) {
         stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
      }
      if (name.getString().isBlank()) {
         stack.set(DataComponentTypes.HIDE_TOOLTIP, Unit.INSTANCE);
      } else {
         stack.set(DataComponentTypes.ITEM_NAME, name);
      }
      if (!lore.isEmpty()) {
         List<Text> textLore = Lists.newArrayList();
         this.lore.forEach(lore -> textLore.add(lore.setStyle(lore.getStyle().withItalic(false))));
         stack.set(DataComponentTypes.LORE, new LoreComponent(textLore));
      }
      if (!nbt.isEmpty()) {
         NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
         NbtCompound customNBT = customData != null ? customData.copyNbt() : new NbtCompound();
         
         for (String str : nbt) {
             String[] split = str.split("/", 3);
             String datatype = split[0];
             String key = split[1];
             String value = split[2];
             
             if (datatype.equals("String")) customNBT.putString(key, value);
             else if (datatype.equals("Byte")) customNBT.putByte(key, Byte.parseByte(value));
             else if (datatype.equals("Long")) customNBT.putLong(key, Long.parseLong(value));
             else if (datatype.equals("Short")) customNBT.putShort(key, Short.parseShort(value));
             else if (datatype.equals("Float")) customNBT.putFloat(key, Float.parseFloat(value));
             else if (datatype.equals("Integer")) {
                if (key.equals("custom_model_data")) stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(Integer.parseInt(value)));
                else customNBT.putInt(key, Integer.parseInt(value));
             }
         }
         if (!customNBT.isEmpty()) stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(customNBT));
      }
      return stack;
    }
  }
}