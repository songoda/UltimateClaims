package com.songoda.ultimateclaims.command;

import com.songoda.ultimateclaims.UltimateClaims;
import com.songoda.ultimateclaims.command.commands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandManager implements CommandExecutor {

    private static final List<AbstractCommand> commands = new ArrayList<>();
    private UltimateClaims plugin;

    public CommandManager(UltimateClaims plugin) {
        this.plugin = plugin;

        plugin.getCommand("UltimateClaims").setExecutor(this);

        AbstractCommand commandUltimateClaims = addCommand(new CommandUltimateClaims());

        addCommand(new CommandSettings(commandUltimateClaims));
        addCommand(new CommandReload(commandUltimateClaims));
        addCommand(new CommandClaim(commandUltimateClaims));
        addCommand(new CommandUnClaim(commandUltimateClaims));
        addCommand(new CommandInvite(commandUltimateClaims));
        addCommand(new CommandAccept(commandUltimateClaims));
        addCommand(new CommandKick(commandUltimateClaims));
    }

    private AbstractCommand addCommand(AbstractCommand abstractCommand) {
        commands.add(abstractCommand);
        return abstractCommand;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        for (AbstractCommand abstractCommand : commands) {
            if (abstractCommand.getCommand().equalsIgnoreCase(command.getName())) {
                if (strings.length == 0) {
                    processRequirements(abstractCommand, commandSender, strings);
                    return true;
                }
            } else if (strings.length != 0 && abstractCommand.getParent() != null && abstractCommand.getParent().getCommand().equalsIgnoreCase(command.getName())) {
                String cmd = strings[0];
                if (cmd.equalsIgnoreCase(abstractCommand.getCommand())) {
                    processRequirements(abstractCommand, commandSender, strings);
                    return true;
                }
            }
        }
        plugin.getLocale().newMessage("&7The command you entered does not exist or is spelt incorrectly.").sendPrefixedMessage(commandSender);
        return true;
    }

    private void processRequirements(AbstractCommand command, CommandSender sender, String[] strings) {
        if (!(sender instanceof Player) && command.isNoConsole()) {
            sender.sendMessage("You must be a player to use this command.");
            return;
        }
        if (command.getPermissionNode() == null || sender.hasPermission(command.getPermissionNode())) {
            AbstractCommand.ReturnType returnType = command.runCommand(plugin, sender, strings);
            if (returnType == AbstractCommand.ReturnType.SYNTAX_ERROR) {
                plugin.getLocale().newMessage("&cInvalid Syntax!").sendPrefixedMessage(sender);
                plugin.getLocale().newMessage("&7The valid syntax is: &6" + command.getSyntax() + "&7.").sendPrefixedMessage(sender);
            }
            return;
        }
        plugin.getLocale().newMessage("event.general.nopermission").sendPrefixedMessage(sender);
    }

    public List<AbstractCommand> getCommands() {
        return Collections.unmodifiableList(commands);
    }
}