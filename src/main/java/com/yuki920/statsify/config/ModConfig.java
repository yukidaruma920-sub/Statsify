package com.yuki920.statsify.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.*;

public class ModConfig {
    private static final String CONFIG_PATH = "config/statsify.json";
    private static final ModConfig INSTANCE = new ModConfig();

    // 設定値（publicにして直接アクセス可能にするか、getterを作る）
    public int minFkdr = -1;
    public String mode = "bws";
    public boolean tags = false;
    public boolean tabstats = true;
    public boolean urchin = false;
    public String urchinkey = "";
    public boolean reqUUID = false;
    public boolean autowho = true;
    public String tabFormat = "bracket_star_name_dot_fkdr";
    public String hypixelApiKey = "";

    // コンストラクタをprivateにして勝手にnewできないようにする
    private ModConfig() {}

    public static ModConfig getInstance() {
        return INSTANCE;
    }

    public void load() {
        File configFile = new File(CONFIG_PATH);
        if (!configFile.exists()) {
            save();
            return;
        }

        try (Reader reader = new FileReader(configFile)) {
            JsonObject json = new Gson().fromJson(reader, JsonObject.class);
            if (json == null) return;
            
            if (json.has("minFkdr")) minFkdr = json.get("minFkdr").getAsInt();
            if (json.has("mode")) mode = json.get("mode").getAsString();
            if (json.has("tags")) tags = json.get("tags").getAsBoolean();
            if (json.has("tablist")) tabstats = json.get("tablist").getAsBoolean();
            if (json.has("urchin")) urchin = json.get("urchin").getAsBoolean();
            if (json.has("urchinkey")) urchinkey = json.get("urchinkey").getAsString();
            if (json.has("reqUUID")) reqUUID = json.get("reqUUID").getAsBoolean();
            if (json.has("autowho")) autowho = json.get("autowho").getAsBoolean();
            if (json.has("tabformat")) tabFormat = json.get("tabformat").getAsString();
            if (json.has("hypixelApiKey")) hypixelApiKey = json.get("hypixelApiKey").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        File configFile = new File(CONFIG_PATH);
        File parentDir = configFile.getParentFile();
        if (parentDir != null) parentDir.mkdirs();

        try (Writer writer = new FileWriter(configFile)) {
            JsonObject json = new JsonObject();
            json.addProperty("minFkdr", minFkdr);
            json.addProperty("mode", mode);
            json.addProperty("tags", tags);
            json.addProperty("tablist", tabstats);
            json.addProperty("urchin", urchin);
            json.addProperty("urchinkey", urchinkey);
            json.addProperty("reqUUID", reqUUID);
            json.addProperty("autowho", autowho);
            json.addProperty("tabformat", tabFormat);
            json.addProperty("hypixelApiKey", hypixelApiKey);
            new Gson().toJson(json, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}