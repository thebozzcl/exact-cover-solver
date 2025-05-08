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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@UtilityClass
public class LinkedInQueensTranslator {

    // For n * n matrix:
    // Choices: n * n * 2 (empty or queen -> it has to be an EXPLICIT choice)
    // Row-column: all cells must be empty or have a queen = n * n constraints
    // Row-queen: 1 queen per row = n constraints
    // Column-queen: 1 queen per column = n constraints
    // Color-queen: 1 queen per color = n constraints
    // Neighbors: no neighbors = oof, hard to answer how many

    public DlxColumn parseFromInputStream(final InputStream inputStream) {
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            final String[][] queensMatrix = reader.lines()
                    .map(line -> line.split(","))
                    .toArray(String[][]::new);

            final int n = queensMatrix.length;

            final Set<String> groups = new HashSet<>();
            final Set<LinkedInQueensBit> choices = new HashSet<>();
            final Set<LinkedInQueensBit> constraints = new HashSet<>();
            final Set<String> processedNeighbors = new HashSet<>();

            for (int i = 1; i <= n; i++) {
                for (int j = 1; j <= n; j++) {
                    // Choices
                    final LinkedInQueensBit currentBit = new LinkedInQueensBit(
                            String.format(
                                    "R%dC%dG%s",
                                    i,
                                    j,
                                    queensMatrix[i - 1][j - 1]
                            ),
                            i,
                            j,
                            queensMatrix[i - 1][j - 1],
                            null,
                            false
                    );
                    choices.add(currentBit);

                    groups.add(queensMatrix[i - 1][j - 1]);

                    //Row-queen
                    constraints.add(new LinkedInQueensBit(
                            String.format(
                                    "R%d",
                                    i
                            ),
                            i,
                            -1,
                            null,
                            null,
                            false
                    ));

                    // Column-queen
                    constraints.add(new LinkedInQueensBit(
                            String.format(
                                    "C%d",
                                    j
                            ),
                            -1,
                            j,
                            null,
                            null,
                            false
                    ));

                    final LinkedInQueensBit currentConstraint = new LinkedInQueensBit(
                            String.format(
                                    "R%dC%d",
                                    i,
                                    j
                            ),
                            i,
                            j,
                            null,
                            null,
                            false
                    );

                    for (int k = -1; k <= 1; k++) {
                        for (int l = -1; l <= 1; l++) {
                            if (k == 0 && l == 0) {
                                continue;
                            }

                            if (i + k > n || j + l > n || i + k < 1 || j + l < 1) {
                                continue;
                            }

                            final LinkedInQueensBit neighborConstraint = new LinkedInQueensBit(
                                    String.format(
                                            "R%dC%d",
                                            i + k,
                                            j + l
                                    ),
                                    i + k,
                                    j + l,
                                    null,
                                    null,
                                    false
                            );
                            if (processedNeighbors.contains(neighborConstraint.toString())) {
                                // If we've added the neighbor bit to the choices,
                                // assume we've created neighbor constraints for it.
                                continue;
                            }
                            final LinkedInQueensBit negatedChoiceBit = new LinkedInQueensBit(
                                    String.format(
                                            "-(%s_N_%s)",
                                            currentConstraint,
                                            neighborConstraint
                                    ),
                                    -1,
                                    -1,
                                    null,
                                    null,
                                    true
                            );
                            choices.add(negatedChoiceBit);
                            constraints.add(new LinkedInQueensBit(
                                    String.format(
                                            "O_%s_N_%s",
                                            currentConstraint,
                                            neighborConstraint
                                    ),
                                    -1,
                                    -1,
                                    null,
                                    Set.of(currentConstraint, neighborConstraint, negatedChoiceBit),
                                    false
                            ));
                        }
                    }
                    processedNeighbors.add(currentConstraint.toString());
                }
            }
            groups.forEach(group -> {
                constraints.add(new LinkedInQueensBit(
                        String.format(
                                "G%s",
                                group
                        ),
                        -1,
                        -1,
                        group,
                        null,
                        false
                ));
            });

            final List<LinkedInQueensBit> choicesList = new ArrayList<>(choices);
            final List<LinkedInQueensBit> constraintsList = new ArrayList<>(constraints);

            final boolean[][] matrix = new boolean[choices.size()][constraints.size()];
            for (int i = 0; i < constraintsList.size(); i++) {
                final LinkedInQueensBit constraint = constraintsList.get(i);
                for (int j = 0; j < choicesList.size(); j++) {
                    final LinkedInQueensBit choice = choicesList.get(j);

                    if (constraint.matches(choice)) {
                        matrix[j][i] = true;
                    }
                }
            }

            final String[] choiceArray = choicesList.stream()
                    .map(Objects::toString)
                    .toArray(String[]::new);
            final String[] constraintArray = constraintsList.stream()
                    .map(Objects::toString)
                    .toArray(String[]::new);

            return ExactCoverParser.convert(constraintArray, choiceArray, matrix);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Value
    private static class LinkedInQueensBit {
        String label;
        int row;
        int col;
        String group;
        Set<LinkedInQueensBit> members;
        boolean isLabelMatcher;

        @Override
        public String toString() {
            return label;
        }

        public boolean matches(final LinkedInQueensBit other) {
            if (isLabelMatcher) {
                return this.label.equals(other.label);
            }

            if (members != null) {
                return members.stream().anyMatch(b -> b.matches(other));
            }

            if (row > 0 && other.row != row) {
                return false;
            }
            if (col > 0 && other.col != col) {
                return false;
            }
            if (group != null && !group.equals(other.group)) {
                return false;
            }

            return true;
        }
    }
}
