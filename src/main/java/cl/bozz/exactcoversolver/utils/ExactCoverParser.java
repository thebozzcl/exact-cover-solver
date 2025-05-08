package cl.bozz.exactcoversolver.utils;

import cl.bozz.exactcoversolver.model.DlxColumn;
import cl.bozz.exactcoversolver.model.DlxNode;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@UtilityClass
public class ExactCoverParser {
    private static final String HEADER_LABEL = "header";

    public DlxColumn convert(final String[] columnLabels, final String[] rowLabels, final boolean[][] matrix) {

        final DlxColumn start = new DlxColumn(HEADER_LABEL, "start");

        if (rowLabels.length == 0 && columnLabels.length == 0 && matrix.length == 0) {
            return start;
        }

        if (rowLabels.length != matrix.length || columnLabels.length != matrix[0].length) {
            throw new IllegalArgumentException(String.format(
                    "Label array sizes do not match matrix: labels[%d x %d] vs matrix[%d x %d]",
                    rowLabels.length, columnLabels.length,
                    matrix.length, matrix[0].length
            ));
        }

        DlxColumn currentColumn = start;
        final DlxColumn[] columnByIndex = new DlxColumn[columnLabels.length];
        final DlxNode[] lastOfColumn = new DlxNode[columnLabels.length];
        for (int colIndex = 0; colIndex < columnLabels.length; colIndex++) {
            final String colLabel = columnLabels[colIndex].trim();
            final DlxColumn newColumn = new DlxColumn(HEADER_LABEL, colLabel);
            lastOfColumn[colIndex] = newColumn;
            columnByIndex[colIndex] = newColumn;
            currentColumn.linkRight(newColumn);
            currentColumn = newColumn;
        }

        for (int rowIndex = 0; rowIndex < rowLabels.length; rowIndex++) {
            final String rowLabel = rowLabels[rowIndex].trim();
            DlxNode lastOfRow = null;
            for (int colIndex = 0; colIndex < matrix[rowIndex].length; colIndex++) {
                if (!matrix[rowIndex][colIndex]) {
                    continue;
                }
                final DlxColumn column = columnByIndex[colIndex];
                final DlxNode upperNode = lastOfColumn[colIndex];
                final DlxNode newNode = new DlxNode(
                        column,
                        rowLabel
                );
                upperNode.linkDown(newNode);
                column.setSize(column.getSize() + 1);
                lastOfColumn[colIndex] = newNode;

                if (lastOfRow != null) {
                    lastOfRow.linkRight(newNode);
                }
                lastOfRow = newNode;
            }
        }

        return start;
    }

    public DlxColumn parseFromInputStream(final InputStream inputStream) {
        try(final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            final String[] columnLabels = reader.readLine().split(",");

            final List<String> rowLines = reader.lines().toList();
            final String[] rowLabels = new String[rowLines.size()];
            final boolean[][] matrix = new boolean[rowLines.size()][columnLabels.length];

            for (int rowIndex = 0; rowIndex < rowLabels.length; rowIndex++) {
                final String[] rowContents = rowLines.get(rowIndex).split(",");
                rowLabels[rowIndex] = rowContents[0];

                for(int rowContentIndex = 1; rowContentIndex < rowContents.length; rowContentIndex++) {
                    for (int colIndex = 0; colIndex < columnLabels.length; colIndex++) {
                        if (columnLabels[colIndex].equals(rowContents[rowContentIndex])) {
                            matrix[rowIndex][colIndex] = true;
                        }
                    }
                }
            }

            return convert(columnLabels, rowLabels, matrix);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
