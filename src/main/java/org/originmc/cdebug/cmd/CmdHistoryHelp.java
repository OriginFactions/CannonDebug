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
package org.originmc.cdebug.cmd;

import mkremins.fanciful.FancyMessage;
import org.bukkit.command.CommandSender;
import org.originmc.cdebug.CannonDebug;
import org.originmc.cdebug.FancyPager;

import static org.bukkit.ChatColor.*;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.YELLOW;

public final class CmdHistoryHelp extends CommandExecutor {

    private static final FancyPager HELP_PAGER = new FancyPager("Help for command \"/c h\"", new FancyMessage[]{
            new FancyMessage("/c h a,all ").color(AQUA).then("Prints all up to date profiling history.").color(YELLOW),
            new FancyMessage("/c h ?,help ").color(AQUA).then("Displays this plugins' history help page.").color(YELLOW),
            new FancyMessage("/c h i,id ").color(AQUA).then("[id] ").color(DARK_AQUA).then("View all history for an entity id.").color(YELLOW),
            new FancyMessage("/c h t,tick ").color(AQUA).then("[tick] ").color(DARK_AQUA).then("View all history in a server tick.").color(YELLOW)
    });

    public CmdHistoryHelp(CannonDebug plugin, CommandSender sender, String[] args, String permission) {
        super(plugin, sender, args, permission);
    }

    @Override
    public boolean perform() {
        // Send the sender this plugins' history help message.
        send(HELP_PAGER, 0);
        return true;
    }

}
