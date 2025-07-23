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
import java.util.List;

public class MainConfig {

    public static final String PATH = "config/PokeLog/main.json";
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static MainConfig INSTANCE = new MainConfig();

    public List<String> CommandAlias = Lists.newArrayList("pokelog", "plog");
    public String TimeZone = "GMT+8";
    public String TimeFormat = "h:mm a M/d/yyyy";
    public List<String> PlayerWhitelist = Lists.newArrayList(
            "7b0b5d51-4786-462b-9866-c71ee6f8604e",
            "7b0b5d51-4786-462b-9866-c71ee6f8604e"
    );

    public boolean useMariaDB = false;
    public String dbHost = "localhost";
    public int dbPort = 3306;
    public String dbName = "pokelog";
    public String dbUser = "root";
    public String dbPassword = "password";

    public static void save() {
        File file = new File(PATH);
        file.getParentFile().mkdirs();

        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            osw.write(GSON.toJson(INSTANCE));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void load() {
        File file = new File(PATH);

        try {
            if (!file.exists()) {
                save();
            } else {
                try (InputStreamReader isr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                    INSTANCE = GSON.fromJson(isr, MainConfig.class);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}