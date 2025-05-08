package cl.bozz.exactcoversolver.utils;

import cl.bozz.exactcoversolver.Main;
import cl.bozz.exactcoversolver.model.DlxColumn;
import cl.bozz.exactcoversolver.model.DlxNode;
import cl.bozz.exactcoversolver.testutils.ExactCoverTestData;
import cl.bozz.exactcoversolver.testutils.ExactCoverTestUtils;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExactCoverParserTest {
    @Test
    public void testConvert_happyPath() {
        // Arrange, Act
        final DlxColumn start = ExactCoverTestData.createSimpleCase();

        // Assert
        final DlxColumn col1 = (DlxColumn) start.getRight();
        assertEquals("1", col1.getColumnId());
        assertEquals("header", col1.getRowId());
        final DlxNode col1Node1 = col1.getDown();
        assertEquals("A", col1Node1.getRowId());
        assertEquals(col1, col1Node1.getColumn());
        final DlxNode col1Node2 = col1Node1.getDown();
        assertEquals("B", col1Node2.getRowId());
        assertEquals(col1, col1Node2.getDown());
        assertEquals(col1, col1Node2.getColumn());

        final DlxColumn col2 = (DlxColumn) col1.getRight();
        assertEquals("2", col2.getColumnId());
        assertEquals("header", col2.getRowId());
        final DlxNode col2Node1 = col2.getDown();
        assertEquals("B", col2Node1.getRowId());
        assertEquals(col2, col2Node1.getColumn());
        assertEquals(col2, col2Node1.getDown());

        final DlxColumn col3 = (DlxColumn) col2.getRight();
        assertEquals("3", col3.getColumnId());
        assertEquals("header", col3.getRowId());
        final DlxNode col3Node1 = col3.getDown();
        assertEquals("A", col3Node1.getRowId());
        assertEquals(col3, col3Node1.getColumn());
        final DlxNode col3Node2 = col3Node1.getDown();
        assertEquals("C", col3Node2.getRowId());
        assertEquals(col3, col3Node2.getDown());
        assertEquals(col3, col3Node2.getColumn());

        assertEquals(col3Node1, col1Node1.getRight());
        assertEquals(col3Node1, col1Node1.getLeft());
        assertEquals(col1Node1, col3Node1.getLeft());
        assertEquals(col1Node1, col3Node1.getRight());

        assertEquals(col2Node1, col1Node2.getRight());
        assertEquals(col2Node1, col1Node2.getLeft());
        assertEquals(col1Node2, col2Node1.getLeft());
        assertEquals(col1Node2, col2Node1.getRight());

        assertEquals(col3Node2, col3Node2.getRight());
        assertEquals(col3Node2, col3Node2.getLeft());
    }

    @Test
    public void testParseFromInputStream_happyPath() {
        // Arrange
        final InputStream stream = Main.class.getClassLoader().getResourceAsStream("examples/simple.csv");
        final DlxColumn expected = ExactCoverTestData.createSimpleCase();

        // Act
        final DlxColumn actual = ExactCoverParser.parseFromInputStream(stream);
        
        // Assert
        ExactCoverTestUtils.testForEquality(expected, actual);
    }
}
