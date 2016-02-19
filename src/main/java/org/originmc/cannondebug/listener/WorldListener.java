package org.originmc.cannondebug.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.material.Dispenser;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugPlugin;
import org.originmc.cannondebug.EntityTracker;
import org.originmc.cannondebug.User;

import static org.originmc.cannondebug.utils.MaterialUtils.isDispenser;
import static org.originmc.cannondebug.utils.MaterialUtils.isExplosives;
import static org.originmc.cannondebug.utils.MaterialUtils.isStacker;

public class WorldListener implements Listener {

    private final CannonDebugPlugin plugin;

    public WorldListener(CannonDebugPlugin plugin) {
        this.plugin = plugin;
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
        for (User user : plugin.getUsers().values()) {
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
                tracker = new EntityTracker(tnt.getType(), plugin.getCurrentTick());
                tracker.setEntity(tnt);
                plugin.getActiveTrackers().add(tracker);
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
        for (User user : plugin.getUsers().values()) {
            // Do nothing if user is not attempting to profile current block.
            selection = user.getSelection(block.getLocation());
            if (selection == null) {
                continue;
            }

            // Build a new tracker due to it being used.
            if (tracker == null) {
                tracker = new EntityTracker(event.getEntityType(), plugin.getCurrentTick());
                tracker.setEntity(event.getEntity());
                plugin.getActiveTrackers().add(tracker);
            }

            // Add block tracker to user.
            selection.setTracker(tracker);
        }
    }

}
