package com.mathmod.client.screen;

final class PaletteCursor {
    private int size;
    private int index;

    PaletteCursor(int size) {
        resize(size);
    }

    int index() {
        return index;
    }

    void resize(int size) {
        this.size = Math.max(0, size);
        index = clamp(index);
    }

    void select(int index) {
        this.index = clamp(index);
    }

    void move(int distance) {
        select(index + distance);
    }

    void first() {
        select(0);
    }

    void last() {
        select(size - 1);
    }

    static int revealRow(int scroll, int rowTop, int rowHeight, int viewportHeight, int maxScroll) {
        int next = scroll;
        if (rowTop < next) {
            next = rowTop;
        } else if (rowTop + rowHeight > next + viewportHeight) {
            next = rowTop + rowHeight - viewportHeight;
        }
        return Math.max(0, Math.min(maxScroll, next));
    }

    static int wholeRowsHeight(int availableHeight, int rowHeight) {
        if (availableHeight <= 0 || rowHeight <= 0) {
            return 0;
        }
        return availableHeight - availableHeight % rowHeight;
    }

    static boolean rowFits(int rowTop, int rowHeight, int viewportTop, int viewportHeight) {
        return rowHeight > 0
                && viewportHeight > 0
                && rowTop >= viewportTop
                && rowTop + rowHeight <= viewportTop + viewportHeight;
    }

    static boolean rowsFit(
            int rowTop,
            int rowHeight,
            int rowCount,
            int viewportTop,
            int viewportHeight
    ) {
        if (rowHeight <= 0 || rowCount <= 0) {
            return false;
        }
        long totalHeight = (long) rowHeight * rowCount;
        return totalHeight <= Integer.MAX_VALUE
                && rowFits(rowTop, (int) totalHeight, viewportTop, viewportHeight);
    }

    static int alignedMaxScroll(int contentHeight, int viewportHeight, int... rowStarts) {
        int firstScrollThatShowsTheEnd = Math.max(0, contentHeight - viewportHeight);
        if (firstScrollThatShowsTheEnd == 0 || rowStarts.length == 0) {
            return 0;
        }
        for (int rowStart : rowStarts) {
            if (rowStart >= firstScrollThatShowsTheEnd) {
                return rowStart;
            }
        }
        return Math.max(0, rowStarts[rowStarts.length - 1]);
    }

    static int revealAlignedRow(
            int scroll,
            int rowTop,
            int rowHeight,
            int viewportHeight,
            int maxScroll,
            int... rowStarts
    ) {
        int desired = scroll;
        if (rowTop < scroll) {
            desired = rowTop;
        } else if (rowTop + rowHeight > scroll + viewportHeight) {
            desired = rowTop + rowHeight - viewportHeight;
        } else {
            return Math.max(0, Math.min(maxScroll, scroll));
        }
        return alignedScrollAtOrAfter(desired, maxScroll, rowStarts);
    }

    static int moveAlignedScroll(int scroll, int direction, int maxScroll, int... rowStarts) {
        if (direction > 0) {
            for (int rowStart : rowStarts) {
                if (rowStart > scroll && rowStart <= maxScroll) {
                    return rowStart;
                }
            }
            return maxScroll;
        }
        if (direction < 0) {
            int previous = 0;
            for (int rowStart : rowStarts) {
                if (rowStart >= scroll || rowStart > maxScroll) {
                    break;
                }
                previous = rowStart;
            }
            return previous;
        }
        return Math.max(0, Math.min(maxScroll, scroll));
    }

    static int nearestAlignedScroll(int desired, int maxScroll, int... rowStarts) {
        int clamped = Math.max(0, Math.min(maxScroll, desired));
        int nearest = 0;
        int nearestDistance = clamped;
        for (int rowStart : rowStarts) {
            if (rowStart < 0 || rowStart > maxScroll) {
                continue;
            }
            int distance = Math.abs(rowStart - clamped);
            if (distance < nearestDistance) {
                nearest = rowStart;
                nearestDistance = distance;
            }
        }
        int maxDistance = Math.abs(maxScroll - clamped);
        return maxDistance < nearestDistance ? maxScroll : nearest;
    }

    private static int alignedScrollAtOrAfter(int desired, int maxScroll, int... rowStarts) {
        int clamped = Math.max(0, Math.min(maxScroll, desired));
        for (int rowStart : rowStarts) {
            if (rowStart >= clamped && rowStart <= maxScroll) {
                return rowStart;
            }
        }
        return maxScroll;
    }

    private int clamp(int candidate) {
        return size == 0 ? 0 : Math.max(0, Math.min(size - 1, candidate));
    }
}
