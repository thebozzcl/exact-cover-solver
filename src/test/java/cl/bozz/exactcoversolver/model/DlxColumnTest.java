package cl.bozz.exactcoversolver.model;

import cl.bozz.exactcoversolver.testutils.ExactCoverTestData;
import cl.bozz.exactcoversolver.utils.ExactCoverPrinter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DlxColumnTest {
    @Test
    public void testCoverAndUncover_happyPath() {
        // Arrange
        final DlxColumn start = ExactCoverTestData.createSimpleCase();
        final DlxColumn column = (DlxColumn) start.getRight().getRight();

        final String initialState = ExactCoverPrinter.printProblem(start);
        final String finalState = String.join(System.lineSeparator(), "1,3", "A,1,3", "B,1", "C,3");

        // Act & Assert 1 - cover
        column.cover();
        assertEquals(finalState, ExactCoverPrinter.printProblem(start));

        // Act && Assert 2 - uncover
        column.uncover();
        assertEquals(initialState, ExactCoverPrinter.printProblem(start));
    }
}
