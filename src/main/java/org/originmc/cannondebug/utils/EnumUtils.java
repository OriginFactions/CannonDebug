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

public final class EnumUtils {

    /**
     * Creates a more user friendly name for an enum and returns it.
     *
     * @param e the ${@link Enum} to get the friendly name of.
     * @return a friendlier name for the passed enum.
     */
    public static String getFriendlyName(Enum e) {
        // Iterate through every character.
        char[] name = e.name().toCharArray();
        for (int i = 0; i < name.length; i++) {
            // Replace all underscores with spaces.
            if (name[i] == '_') {
                name[i] = ' ';
            }

            // Change to lower case if not first letter and does not have a space before it.
            else if (i != 0 && name[i - 1] != ' ') {
                name[i] = Character.toLowerCase(name[i]);
            }
        }
        return String.valueOf(name);
    }

}
