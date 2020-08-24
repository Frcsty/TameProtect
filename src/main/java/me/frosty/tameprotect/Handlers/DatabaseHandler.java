package me.frosty.tameprotect.Handlers;

import me.frosty.tameprotect.Protection;
import me.frosty.tameprotect.TameProtect;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseHandler
{

    private FileConfiguration protectionConfig;
    private File              savedProtections;
    private TameProtect       plugin;

    public DatabaseHandler(TameProtect plugin)
    {
        this.plugin = plugin;
        reloadProtections();

        // Save the protections database every minute
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
        {
            public void run()
            {
                saveProtections();
            }
        }, 0L, plugin.getConfig().getLong("save_interval") * 20);
    }

    /**
     * Reload all protections from the database file.
     */
    public void reloadProtections()
    {
        if (savedProtections == null)
        {
            savedProtections = new File(plugin.getDataFolder() + "/protections.yml");
        }
        protectionConfig = YamlConfiguration.loadConfiguration(savedProtections);
        if (!protectionConfig.contains("migrated"))
        {
            // Set an option in case we want to convert the database to for example SQL at a later time.
            protectionConfig.set("migrated", false);
        }
    }

    /**
     * Save all protections to the database file.
     */
    public void saveProtections()
    {
        try
        {
            protectionConfig.save(savedProtections);
        }
        catch (IOException e)
        {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't save protections!");
        }
    }

    public Protection loadProtectionFromConfig(UUID animalId)
    {
        Set<String> ids = protectionConfig.getKeys(false);
        String animalIdString = animalId.toString();

        if (ids.contains(animalIdString))
        {
            List<String> memberList = protectionConfig.getStringList(animalIdString + ".members");
            HashSet<UUID> members = new HashSet<>();

            for (String m : memberList)
            {
                UUID member = UUID.fromString(m);
                members.add(member);
            }

            return new Protection(animalId, members, this);
        }
        else
        {
            return null;
        }
    }

    public void addMember(UUID animalId, UUID memberId)
    {
        List<String> oldList = protectionConfig.getStringList(animalId.toString() + ".members");
        oldList.add(memberId.toString());
        protectionConfig.set(animalId.toString() + ".members", oldList);
    }


    public void removeMember(UUID animalId, UUID memberId)
    {
        List<String> oldList = protectionConfig.getStringList(animalId.toString() + ".members");
        oldList.remove(memberId.toString());
        protectionConfig.set(animalId.toString() + ".members", oldList);
    }


    public void createProtection(UUID animalId)
    {
        protectionConfig.set(animalId.toString() + ".members", new LinkedList<String>());
    }


    public void removeProtection(UUID animalId)
    {
        protectionConfig.set(animalId.toString(), null);
    }

}
