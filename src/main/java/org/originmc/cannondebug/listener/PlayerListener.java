package org.originmc.cannondebug.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.originmc.cannondebug.BlockSelection;
import org.originmc.cannondebug.CannonDebugPlugin;
import org.originmc.cannondebug.User;
import org.originmc.cannondebug.utils.EnumUtils;
import org.originmc.cannondebug.utils.MaterialUtils;
import org.originmc.cannondebug.utils.NumberUtils;

import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;

public class PlayerListener implements Listener {

    private final CannonDebugPlugin plugin;

    public PlayerListener(CannonDebugPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void createUser(PlayerJoinEvent event) {
        plugin.getUsers().put(event.getPlayer().getUniqueId(), new User(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void deleteUser(PlayerQuitEvent event) {
        plugin.getUsers().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void addSelection(PlayerInteractEvent event) {
        // Do nothing if the player is not right clicking a block.
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // Do nothing if the player has no user profile attached.
        Player player = event.getPlayer();
        User user = plugin.getUser(player.getUniqueId());
        if (user == null) return;

        // Do nothing if the user is not selecting.
        if (!user.isSelecting()) return;

        // Cancel the event.
        event.setCancelled(true);

        // Do nothing if the block is not selectable.
        Block block = event.getClickedBlock();
        handleSelection(user, block);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void removeSelection(PlayerInteractEvent event) {
        // Do nothing if the player is not right clicking a block.
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        // Do nothing if the player has no user profile attached.
        Player player = event.getPlayer();
        User user = plugin.getUser(player.getUniqueId());
        if (user == null) return;

        // Do nothing if the user is not selecting.
        if (!user.isSelecting()) return;

        // Cancel the event.
        event.setCancelled(true);

        // Do nothing if the block is not selectable.
        Block block = event.getClickedBlock();
        handleSelection(user, block);
    }

    /**
     * Attempts to either add or remove a selection depending on whether or not
     * the user already had this position set.
     *
     * @param user  the user that is adding to their selection.
     * @param block the block to select.
     */
    private void handleSelection(User user, Block block) {
        // Do nothing if not a selectable block.
        if (!MaterialUtils.isSelectable(block.getType())) return;

        // Attempt to deselect block if it is already selected.
        BlockSelection selection = user.getSelection(block.getLocation());
        Player player = user.getBase();
        if (selection != null) {
            // Inform the player.
            player.sendMessage(String.format(RED + "" + BOLD + "REM " + WHITE + "%m @ %x %y %z " + GRAY + "ID: %i",
                    EnumUtils.getFriendlyName(block.getType()), block.getX(), block.getY(), block.getZ(), selection.getId()));

            // Remove the clicked location.
            user.getSelections().remove(selection);

            // Update users preview.
            if (user.isPreviewing()) {
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        player.sendBlockChange(block.getLocation(), block.getType(), block.getData()));
            }
            return;
        }

        // Do nothing if the user has too many selections.
        int max = NumberUtils.getNumericalPerm(player, "cannondebug.maxselections.");
        if (user.getSelections().size() >= max) {
            player.sendMessage(String.format(RED + "You have too many selections! " + GRAY + "(Max = %s)", max));
            return;
        }

        // Update users preview.
        if (user.isPreviewing()) {
            Bukkit.getScheduler().runTask(plugin, () ->
                    player.sendBlockChange(block.getLocation(), Material.EMERALD_BLOCK, (byte) 0));
        }

        // Add the selected location.
        selection = user.addSelection(block.getLocation());

        // Inform the player.
        player.sendMessage(String.format(GREEN + "" + BOLD + "ADD " + WHITE + "%s @ %s %s %s " + GRAY + "ID: %s",
                EnumUtils.getFriendlyName(block.getType()), block.getX(), block.getY(), block.getZ(), selection.getId()));
    }

}
