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
package org.originmc.cdebug;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.material.Dispenser;
import org.bukkit.plugin.java.JavaPlugin;
import org.originmc.cdebug.cmd.CommandType;
import org.originmc.cdebug.utils.MaterialUtils;
import org.originmc.cdebug.utils.NumberUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import static org.bukkit.ChatColor.*;
import static org.originmc.cdebug.utils.MaterialUtils.*;

public final class CannonDebug extends JavaPlugin implements Listener, Runnable {

    private static final String TOO_MANY_SELECTIONS = RED + "You have too many selections! " + GRAY + "(Max = %m)";

    private static final String ADDED_SELECTION = GREEN + "" + BOLD + "ADD " + WHITE + "%m @ %x %y %z " + GRAY + "ID: %i";

    private static final String REMOVED_SELECTION = RED + "" + BOLD + "REM " + WHITE + "%m @ %x %y %z " + GRAY + "ID: %i";

    private final HashMap<UUID, User> users = new HashMap<>();

    private final ArrayList<EntityTracker> activeTrackers = new ArrayList<>();

    private long currentTick = 0;

    /**
     * Attempts to retrieve a valid user profile from a player id.
     *
     * @param playerId a players unique id.
     * @return the user profile attached to this players id.
     */
    public User getUser(UUID playerId) {
        // Return null if the player id has no user profile attached.
        if (!users.containsKey(playerId)) return null;

        // Return the user.
        return users.get(playerId);
    }

    /**
     * Gets what tick the server is currently on.
     *
     * @return current tick.
     */
    public long getCurrentTick() {
        return currentTick;
    }

    /**
     * Attempts to either add or remove a selection depending on whether or not
     * the user already had this position set.
     *
     * @param player the player to select the region.
     * @param user   the players' corresponding user account.
     * @param block  the block to select.
     */
    public void handleSelection(Player player, User user, Block block) {
        // Do nothing if not a selectable block.
        if (!MaterialUtils.isSelectable(block.getType())) return;

        // Attempt to deselect block if it is already selected.
        BlockSelection selection = user.getSelection(block.getLocation());
        if (selection != null) {
            // Inform the player.
            player.sendMessage(REMOVED_SELECTION
                            .replace("%m", getFriendlyName(block.getType()))
                            .replace("%x", "" + block.getX())
                            .replace("%y", "" + block.getY())
                            .replace("%z", "" + block.getZ())
                            .replace("%i", "" + selection.getId())
            );

            // Remove the clicked location.
            user.getSelections().remove(selection);

            // Update users preview.
            if (user.isPreviewing()) {
                Bukkit.getScheduler().runTask(this, () -> player.sendBlockChange(block.getLocation(), block.getType(), block.getData()));
            }
            return;
        }

        // Do nothing if the user has too many selections.
        int max = NumberUtils.getNumericalPerm(player, "cannondebug.maxselections.");
        if (user.getSelections().size() >= max) {
            player.sendMessage(TOO_MANY_SELECTIONS.replace("%m", "" + max));
            return;
        }

        // Update users preview.
        if (user.isPreviewing()) {
            Bukkit.getScheduler().runTask(this, () -> player.sendBlockChange(block.getLocation(), Material.EMERALD_BLOCK, (byte) 0));
        }

        // Add the selected location.
        selection = user.addSelection(block.getLocation());

        // Inform the player.
        player.sendMessage(ADDED_SELECTION
                        .replace("%m", getFriendlyName(block.getType()))
                        .replace("%x", "" + block.getX())
                        .replace("%y", "" + block.getY())
                        .replace("%z", "" + block.getZ())
                        .replace("%i", "" + selection.getId())
        );
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getScheduler().runTaskTimer(this, this, 1, 1);

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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void createUser(PlayerJoinEvent event) {
        users.put(event.getPlayer().getUniqueId(), new User(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void deleteUser(PlayerQuitEvent event) {
        users.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void addSelection(PlayerInteractEvent event) {
        // Do nothing if the player is not right clicking a block.
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // Do nothing if the player has no user profile attached.
        Player player = event.getPlayer();
        User user = getUser(player.getUniqueId());
        if (user == null) return;

        // Do nothing if the user is not selecting.
        if (!user.isSelecting()) return;

        // Cancel the event.
        event.setCancelled(true);

        // Do nothing if the block is not selectable.
        Block block = event.getClickedBlock();
        handleSelection(player, user, block);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void removeSelection(PlayerInteractEvent event) {
        // Do nothing if the player is not right clicking a block.
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        // Do nothing if the player has no user profile attached.
        Player player = event.getPlayer();
        User user = getUser(player.getUniqueId());
        if (user == null) return;

        // Do nothing if the user is not selecting.
        if (!user.isSelecting()) return;

        // Cancel the event.
        event.setCancelled(true);

        // Do nothing if the block is not selectable.
        Block block = event.getClickedBlock();
        handleSelection(player, user, block);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void startProfiling(BlockDispenseEvent event) {
        // Do nothing if block is not a dispenser.
        Block block = event.getBlock();
        if (!isDispenser(block.getType())) return;

        // Do nothing if not shot TNT.
        if (!isExplosives(event.getItem().getType())) return;

        // Loop through each user profile.
        BlockSelection selection;
        EntityTracker tracker = null;
        for (User user : users.values()) {
            // Do nothing if user is not attempting to profile current block.
            selection = user.getSelection(block.getLocation());
            if (selection == null) {
                continue;
            }

            // Build a new tracker due to it being used.
            if (tracker == null) {
                // Cancel the event.
                event.setCancelled(true);

                // Shoot a new falling block with the exact same properties as current.
                BlockFace face = ((Dispenser) block.getState().getData()).getFacing();
                Location location = block.getLocation().clone();
                location.add(face.getModX() + 0.5, face.getModY(), face.getModZ() + 0.5);
                TNTPrimed tnt = block.getWorld().spawn(location, TNTPrimed.class);
                tracker = new EntityTracker(tnt.getType(), currentTick);
                tracker.setEntity(tnt);
                activeTrackers.add(tracker);
            }

            // Add block tracker to user.
            selection.setTracker(tracker);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void startProfiling(EntityChangeBlockEvent event) {
        // Do nothing if the material is not used for stacking in cannons.
        Block block = event.getBlock();
        if (!isStacker(block.getType())) return;

        // Do nothing if block is not turning into a falling block.
        if (!(event.getEntity() instanceof FallingBlock)) return;

        // Loop through each user profile.
        BlockSelection selection;
        EntityTracker tracker = null;
        for (User user : users.values()) {
            // Do nothing if user is not attempting to profile current block.
            selection = user.getSelection(block.getLocation());
            if (selection == null) {
                continue;
            }

            // Build a new tracker due to it being used.
            if (tracker == null) {
                tracker = new EntityTracker(event.getEntityType(), currentTick);
                tracker.setEntity(event.getEntity());
                activeTrackers.add(tracker);
            }

            // Add block tracker to user.
            selection.setTracker(tracker);
        }
    }

}
