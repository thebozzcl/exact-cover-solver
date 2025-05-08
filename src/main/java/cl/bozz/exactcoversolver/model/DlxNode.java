package cl.bozz.exactcoversolver.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DlxNode {
    private DlxNode left = this;
    private DlxNode right = this;
    private DlxNode up = this;
    private DlxNode down = this;
    private final DlxColumn column;
    private final String rowId;

    // Link methods
    public void linkDown(final DlxNode node) {
        node.down = this.down;  // Point to the node below current
        this.down.up = node;    // The node below points up to the new node
        node.up = this;         // New node points up to current
        this.down = node;       // Current points down to new node
    }

    public void linkRight(final DlxNode node) {
        node.right = this.right;  // Point to the node right of current
        this.right.left = node;   // The node to the right points left to the new node
        node.left = this;         // New node points left to current
        this.right = node;        // Current points right to new node
    }

    // Remove/restore methods
    public void removeFromRow() {
        left.right = right;
        right.left = left;
    }

    public void restoreInRow() {
        left.right = this;
        right.left = this;
    }

    public void removeFromColumn() {
        up.down = down;
        down.up = up;
        column.setSize(column.getSize() - 1);
    }

    public void restoreInColumn() {
        up.down = this;
        down.up = this;
        column.setSize(column.getSize() + 1);
    }

    @Override
    public String toString() {
        return String.format(
                "< %s.%s >",
                rowId,
                column.getColumnId()
        );
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
