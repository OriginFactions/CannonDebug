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
import org.bukkit.command.CommandSender;
import org.originmc.cannondebug.CannonDebugPlugin;
import org.originmc.cannondebug.FancyPager;

import static org.bukkit.ChatColor.*;

public final class CmdHelp extends CommandExecutor {

    private static final FancyPager HELP_PAGER = new FancyPager("Help for command \"/c\"", new FancyMessage[]{
            new FancyMessage("/c c,clear ").color(AQUA).then("[history,h,selections,s] ").color(DARK_AQUA).then("Clear history or selections.").color(YELLOW),
            new FancyMessage("/c ?,help ").color(AQUA).then("Displays this plugins' main help page.").color(YELLOW),
            new FancyMessage("/c h,l,history,lookup ").color(AQUA).then("[?,params] ").color(DARK_AQUA).then("Lists latest profiling history.").color(YELLOW),
            new FancyMessage("/c p,page ").color(AQUA).then("[page] ").color(DARK_AQUA).then("Go to specific page for current pager.").color(YELLOW),
            new FancyMessage("/c v,pre,view,preview ").color(AQUA).then("Preview all selected blocks.").color(YELLOW),
            new FancyMessage("/c r,region ").color(AQUA).then("Select all available blocks in WorldEdit region.").color(YELLOW),
            new FancyMessage("/c s,select ").color(AQUA).then("Bind block selector tool to hand.").color(YELLOW),
            new FancyMessage(""),
            new FancyMessage("This plugin was made with the intentions of providing an easy to use method of profiling cannons.").color(GREEN),
            new FancyMessage(""),
            new FancyMessage("To accomplish this, this plugin will grant you the ability to select either blocks of " +
                    "sand or dispensers around your cannon. The entities that are either shot or falling forms of " +
                    "their previous block states will be profiled every tick.").color(GREEN),
            new FancyMessage(""),
            new FancyMessage("All information such as velocities and locations tracked by the plugin can then be accessed " +
                    "via lookup commands. Profiling information is then indexed, to be clear and easy to use.").color(GREEN)
    });

    public CmdHelp(CannonDebugPlugin plugin, CommandSender sender, String[] args, String permission) {
        super(plugin, sender, args, permission);
    }

    @Override
    public boolean perform() {
        // Send the sender this plugins' help message.
        send(HELP_PAGER, 0);
        return true;
    }

}
