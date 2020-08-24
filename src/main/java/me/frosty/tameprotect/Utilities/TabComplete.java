package me.frosty.tameprotect.Utilities;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabComplete implements TabCompleter
{

    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String alias, String[] args)
    {

        if (args.length > 2)
        {
            return Collections.emptyList();
        }

        if (args.length == 1)
        {
            final List<String> commands = new ArrayList<>();
            commands.add("add");
            commands.add("remove");
            commands.add("setowner");
            commands.add("info");
            commands.add("unname");
            commands.add("untame");

            return commands;
        }
        else if (args.length == 2)
        {
            final List<String> players = new ArrayList<>();
            for (Player plr : Bukkit.getServer().getOnlinePlayers())
            {
                players.add(plr.getName());
            }

            return players;
        }
        return Collections.emptyList();
    }

}

