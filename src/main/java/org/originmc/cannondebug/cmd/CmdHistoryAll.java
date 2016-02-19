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
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugPlugin;
import org.originmc.cannondebug.EntityTracker;
import org.originmc.cannondebug.FancyPager;
import org.originmc.cannondebug.utils.EnumUtils;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.*;

public final class CmdHistoryAll extends CommandExecutor {

    public CmdHistoryAll(CannonDebugPlugin plugin, CommandSender sender, String[] args, String permission) {
        super(plugin, sender, args, permission);
    }

    @Override
    public boolean perform() {
        // Generate fancy message lines for all new message data.
        List<FancyMessage> lines = new ArrayList<>();
        for (BlockSelection selection : user.getSelections()) {
            // Do nothing if tracker has not been spawned for this selection yet.
            EntityTracker tracker = selection.getTracker();
            if (tracker == null) continue;

            // Generate a new fancy message line to add to the pager.
            Location initial = tracker.getLocationHistory().get(0);
            Location latest = tracker.getLocationHistory().get(tracker.getLocationHistory().size() - 1);
            lines.add(new FancyMessage("ID: " + selection.getId() + " ")
                            .color(GRAY)
                            .formattedTooltip(
                                    new FancyMessage("Click for all history on this ID.")
                                            .color(DARK_AQUA)
                                            .style(BOLD),

                                    new FancyMessage("Spawned tick: ")
                                            .color(YELLOW)
                                            .then("" + tracker.getSpawnTick())
                                            .color(AQUA),

                                    new FancyMessage("Death tick: ")
                                            .color(YELLOW)
                                            .then((tracker.getDeathTick() == -1 ? "Still alive" : "" + tracker.getDeathTick()))
                                            .color(RED),

                                    new FancyMessage("Cached tick: ")
                                            .color(YELLOW)
                                            .then("" + plugin.getCurrentTick())
                                            .color(GREEN),

                                    new FancyMessage("Initial Location: ")
                                            .color(YELLOW)
                                            .then(initial.getBlockX() + " " + initial.getBlockY() + " " + initial.getBlockZ())
                                            .color(GRAY)
                            )

                            .command("/cannondebug h i " + selection.getId())

                            .then(EnumUtils.getFriendlyName(tracker.getEntityType()))
                            .color(YELLOW)

                            .then(" | ")
                            .color(DARK_GRAY)

                            .then("Last location: ")
                            .color(WHITE)

                            .then(latest.getBlockX() + " " + latest.getBlockY() + " " + latest.getBlockZ())
                            .color(RED)
            );
        }

        // Send user the pager messages.
        FancyPager pager = new FancyPager("All Latest History", lines.toArray(new FancyMessage[lines.size()]));
        send(pager, 0);
        return true;
    }

}
