package cl.bozz.exactcoversolver.utils;

import cl.bozz.exactcoversolver.model.DlxColumn;
import cl.bozz.exactcoversolver.model.DlxNode;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UtilityClass
public class ExactCoverSolver {
    public Set<DlxNode> solve(final DlxColumn start, final Set<DlxNode> partialSolution) {
        // Base case: if we've covered all columns, we have a solution
        if (start.getRight() == start) {
            return new HashSet<>(partialSolution);
        }

        // Select column with the fewest nodes (optimization)
        final DlxColumn smallest = selectSmallestColumn(start);

        // If any column has no nodes, this branch has no solution
        if (smallest.getSize() == 0) {
            return null;
        }

        // Try each row in the selected column
        DlxNode row = smallest.getDown();
        while (row != smallest) {
            // Cover columns for this row
            final List<DlxColumn> coveredColumns = coverColumns(row);

            // Add this row to our partial solution
            final Set<DlxNode> updatedSolution = new HashSet<>(partialSolution);
            updatedSolution.add(row);

            // Recursively solve with this row included
            final Set<DlxNode> solution = solve(start, updatedSolution);

            // If we found a solution, return it
            if (solution != null) {
                return solution;
            }

            // Otherwise, backtrack
            uncoverColumns(coveredColumns);

            // Try the next row
            row = row.getDown();
        }

        return null;
    }

    private DlxColumn selectSmallestColumn(final DlxColumn start) {
        DlxColumn col = (DlxColumn) start.getRight();
        DlxColumn smallest = col;

        while (col != start) {
            if (smallest.getSize() > col.getSize()) {
                smallest = col;
            }
            col = (DlxColumn) col.getRight();
        }
        return smallest;
    }

    private List<DlxColumn> coverColumns(final DlxNode row) {
        final List<DlxColumn> columnsToRemove = new ArrayList<>();
        DlxNode current = row;
        do {
            columnsToRemove.add(current.getColumn());
            current = current.getRight();
        } while (current != row);

        columnsToRemove.forEach(DlxColumn::cover);
        return columnsToRemove;
    }

    private void uncoverColumns(final List<DlxColumn> columns) {
        // Note: We need to uncover in reverse order
        for (int i = columns.size() - 1; i >= 0; i--) {
            columns.get(i).uncover();
        }
    }
}
