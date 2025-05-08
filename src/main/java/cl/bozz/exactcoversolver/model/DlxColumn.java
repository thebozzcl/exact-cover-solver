package cl.bozz.exactcoversolver.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class DlxColumn extends DlxNode {
    private final String columnId;
    private int size;

    public DlxColumn(
            final String rowId,
            final String columnId
    ) {
        super(null, rowId);
        this.columnId = columnId;
    }

    public DlxColumn getColumn() {
        return this;
    }

    public void cover() {
        removeFromRow();
        for (DlxNode i = getDown(); i != this; i = i.getDown()) {
            for (DlxNode j = i.getRight(); j != i; j = j.getRight()) {
                j.removeFromColumn();
            }
        }
    }

    public void uncover() {
        for (DlxNode i = getUp(); i != this; i = i.getUp()) {
            for (DlxNode j = i.getLeft(); j != i; j = j.getLeft()) {
                j.restoreInColumn();
            }
        }
        restoreInRow();
    }

    @Override
    public String toString() {
        return String.format(
                "< %s.%s >",
                getRowId(),
                columnId
        );
    }
}
