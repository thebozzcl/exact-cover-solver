package cl.bozz.exactcoversolver.utils;

import cl.bozz.exactcoversolver.Main;
import cl.bozz.exactcoversolver.model.DlxColumn;
import cl.bozz.exactcoversolver.testutils.ExactCoverTestData;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExactCoverPrinterTest {
    @Test
    public void testPrintProblem_happyPath() {
        // Arrange
        final DlxColumn start = ExactCoverTestData.createSimpleCase();
        final InputStream stream = ExactCoverPrinterTest.class.getClassLoader().getResourceAsStream("examples/simple.csv");
        final String expected = new BufferedReader(new InputStreamReader(stream)).lines().collect(Collectors.joining(System.lineSeparator()));

        // Act
        final String actual = ExactCoverPrinter.printProblem(start);

        // Assert
        assertEquals(expected, actual);
    }
}
