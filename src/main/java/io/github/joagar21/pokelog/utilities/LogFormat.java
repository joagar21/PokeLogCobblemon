package io.github.joagar21.pokelog.utilities;

public class LogFormat {
  
  public static class CaptureFormat {
    
    private long time;
    private String player;
    private String nbt;
    
    public CaptureFormat(long time, String player, String nbt) {
      this.time = time;
      this.player = player;
      this.nbt = nbt;
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
  }
  public static class HatchFormat {
    
    private long time;
    private String player;
    private String nbt;
    
    public HatchFormat(long time, String player, String nbt) {
      this.time = time;
      this.player = player;
      this.nbt = nbt;
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
  }
  public static class TradeFormat {
    
    private long time;
    private String player;
    private String tradedTo;
    private String nbt;
    
    public TradeFormat(long time, String player, String tradedTo, String nbt) {
      this.time = time;
      this.player = player;
      this.tradedTo = tradedTo;
      this.nbt = nbt;
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
    public String getTradedTo() {
      return tradedTo;
    }
    public void setTradedTo(String tradedTo) {
      this.tradedTo = tradedTo;
    }
    public String getNbt() {
      return nbt;
    }
    public void setNbt(String nbt) {
      this.nbt = nbt;
    }
  }
  public static class ReleaseFormat {
    
    private long time;
    private String player;
    private String nbt;
    
    public ReleaseFormat(long time, String player, String nbt) {
      this.time = time;
      this.player = player;
      this.nbt = nbt;
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
  }
}