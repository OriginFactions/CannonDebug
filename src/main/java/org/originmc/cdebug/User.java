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

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Data
public final class User {

    private int id = 1;

    private final Player player;

    private final ArrayList<BlockSelection> selections = new ArrayList<>();

    private boolean selecting;

    private boolean previewing;

    private FancyPager pager = FancyPager.DEFAULT;

    public BlockSelection getSelection(int id) {
        for (BlockSelection selection : selections) {
            if (selection.getId() == id) {
                return selection;
            }
        }
        return null;
    }

    public BlockSelection getSelection(Location location) {
        for (BlockSelection selection : selections) {
            if (selection.getLocation().equals(location)) {
                return selection;
            }
        }
        return null;
    }

    public BlockSelection addSelection(Location location) {
        // Do nothing if user already has this selection.
        BlockSelection selection = getSelection(location);
        if (selection != null) return selection;

        // Add selection.
        selection = new BlockSelection(id++, location);
        selections.add(selection);
        return selection;
    }

}
