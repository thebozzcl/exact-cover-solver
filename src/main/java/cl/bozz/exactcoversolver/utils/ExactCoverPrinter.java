package cl.bozz.exactcoversolver.utils;

import cl.bozz.exactcoversolver.model.DlxColumn;
import cl.bozz.exactcoversolver.model.DlxNode;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class ExactCoverPrinter {
    public String printProblem(final DlxColumn start) {
        final StringBuilder sb = new StringBuilder();

        final Map<String, String> rowStrings = new HashMap<>();

        final List<String> colLabels = new ArrayList<>();
        DlxColumn col = (DlxColumn) start.getRight();
        while (col != start) {
            colLabels.add(col.getColumnId());
            col = (DlxColumn) col.getRight();
        }

        col = (DlxColumn) start.getRight();
        while (col != start) {
            DlxNode colRow = col.getDown();
            while (colRow != col) {
                if (!rowStrings.containsKey(colRow.getRowId())) {
                    rowStrings.put(colRow.getRowId(), printRow(colRow, colLabels));
                }
                colRow = colRow.getDown();
            }
            col = (DlxColumn) col.getRight();
        }

        sb.append(String.join(",", colLabels));

        rowStrings.forEach((_, rowString) -> {
            sb.append(System.lineSeparator());
            sb.append(rowString);
        });

        return sb.toString();
    }

    public String printRow(final DlxNode node, final List<String> colLabels) {
        final Set<String> presentColLabels = new HashSet<>();

        DlxNode row = node;
        do {
            presentColLabels.add(row.getColumn().getColumnId());
            row = row.getRight();
        } while (row != node);

        return String.format(
                "%s,%s",
                node.getRowId(),
                colLabels.stream().filter(presentColLabels::contains).collect(Collectors.joining(","))
        );
    }
}
