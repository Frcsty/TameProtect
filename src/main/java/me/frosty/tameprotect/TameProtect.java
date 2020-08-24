package me.frosty.tameprotect;

import me.frosty.tameprotect.Classes.MessageInfo;
import me.frosty.tameprotect.Handlers.CommandHandler;
import me.frosty.tameprotect.Handlers.DatabaseHandler;
import me.frosty.tameprotect.Utilities.TabComplete;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TameProtect extends JavaPlugin
{

    private static Map<UUID, Protection> protections = new HashMap<>();
    private        DatabaseHandler       protectionDatabase;
    private        CommandHandler        commandHandler;

    private final TabComplete tabComplete = new TabComplete();

    public void loadConfiguration()
    {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

    public void reloadConfiguration()
    {
        this.reloadConfig();
        commandHandler.reload();
    }

    @Override
    public void onEnable()
    {
        loadConfiguration();
        this.protectionDatabase = new DatabaseHandler(this);
        this.commandHandler = new CommandHandler(this);

        getCommand("tameprotect").setTabCompleter(tabComplete);

        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }

    @Override
    public void onDisable()
    {
        protectionDatabase.saveProtections();
    }

    public DatabaseHandler getProtectionDatabase()
    {
        return this.protectionDatabase;
    }

    public Map<UUID, Protection> getProtections()
    {
        return protections;
    }

    public CommandHandler getCommandHandler()
    {
        return commandHandler;
    }

    public void sendMessage(Player player, String msgType, MessageInfo msgInfo)
    {
        String message = "";
        if (msgType.equals("info"))
        {
            message += this.getConfig().getString("messages.info1") + "\n";
            message += this.getConfig().getString("messages.info2") + "\n";
            message += this.getConfig().getString("messages.info3");
        }
        else
        {
            if (this.getConfig().contains("messages." + msgType))
            {
                message = this.getConfig().getString("message_prefix");
                message += this.getConfig().getString("messages." + msgType);
            }
        }
        message = message.replaceAll("&p", msgInfo.playerName);
        message = message.replaceAll("&w", msgInfo.animalName);
        message = message.replaceAll("&t", msgInfo.members);
        if (message.length() > 0)
        {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    public TabComplete getTabComplete()
    {
        return tabComplete;
    }

}