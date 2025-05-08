package cl.bozz.exactcoversolver.testutils;

import cl.bozz.exactcoversolver.model.DlxColumn;
import cl.bozz.exactcoversolver.utils.ExactCoverParser;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExactCoverTestData {
    public DlxColumn createSimpleCase() {
        final String[] columnLabels = {"1", "2", "3"};
        final String[] rowLabels = {"A", "B", "C"};
        final boolean[][] matrix = {
                { true, false, true },
                { true, true, false },
                { false, false, true },
        };

        return ExactCoverParser.convert(columnLabels, rowLabels, matrix);
    }
}
