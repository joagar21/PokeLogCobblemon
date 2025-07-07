package io.github.joagar21.pokelog.utilities;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.github.joagar21.pokelog.PokeLog;

public class Concurrency {
  
  private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(5, new ThreadFactoryBuilder()
  .setDaemon(true)
  .setNameFormat(PokeLog.MODID +"-%d")
  .setUncaughtExceptionHandler((thread, throwable) -> PokeLog.getLogger().error("Error while executing async task: "+ throwable))
  .build());
  
  public static CompletableFuture<Void> runAsync(Runnable runnable) {
    return CompletableFuture.runAsync(runnable, EXECUTOR)
    .exceptionally(throwable -> {
      PokeLog.getLogger().error("Error while executing async task: "+ throwable);
      return null;
    });
  }
}