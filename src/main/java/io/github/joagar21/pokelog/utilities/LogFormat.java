package io.github.joagar21.pokelog.utilities;

public class LogFormat {
  
  public static class BaseFormat {
    
    private long time;
    private String player;
    private String nbt;
    private String extra;
    
    public BaseFormat(long time, String player, String nbt, String extra) {
      this.time = time;
      this.player = player;
      this.nbt = nbt;
      this.extra = extra;
    }
    public long getTime() {
      return time;
    }
    public void setTime(long time) {
      this.time = time;
    }
    public String getPlayer() {
      return player;
    }
    public void setPlayer(String player) {
      this.player = player;
    }
    public String getNbt() {
      return nbt;
    }
    public void setNbt(String nbt) {
      this.nbt = nbt;
    }
    public String getExtra() {
      return extra;
    }
    public void setExtra(String extra) {
      this.extra = extra;
    }
  }
  public static class TradeFormat extends BaseFormat {
    
    private String tradedTo;
    
    public TradeFormat(long time, String player, String tradedTo, String nbt, String extra) {
      super(time, player, nbt, extra);
      this.tradedTo = tradedTo;
    }
    public String getTradedTo() {
      return tradedTo;
    }
    public void setTradedTo(String tradedTo) {
      this.tradedTo = tradedTo;
    }
  }
}