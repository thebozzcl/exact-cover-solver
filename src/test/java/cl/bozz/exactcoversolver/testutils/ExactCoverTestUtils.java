package cl.bozz.exactcoversolver.testutils;

import cl.bozz.exactcoversolver.model.DlxColumn;
import cl.bozz.exactcoversolver.model.DlxNode;
import cl.bozz.exactcoversolver.utils.ExactCoverParser;
import lombok.experimental.UtilityClass;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UtilityClass
public class ExactCoverTestUtils {
    public void testForEquality(final DlxColumn expected, final DlxColumn actual) {
        final Set<DlxNode> expectedTestedNode = new HashSet<>();
        final Set<DlxNode> actualTestedNode = new HashSet<>();

        final Queue<DlxNode> expectedQueue = new ArrayDeque<>();
        expectedQueue.add(expected);
        final Queue<DlxNode> actualQueue = new ArrayDeque<>();
        actualQueue.add(actual);

        while (!expectedQueue.isEmpty() && !actualQueue.isEmpty()) {
            final DlxNode expectedNode = expectedQueue.poll();
            final DlxNode actualNode = actualQueue.poll();

            assertTrue(expectedNode != null && actualNode != null);

            expectedTestedNode.add(expectedNode);
            actualTestedNode.add(actualNode);

            assertEquals(expectedNode.getRowId(), actualNode.getRowId());
            assertEquals(expectedNode.getColumn(), actualNode.getColumn());
            assertEquals(expectedNode.getRight().getRowId(), actualNode.getRight().getRowId());
            assertEquals(expectedNode.getLeft().getRowId(), actualNode.getLeft().getRowId());
            assertEquals(expectedNode.getUp().getRowId(), actualNode.getUp().getRowId());
            assertEquals(expectedNode.getDown().getRowId(), actualNode.getDown().getRowId());

            if (expectedNode instanceof final DlxColumn expectedColumn) {
                assertInstanceOf(DlxColumn.class, actualNode);

                final DlxColumn actualColumn = (DlxColumn) actualNode;

                assertEquals(expectedColumn.getColumnId(), actualColumn.getColumnId());
                assertEquals(expectedColumn.getSize(), actualColumn.getSize());
            }

            if (!expectedTestedNode.contains(expectedNode.getUp())) {
                expectedQueue.add(expectedNode.getUp());
            }
            if (!actualTestedNode.contains(actualNode.getUp())) {
                actualQueue.add(actualNode.getUp());
            }

            if (!expectedTestedNode.contains(expectedNode.getDown())) {
                expectedQueue.add(expectedNode.getDown());
            }
            if (!actualTestedNode.contains(actualNode.getDown())) {
                actualQueue.add(actualNode.getDown());
            }

            if (!expectedTestedNode.contains(expectedNode.getLeft())) {
                expectedQueue.add(expectedNode.getLeft());
            }
            if (!actualTestedNode.contains(actualNode.getLeft())) {
                actualQueue.add(actualNode.getLeft());
            }

            if (!expectedTestedNode.contains(expectedNode.getRight())) {
                expectedQueue.add(expectedNode.getRight());
            }
            if (!actualTestedNode.contains(actualNode.getRight())) {
                actualQueue.add(actualNode.getRight());
            }
        }

        assertTrue(expectedQueue.isEmpty());
        assertTrue(actualQueue.isEmpty());
    }
}
