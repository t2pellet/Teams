package com.daposeidonguy.teamsmod.common.command;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.network.PacketHandler;
import com.daposeidonguy.teamsmod.common.network.messages.MessageInvite;
import com.daposeidonguy.teamsmod.common.network.messages.MessageSaveData;
import com.daposeidonguy.teamsmod.common.storage.StorageEvents;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;

public class CommandTeam {

    private static final String[] aliases = {"teamsmod", "teams", "t"};

    /* Constructs and registers the team command */
    public static void register(final CommandDispatcher<CommandSource> dispatcher) {
        TeamsMod.logger.info("Registering commands...");
        final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        for (String alias : aliases) {
            dispatcher.register(Commands.literal(alias)
                    .then(Commands.literal("create")
                            .then(Commands.argument("teamName", StringArgumentType.word())
                                    .executes(ctx -> teamCreate(server, ctx.getSource(), StringArgumentType.getString(ctx, "teamName")))))
                    .then(Commands.literal("list")
                            .executes(ctx -> teamList(ctx.getSource())))
                    .then(Commands.literal("info")
                            .then(Commands.argument("teamName", StringArgumentType.word()).suggests(SuggestionHandler.TEAM_SUGGESTIONS)
                                    .executes(ctx -> teamInfo(server, ctx.getSource(), StringArgumentType.getString(ctx, "teamName")))))
                    .then(Commands.literal("player")
                            .then(Commands.argument("playerName", EntityArgument.player())
                                    .executes(ctx -> teamPlayer(server, ctx.getSource(), EntityArgument.getPlayer(ctx, "playerName").getGameProfile().getName()))))
                    .then(Commands.literal("invite")
                            .then(Commands.argument("playerName", EntityArgument.player())
                                    .executes(ctx -> teamInvite(ctx.getSource(), EntityArgument.getPlayer(ctx, "playerName").getGameProfile().getName()))))
                    .then(Commands.literal("accept")
                            .executes(ctx -> teamAccept(server, ctx.getSource())))
                    .then(Commands.literal("kick")
                            .then(Commands.argument("playerName", EntityArgument.player())
                                    .executes(ctx -> teamKick(server, ctx.getSource(), EntityArgument.getPlayer(ctx, "playerName").getGameProfile().getName()))))
                    .then(Commands.literal("leave")
                            .executes(ctx -> teamLeave(server, ctx.getSource())))
                    .then(Commands.literal("remove")
                            .then(Commands.argument("teamName", StringArgumentType.word()).suggests(SuggestionHandler.TEAM_SUGGESTIONS).requires(source -> source.hasPermissionLevel(server.getOpPermissionLevel()) || server.isSinglePlayer() || TeamConfig.noOpRemoveTeam)
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
    private static int teamCreate(final MinecraftServer server, final CommandSource sender, final String teamName) throws CommandSyntaxException {
        PlayerEntity player = sender.asPlayer();
        if (StorageHandler.teamToUuidsMap.containsKey(teamName)) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("teamsmod.create.nametaken")).create();
        } else if (StorageHandler.uuidToTeamMap.containsKey(player.getUniqueID())) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("teamsmod.inteam")).create();
        }
        sender.sendFeedback(new TranslationTextComponent("teamsmod.create.success").appendText(teamName), false);
        StorageEvents.data.addTeam(teamName, player);
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(server.getWorld(DimensionType.OVERWORLD)));
        return Command.SINGLE_SUCCESS;
    }

    /* Sends the list of teams in the server to the command sender */
    private static int teamList(final CommandSource sender) {
        sender.sendFeedback(new TranslationTextComponent("teamsmod.list.success"), false);
        for (String s : StorageHandler.teamToUuidsMap.keySet()) {
            sender.sendFeedback(new StringTextComponent(s), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    /* Sends information about the team "teamName" to the command sender */
    private static int teamInfo(final MinecraftServer server, final CommandSource sender, final String teamName) throws CommandSyntaxException {
        if (!StorageHandler.teamToUuidsMap.containsKey(teamName)) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("teamsmod.info.invalidteam")).create();
        }
        sender.sendFeedback(new TranslationTextComponent("teamsmod.info.success"), false);
        for (UUID id : StorageHandler.teamToUuidsMap.get(teamName)) {
            GameProfile profile = server.getPlayerProfileCache().getProfileByUUID(id);
            if (profile != null) {
                sender.sendFeedback(new StringTextComponent(profile.getName()), false);
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /* Sends the team of the player "playerName" to the command sender */
    private static int teamPlayer(final MinecraftServer server, final CommandSource sender, final String playerName) throws CommandSyntaxException {
        GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(playerName);
        if (profile == null) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("teamsmod.nosuchplayer", playerName)).create();
        } else if (StorageHandler.uuidToTeamMap.containsKey(profile.getId())) {
            String playerTeam = StorageHandler.uuidToTeamMap.get(profile.getId());
            sender.sendFeedback(new TranslationTextComponent("teamsmod.player.success", playerName, playerTeam), false);
            return Command.SINGLE_SUCCESS;
        } else {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("teamsmod.player.teamless", playerName)).create();
        }
    }

    /* Sends an invite to the player "playerName" to join the team of the command sender */
    private static int teamInvite(final CommandSource sender, final String playerName) throws CommandSyntaxException {
        ServerPlayerEntity newPlayer = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUsername(playerName);
        PlayerEntity oldPlayer = sender.asPlayer();
        String teamName = StorageHandler.uuidToTeamMap.get(oldPlayer.getUniqueID());
        if (teamName != null) {
            if (newPlayer == null) {
                throw new SimpleCommandExceptionType(new TranslationTextComponent("teamsmod.invite.nosuchplayer")).create();
            } else if (StorageHandler.teamToUuidsMap.get(teamName).contains(newPlayer.getUniqueID())) {
                throw new SimpleCommandExceptionType(new TranslationTextComponent("teamsmod.invite.alreadyinteam")).create();
            }
            newPlayer.getPersistentData().putString("invitedto", teamName);
            newPlayer.getPersistentData().putUniqueId("invitedby", oldPlayer.getUniqueID());
            oldPlayer.sendMessage(new TranslationTextComponent("teamsmod.invite.success", newPlayer.getGameProfile().getName()));
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> newPlayer), new MessageInvite(teamName));
            newPlayer.sendMessage(new TranslationTextComponent("teamsmod.invitedtoteam").appendText(teamName));
            return Command.SINGLE_SUCCESS;
        } else {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("teamsmod.notinteam")).create();
        }
    }

    /* Accepts most recent team invite */
    private static int teamAccept(final MinecraftServer server, final CommandSource sender) throws CommandSyntaxException {
        ServerPlayerEntity invitee = sender.asPlayer();
        String teamName = invitee.getPersistentData().getString("invitedto");
        UUID uid = invitee.getUniqueID();
        if (teamName.equals("")) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("teamsmod.accept.notinvited")).create();
        } else if (StorageHandler.uuidToTeamMap.containsKey(uid)) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("teamsmod.inteam")).create();
        }
        StorageEvents.data.addPlayer(teamName, uid);
        if (!TeamConfig.disableAdvancementSync) {
            StorageHandler.syncPlayers(teamName, invitee);
        }
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(server.getWorld(DimensionType.OVERWORLD)));
        sender.sendFeedback(new TranslationTextComponent("teamsmod.accept.success", teamName), false);
        ServerPlayerEntity inviter = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(invitee.getPersistentData().getUniqueId("invitedby"));
        if (inviter != null) {
            inviter.sendMessage(new TranslationTextComponent("teamsmod.accept.joined", invitee.getGameProfile().getName()));
        }
        return Command.SINGLE_SUCCESS;
    }

    /* Kicks player "playerName" from the team of the command sender */
    private static int teamKick(final MinecraftServer server, final CommandSource sender, final String playerName) throws CommandSyntaxException {
        GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(playerName);
        if (profile == null) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("teamsmod.nosuchplayer")).create();
        }
        UUID kickID = profile.getId();
        UUID senderID = sender.asPlayer().getUniqueID();
        if (StorageHandler.uuidToTeamMap.get(senderID) == null) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("teamsmod.notinteam")).create();
        } else if (StorageHandler.uuidToTeamMap.get(kickID) == null || !StorageHandler.uuidToTeamMap.get(senderID).equals(StorageHandler.uuidToTeamMap.get(kickID))) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("teamsmod.playernotinteam", profile.getName())).create();
        } else {
            String myTeam = StorageHandler.uuidToTeamMap.get(senderID);
            if (!StorageHandler.teamToOwnerMap.get(myTeam).equals(senderID)) {
                throw new SimpleCommandExceptionType(new TranslationTextComponent("teamsmod.notowner", profile.getName())).create();
            } else {
                StorageEvents.data.removePlayer(myTeam, kickID);
                sender.sendFeedback(new TranslationTextComponent("teamsmod.kick.success", profile.getName()), false);
                if (server.getPlayerList().getPlayerByUUID(kickID) != null) {
                    server.getPlayerList().getPlayerByUUID(kickID).sendMessage(new TranslationTextComponent("teamsmod.kicked"));
                }
                PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(server.getWorld(DimensionType.OVERWORLD)));
                return Command.SINGLE_SUCCESS;
            }
        }
    }

    /* Removes the command sender from their team */
    private static int teamLeave(final MinecraftServer server, final CommandSource sender) throws CommandSyntaxException {
        PlayerEntity p = sender.asPlayer();
        if (!StorageHandler.uuidToTeamMap.containsKey(p.getUniqueID())) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("teamsmod.notinteam")).create();
        }
        String toLeave = StorageHandler.uuidToTeamMap.get(p.getUniqueID());
        if (StorageHandler.teamToOwnerMap.get(toLeave).equals(sender.asPlayer().getGameProfile().getId())) {
            if (StorageHandler.teamToUuidsMap.get(toLeave).size() > 1) {
                throw new SimpleCommandExceptionType(new TranslationTextComponent("teamsmod.leave.owner")).create();
            }
        }
        StorageEvents.data.removePlayer(toLeave, p.getUniqueID());
        p.sendMessage(new TranslationTextComponent("teamsmod.leave.success"));
        if (StorageHandler.teamToUuidsMap.get(toLeave).isEmpty()) {
            StorageEvents.data.removeTeam(toLeave);
        }
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(server.getWorld(DimensionType.OVERWORLD)));
        return Command.SINGLE_SUCCESS;
    }

    /* Removes the team "teamName" from the server */
    private static int teamRemove(final MinecraftServer server, final CommandSource sender, final String teamName) throws CommandSyntaxException {
        if (!StorageHandler.teamToUuidsMap.containsKey(teamName)) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("teamsmod.remove.nosuchteam", teamName)).create();
        }
        sender.sendFeedback(new TranslationTextComponent("teamsmod.remove.success", teamName), false);
        StorageEvents.data.removeTeam(teamName);
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(server.getWorld(DimensionType.OVERWORLD)));
        return Command.SINGLE_SUCCESS;
    }

    /* Sets configuration option for team "teamName" */
    private static int teamConfig(final MinecraftServer server, final CommandSource sender, final String teamName, final String configOption, boolean configValue) throws CommandSyntaxException {
        if (!configOption.equals("disableAdvancementSync") && !configOption.equals("enableFriendlyFire")) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("teamsmod.config.invalid")).create();
        } else if (!sender.asPlayer().getUniqueID().equals(StorageHandler.teamToOwnerMap.get(teamName))) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("teamsmod.notowner")).create();
        }
        StorageHandler.teamSettingsMap.get(teamName).put(configOption, configValue);
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new MessageSaveData(server.getWorld(DimensionType.OVERWORLD)));
        sender.sendFeedback(new TranslationTextComponent("teamsmod.config.success"), false);
        return Command.SINGLE_SUCCESS;
    }

}
