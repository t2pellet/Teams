package com.daposeidonguy.teamsmod.common.command;

import com.daposeidonguy.teamsmod.TeamsMod;
import com.daposeidonguy.teamsmod.common.config.TeamConfig;
import com.daposeidonguy.teamsmod.common.network.PacketHandler;
import com.daposeidonguy.teamsmod.common.network.messages.MessageInvite;
import com.daposeidonguy.teamsmod.common.network.messages.MessageSaveData;
import com.daposeidonguy.teamsmod.common.storage.StorageEvents;
import com.daposeidonguy.teamsmod.common.storage.StorageHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandTeam extends CommandBase {

    private final List<String> aliases;
    private final String help = "Teams Commands: " +
            "\n/teamsmod create <name> : creates team with the name <name>" +
            "\n/teamsmod list : lists all created teams" +
            "\n/teamsmod info <name> : lists all players in the team with name <name>" +
            "\n/teamsmod player <name> : prints the team of the player with name <name>" +
            "\n/teamsmod invite <name> : invites player with name <name> to your team" +
            "\n/teamsmod accept : accepts invitation to team" +
            "\n/teamsmod kick <name> : kicks player with name <name> from your team" +
            "\n/teamsmod leave : leaves your team" +
            "\n/teamsmod remove <name> : ADMIN ONLY - deletes the team with name <name>";

    public CommandTeam() {
        aliases = new ArrayList<>();
        aliases.add("t");
        aliases.add("teams");
        aliases.add("teamsmod");
    }

    @Override
    public String getName() {
        return TeamsMod.MODID;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return help;
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer() && args.length > 0) {
            switch (args[0]) {
                case "create":
                    checkLength(args, 2);
                    teamCreate(server, sender, args[1]);
                    break;
                case "list":
                    teamList(sender);
                    break;
                case "info":
                    checkLength(args, 2);
                    teamInfo(server, sender, args[1]);
                    break;
                case "player":
                    checkLength(args, 2);
                    teamPlayer(server, sender, args[1]);
                    break;
                case "invite":
                    checkLength(args, 2);
                    teamInvite(sender, args[1]);
                    break;
                case "accept":
                    teamAccept(server, sender);
                    break;
                case "kick":
                    checkLength(args, 2);
                    teamKick(server, sender, args[1]);
                    break;
                case "leave":
                    teamLeave(server, sender);
                    break;
                case "remove":
                    checkLength(args, 2);
                    teamRemove(server, sender, args[1]);
                    break;
                case "config":
                    checkLength(args, 3);
                    if (!args[2].equalsIgnoreCase("false") && !args[2].equalsIgnoreCase("true")) {
                        throw new CommandException(I18n.format("teamsmod.badarguments"));
                    }
                    teamConfig(server, sender, args[1], Boolean.valueOf(args[2]));
                    break;
            }
        } else {
            sender.sendMessage(new TextComponentString(getUsage(sender)));
        }
    }

    /* Creates a team with name teamName and adds the command sender to the team */
    private void teamCreate(final MinecraftServer server, final ICommandSender sender, final String teamName) throws CommandException {
        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            if (StorageHandler.teamToUuidsMap.containsKey(teamName)) {
                throw new CommandException(I18n.format("teamsmod.create.nametaken"));
            } else if (StorageHandler.uuidToTeamMap.containsKey(player.getUniqueID())) {
                throw new CommandException(I18n.format("teamsmod.inteam"));
            }
            sender.sendMessage(new TextComponentTranslation("teamsmod.create.success").appendText(teamName));
            StorageEvents.data.addTeam(teamName, player);
            PacketHandler.INSTANCE.sendToAll(new MessageSaveData());
        }
    }

    /* Sends the list of teams in the server to the command sender */
    private void teamList(final ICommandSender sender) {
        sender.sendMessage(new TextComponentTranslation("teamsmod.list.success"));
        for (String s : StorageHandler.teamToUuidsMap.keySet()) {
            sender.sendMessage(new TextComponentString(s));
        }
    }

    /* Sends information about the team "teamName" to the command sender */
    private void teamInfo(final MinecraftServer server, final ICommandSender sender, final String teamName) throws CommandException {
        if (!StorageHandler.teamToUuidsMap.containsKey(teamName)) {
            throw new CommandException(I18n.format("teamsmod.info.invalidteam"));
        }
        sender.sendMessage(new TextComponentTranslation("teamsmod.info.success"));
        for (UUID id : StorageHandler.teamToUuidsMap.get(teamName)) {
            GameProfile profile = server.getPlayerProfileCache().getProfileByUUID(id);
            if (profile != null) {
                sender.sendMessage(new TextComponentString(profile.getName()));
            }
        }
    }

    /* Sends the team of the player "playerName" to the command sender */
    private void teamPlayer(final MinecraftServer server, final ICommandSender sender, final String playerName) throws CommandException {
        GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(playerName);
        if (profile == null) {
            throw new CommandException(I18n.format("teamsmod.nosuchplayer", playerName));
        } else if (StorageHandler.uuidToTeamMap.containsKey(profile.getId())) {
            String playerTeam = StorageHandler.uuidToTeamMap.get(profile.getId());
            sender.sendMessage(new TextComponentTranslation("teamsmod.player.success", playerName, playerTeam));
        } else {
            throw new CommandException(I18n.format("teamsmod.player.teamless", playerName));
        }
    }

    /* Sends an invite to the player "playerName" to join the team of the command sender */
    private void teamInvite(final ICommandSender sender, final String playerName) throws CommandException {
        if (sender instanceof EntityPlayer) {
            EntityPlayerMP newPlayer = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(playerName);
            EntityPlayer oldPlayer = (EntityPlayer) sender;
            String teamName = StorageHandler.uuidToTeamMap.get(oldPlayer.getUniqueID());
            if (teamName != null) {
                if (newPlayer == null) {
                    throw new CommandException(I18n.format("teamsmod.invite.nosuchplayer"));
                } else if (StorageHandler.teamToUuidsMap.get(teamName).contains(newPlayer.getUniqueID())) {
                    throw new CommandException(I18n.format("teamsmod.invite.alreadyinteam"));
                }
                newPlayer.getEntityData().setString("invitedto", teamName);
                newPlayer.getEntityData().setUniqueId("invitedby", oldPlayer.getUniqueID());
                oldPlayer.sendMessage(new TextComponentTranslation("teamsmod.invite.success", newPlayer.getGameProfile().getName()));
                PacketHandler.INSTANCE.sendTo(new MessageInvite(teamName), newPlayer);
                newPlayer.sendMessage(new TextComponentTranslation("teamsmod.invitedtoteam").appendText(teamName));
            } else {
                throw new CommandException(I18n.format("teamsmod.notinteam"));
            }
        }
    }

    /* Accepts most recent team invite */
    private void teamAccept(final MinecraftServer server, final ICommandSender sender) throws CommandException {
        if (sender instanceof EntityPlayer) {
            EntityPlayerMP invitee = (EntityPlayerMP) sender;
            String teamName = invitee.getEntityData().getString("invitedto");
            UUID uid = invitee.getUniqueID();
            if (teamName.equals("")) {
                throw new CommandException(I18n.format("teamsmod.accept.notinvited"));
            } else if (StorageHandler.uuidToTeamMap.containsKey(uid)) {
                throw new CommandException(I18n.format("teamsmod.inteam"));
            }
            StorageEvents.data.addPlayer(teamName, uid);
            if (!TeamConfig.server.disableAdvancementSync) {
                StorageHandler.syncPlayers(teamName, invitee);
            }
            PacketHandler.INSTANCE.sendToAll(new MessageSaveData());
            sender.sendMessage(new TextComponentTranslation("teamsmod.accept.success", teamName));
            EntityPlayerMP inviter = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(invitee.getEntityData().getUniqueId("invitedby"));
            if (inviter != null) {
                inviter.sendMessage(new TextComponentTranslation("teamsmod.accept.joined", invitee.getGameProfile().getName()));
            }
        }
    }

    /* Kicks player "playerName" from the team of the command sender */
    private void teamKick(final MinecraftServer server, final ICommandSender sender, final String playerName) throws CommandException {
        if (sender instanceof EntityPlayer) {
            GameProfile profile = server.getPlayerProfileCache().getGameProfileForUsername(playerName);
            if (profile == null) {
                throw new CommandException(I18n.format("teamsmod.nosuchplayer"));
            }
            UUID kickID = profile.getId();
            UUID senderID = ((EntityPlayer) sender).getUniqueID();
            if (StorageHandler.uuidToTeamMap.get(senderID) == null) {
                throw new CommandException(I18n.format("teamsmod.notinteam"));
            } else if (StorageHandler.uuidToTeamMap.get(kickID) == null || !StorageHandler.uuidToTeamMap.get(senderID).equals(StorageHandler.uuidToTeamMap.get(kickID))) {
                throw new CommandException(I18n.format("teamsmod.playernotinteam", profile.getName()));
            } else {
                String myTeam = StorageHandler.uuidToTeamMap.get(senderID);
                if (!StorageHandler.teamToOwnerMap.get(myTeam).equals(senderID)) {
                    throw new CommandException(I18n.format("teamsmod.notowner", profile.getName()));
                } else {
                    StorageEvents.data.removePlayer(myTeam, kickID);
                    sender.sendMessage(new TextComponentTranslation("teamsmod.kick.success", profile.getName()));
                    if (server.getPlayerList().getPlayerByUUID(kickID) != null) {
                        server.getPlayerList().getPlayerByUUID(kickID).sendMessage(new TextComponentTranslation("teamsmod.kicked"));
                    }
                    PacketHandler.INSTANCE.sendToAll(new MessageSaveData());
                }
            }
        }
    }

    /* Removes the command sender from their team */
    private void teamLeave(final MinecraftServer server, final ICommandSender sender) throws CommandException {
        if (sender instanceof EntityPlayer) {
            EntityPlayer p = (EntityPlayer) sender;
            if (!StorageHandler.uuidToTeamMap.containsKey(p.getUniqueID())) {
                throw new CommandException(I18n.format("teamsmod.notinteam"));
            }
            String toLeave = StorageHandler.uuidToTeamMap.get(p.getUniqueID());
            if (StorageHandler.teamToOwnerMap.get(toLeave).equals(p.getGameProfile().getId())) {
                if (StorageHandler.teamToUuidsMap.get(toLeave).size() > 1) {
                    throw new CommandException(I18n.format("teamsmod.leave.owner"));
                }
            }
            StorageEvents.data.removePlayer(toLeave, p.getUniqueID());
            p.sendMessage(new TextComponentTranslation("teamsmod.leave.success"));
            if (StorageHandler.teamToUuidsMap.get(toLeave).isEmpty()) {
                StorageEvents.data.removeTeam(toLeave);
            }
            PacketHandler.INSTANCE.sendToAll(new MessageSaveData());
        }
    }

    /* Removes the team "teamName" from the server */
    private void teamRemove(final MinecraftServer server, final ICommandSender sender, final String teamName) throws CommandException {
        if (sender.canUseCommand(2, this.getName()) || FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer()) {
            if (!StorageHandler.teamToUuidsMap.containsKey(teamName)) {
                throw new CommandException(I18n.format("teamsmod.remove.nosuchteam", teamName));
            }
            sender.sendMessage(new TextComponentTranslation("teamsmod.remove.success", teamName));
            StorageEvents.data.removeTeam(teamName);
            PacketHandler.INSTANCE.sendToAll(new MessageSaveData());
        } else {
            throw new CommandException("Missing permissions");
        }
    }

    /* Sets configuration option for team "teamName" */
    private void teamConfig(final MinecraftServer server, final ICommandSender sender, final String configOption, boolean configValue) throws CommandException {
        if (sender instanceof EntityPlayer) {
            String teamName = StorageHandler.uuidToTeamMap.get(((EntityPlayer) sender).getUniqueID());
            if (teamName == null) {
                throw new CommandException(I18n.format("teamsmod.notinteam"));
            }
            if (!configOption.equals("disableAdvancementSync") && !configOption.equals("enableFriendlyFire")) {
                throw new CommandException(I18n.format("teamsmod.config.invalid"));
            } else if (!((EntityPlayer) sender).getUniqueID().equals(StorageHandler.teamToOwnerMap.get(teamName))) {
                throw new CommandException(I18n.format("teamsmod.notowner"));
            }
            StorageHandler.teamSettingsMap.get(teamName).put(configOption, configValue);
            PacketHandler.INSTANCE.sendToAll(new MessageSaveData());
            sender.sendMessage(new TextComponentTranslation("teamsmod.config.success"));
        }
    }

    private void checkLength(String[] args, int length) throws CommandException {
        if (args.length != length) {
            throw new CommandException(I18n.format("teamsmod.badarguments"));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        List<String> tabCompletions = new ArrayList<>();
        if (args.length == 1) {
            char[] charArray = args[0].toCharArray();
            if (charArray.length < 1) {
                tabCompletions.add("config");
                tabCompletions.add("remove");
                tabCompletions.add("leave");
                tabCompletions.add("kick");
                tabCompletions.add("accept");
                tabCompletions.add("invite");
                tabCompletions.add("player");
                tabCompletions.add("info");
                tabCompletions.add("list");
                tabCompletions.add("create");
            } else {
                if (charArray[0] == 'c') {
                    tabCompletions.add("config");
                    tabCompletions.add("create");
                } else if (charArray[0] == 'r') {
                    tabCompletions.add("remove");
                } else if (charArray[0] == 'l') {
                    tabCompletions.add("leave");
                    tabCompletions.add("list");
                } else if (charArray[0] == 'k') {
                    tabCompletions.add("kick");
                } else if (charArray[0] == 'a') {
                    tabCompletions.add("accept");
                } else if (charArray[0] == 'i') {
                    tabCompletions.add("info");
                    tabCompletions.add("invite");
                } else if (charArray[0] == 'p') {
                    tabCompletions.add("player");
                } else {
                    tabCompletions.add("config");
                    tabCompletions.add("remove");
                    tabCompletions.add("leave");
                    tabCompletions.add("kick");
                    tabCompletions.add("accept");
                    tabCompletions.add("invite");
                    tabCompletions.add("player");
                    tabCompletions.add("info");
                    tabCompletions.add("list");
                    tabCompletions.add("create");
                }
            }
        }
        if (args.length == 2) {
            switch (args[0]) {
                case "config":
                    tabCompletions.add("disableAdvancementSync");
                    tabCompletions.add("enableFriendlyFire");
                    break;
                case "remove":
                    for (String team : StorageHandler.teamToUuidsMap.keySet()) {
                        tabCompletions.add(team);
                    }
                    break;
                case "kick":
                    try {
                        String teamName = StorageHandler.uuidToTeamMap.get(sender.getCommandSenderEntity().getUniqueID());
                        for (UUID playerId : StorageHandler.teamToUuidsMap.get(StorageHandler.uuidToTeamMap.get(teamName))) {
                            tabCompletions.add(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(playerId).getName());
                        }
                    } catch (NullPointerException ignored) {
                    }
                    break;
                case "invite":
                    for (String playerName : FMLCommonHandler.instance().getMinecraftServerInstance().getOnlinePlayerNames()) {
                        tabCompletions.add(playerName);
                    }
                    break;
                case "player":
                    for (String playerName : FMLCommonHandler.instance().getMinecraftServerInstance().getOnlinePlayerNames()) {
                        tabCompletions.add(playerName);
                    }
                    break;
                case "info":
                    for (String teamName : StorageHandler.teamToUuidsMap.keySet()) {
                        tabCompletions.add(teamName);
                    }
                    break;
            }
        } else if (args.length == 3) {
            tabCompletions.add("true");
            tabCompletions.add("false");
        }
        return tabCompletions;
    }

}
