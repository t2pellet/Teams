package com.daposeidonguy.teamsmod.commands;

import com.daposeidonguy.teamsmod.handlers.ConfigHandler;
import com.daposeidonguy.teamsmod.network.MessageSaveData;
import com.daposeidonguy.teamsmod.network.PacketHandler;
import com.daposeidonguy.teamsmod.team.SaveData;
import com.mojang.authlib.GameProfile;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class CommandTeam implements ICommand {

    private final List aliases;


    public CommandTeam() {
        aliases = new ArrayList();
        aliases.add("team");
        aliases.add("teams");
        aliases.add("t");
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

    @Override
    public String getName() {
        return "team";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "\nteam create <name>\nteam list\nteam invite <name>\nteam kick <name>\nteam leave\nteam remove <name>\nteam info <name>\nteam player <name>";
    }

    @Override
    public List<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {

        if (!sender.getEntityWorld().isRemote && args.length > 0) {
            SaveData data = SaveData.get(sender.getEntityWorld());
            switch (args[0]) {
                case "create":
                    try {
                        String name = args[1];
                        if(SaveData.teamsMap.containsKey(name)) {
                            sender.sendMessage(new TextComponentString("That team already exists"));
                            return;
                        }
                        EntityPlayer player = (EntityPlayer)sender;
                        if(SaveData.teamMap.containsKey(player.getUniqueID())) {
                            sender.sendMessage(new TextComponentString("You're already in a team! Leave it first!"));
                            return;
                        }
                        data.addTeam(name, player);
                        sender.sendMessage(new TextComponentString("New team named" + " \"" + name + "\" created"));
                    } catch (Exception ex) {
                        sender.sendMessage(new TextComponentString("Incorrect syntax. /team create <teamname>"));
                    }
                    break;
                case "list":
                    Iterator<String> teamIterator = SaveData.teamsMap.keySet().iterator();
                    sender.sendMessage(new TextComponentString("List of teams:"));
                    while (teamIterator.hasNext()) {
                        String team = teamIterator.next();
                        sender.sendMessage(new TextComponentString(team));
                    }
                    break;
                case "kick":
                    try {
                        String playerName= args[1];
                        UUID uid = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache().getGameProfileForUsername(playerName).getId();
                        if(SaveData.teamMap.containsKey(uid)) {
                            sender.sendMessage(new TextComponentString("Removing that player from your team!"));
                            data.removePlayer((EntityPlayer)sender,uid);
                        }
                    } catch (Exception ex) {
                        sender.sendMessage(new TextComponentString("Must enter a valid playername to remove from your team: /team remove <playername>"));
                    }
                    break;
                case "accept":
                    EntityPlayer invitee = (EntityPlayer)sender;
                    EntityPlayerMP inviter = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(UUID.fromString(invitee.getEntityData().getString("invitedby")));
                    UUID uid = invitee.getUniqueID();
                    if(inviter==null) {
                        sender.sendMessage(new TextComponentString("You have not been invited to a team"));
                        break;
                    }
                    else if (SaveData.teamMap.containsKey(uid)){
                        sender.sendMessage(new TextComponentString("Removing you from your old team..."));
                        data.removePlayer(invitee,uid);
                    }
                    data.addPlayer(inviter, uid);
                    PacketHandler.INSTANCE.sendTo(new MessageSaveData(SaveData.teamsMap),(EntityPlayerMP)invitee);
                    if (SaveData.teamMap.containsKey(inviter.getUniqueID()) && inviter != null && !ConfigHandler.disableAchievementSync) {
                        String name = SaveData.teamMap.get(inviter.getUniqueID());
                        Iterator<UUID> uuidIterator = SaveData.teamsMap.get(name).iterator();
                        while (uuidIterator.hasNext()) {
                            EntityPlayerMP playerMP = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(uuidIterator.next());
                            if(playerMP!=null) {
                                SaveData.syncPlayers(name,playerMP);
                            }
                        }
                    }
                    sender.sendMessage(new TextComponentString("Joined " + inviter.getDisplayNameString() + "'s team"));
                    inviter.sendMessage(new TextComponentString(invitee.getDisplayNameString() + " has joined your team!"));
                    break;
                case "invite":
                    try {
                        String playerName = args[1];
                        EntityPlayer newp = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(playerName);
                        EntityPlayer oldp = (EntityPlayer)sender;
                        UUID uuid = newp.getUniqueID();
                        String theteam = SaveData.teamMap.get(oldp.getUniqueID());
                        if(SaveData.teamsMap.get(theteam).contains(uuid)) {
                            sender.sendMessage(new TextComponentString("That player is already in your team!"));
                            return;
                        }
                        newp.getEntityData().setString("invitedby",oldp.getUniqueID().toString());
                        oldp.sendMessage(new TextComponentString("You have invited " + newp.getDisplayNameString() + " to your team"));
                        newp.sendMessage(new TextComponentString("You have been invited by " + oldp.getDisplayNameString() + " to join their team. Type /team accept to accept"));
                    } catch (Exception ex) {
                        sender.sendMessage(new TextComponentString("Must enter an online player's username to invite"));
                    }
                    break;
                case "leave":
                    try {
                        EntityPlayer p = (EntityPlayer)sender;
                        String toLeave = SaveData.teamMap.get(p.getUniqueID());
                        data.removePlayer(p,p.getUniqueID());
                        p.sendMessage(new TextComponentString("You left your team"));
                        if(SaveData.teamsMap.get(toLeave).isEmpty()) {
                            data.removeTeam(toLeave);
                        }
                    } catch (Exception ex) {
                        sender.sendMessage(new TextComponentString("You're not in a team"));
                    }
                    break;
                case "player":
                    try {
                        String playerName = args[1];
                        if(SaveData.teamMap.containsKey(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache().getGameProfileForUsername(playerName))) {
                            String playerteam = SaveData.teamMap.get(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache().getGameProfileForUsername(playerName).getId());
                            sender.sendMessage(new TextComponentString(playerName + " is in the following team:"));
                            sender.sendMessage(new TextComponentString(playerteam));
                        } else {
                            sender.sendMessage(new TextComponentString(playerName + " is not in a team"));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        sender.sendMessage(new TextComponentString("Enter valid playername"));
                    }
                    break;
                case "remove":
                    boolean flag = false;
                    if(FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getOppedPlayers().getEntry(((EntityPlayerMP)sender).getGameProfile())==null && !ConfigHandler.noOpRemoveTeam) {
                        flag=true;
                    } else if (FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer()) {
                        if (!FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().getWorldInfo().areCommandsAllowed()) {
                            flag = true;
                        }
                    }
                    if (flag) {
                        TextComponentString error = new TextComponentString("You do not have permission to use this command");
                        Style red = new Style();
                        red.setColor(TextFormatting.RED);
                        error.setStyle(red);
                        sender.sendMessage(error);
                        break;
                    }
                    try {
                        String teamName = args[1];
                        sender.sendMessage(new TextComponentString("The team \"" + teamName + "\" has been removed"));
                        data.removeTeam(teamName);
                    } catch (Exception ex) {
                        sender.sendMessage(new TextComponentString("That team doesn't exist, or an error occurred"));
                    }
                    break;
                case "info":
                    sender.sendMessage(new TextComponentString("Players in Team: "));
                    try {
                        String teamName = args[1];
                        if(SaveData.teamsMap.containsKey(teamName)) {
                            Iterator<UUID> uuidIterator = SaveData.teamsMap.get(teamName).iterator();
                            while(uuidIterator.hasNext()) {
                                UUID id = uuidIterator.next();
                                GameProfile profile = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache().getProfileByUUID(id);
                                if (profile!=null) {
                                    sender.sendMessage(new TextComponentString(profile.getName()));
                                }
                            }
                        }
                    } catch(Exception ex){
                        sender.sendMessage(new TextComponentString("Enter team name to get info on"));
                    }
                    break;
                default:
                    sender.sendMessage(new TextComponentString("Invalid command"));
            }
        } else {
            sender.sendMessage(new TextComponentString("Must include command"));
        }
        PacketHandler.INSTANCE.sendToAll(new MessageSaveData(SaveData.teamsMap));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length < 2) {
            List<String> tabCompletions = new ArrayList<>();
            if(args[0].contains("cr")) {
                tabCompletions.add("create");
            } else if (args[0].contains("li")) {
                tabCompletions.add("list");
            } else if (args[0].contains("di")) {
                tabCompletions.add("disband");
            } else if (args[0].contains("in")) {
                if (args[0].contains("inv")) {
                    tabCompletions.add("invite");
                } else if (args[0].contains("inf")) {
                    tabCompletions.add("info");
                }
                else {
                    tabCompletions.add("invite");
                    tabCompletions.add("info");
                }
            } else if (args[0].contains("pl")) {
                tabCompletions.add("player");
            } else if (args[0].contains("re")) {
                tabCompletions.add("remove");
            } else if (args[0].contains("k")) {
                tabCompletions.add("kick");
            } else if (args[0].contains("ac")) {
                tabCompletions.add("accept");
            } else {
                tabCompletions.add("create");
                tabCompletions.add("list");
                tabCompletions.add("disband");
                tabCompletions.add("invite");
                tabCompletions.add("info");
                tabCompletions.add("player");
                tabCompletions.add("remove");
                tabCompletions.add("kick");
                tabCompletions.add("accept");
            }
            return tabCompletions;
        } else {
            if (args[0].equals("invite") || args[0].equals("player") || args[0].equals("kick")) {
                List<String> playerList = new ArrayList<>();
                for (EntityPlayer player : sender.getEntityWorld().playerEntities) {
                    playerList.add(player.getDisplayNameString());
                }
                return playerList;
            } else if (args[0].equals("info") || args[0].equals("remove")) {

                return new ArrayList(SaveData.teamsMap.keySet());
            }
            return NonNullList.create();
        }
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }
}
