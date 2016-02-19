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

package org.originmc.cannondebug.utils;

import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachmentInfo;

public final class NumberUtils {

    /**
     * Attempts to find an integer from a string. Failing that, it will return
     * zero.
     *
     * @param str string to parse for an integer.
     * @return a valid integer. Default as zero if parsing fails.
     */
    public static int parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Attempts to find the highest integer based off the effective permissions
     * of a permissible.
     *
     * @param permissible the permissible to check for permissions.
     * @param permission the start of the permission string.
     * @return highest value that begins with permission string.
     */
    public static int getNumericalPerm(Permissible permissible, String permission) {
        int value = 0;
        for (PermissionAttachmentInfo perm : permissible.getEffectivePermissions()) {
            // Do nothing if permission does not start with called.
            String checkPerm = perm.getPermission();
            if (!checkPerm.startsWith(permission)) continue;

            // Do nothing if permission has no third argument.
            String[] segmented = checkPerm.split("\\.");
            if (segmented.length != 3) continue;

            // Attempt to read the next argument as an integer.
            int comparison = 0;
            try {
                comparison = Integer.parseInt(segmented[2]);
            } catch (NumberFormatException e) {
                // Return maximum value if user has the unlimited permission.
                if (segmented[2].equalsIgnoreCase("unlimited")) {
                    return Integer.MAX_VALUE;
                }
            }

            // Replace max area with higher value.
            if (value < comparison) {
                value = comparison;
            }
        }
        return value;
    }

}
