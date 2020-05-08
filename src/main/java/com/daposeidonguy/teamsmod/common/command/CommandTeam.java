package com.daposeidonguy.teamsmod.common.command;

import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.network.PacketHandler;
import com.daposeidonguy.teamsmod.common.network.messages.MessageInvite;
import com.daposeidonguy.teamsmod.common.network.messages.MessageSaveData;
import com.daposeidonguy.teamsmod.common.storage.SaveData;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class CommandTeam {

    /* Provides autocomplete suggestion list of teams */
    private static final SuggestionProvider<CommandSource> TEAM_SUGGESTIONS = SuggestionProviders.register(new ResourceLocation("teams"), (ctx, builder) -> {
        Set<String> teamSet = SaveData.teamsMap.keySet();
        if (teamSet.isEmpty()) {
            return Suggestions.empty();
        }
        for (String team : teamSet) {
            builder.suggest(team);
        }
        return builder.buildFuture();
    });

    private static String[] aliases = {"teamsmod", "teams", "t"};

    /* Constructs and registers the command command */
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        for (String alias : aliases) {
            dispatcher.register(Commands.literal(alias)
                    .then(Commands.literal("create")
                            .then(Commands.argument("teamName", StringArgumentType.word())
                                    .executes(ctx -> teamCreate(server, ctx.getSource(), StringArgumentType.getString(ctx, "teamName")))))
                    .then(Commands.literal("list")
                            .executes(ctx -> teamList(server, ctx.getSource())))
                    .then(Commands.literal("info")
                            .then(Commands.argument("teamName", StringArgumentType.word()).suggests(TEAM_SUGGESTIONS)
                                    .executes(ctx -> teamInfo(server, ctx.getSource(), StringArgumentType.getString(ctx, "teamName")))))
                    .then(Commands.literal("player")
                            .then(Commands.argument("playerName", EntityArgument.player())
                                    .executes(ctx -> teamPlayer(server, ctx.getSource(), EntityArgument.getPlayer(ctx, "playerName").getGameProfile().getName()))))
                    .then(Commands.literal("invite")
                            .then(Commands.argument("playerName", EntityArgument.player())
                                    .executes(ctx -> teamInvite(server, ctx.getSource(), EntityArgument.getPlayer(ctx, "playerName").getGameProfile().getName()))))
                    .then(Commands.literal("accept")
                            .executes(ctx -> teamAccept(server, ctx.getSource())))
                    .then(Commands.literal("kick")
                            .then(Commands.argument("playerName", EntityArgument.player())
                                    .executes(ctx -> teamKick(server, ctx.getSource(), EntityArgument.getPlayer(ctx, "playerName").getGameProfile().getName()))))
                    .then(Commands.literal("leave")
                            .executes(ctx -> teamLeave(server, ctx.getSource())))
                    .then(Commands.literal("remove")
                            .then(Commands.argument("teamName", StringArgumentType.word()).suggests(TEAM_SUGGESTIONS)
                                    .executes(ctx -> teamRemove(server, ctx.getSource(), StringArgumentType.getString(ctx, "teamName")))))
                    .executes(ctx -> {
                        ctx.getSource().sendFeedback(new StringTextComponent("Teams Commands: " +
                                "\n/teamsmod create <name> : creates command with the name <name>" +
                                "\n/teamsmod list : lists all created team" +
                                "\n/teamsmod info <name> : lists all players in the team with name <name>" +
                                "\n/teamsmod player <name> : prints the team of the player with name <name>" +
                                "\n/teamsmod invite <name> : invites player with name <name> to your team" +
                                "\n/teamsmod accept : accepts invitation to team" +
                                "\n/teamsmod kick <name> : kicks player with name <name> from your team" +
                                "\n/teamsmod leave : leaves your team" +
                                "\n/teamsmod remove <name> : ADMIN ONLY - deletes the team with name <name>"), false);
                        return Command.SINGLE_SUCCESS;
                    }));
        }
    }

    /* Creates a command with name teamName and adds the command sender to the command */
    private static int teamCreate(MinecraftServer server, CommandSource sender, String teamName) throws CommandSyntaxException {
        SaveData data = SaveData.get(server.getWorld(DimensionType.OVERWORLD));
        PlayerEntity player = sender.asPlayer();
        if (SaveData.teamsMap.containsKey(teamName)) {
            throw new CommandException(new StringTextComponent("Team name already exists"));
        } else if (teamName.contains(">") || teamName.contains("<")) {
            throw new CommandException(new StringTextComponent("Team name cannot contain '<' or '>'"));
        } else if (SaveData.teamMap.containsKey(player.getUniqueID())) {
            throw new CommandException(new StringTextComponent("You're already in a team! Leave it first"));
        }
        sender.sendFeedback(new StringTextComponent("Created team: " + teamName), false);
        data.addTeam(teamName, player);
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(SaveData.teamsMap));
        return Command.SINGLE_SUCCESS;
    }

    /* Sends the list of teams in the server to the command sender */
    private static int teamList(MinecraftServer server, CommandSource sender) {
        SaveData.get(server.getWorld(DimensionType.OVERWORLD));
        sender.sendFeedback(new StringTextComponent("List of teams:"), false);
        Iterator<String> teamIterator = SaveData.teamsMap.keySet().iterator();
        while (teamIterator.hasNext()) {
            sender.sendFeedback(new StringTextComponent(teamIterator.next()), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    /* Sends information about the command "teamName" to the command sender */
    private static int teamInfo(MinecraftServer server, CommandSource sender, String teamName) throws CommandException {
        SaveData.get(server.getWorld(DimensionType.OVERWORLD));
        if (!SaveData.teamsMap.containsKey(teamName)) {
            throw new CommandException(new StringTextComponent("Enter valid team"));
        }
        sender.sendFeedback(new StringTextComponent("Players in Team: "), false);
        Iterator<UUID> uuidIterator = SaveData.teamsMap.get(teamName).iterator();
        while (uuidIterator.hasNext()) {
            UUID id = uuidIterator.next();
            GameProfile profile = server.getPlayerProfileCache().getProfileByUUID(id);
            if (profile != null) {
                sender.sendFeedback(new StringTextComponent(profile.getName()), false);
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /* Sends the command of the player "playerName" to the command sender */
    private static int teamPlayer(MinecraftServer server, CommandSource sender, String playerName) {
        SaveData.get(server.getWorld(DimensionType.OVERWORLD));
        if (SaveData.teamMap.containsKey(server.getPlayerProfileCache().getGameProfileForUsername(playerName).getId())) {
            String playerTeam = SaveData.teamMap.get(server.getPlayerProfileCache().getGameProfileForUsername(playerName).getId());
            sender.sendFeedback(new StringTextComponent(playerName + " is in the following team:"), false);
            sender.sendFeedback(new StringTextComponent(playerTeam), false);
            return Command.SINGLE_SUCCESS;
        } else {
            sender.sendFeedback(new StringTextComponent(playerName + " is not in a team"), false);
            return 0;
        }
    }

    /* Sends an invite to the player "playerName" to join the command of the command sender */
    private static int teamInvite(MinecraftServer server, CommandSource sender, String playerName) throws CommandSyntaxException {
        SaveData.get(server.getWorld(DimensionType.OVERWORLD));
        ServerPlayerEntity newPlayer = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUsername(playerName);
        PlayerEntity oldPlayer = sender.asPlayer();
        if (SaveData.teamMap.containsKey(oldPlayer.getUniqueID())) {
            String teamName = SaveData.teamMap.get(oldPlayer.getUniqueID());
            if (SaveData.teamsMap.get(teamName).contains(newPlayer.getUniqueID())) {
                throw new CommandException(new StringTextComponent("That player is already on your team!"));
            }
            newPlayer.getPersistentData().putString("invitedby", oldPlayer.getCachedUniqueIdString());
            oldPlayer.sendMessage(new StringTextComponent("You have invited: " + newPlayer.getGameProfile().getName() + " to your team"));
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> newPlayer), new MessageInvite(teamName));
            newPlayer.sendMessage(new StringTextComponent("You have been invited to join the team: " + teamName + ". Type /teamsmod accept to accept"));
            return Command.SINGLE_SUCCESS;
        } else {
            throw new CommandException(new StringTextComponent("Failed to invite " + newPlayer.getGameProfile().getName() + " to your team"));
        }
    }

    /* Accepts most recent command invite */
    private static int teamAccept(MinecraftServer server, CommandSource sender) throws CommandSyntaxException {
        SaveData data = SaveData.get(server.getWorld(DimensionType.OVERWORLD));
        ServerPlayerEntity invitee = sender.asPlayer();
        ServerPlayerEntity inviter = server.getPlayerList().getPlayerByUUID(UUID.fromString(invitee.getPersistentData().getString("invitedby")));
        UUID uid = invitee.getUniqueID();
        if (inviter == null) {
            throw new CommandException(new StringTextComponent("You have not been invited to a team"));
        } else if (SaveData.teamMap.containsKey(uid)) {
            sender.sendFeedback(new StringTextComponent("Removing you from your old team..."), false);
            data.removePlayer(invitee, uid);
        }
        data.addPlayer(inviter, uid);
        if (SaveData.teamMap.containsKey(inviter.getUniqueID()) && !TeamConfig.disableAchievementSync) {
            String name = SaveData.teamMap.get(inviter.getUniqueID());
            Iterator<UUID> uuidIterator = SaveData.teamsMap.get(name).iterator();
            while (uuidIterator.hasNext()) {
                ServerPlayerEntity playerMP = server.getPlayerList().getPlayerByUUID(uuidIterator.next());
                if (playerMP != null) {
                    SaveData.syncPlayers(name, playerMP);
                }
            }
        }
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(SaveData.teamsMap));
        sender.sendFeedback(new StringTextComponent("Joined " + inviter.getGameProfile().getName() + "'s team"), false);
        inviter.sendMessage(new StringTextComponent(invitee.getGameProfile().getName() + " has joined your team!"));
        return Command.SINGLE_SUCCESS;
    }

    /* Kicks player "playerName" from the command of the command sender */
    private static int teamKick(MinecraftServer server, CommandSource sender, String playerName) throws CommandSyntaxException {
        SaveData data = SaveData.get(server.getWorld(DimensionType.OVERWORLD));
        UUID uid = server.getPlayerProfileCache().getGameProfileForUsername(playerName).getId();
        if (SaveData.teamMap.containsKey(uid)) {
            sender.sendFeedback(new StringTextComponent("Removing that player from your team!"), false);
            String toLeave = SaveData.teamMap.get(uid);
            data.removePlayer(sender.asPlayer(), uid);
            server.getPlayerList().getPlayerByUUID(uid).sendMessage(new StringTextComponent("You have been kicked from your team"));
            if (SaveData.teamsMap.get(toLeave).isEmpty()) {
                data.removeTeam(toLeave);
            }
        } else {
            throw new CommandException(new StringTextComponent("Must enter player name on your team"));
        }
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(SaveData.teamsMap));
        return Command.SINGLE_SUCCESS;
    }

    /* Removes the command sender from their command */
    private static int teamLeave(MinecraftServer server, CommandSource sender) throws CommandSyntaxException {
        SaveData data = SaveData.get(server.getWorld(DimensionType.OVERWORLD));
        PlayerEntity p = sender.asPlayer();
        if (!SaveData.teamMap.containsKey(p.getUniqueID())) {
            throw new CommandException(new StringTextComponent("You're not in a team"));
        }
        String toLeave = SaveData.teamMap.get(p.getUniqueID());
        data.removePlayer(p, p.getUniqueID());
        p.sendMessage(new StringTextComponent("You left your team"));
        if (SaveData.teamsMap.get(toLeave).isEmpty()) {
            data.removeTeam(toLeave);
        }
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(SaveData.teamsMap));
        return Command.SINGLE_SUCCESS;
    }

    /* Removes the command "teamName" from the server */
    private static int teamRemove(MinecraftServer server, CommandSource sender, String teamName) throws CommandSyntaxException {
        SaveData data = SaveData.get(server.getWorld(DimensionType.OVERWORLD));
        if (TeamConfig.noOpRemoveTeam || server.isSinglePlayer() || sender.asPlayer().hasPermissionLevel(server.getOpPermissionLevel())) {
            if (!SaveData.teamsMap.containsKey(teamName)) {
                throw new CommandException(new StringTextComponent("The team \"" + teamName + "\" doesn't exist"));
            }
            sender.sendFeedback(new StringTextComponent("The team \"" + teamName + "\" has been removed"), false);
            data.removeTeam(teamName);
            PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(SaveData.teamsMap));
            return Command.SINGLE_SUCCESS;
        }
        throw new CommandException(new StringTextComponent("You do not have permission to use this command"));
    }
}
