package com.daposeidonguy.teamsmod.common.command;

import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.network.PacketHandler;
import com.daposeidonguy.teamsmod.common.network.messages.MessageInvite;
import com.daposeidonguy.teamsmod.common.network.messages.MessageSaveData;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Iterator;
import java.util.UUID;

public class CommandTeam {

    private static String[] aliases = {"teamsmod", "teams", "t"};

    /* Constructs and registers the team command */
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
                            .then(Commands.argument("teamName", StringArgumentType.word()).suggests(SuggestionHandler.TEAM_SUGGESTIONS)
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
                            .then(Commands.argument("teamName", StringArgumentType.word()).suggests(SuggestionHandler.TEAM_SUGGESTIONS)
                                    .executes(ctx -> teamRemove(server, ctx.getSource(), StringArgumentType.getString(ctx, "teamName")))))
                    .then(Commands.literal("config")
                            .then(Commands.argument("configOption", StringArgumentType.word()).suggests(SuggestionHandler.CONFIG_SUGGESTIONS)
                                    .then(Commands.argument("configValue", BoolArgumentType.bool())
                                            .executes(ctx -> teamConfig(server, ctx.getSource(), StorageHandler.uuidToTeamMap.get(ctx.getSource().asPlayer().getUniqueID()),
                                                    StringArgumentType.getString(ctx, "configOption"),
                                                    BoolArgumentType.getBool(ctx, "configValue"))))))
                    .executes(ctx -> {
                        ctx.getSource().sendFeedback(new StringTextComponent("Teams Commands: " +
                                "\n/teamsmod create <name> : creates team with the name <name>" +
                                "\n/teamsmod list : lists all created teams" +
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

    /* Creates a team with name teamName and adds the command sender to the team */
    private static int teamCreate(MinecraftServer server, CommandSource sender, String teamName) throws CommandSyntaxException {
        StorageHandler data = StorageHandler.get(server.getWorld(DimensionType.OVERWORLD));
        PlayerEntity player = sender.asPlayer();
        if (StorageHandler.teamToUuidsMap.containsKey(teamName)) {
            throw new CommandException(new StringTextComponent("Team name already exists"));
        } else if (teamName.contains(">") || teamName.contains("<")) {
            throw new CommandException(new StringTextComponent("Team name cannot contain '<' or '>'"));
        } else if (StorageHandler.uuidToTeamMap.containsKey(player.getUniqueID())) {
            throw new CommandException(new StringTextComponent("You're already in a team! Leave it first"));
        }
        sender.sendFeedback(new StringTextComponent("Created team: " + teamName), false);
        data.addTeam(teamName, player);
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(StorageHandler.teamToUuidsMap));
        return Command.SINGLE_SUCCESS;
    }

    /* Sends the list of teams in the server to the command sender */
    private static int teamList(MinecraftServer server, CommandSource sender) {
        StorageHandler.get(server.getWorld(DimensionType.OVERWORLD));
        sender.sendFeedback(new StringTextComponent("List of teams:"), false);
        Iterator<String> teamIterator = StorageHandler.teamToUuidsMap.keySet().iterator();
        while (teamIterator.hasNext()) {
            sender.sendFeedback(new StringTextComponent(teamIterator.next()), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    /* Sends information about the team "teamName" to the command sender */
    private static int teamInfo(MinecraftServer server, CommandSource sender, String teamName) throws CommandException {
        StorageHandler.get(server.getWorld(DimensionType.OVERWORLD));
        if (!StorageHandler.teamToUuidsMap.containsKey(teamName)) {
            throw new CommandException(new StringTextComponent("Enter valid team"));
        }
        sender.sendFeedback(new StringTextComponent("Players in Team: "), false);
        Iterator<UUID> uuidIterator = StorageHandler.teamToUuidsMap.get(teamName).iterator();
        while (uuidIterator.hasNext()) {
            UUID id = uuidIterator.next();
            GameProfile profile = server.getPlayerProfileCache().getProfileByUUID(id);
            if (profile != null) {
                sender.sendFeedback(new StringTextComponent(profile.getName()), false);
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /* Sends the team of the player "playerName" to the command sender */
    private static int teamPlayer(MinecraftServer server, CommandSource sender, String playerName) {
        StorageHandler.get(server.getWorld(DimensionType.OVERWORLD));
        if (StorageHandler.uuidToTeamMap.containsKey(server.getPlayerProfileCache().getGameProfileForUsername(playerName).getId())) {
            String playerTeam = StorageHandler.uuidToTeamMap.get(server.getPlayerProfileCache().getGameProfileForUsername(playerName).getId());
            sender.sendFeedback(new StringTextComponent(playerName + " is in the following team:"), false);
            sender.sendFeedback(new StringTextComponent(playerTeam), false);
            return Command.SINGLE_SUCCESS;
        } else {
            sender.sendFeedback(new StringTextComponent(playerName + " is not in a team"), false);
            return 0;
        }
    }

    /* Sends an invite to the player "playerName" to join the team of the command sender */
    private static int teamInvite(MinecraftServer server, CommandSource sender, String playerName) throws CommandSyntaxException {
        StorageHandler.get(server.getWorld(DimensionType.OVERWORLD));
        ServerPlayerEntity newPlayer = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUsername(playerName);
        PlayerEntity oldPlayer = sender.asPlayer();
        if (StorageHandler.uuidToTeamMap.containsKey(oldPlayer.getUniqueID())) {
            String teamName = StorageHandler.uuidToTeamMap.get(oldPlayer.getUniqueID());
            if (StorageHandler.teamToUuidsMap.get(teamName).contains(newPlayer.getUniqueID())) {
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

    /* Accepts most recent team invite */
    private static int teamAccept(MinecraftServer server, CommandSource sender) throws CommandSyntaxException {
        StorageHandler data = StorageHandler.get(server.getWorld(DimensionType.OVERWORLD));
        ServerPlayerEntity invitee = sender.asPlayer();
        ServerPlayerEntity inviter = server.getPlayerList().getPlayerByUUID(UUID.fromString(invitee.getPersistentData().getString("invitedby")));
        UUID uid = invitee.getUniqueID();
        if (inviter == null) {
            throw new CommandException(new StringTextComponent("You have not been invited to a team"));
        } else if (StorageHandler.uuidToTeamMap.containsKey(uid)) {
            sender.sendFeedback(new StringTextComponent("Removing you from your old team..."), false);
            data.removePlayer(invitee, uid);
        }
        data.addPlayer(inviter, uid);
        if (StorageHandler.uuidToTeamMap.containsKey(inviter.getUniqueID()) && !TeamConfig.disableAdvancementSync) {
            String teamName = StorageHandler.uuidToTeamMap.get(inviter.getUniqueID());
            Iterator<UUID> uuidIterator = StorageHandler.teamToUuidsMap.get(teamName).iterator();
            while (uuidIterator.hasNext()) {
                ServerPlayerEntity playerMP = server.getPlayerList().getPlayerByUUID(uuidIterator.next());
                if (playerMP != null && !StorageHandler.teamSettingsMap.get(teamName).get("disableAdvancementSync")) {
                    StorageHandler.syncPlayers(teamName, playerMP);
                }
            }
        }
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(StorageHandler.teamToUuidsMap));
        sender.sendFeedback(new StringTextComponent("Joined " + inviter.getGameProfile().getName() + "'s team"), false);
        inviter.sendMessage(new StringTextComponent(invitee.getGameProfile().getName() + " has joined your team!"));
        return Command.SINGLE_SUCCESS;
    }

    /* Kicks player "playerName" from the team of the command sender */
    private static int teamKick(MinecraftServer server, CommandSource sender, String playerName) throws CommandSyntaxException {
        StorageHandler data = StorageHandler.get(server.getWorld(DimensionType.OVERWORLD));
        UUID uid = server.getPlayerProfileCache().getGameProfileForUsername(playerName).getId();
        if (StorageHandler.uuidToTeamMap.containsKey(uid)) {
            sender.sendFeedback(new StringTextComponent("Removing that player from your team!"), false);
            String toLeave = StorageHandler.uuidToTeamMap.get(uid);
            data.removePlayer(sender.asPlayer(), uid);
            server.getPlayerList().getPlayerByUUID(uid).sendMessage(new StringTextComponent("You have been kicked from your team"));
            if (StorageHandler.teamToUuidsMap.get(toLeave).isEmpty()) {
                data.removeTeam(toLeave);
            }
        } else {
            throw new CommandException(new StringTextComponent("Must enter player name on your team"));
        }
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(StorageHandler.teamToUuidsMap));
        return Command.SINGLE_SUCCESS;
    }

    /* Removes the command sender from their team */
    private static int teamLeave(MinecraftServer server, CommandSource sender) throws CommandSyntaxException {
        StorageHandler data = StorageHandler.get(server.getWorld(DimensionType.OVERWORLD));
        PlayerEntity p = sender.asPlayer();
        if (!StorageHandler.uuidToTeamMap.containsKey(p.getUniqueID())) {
            throw new CommandException(new StringTextComponent("You're not in a team"));
        }
        String toLeave = StorageHandler.uuidToTeamMap.get(p.getUniqueID());
        data.removePlayer(p, p.getUniqueID());
        p.sendMessage(new StringTextComponent("You left your team"));
        if (StorageHandler.teamToUuidsMap.get(toLeave).isEmpty()) {
            data.removeTeam(toLeave);
        }
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(StorageHandler.teamToUuidsMap));
        return Command.SINGLE_SUCCESS;
    }

    /* Removes the team "teamName" from the server */
    private static int teamRemove(MinecraftServer server, CommandSource sender, String teamName) throws CommandSyntaxException {
        StorageHandler data = StorageHandler.get(server.getWorld(DimensionType.OVERWORLD));
        if (TeamConfig.noOpRemoveTeam || server.isSinglePlayer() || sender.asPlayer().hasPermissionLevel(server.getOpPermissionLevel())) {
            if (!StorageHandler.teamToUuidsMap.containsKey(teamName)) {
                throw new CommandException(new StringTextComponent("The team \"" + teamName + "\" doesn't exist"));
            }
            sender.sendFeedback(new StringTextComponent("The team \"" + teamName + "\" has been removed"), false);
            data.removeTeam(teamName);
            PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(StorageHandler.teamToUuidsMap));
            return Command.SINGLE_SUCCESS;
        }
        throw new CommandException(new StringTextComponent("You do not have permission to use this command"));
    }

    /* Sets configuration option for team "teamName" */
    private static int teamConfig(MinecraftServer server, CommandSource sender, String teamName, String configOption, boolean configValue) {
        StorageHandler.get(server.getWorld(DimensionType.OVERWORLD));
        if (!configOption.equals("disableAdvancementSync") && !configOption.equals("enableFriendlyFire")) {
            throw new CommandException(new StringTextComponent("That is not a valid configuration option"));
        }
        StorageHandler.teamSettingsMap.get(teamName).put(configOption, configValue);
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(StorageHandler.teamToUuidsMap));
        return Command.SINGLE_SUCCESS;
    }
}
