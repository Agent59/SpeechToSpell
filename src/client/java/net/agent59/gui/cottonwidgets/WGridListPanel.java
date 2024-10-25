package net.agent59.gui.cottonwidgets;

import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WPanelWithInsets;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Insets;

/**
 * A panel that automatically positions elements in a grid, based on the order they are added / stored in.
 * <p>The elements are placed side by side in rows until the right border is encountered,
 * after which the next row below will be filled.<br>
 * By default, the width, set by {@link #setSize(int, int)}, will be used, but if a single element is wider,
 * the panel will be widened to fit this element.<br>
 * The panel automatically extends its height to fit all the elements.
 */
public class WGridListPanel extends WPanelWithInsets {
    protected int grid = 18;
    protected int horizontalGap = 0;
    protected int verticalGap = 0;

    /**
     * Set the gaps between grid cells.
     * @param horizontalGap the horizontal gap between grid cells
     * @param verticalGap   the vertical gap between grid cells
     */
    public WGridListPanel setGaps(int horizontalGap, int verticalGap) {
        this.horizontalGap = horizontalGap;
        this.verticalGap = verticalGap;
        this.layout();
        return this;
    }

    @Override
    public WGridListPanel setInsets(Insets insets) {
        this.insets = insets;
        this.layout();
        return this;
    }

    public WGridListPanel add(WWidget widget) {
        widget.setParent(this);
        this.children.add(widget);
        this.layout();
        return this;
    }

    public WGridListPanel add(int index, WWidget widget) {
        widget.setParent(this);
        this.children.add(index, widget);
        this.layout();
        return this;
    }

    public WGridListPanel set(int index, WWidget widget) {
        widget.setParent(this);
        this.children.set(index, widget);
        this.layout();
        return this;
    }

    public WGridListPanel remove(int index) {
        this.children.remove(index);
        this.layout();
        return this;
    }

    /**
     * @return The width that the children of this panel can occupy.
     */
    public int getContentWidth() {
        return this.width - this.insets.left() - this.insets.right();
    }

    /**
     * @return The height that the children of this panel can occupy.
     */
    public int getContentHeight() {
        return this.height - this.insets.top() - this.insets.bottom();
    }

    /**
     * Positions all the {@link #children}, based on the order they are stored in.
     */
    @Override
    public void layout() {
        // Changes this panel's width to fit the largest child.
        for (WWidget child : this.children) {
            if (child instanceof WPanel) ((WPanel) child).layout();
            // If the child is wider than the panel, the child or the panel must resize.
            if (child.getWidth() > this.getContentWidth()) {
                if (child.canResize()) child.setSize(this.grid, this.grid);
                else this.width = child.getWidth() + this.insets.left() + this.insets.right();
            }
        }

        int maxWidthInCells = (int) Math.floor((double) this.getContentWidth() / (this.grid + this.horizontalGap));

        int currentX = 0;
        int currentY = 0;
        int nextY = 1; // The space downwards that is unoccupied.
        // The tallest cell of a row will fully block the rows below it, that it reaches into.

        for (WWidget child : this.children) {
            int cellsWide = (int) Math.ceil((double) child.getWidth() / (this.grid + this.horizontalGap));
            int cellsHigh = (int) Math.ceil((double) child.getHeight() / (this.grid + this.verticalGap));

            // If the child is wider than the space left in the row, we jump to the next row.
            if (currentX + cellsWide > maxWidthInCells) {
                this.setGridLocation(child, 0, nextY);
                currentX = cellsWide;
                currentY = nextY;
                nextY += cellsHigh;
            } else {
                this.setGridLocation(child, currentX, currentY);
                currentX += cellsWide;
                nextY = Math.max(nextY, cellsHigh + currentY);
            }

            // When the current element's bottom reaches out of the panel, we adjust the panel's height.
            int overflow = nextY * (this.grid + this.verticalGap) - this.getContentHeight();
            if (overflow > 0) this.height += overflow;
        }
    }

    /**
     * Sets the location of the widget, based on the coordinates in the grid.
     *
     * @param widget The widget, whose location is set.
     * @param gridX  The x coordinate in the grid.
     * @param gridY  The y coordinate in the grid.
     */
    private void setGridLocation(WWidget widget, int gridX, int gridY) {
        widget.setLocation(
                gridX * (this.grid + this.horizontalGap) + this.insets.left(),
                gridY * (this.grid + this.verticalGap) + this.insets.top()
        );
    }
}
