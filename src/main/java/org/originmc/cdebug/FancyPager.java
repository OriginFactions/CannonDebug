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
import mkremins.fanciful.FancyMessage;

import static org.bukkit.ChatColor.*;

@Data
public final class FancyPager {

    public static final FancyPager DEFAULT = new FancyPager("Default Pager", new FancyMessage[]{});

    private static final int maxLines = 10;

    private final FancyMessage[][] pages;

    private final int pageCount;

    private final int totalLines;

    public FancyPager(String header, FancyMessage[] lines) {
        // Grant default line if search ended up empty.
        if (lines.length == 0) {
            lines = new FancyMessage[]{new FancyMessage("Sorry, no results were found.").color(YELLOW)};
        }

        // Initialize pager data.
        int totalLines = lines.length + ((lines.length / (maxLines - 2)) * 2);
        if (totalLines % maxLines == 0) totalLines -= 2;
        this.totalLines = totalLines;
        pageCount = (totalLines / maxLines) + 1;
        pages = new FancyMessage[pageCount][maxLines];

        // Loop through every possible line.
        int page = 0;
        for (int i = 0; i <= totalLines; i++) {
            // Increment page number if reached max number of lines.
            if (i != 0 && i % maxLines == 0) {
                page++;
            }

            // Switch the line number to add header and footer.
            int line = i % maxLines;
            switch (line) {
                case 0:
                    pages[page][line] = new FancyMessage("_____.[ ").color(GOLD)
                            .then(header).color(DARK_GREEN)
                            .then(" - ").color(GOLD)
                            .then((page + 1) + "/" + pageCount).color(AQUA)
                            .then(" ]._____").color(GOLD);
                    break;
                case 9:
                    pages[page][line] = getFooter(page);
                    break;
                default:
                    pages[page][line] = lines[i - (2 * page) - 1];
                    break;
            }
        }

        // Always attempt to add a footer.
        if (pages[pageCount - 1][maxLines - 1] == null) {
            pages[pageCount - 1][maxLines - 1] = getFooter(pageCount - 1);
        }
    }

    private FancyMessage getFooter(int page) {
        // Create formatted tooltips.
        FancyMessage prev = new FancyMessage("Previous Page: ").color(YELLOW).then("" + page).color(LIGHT_PURPLE);
        FancyMessage next = new FancyMessage("Next Page: ").color(YELLOW).then("" + (page + 2)).color(LIGHT_PURPLE);

        // Only apply next footer if first page.
        if (page == 0) {
            // Do not bother adding a footer with a page count of 1.
            if (pageCount == 1) return null;
            return new FancyMessage("")
                    .then("NEXT").color(GREEN).style(BOLD).command("/cannondebug p " + (page + 2)).formattedTooltip(next)
                    .then(" >>>").color(DARK_GRAY).command("/cannondebug p " + (page + 2)).formattedTooltip(next);
        }

        // Only apply previous footer if last page.
        else if (page == pageCount - 1) {
            return new FancyMessage("")
                    .then("<<< ").color(DARK_GRAY).command("/cannondebug p " + page).formattedTooltip(prev)
                    .then("PREV").color(RED).style(BOLD).command("/cannondebug p " + page).formattedTooltip(prev);
        }

        // Apply both footers if middle page.
        else {
            return new FancyMessage("")
                    .then("<<< ").color(DARK_GRAY).command("/cannondebug p " + page).formattedTooltip(prev)
                    .then("PREV").color(RED).style(BOLD).command("/cannondebug p " + page).formattedTooltip(prev)
                    .then("    ")
                    .then("NEXT").color(GREEN).style(BOLD).command("/cannondebug p " + (page + 2)).formattedTooltip(next)
                    .then(" >>>").color(DARK_GRAY).command("/cannondebug p " + (page + 2)).formattedTooltip(next);
        }
    }

    public FancyMessage[] getPage(int page) {
        return pages[page];
    }

}
