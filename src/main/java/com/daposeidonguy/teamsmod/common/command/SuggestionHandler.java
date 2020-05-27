package com.daposeidonguy.teamsmod.common.command;

import com.daposeidonguy.teamsmod.common.storage.StorageHelper;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.util.ResourceLocation;

import java.util.Set;

class SuggestionHandler {
    /* Provides autocomplete suggestion list of teams */
    static final SuggestionProvider<CommandSource> TEAM_SUGGESTIONS = SuggestionProviders.register(new ResourceLocation("teams"), (ctx, builder) -> {
        Set<String> teamSet = StorageHelper.getTeamSet();
        if (teamSet.isEmpty()) {
            return Suggestions.empty();
        }
        for (String team : teamSet) {
            builder.suggest(team);
        }
        return builder.buildFuture();
    });
    /* Provides autocomplete suggestion list of config options */
    static final SuggestionProvider<CommandSource> CONFIG_SUGGESTIONS = SuggestionProviders.register(new ResourceLocation("config"), (ctx, builder) -> {
        builder.suggest("disableAdvancementSync").suggest("enableFriendlyFire");
        return builder.buildFuture();
    });
}
