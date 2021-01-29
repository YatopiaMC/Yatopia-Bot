package org.yatopiamc.bot.timings;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TimingsSuggestions {

    public static final String CURRENT_MINECRAFT_VERSION = "1.16.5";
    public static final Map<String, PluginServerSuggestion> SERVER_PLUGIN_SUGGESTIONS;

    static {
        Map<String, PluginServerSuggestion> serverSuggestions = new HashMap<>();
        serverSuggestions.put("spigot", new PluginServerSuggestion(
                () -> {
                    Map<String, PluginSuggestion> suggestions = new HashMap<>();
                    suggestions.put("VillagerOptimiser", new PluginSuggestion("", "You probably don't need VillagerOptimiser as Spigot already adds its features. See entity-activation-range in spigot.yml."));
                    return suggestions;
                }
        ));
        serverSuggestions.put("paper", new PluginServerSuggestion(
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
        serverSuggestions.put("tuinity", new PluginServerSuggestion(
                () -> {
                    Map<String, PluginSuggestion> suggestions = new HashMap<>();
                    suggestions.put("PacketLimiter", new PluginSuggestion("", "You don't need PacketLimiter as Tuinity already has its features."));
                    return suggestions;
                }
        ));
        serverSuggestions.put("purpur", new PluginServerSuggestion(
                () -> {
                    Map<String, PluginSuggestion> suggestions = new HashMap<>();
                    suggestions.put("SilkSpawners", new PluginSuggestion("", "You probably don't need SilkSpawners as Purpur already has its features."));
                    suggestions.put("MineableSpawners", new PluginSuggestion("", "You probably don't need MineableSpawners as Purpur already has its features."));
                    suggestions.put("VillagerLobotomizatornator", new PluginSuggestion("", "You probably don't need VillagerLobotomizatornator as Purpur already adds its features. Enable villager.lobotomize.enabled in [purpur.yml](http://bit.ly/purpurc)."));
                    return suggestions;
                }
        ));
        SERVER_PLUGIN_SUGGESTIONS = Collections.unmodifiableMap(serverSuggestions);
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
}
