package io.github.joagar21.pokelog.utilities;

public class LogFormat {
  
  public static class CaptureFormat {
    
    private long time;
    private String player;
    private String properties;
    
    public CaptureFormat(long time, String player, String properties) {
      this.time = time;
      this.player = player;
      this.properties = properties;
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
    public String getProperties() {
      return properties;
    }
    public void setProperties(String properties) {
      this.properties = properties;
    }
  }
  public static class HatchFormat {
    
    private long time;
    private String player;
    private String properties;
    
    public HatchFormat(long time, String player, String properties) {
      this.time = time;
      this.player = player;
      this.properties = properties;
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
    public String getProperties() {
      return properties;
    }
    public void setProperties(String properties) {
      this.properties = properties;
    }
  }
  public static class TradeFormat {
    
    private long time;
    private String player;
    private String tradedTo;
    private String properties;
    
    public TradeFormat(long time, String player, String tradedTo, String properties) {
      this.time = time;
      this.player = player;
      this.tradedTo = tradedTo;
      this.properties = properties;
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
    public String getProperties() {
      return properties;
    }
    public void setProperties(String properties) {
      this.properties = properties;
    }
  }
}