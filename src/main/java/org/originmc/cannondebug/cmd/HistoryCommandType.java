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

import org.bukkit.command.CommandSender;
import org.originmc.cannondebug.CannonDebugPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public enum HistoryCommandType {

    ALL(CmdHistoryAll.class, new String[]{"all", "a"}),

    HELP(CmdHistoryHelp.class, new String[]{"help", "?"}),

    ID(CmdHistoryID.class, new String[]{"id", "i"}),

    TICK(CmdHistoryTick.class, new String[]{"tick", "t"});

    private static final Map<String, HistoryCommandType> BY_ALIAS = new HashMap<>();

    private static final String BASE_PERMISSION = "cannondebug.history.";

    private final Class<? extends CommandExecutor> commandExecutor;

    private final String[] aliases;

    private final String permission;

    HistoryCommandType(Class<? extends CommandExecutor> commandExecutor, String[] aliases) {
        this.commandExecutor = commandExecutor;
        this.aliases = aliases;
        this.permission = BASE_PERMISSION + name().toLowerCase();
    }

    /**
     * Attempts to grab a new instance of the corresponding command executor for
     * this current command.
     *
     * @param plugin the plugin instance.
     * @param sender entity that executed this command.
     * @param args   arguments included with the command.
     * @return a new CommandExecutor instance that corresponds to the command arguments.
     */
    public static CommandExecutor fromCommand(CannonDebugPlugin plugin, CommandSender sender, String[] args) {
        // Return default (HELP) command type if invalid arguments.
        if (args.length == 1 || !BY_ALIAS.containsKey(args[1])) {
            return newInstance(HELP, plugin, sender, args);
        }

        // Return corresponding command type to inputted arguments.
        return newInstance(BY_ALIAS.get(args[1]), plugin, sender, args);
    }

    /**
     * Creates a new instance of the CommandExecutor corresponding to the
     * command type parameter.
     *
     * @param commandType command type to retrieve a new instance for.
     * @param plugin      the plugin instance.
     * @param sender      entity that executed this command.
     * @param args        arguments included with the command.
     * @return a new CommandExecutor instance that corresponds to the command type.
     */
    public static CommandExecutor newInstance(HistoryCommandType commandType, CannonDebugPlugin plugin, CommandSender sender, String[] args) {
        try {
            return commandType.commandExecutor
                    .getConstructor(CannonDebugPlugin.class, CommandSender.class, String[].class, String.class)
                    .newInstance(plugin, sender, args, commandType.permission);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        for (HistoryCommandType commandType : values()) {
            for (String alias : commandType.aliases) {
                BY_ALIAS.put(alias, commandType);
            }
        }
    }

}
