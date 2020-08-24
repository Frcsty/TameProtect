package me.frosty.tameprotect.Handlers;

import me.frosty.tameprotect.Classes.MessageInfo;
import me.frosty.tameprotect.Classes.Pair;
import me.frosty.tameprotect.Classes.TamePlayer;
import me.frosty.tameprotect.Protection;
import me.frosty.tameprotect.TameProtect;
import me.frosty.tameprotect.Utilities.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandHandler implements CommandExecutor
{
    private TameProtect plugin;

    private long                  commandTimeout;
    private Map<String, TameTask> commandToTask;
    private enum TameTask {ADD, REMOVE, SET_OWNER, INFO, UNNAME, UNTAME}

    private Map<UUID, Pair<TameTask, String>> commandQueue = new HashMap<>();

    public CommandHandler(TameProtect plugin) {
        this.commandTimeout = plugin.getConfig().getLong("command_timeout") * 20;
        this.plugin = plugin;
        plugin.getCommand("tame").setExecutor(this);
        plugin.getCommand("tprot").setExecutor(this);
        plugin.getCommand("tameprotect").setExecutor(this);

        commandToTask = new HashMap<>();
        commandToTask.put("add", TameTask.ADD);
        commandToTask.put("remove", TameTask.REMOVE);
        commandToTask.put("setowner", TameTask.SET_OWNER);
        commandToTask.put("info", TameTask.INFO);
        commandToTask.put("unname", TameTask.UNNAME);
        commandToTask.put("untame", TameTask.UNTAME);
    }

    public void reload() {
        this.commandTimeout = plugin.getConfig().getLong("command_timeout") * 20;
    }


    private void queueCommand(final UUID player, TameTask intent, String extraInfo) {
        commandQueue.put(player, new Pair<>(intent, extraInfo));
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
                commandQueue.remove(player)
        , commandTimeout);
    }

    public boolean onEntityInteract(Player player, Entity entity) {
        if (!(entity instanceof Tameable)) return false;
        Pair<TameTask, String> command = commandQueue.get(player.getUniqueId());
        Protection protection = Protection.loadProtection(entity, plugin);
        if (protection == null || command == null) return false;
        commandQueue.remove(player.getUniqueId());

        String action = "";
        switch (command.getKey()) {
            case ADD:
                action = "addmember";
                break;
            case REMOVE:
                action = "removemember";
                break;
            case SET_OWNER:
                action = "setowner";
                break;
            case INFO:
                action = "info";
                break;
            case UNNAME:
                action = "unname";
                break;
            case UNTAME:
                action = "untame";
                break;
        }

        final boolean mountOnCommand = plugin.getConfig().getBoolean("mount_on_command");
        final boolean playerIsOwner = protection.getOwner().equals(player.getUniqueId());
        final boolean actionPerm = player.hasPermission(String.format("tameprotect.%s", action));
        final boolean actionPermOther = player.hasPermission(String.format("tameprotect.%s.other", action));
        final String argument = command.getValue();
        final TamePlayer argPlayer = PlayerUtils.getPlayer(argument);
        final MessageInfo msgInfo = new MessageInfo();
        msgInfo.animalName = protection.getName();

        if (actionPermOther || (actionPerm && playerIsOwner)) {
            if (!command.getKey().equals(TameTask.INFO)) {
                if (argPlayer == null || (!argPlayer.hasPlayedBefore()) && !argPlayer.isOnline()) {
                    plugin.sendMessage(player, "unknown",  msgInfo);
                    return !mountOnCommand;
                }

                msgInfo.animalName = protection.getName();
                msgInfo.playerName = argPlayer.getName();
            }
            else {
                msgInfo.animalName = protection.getAnimal().getCustomName();
                msgInfo.playerName = PlayerUtils.getPlayerName(protection.getOwner());
                msgInfo.members = protection.getFormattedMembers();
            }

            switch (command.getKey()) {
                case ADD:
                    protection.addMember(argPlayer.getUniqueId());
                    plugin.sendMessage(player, "add", msgInfo);
                    break;
                case REMOVE:
                    protection.removeMember(argPlayer.getUniqueId());
                    plugin.sendMessage(player, "remove", msgInfo);
                    break;
                case SET_OWNER:
                    Player newPlayer = Bukkit.getPlayer(command.getValue());
                    if (newPlayer == null) {
                        plugin.sendMessage(player, "offline", msgInfo);
                        return !mountOnCommand;
                    }
                    protection.setOwner(newPlayer, plugin);
                    break;
                case UNNAME:
                    protection.removeCustomName();
                    plugin.sendMessage(player, "unname", msgInfo);
                    break;
                case UNTAME:
                    final UUID entityId = protection.getAnimal().getUniqueId();
                    ((Tameable) protection.getAnimal()).setOwner(null);
                    protection.removeCustomName();
                    plugin.getProtections().remove(entityId);
                    plugin.getProtectionDatabase().removeProtection(entityId);
                    plugin.getProtectionDatabase().saveProtections();
                    plugin.sendMessage(player, "untame", msgInfo);
                    break;
                case INFO:
                    plugin.sendMessage(player, "info", msgInfo);
                    break;
            }
        }
        else {
            plugin.sendMessage(player, "permission", msgInfo);
        }
        return !mountOnCommand;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equals("reload") && sender.hasPermission("tameprotect.reload")) {
                plugin.reloadConfiguration();
                plugin.sendMessage((Player) sender, "configuration", new MessageInfo());
                return true;
            }

            final boolean isPlayer = sender instanceof Player;
            if (isPlayer) {
                final Player player = (Player) sender;
                final TameTask queuedType = commandToTask.get(args[0].toLowerCase());
                final String secondArg = args.length > 1 ? args[1] : "";
                if (queuedType != null) {
                    queueCommand(player.getUniqueId(), queuedType, secondArg);
                    plugin.sendMessage(player, "rightclick", new MessageInfo());
                }
                return true;
            }
        }
        return false;
    }
}
