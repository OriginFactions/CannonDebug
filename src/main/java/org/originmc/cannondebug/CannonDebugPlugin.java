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

package org.originmc.cannondebug;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.originmc.cannondebug.cmd.CommandType;
import org.originmc.cannondebug.listener.PlayerListener;
import org.originmc.cannondebug.listener.WorldListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class CannonDebugPlugin extends JavaPlugin implements Runnable {

    private final Map<UUID, User> users = new HashMap<>();

    private final List<EntityTracker> activeTrackers = new ArrayList<>();

    private long currentTick = 0;

    public Map<UUID, User> getUsers() {
        return users;
    }

    public User getUser(UUID playerId) {
        // Return null if the player id has no user profile attached.
        if (!users.containsKey(playerId)) return null;

        // Return the user.
        return users.get(playerId);
    }

    public List<EntityTracker> getActiveTrackers() {
        return activeTrackers;
    }

    public long getCurrentTick() {
        return currentTick;
    }

    public void setCurrentTick(long currentTick) {
        this.currentTick = currentTick;
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(this), this);
        getServer().getScheduler().runTaskTimer(this, this, 1, 1);

        // Load user profiles.
        for (Player player : Bukkit.getOnlinePlayers()) {
            users.put(player.getUniqueId(), new User(player));
        }
    }

    @Override
    public void run() {
        // Loop through every active tracker.
        Iterator<EntityTracker> iterator = activeTrackers.iterator();
        while (iterator.hasNext()) {
            // Add new location and velocity to the tracker histories.
            EntityTracker tracker = iterator.next();
            tracker.getLocationHistory().add(tracker.getEntity().getLocation());
            tracker.getVelocityHistory().add(tracker.getEntity().getVelocity());

            // Remove dead entities from tracker.
            if (tracker.getEntity().isDead()) {
                tracker.setDeathTick(currentTick);
                tracker.setEntity(null);
                iterator.remove();
            }
        }

        // Increment the tick counter.
        currentTick++;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        return CommandType.fromCommand(this, sender, args).execute();
    }

}
