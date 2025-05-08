package cl.bozz.exactcoversolver.translator;

import cl.bozz.exactcoversolver.model.DlxColumn;
import cl.bozz.exactcoversolver.utils.ExactCoverParser;
import lombok.Value;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class SudokuTranslator {

    // Row-column: only one value per cell
    // 9 rows x 9 columns = 81 constraints
    // Row-number: each number once per row
    // 9 rows x 9 values = 81 constraints
    // Column-number: each number once per column
    // 9 columns x 9 values = 81 constraints
    // Box-number: each number once per 3x3 box
    // 9 boxes x 9 values = 81 constraints
    // Total: 324 constraints
    // 9 columns x 9 rows x 9 values = 729 choices

    public DlxColumn parseFromInputStream(final InputStream inputStream) {
        try(final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final int[][] sudokuMatrix = reader.lines()
                    .map(line -> line.split(","))
                    .map(stringRow -> Arrays.stream(stringRow)
                            .mapToInt(Integer::parseInt)
                            .toArray()
                    ).toArray(int[][]::new);

            final Set<SudokuBit> choices = new HashSet<>();
            final Set<SudokuBit> constraints = new HashSet<>();

            for (int i = 1; i <= 9; i++) {
                for (int j = 1; j <= 9; j++) {
                    constraints.add(new SudokuBit(
                            i,
                            0,
                            0,
                            j
                    ));
                    constraints.add(new SudokuBit(
                            0,
                            i,
                            0,
                            j
                    ));
                    constraints.add(new SudokuBit(
                            i,
                            j,
                            0,
                            0
                    ));
                    constraints.add(new SudokuBit(
                            0,
                            0,
                            i,
                            j
                    ));
                }
            }
            for (int row = 1; row <= 9; row++) {
                for (int col = 1; col <= 9; col++) {
                    final int box = getBoxNumber(row, col);
                    for (int value = 1; value <= 9; value++) {
                        choices.add(new SudokuBit(
                                row,
                                col,
                                box,
                                value
                        ));
                    }
                    final int actualValue = sudokuMatrix[row - 1][col - 1];
                    if (actualValue != 0) {
                        final SudokuBit actual = new SudokuBit(
                                row,
                                col,
                                box,
                                actualValue
                        );
                        final Set<SudokuBit> constraintMatches = constraints.stream()
                                .filter(constraint -> constraint.matches(actual))
                                .collect(Collectors.toSet());
                        final Set<SudokuBit> choiceMatches = choices.stream()
                                .filter(choice -> constraintMatches.stream()
                                        .anyMatch(constraint -> constraint.matches(choice))
                                ).collect(Collectors.toSet());

                        choices.removeAll(choiceMatches);
                        constraints.removeAll(constraintMatches);
                    }
                }
            }

            final List<SudokuBit> choiceList = new ArrayList<>(choices);
            final List<SudokuBit> constraintList = new ArrayList<>(constraints);

            final boolean[][] matrix = new boolean[choices.size()][constraints.size()];
            for (int i = 0; i < constraintList.size(); i++) {
                final SudokuBit constraint = constraintList.get(i);
                for (int j = 0; j < choiceList.size(); j++) {
                    final SudokuBit choice = choiceList.get(j);

                    if (constraint.matches(choice)) {
                        matrix[j][i] = true;
                    }
                }
            }

            final String[] choiceArray = choiceList.stream()
                    .map(Objects::toString)
                    .toArray(String[]::new);
            final String[] constraintArray = constraintList.stream()
                    .map(Objects::toString)
                    .toArray(String[]::new);

            return ExactCoverParser.convert(constraintArray, choiceArray, matrix);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getBoxNumber(final int row, final int col) {
        return (int) (1 + Math.floor((double) (col - 1) / 3) + Math.floor((double) (row - 1) / 3) * 3);
    }
    
    @Value
    private static class SudokuBit {
        int row;
        int col;
        int box;
        int value;
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            
            if (row > 0) {
                sb.append("R").append(row);
            }
            if (col > 0) {
                sb.append("C").append(col);
            }
            if (box > 0) {
                sb.append("B").append(box);
            }
            if (value > 0) {
                sb.append("V").append(value);
            }
            
            return sb.toString();
        }

        public boolean matches(final SudokuBit other) {
            if (row > 0 && other.row != row) {
                return false;
            }
            if (col > 0 && other.col != col) {
                return false;
            }
            if (box > 0 && other.box != box) {
                return false;
            }
            if (value > 0 && other.value != value) {
                return false;
            }

            return true;
        }
    }
}
