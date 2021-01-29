package org.yatopiamc.bot.timings;

import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TimingsSuggestions {

    public static final String CURRENT_MINECRAFT_VERSION = "1.16.5";
    public static final Map<String, PluginServerSuggestion> SERVER_PLUGIN_SUGGESTIONS;
    public static final Map<String, ConfigServerSuggestion> SERVER_CONFIG_SUGGESTIONS;

    static {
        Map<String, PluginServerSuggestion> pluginSuggestions = new HashMap<>();
        pluginSuggestions.put("spigot", new PluginServerSuggestion(
                () -> {
                    Map<String, PluginSuggestion> suggestions = new HashMap<>();
                    suggestions.put("VillagerOptimiser", new PluginSuggestion("", "You probably don't need VillagerOptimiser as Spigot already adds its features. See entity-activation-range in spigot.yml."));
                    return suggestions;
                }
        ));
        pluginSuggestions.put("paper", new PluginServerSuggestion(
                () -> {
                    Map<String, PluginSuggestion> suggestions = new HashMap<>();
                    suggestions.put("ClearLag", new PluginSuggestion("", "Plugins that claim to remove lag actually cause more lag."));
                    suggestions.put("LagAssist", new PluginSuggestion("", "LagAssist should only be used for analytics and preventative measures. All other features of the plugin should be disabled."));
                    suggestions.put("NoChunkLag", new PluginSuggestion("", "Plugins that claim to remove lag actually cause more lag."));
                    suggestions.put("NoMobLag", new PluginSuggestion("", "Plugins that claim to remove lag actually cause more lag."));
                    suggestions.put("ServerBooster", new PluginSuggestion("", "Plugins that claim to remove lag actually cause more lag."));
                    suggestions.put("AntiLag", new PluginSuggestion("", "Plugins that claim to remove lag actually cause more lag."));
                    suggestions.put("BookLimiter", new PluginSuggestion("", "You don't need BookLimiter as Paper already fixes all crash bugs."));
                    suggestions.put("LimitPillagers", new PluginSuggestion("", "You probably don't need LimitPillagers as Paper already adds its features."));
                    suggestions.put("StackMob", new PluginSuggestion("", "Stacking mobs causes more lag."));
                    suggestions.put("Stacker", new PluginSuggestion("", "Stacking mobs causes more lag."));
                    suggestions.put("MobStacker", new PluginSuggestion("", "Stacking mobs causes more lag."));
                    suggestions.put("WildStacker", new PluginSuggestion("", "Stacking mobs causes more lag."));
                    suggestions.put("UltimateStacker", new PluginSuggestion("", "Stacking mobs causes more lag."));
                    suggestions.put("FastAsyncWorldEdit", new PluginSuggestion("", "FAWE has been known to cause issues. Consider replacing FAWE with [Worldedit](https://enginehub.org/worldedit/#downloads)."));
                    suggestions.put("CMI", new PluginSuggestion("", "CMI is a buggy plugin. Consider replacing CMI with [EssentialsX](https://essentialsx.net/downloads.html)."));
                    suggestions.put("IllegalStack", new PluginSuggestion("", "You probably don't need IllegalStack as Paper already fixes all dupe and crash bugs."));
                    suggestions.put("ExploitFixer", new PluginSuggestion("", "You probably don't need ExploitFixer as Paper already fixes all dupe and crash bugs."));
                    suggestions.put("EntityTrackerFixer", new PluginSuggestion("", "You don't need EntityTrackerFixer as Paper already has its features."));
                    suggestions.put("Orebfuscator", new PluginSuggestion("", "You don't need Orebfuscator as [Paper](https://gist.github.com/stonar96/ba18568bd91e5afd590e8038d14e245e) already has its features."));
                    suggestions.put("GroupManager", new PluginSuggestion("", "GroupManager is an outdated permission plugin. Consider replacing it with [LuckPerms](https://www.spigotmc.org/resources/luckperms.28140/)."));
                    suggestions.put("PermissionsEx", new PluginSuggestion("", "PermissionsEx is an outdated permission plugin. Consider replacing it with [LuckPerms](https://www.spigotmc.org/resources/luckperms.28140/)."));
                    suggestions.put("bPermissions", new PluginSuggestion("", "bPermissions is an outdated permission plugin. Consider replacing it with [LuckPerms](https://www.spigotmc.org/resources/luckperms.28140/)."));
                    suggestions.put("PhantomSMP", new PluginSuggestion("", "You probably don't need PhantomSMP as Paper already has its features. See phantoms-only-attack-insomniacs in paper.yml"));
                    suggestions.put("EpicHeads", new PluginSuggestion("", "This plugin was made by Songoda. Songoda resources are poorly developed and often cause problems. You should find an alternative such as [HeadsPlus](https://spigotmc.org/resources/headsplus-»-1-8-1-16-4.40265/) or [HeadDatabase](https://www.spigotmc.org/resources/head-database.14280/)."));
                    return suggestions;
                }
        ));
        pluginSuggestions.put("tuinity", new PluginServerSuggestion(
                () -> {
                    Map<String, PluginSuggestion> suggestions = new HashMap<>();
                    suggestions.put("PacketLimiter", new PluginSuggestion("", "You don't need PacketLimiter as Tuinity already has its features."));
                    return suggestions;
                }
        ));
        pluginSuggestions.put("purpur", new PluginServerSuggestion(
                () -> {
                    Map<String, PluginSuggestion> suggestions = new HashMap<>();
                    suggestions.put("SilkSpawners", new PluginSuggestion("", "You probably don't need SilkSpawners as Purpur already has its features."));
                    suggestions.put("MineableSpawners", new PluginSuggestion("", "You probably don't need MineableSpawners as Purpur already has its features."));
                    suggestions.put("VillagerLobotomizatornator", new PluginSuggestion("", "You probably don't need VillagerLobotomizatornator as Purpur already adds its features. Enable villager.lobotomize.enabled in [purpur.yml](http://bit.ly/purpurc)."));
                    return suggestions;
                }
        ));
        SERVER_PLUGIN_SUGGESTIONS = Collections.unmodifiableMap(pluginSuggestions);

        Map<String, ConfigServerSuggestion> configSuggestions = new HashMap<>();
        configSuggestions.put("server.properties", new ConfigServerSuggestion(() -> {
            Map<String, ConfigSuggestion> suggestions = new HashMap<>();
            suggestions.put("online-mode", new ConfigSuggestion("", "Enable this in [server.properties](http://bit.ly/servprop) for security.",
                    configs -> !configs.getAsJsonObject("server.properties").get("online-mode").getAsBoolean() &&
                            !configs.getAsJsonObject("spigot").getAsJsonObject("settings").get("bungeecord").getAsBoolean() &&
                            (!configs.getAsJsonObject("paper").getAsJsonObject("settings").getAsJsonObject("velocity-support").get("online-mode").getAsBoolean() || !configs.getAsJsonObject("paper").getAsJsonObject("settings").getAsJsonObject("velocity-support").get("enabled").getAsBoolean())));
            suggestions.put("network-compression-threshold: standalone", new ConfigSuggestion("", "Increase this in [server.properties](http://bit.ly/servprop). Recommended: 512.",
                    configs -> configs.getAsJsonObject("server.properties").get("network-compression-threshold").getAsInt() <= 256 &&
                            (!configs.getAsJsonObject("spigot").getAsJsonObject("settings").get("bungeecord").getAsBoolean() && !configs.getAsJsonObject("paper").getAsJsonObject("settings").getAsJsonObject("velocity-support").get("enabled").getAsBoolean())));
            suggestions.put("network-compression-threshold: network", new ConfigSuggestion("", "Set this to -1 in [server.properties](http://bit.ly/servprop) for a bungee/velocity server like yours",
                    configs -> configs.getAsJsonObject("server.properties").get("network-compression-threshold").getAsInt() != -1 &&
                            (configs.getAsJsonObject("spigot").getAsJsonObject("settings").get("bungeecord").getAsBoolean() || configs.getAsJsonObject("paper").getAsJsonObject("settings").getAsJsonObject("velocity-support").get("enabled").getAsBoolean())));
            return suggestions;
        }));
        configSuggestions.put("bukkit", new ConfigServerSuggestion(() -> {
            Map<String, ConfigSuggestion> suggestions = new HashMap<>();
            suggestions.put("chunk-gc.period-in-ticks", new ConfigSuggestion("", "Decrease this in [bukkit.yml](https://bukkit.gamepedia.com/Bukkit.yml). Recommended: 400.",
                    configs -> configs.getAsJsonObject("bukkit").getAsJsonObject("chunk-gc").get("period-in-ticks").getAsInt() >= 600));
            suggestions.put("ticks-per.monster-spawns", new ConfigSuggestion("", "Increase this in [bukkit.yml](https://bukkit.gamepedia.com/Bukkit.yml). Recommended: 4.",
                    configs -> configs.getAsJsonObject("bukkit").getAsJsonObject("ticks-per").get("monster-spawns").getAsInt() == 1));
            suggestions.put("spawn-limits.monsters", new ConfigSuggestion("", "Decrease this in [bukkit.yml](https://bukkit.gamepedia.com/Bukkit.yml). Recommended: 15.",
                    configs -> configs.getAsJsonObject("bukkit").getAsJsonObject("spawn-limits").get("monsters").getAsInt() >= 70));
            suggestions.put("spawn-limits.water-ambient", new ConfigSuggestion("", "\"Decrease this in [bukkit.yml](https://bukkit.gamepedia.com/Bukkit.yml). Recommended: 2.",
                    configs -> configs.getAsJsonObject("bukkit").getAsJsonObject("spawn-limits").get("water-ambient").getAsInt() >= 20));
            suggestions.put("spawn-limits.ambient", new ConfigSuggestion("", "Decrease this in [bukkit.yml](https://bukkit.gamepedia.com/Bukkit.yml). Recommended: 1.",
                    configs -> configs.getAsJsonObject("bukkit").getAsJsonObject("spawn-limits").get("ambient").getAsInt() >= 15));
            suggestions.put("spawn-limits.animals", new ConfigSuggestion("", "Decrease this in [bukkit.yml](https://bukkit.gamepedia.com/Bukkit.yml). Recommended: 3.",
                    configs -> configs.getAsJsonObject("bukkit").getAsJsonObject("spawn-limits").get("animals").getAsInt() >= 10));
            suggestions.put("spawn-limits.water-animals", new ConfigSuggestion("", "Decrease this in [bukkit.yml](https://bukkit.gamepedia.com/Bukkit.yml). Recommended: 2.",
                    configs -> configs.getAsJsonObject("bukkit").getAsJsonObject("spawn-limits").get("water-animals").getAsInt() >= 15));
            return suggestions;
        }));
        configSuggestions.put("spigot", new ConfigServerSuggestion(() -> {
            Map<String, ConfigSuggestion> suggestions = new HashMap<>();
            suggestions.put("view-distance", new ConfigSuggestion("", "Decrease this from default (10) in [spigot.yml](http://bit.ly/spiconf). Recommended: 4.",
                    configs -> configs.getAsJsonObject("server.properties").get("view-distance").getAsInt() >= 10 &&
                            configs.getAsJsonObject("spigot").getAsJsonObject("world-settings").entrySet().stream().anyMatch(entry -> entry.getValue().getAsJsonObject().get("view-distance").getAsString().equals("default"))));
            suggestions.put("entity-activation-range.animals", new ConfigSuggestion("", "Decrease this in [spigot.yml](http://bit.ly/spiconf). Recommended: 16.",
                    configs -> configs.getAsJsonObject("spigot").getAsJsonObject("world-settings").entrySet().stream().anyMatch(entry -> entry.getValue().getAsJsonObject().getAsJsonObject("entity-activation-range").get("animals").getAsInt() >= 32)));
            suggestions.put("entity-activation-range.monsters", new ConfigSuggestion("", "Decrease this in [spigot.yml](http://bit.ly/spiconf). Recommended: 16.",
                    configs -> configs.getAsJsonObject("spigot").getAsJsonObject("world-settings").entrySet().stream().anyMatch(entry -> entry.getValue().getAsJsonObject().getAsJsonObject("entity-activation-range").get("monsters").getAsInt() >= 32)));
            suggestions.put("entity-activation-range.misc", new ConfigSuggestion("", "Decrease this in [spigot.yml](http://bit.ly/spiconf). Recommended: 12.",
                    configs -> configs.getAsJsonObject("spigot").getAsJsonObject("world-settings").entrySet().stream().anyMatch(entry -> entry.getValue().getAsJsonObject().getAsJsonObject("entity-activation-range").get("misc").getAsInt() >= 16)));
            suggestions.put("entity-activation-range.water", new ConfigSuggestion("", "Decrease this in [spigot.yml](http://bit.ly/spiconf). Recommended: 12.",
                    configs -> configs.getAsJsonObject("spigot").getAsJsonObject("world-settings").entrySet().stream().anyMatch(entry -> entry.getValue().getAsJsonObject().getAsJsonObject("entity-activation-range").get("water").getAsInt() >= 16)));
            suggestions.put("entity-activation-range.villagers", new ConfigSuggestion("", "Decrease this in [spigot.yml](http://bit.ly/spiconf). Recommended: 16.",
                    configs -> configs.getAsJsonObject("spigot").getAsJsonObject("world-settings").entrySet().stream().anyMatch(entry -> entry.getValue().getAsJsonObject().getAsJsonObject("entity-activation-range").get("villagers").getAsInt() >= 32)));
            suggestions.put("entity-activation-range.tick-inactive-villagers", new ConfigSuggestion("", "Disable this in [spigot.yml](http://bit.ly/spiconf).",
                    configs -> configs.getAsJsonObject("spigot").getAsJsonObject("world-settings").entrySet().stream().anyMatch(entry -> entry.getValue().getAsJsonObject().getAsJsonObject("entity-activation-range").get("tick-inactive-villagers").getAsBoolean())));
            suggestions.put("entity-activation-range.wake-up-inactive.villagers-for", new ConfigSuggestion("", "Decrease this in [spigot.yml](http://bit.ly/spiconf). Recommended: 20.",
                    configs -> configs.getAsJsonObject("spigot").getAsJsonObject("world-settings").entrySet().stream().anyMatch(entry -> entry.getValue().getAsJsonObject().getAsJsonObject("entity-activation-range").getAsJsonObject("wake-up-inactive").get("villagers-max-per-tick").getAsInt() >= 1 && entry.getValue().getAsJsonObject().getAsJsonObject("entity-activation-range").getAsJsonObject("wake-up-inactive").get("villagers-for").getAsInt() >= 100)));
            suggestions.put("entity-activation-range.wake-up-inactive.flying-monsters-for", new ConfigSuggestion("", "Decrease this in [spigot.yml](http://bit.ly/spiconf). Recommended: 60.",
                    configs -> configs.getAsJsonObject("spigot").getAsJsonObject("world-settings").entrySet().stream().anyMatch(entry -> entry.getValue().getAsJsonObject().getAsJsonObject("entity-activation-range").getAsJsonObject("wake-up-inactive").get("flying-monsters-max-per-tick").getAsInt() >= 1 && entry.getValue().getAsJsonObject().getAsJsonObject("entity-activation-range").getAsJsonObject("wake-up-inactive").get("flying-monsters-for").getAsInt() >= 100)));
            suggestions.put("entity-activation-range.wake-up-inactive.animals-for", new ConfigSuggestion("", "Decrease this in [spigot.yml](http://bit.ly/spiconf). Recommended: 40.",
                    configs -> configs.getAsJsonObject("spigot").getAsJsonObject("world-settings").entrySet().stream().anyMatch(entry -> entry.getValue().getAsJsonObject().getAsJsonObject("entity-activation-range").getAsJsonObject("wake-up-inactive").get("animals-max-per-tick").getAsInt() >= 1 && entry.getValue().getAsJsonObject().getAsJsonObject("entity-activation-range").getAsJsonObject("wake-up-inactive").get("animals-for").getAsInt() >= 100)));
            suggestions.put("entity-activation-range.wake-up-inactive.monsters-for", new ConfigSuggestion("", "Decrease this in [spigot.yml](http://bit.ly/spiconf). Recommended: 60.",
                    configs -> configs.getAsJsonObject("spigot").getAsJsonObject("world-settings").entrySet().stream().anyMatch(entry -> entry.getValue().getAsJsonObject().getAsJsonObject("entity-activation-range").getAsJsonObject("wake-up-inactive").get("monsters-max-per-tick").getAsInt() >= 1 && entry.getValue().getAsJsonObject().getAsJsonObject("entity-activation-range").getAsJsonObject("wake-up-inactive").get("monsters-for").getAsInt() >= 100)));
            suggestions.put("entity-activation-range.wake-up-inactive.villagers-max-per-tick", new ConfigSuggestion("", "Decrease this in [spigot.yml](http://bit.ly/spiconf). Recommended: 1.",
                    configs -> configs.getAsJsonObject("spigot").getAsJsonObject("world-settings").entrySet().stream().anyMatch(entry -> entry.getValue().getAsJsonObject().getAsJsonObject("entity-activation-range").get("villagers-max-per-tick").getAsInt() >= 4)));
            suggestions.put("entity-activation-range.wake-up-inactive.monsters-max-per-tick", new ConfigSuggestion("", "Decrease this in [spigot.yml](http://bit.ly/spiconf). Recommended: 4.",
                    configs -> configs.getAsJsonObject("spigot").getAsJsonObject("world-settings").entrySet().stream().anyMatch(entry -> entry.getValue().getAsJsonObject().getAsJsonObject("entity-activation-range").get("monsters-max-per-tick").getAsInt() >= 8)));
            suggestions.put("entity-activation-range.wake-up-inactive.flying-monsters-max-per-tick", new ConfigSuggestion("", "Decrease this in [spigot.yml](http://bit.ly/spiconf). Recommended: 1.",
                    configs -> configs.getAsJsonObject("spigot").getAsJsonObject("world-settings").entrySet().stream().anyMatch(entry -> entry.getValue().getAsJsonObject().getAsJsonObject("entity-activation-range").get("flying-monsters-max-per-tick").getAsInt() >= 8)));
            suggestions.put("entity-activation-range.wake-up-inactive.animals-max-per-tick", new ConfigSuggestion("", "Decrease this in [spigot.yml](http://bit.ly/spiconf). Recommended: 2.",
                    configs -> configs.getAsJsonObject("spigot").getAsJsonObject("world-settings").entrySet().stream().anyMatch(entry -> entry.getValue().getAsJsonObject().getAsJsonObject("entity-activation-range").get("animals-max-per-tick").getAsInt() >= 4)));
            suggestions.put("", new ConfigSuggestion("", "",
                    configs -> ));
            suggestions.put("", new ConfigSuggestion("", "",
                    configs -> ));
            suggestions.put("", new ConfigSuggestion("", "",
                    configs -> ));
            suggestions.put("", new ConfigSuggestion("", "",
                    configs -> ));
            suggestions.put("", new ConfigSuggestion("", "",
                    configs -> ));
            suggestions.put("", new ConfigSuggestion("", "",
                    configs -> ));


            return suggestions;
        }));
        configSuggestions.put("paper", new ConfigServerSuggestion(() -> {
            Map<String, ConfigSuggestion> suggestions = new HashMap<>();
            return suggestions;
        }));
        configSuggestions.put("purpur", new ConfigServerSuggestion(() -> {
            Map<String, ConfigSuggestion> suggestions = new HashMap<>();
            return suggestions;
        }));
        SERVER_CONFIG_SUGGESTIONS = configSuggestions;
    }

    public static class PluginServerSuggestion {
        public final Map<String, PluginSuggestion> suggestions;

        public PluginServerSuggestion(Supplier<Map<String, PluginSuggestion>> suggestions) {
            this.suggestions = Collections.unmodifiableMap(suggestions.get());
        }
    }

    public static class PluginSuggestion {
        public final String prefix;
        public final String warning;

        public PluginSuggestion(String prefix, String warning) {
            this.prefix = prefix;
            this.warning = warning;
        }
    }

    public static class ConfigServerSuggestion {
        public final Map<String, ConfigSuggestion> suggestions;

        public ConfigServerSuggestion(Supplier<Map<String, ConfigSuggestion>> suggestions) {
            this.suggestions = Collections.unmodifiableMap(suggestions.get());
        }
    }

    public static class ConfigSuggestion {
        public final String prefix;
        public final String warning;
        public final Predicate predicate;

        public ConfigSuggestion(String prefix, String warning, Predicate predicate) {
            this.prefix = prefix;
            this.warning = warning;
            this.predicate = predicate;
        }

        public interface Predicate {
            boolean test(JsonObject configs);
        }
    }
}
