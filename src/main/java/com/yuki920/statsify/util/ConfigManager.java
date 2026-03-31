package com.yuki920.statsify.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;

public class ConfigManager {

    private static final String CONFIG_PATH = "config/statsify.json";
    private static final int DEFAULT_MIN_FKDR = -1;
    private static final String DEFAULT_MODE = "bws";

    public int minFkdr = DEFAULT_MIN_FKDR;
    public String mode = DEFAULT_MODE;
    public boolean tags = false;
    public boolean tabstats = true;
    public boolean urchin = false;
    public boolean reqUUID = false;
    public boolean autowho = true;
    public String tabFormat = "bracket_star_name_dot_fkdr";
    public String urchinkey = "";
    public String hypixelApiKey = "";

    public void load() {
        File configFile = new File(CONFIG_PATH);
        if (!configFile.exists()) {
            save();
            return;
        }

        try (Reader reader = new FileReader(configFile)) {
            JsonObject json = new Gson().fromJson(reader, JsonObject.class);
            minFkdr = json.has("minFkdr") ? json.get("minFkdr").getAsInt() : DEFAULT_MIN_FKDR;
            mode = json.has("mode") ? json.get("mode").getAsString() : DEFAULT_MODE;
            tags = json.has("tags") && json.get("tags").getAsBoolean();
            tabstats = !json.has("tablist") || json.get("tablist").getAsBoolean();
            urchin = json.has("urchin") && json.get("urchin").getAsBoolean();
            reqUUID = !json.has("reqUUID") || json.get("reqUUID").getAsBoolean();
            autowho = !json.has("autowho") || json.get("autowho").getAsBoolean();
            urchinkey = json.has("urchinkey") ? json.get("urchinkey").getAsString() : "";
            tabFormat = json.has("tabformat") ? json.get("tabformat").getAsString() : "bracket_star_name_dot_fkdr";
            hypixelApiKey = json.has("hypixelApiKey") ? json.get("hypixelApiKey").getAsString() : "";
        } catch (IOException e) {
            e.printStackTrace();
            resetDefaults();
        }
    }

    public void save() {
        File configFile = new File(CONFIG_PATH);
        configFile.getParentFile().mkdirs();

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

    private void resetDefaults() {
        minFkdr = DEFAULT_MIN_FKDR;
        mode = DEFAULT_MODE;
        tags = false;
        tabstats = true;
        urchin = false;
        urchinkey = "";
        reqUUID = true;
        autowho = true;
        tabFormat = "bracket_star_name_dot_fkdr";
        hypixelApiKey = "";
    }
}
