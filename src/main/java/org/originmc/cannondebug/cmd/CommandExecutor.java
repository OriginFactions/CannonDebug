/*
 * This file is part of CannonProfiler, licensed under the MIT License (MIT).
 *
 * Copyright (c) Origin <http://www.originmc.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.originmc.cannondebug.cmd;

import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.originmc.cannondebug.CannonDebugPlugin;
import org.originmc.cannondebug.FancyPager;
import org.originmc.cannondebug.User;

public abstract class CommandExecutor {

    public final CannonDebugPlugin plugin;

    public final CommandSender sender;

    public final String[] args;

    public final String permission;

    public User user = null;

    public CommandExecutor(CannonDebugPlugin plugin, CommandSender sender, String[] args, String permission) {
        this.plugin = plugin;
        this.sender = sender;
        this.args = args;
        this.permission = permission;
    }

    /**
     * Attempts to check if the sender has permission and if the sender has a
     * user profile attached from this plugin if required.
     *
     * @return true if valid command, otherwise false.
     */
    public boolean execute() {
        // Do nothing if the sender does not have permission.
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "You do not have permission.");
            return true;
        }

        // Do nothing if sender is not a player.
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can execute this command!");
            return true;
        }

        // Do nothing if sender has no user profile.
        user = plugin.getUser(((Player) sender).getUniqueId());
        if (user == null) {
            sender.sendMessage(ChatColor.RED + "Player profile has not been loaded! Please re-log then retry.");
            return true;
        }

        // Perform the command.
        return perform();
    }

    /**
     * Sends command sender a fancy pager message.
     *
     * @param pager the fancy pager instance to send.
     * @param page the page to send to the player.
     */
    public void send(FancyPager pager, int page) {
        for (FancyMessage message : pager.getPage(page)) {
            // Send empty messages if null.
            if (message == null) {
                sender.sendMessage("");
                continue;
            }
            message.send(sender);
        }

        user.setPager(pager);
    }

    /**
     * Executes the command.
     * @return true if valid command, otherwise false.
     */
    public abstract boolean perform();

}
