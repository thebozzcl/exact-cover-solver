package cl.bozz.exactcoversolver.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DlxNodeTest {
    @Test
    public void testLinkDown_happyPath() {
        // Arrange
        final DlxColumn column = new DlxColumn("header", "column");
        final DlxNode node1 = new DlxNode(column, "row1");
        final DlxNode node2 = new DlxNode(column, "row2");

        // Act, Assert 1 - single node
        column.linkDown(node1);

        assertEquals(node1, column.getDown());
        assertEquals(node1, column.getUp());
        assertEquals(column, node1.getDown());
        assertEquals(column, node1.getUp());

        // Act, Assert 2 - two nodes
        node1.linkDown(node2);

        assertEquals(node1, column.getDown());
        assertEquals(node2, column.getUp());
        assertEquals(node2, node1.getDown());
        assertEquals(column, node1.getUp());
        assertEquals(column, node2.getDown());
        assertEquals(node1, node2.getUp());
    }

    @Test
    public void testLinkRight_happyPath() {
        // Arrange
        final DlxColumn column1 = new DlxColumn("header", "column1");
        final DlxColumn column2 = new DlxColumn("header", "column2");
        final DlxColumn column3 = new DlxColumn("header", "column3");

        // Act, Assert 1 - two columns
        column1.linkRight(column2);

        assertEquals(column2, column1.getRight());
        assertEquals(column2, column1.getLeft());
        assertEquals(column1, column2.getRight());
        assertEquals(column1, column2.getLeft());

        // Act, Assert 2 - three columns
        column2.linkRight(column3);

        assertEquals(column2, column1.getRight());
        assertEquals(column3, column1.getLeft());
        assertEquals(column3, column2.getRight());
        assertEquals(column1, column2.getLeft());
        assertEquals(column1, column3.getRight());
        assertEquals(column2, column3.getLeft());
    }

    @Test
    public void testRemoveFromRow_happyPath() {
        // Arrange
        final DlxColumn column1 = new DlxColumn("header", "column1");
        final DlxColumn column2 = new DlxColumn("header", "column2");
        final DlxColumn column3 = new DlxColumn("header", "column3");

        column1.linkRight(column2);
        column2.linkRight(column3);

        // Act
        column2.removeFromRow();

        // Assert
        assertEquals(column3, column1.getRight());
        assertEquals(column1, column3.getLeft());
    }

    @Test
    public void testRestoreInRow_happyPath() {
        // Arrange
        final DlxColumn column1 = new DlxColumn("header", "column1");
        final DlxColumn column2 = new DlxColumn("header", "column2");
        final DlxColumn column3 = new DlxColumn("header", "column3");

        column1.linkRight(column2);
        column2.linkRight(column3);

        column2.removeFromRow();

        // Act
        column2.restoreInRow();

        // Assert
        assertEquals(column2, column1.getRight());
        assertEquals(column2, column3.getLeft());
    }

    @Test
    public void testRemoveFromColumn_happyPath() {
        // Arrange
        final DlxColumn column = new DlxColumn("header", "column");
        final DlxNode node1 = new DlxNode(column, "row1");
        final DlxNode node2 = new DlxNode(column, "row2");
        final DlxNode node3 = new DlxNode(column, "row3");

        column.linkDown(node1);
        node1.linkDown(node2);
        node2.linkDown(node3);

        // Act
        node2.removeFromColumn();

        // Assert
        assertEquals(node3, node1.getDown());
        assertEquals(node1, node3.getUp());
    }

    @Test
    public void testRestoreColumn_happyPath() {
        // Arrange
        final DlxColumn column = new DlxColumn("header", "column");
        final DlxNode node1 = new DlxNode(column, "row1");
        final DlxNode node2 = new DlxNode(column, "row2");
        final DlxNode node3 = new DlxNode(column, "row3");

        column.linkDown(node1);
        node1.linkDown(node2);
        node2.linkDown(node3);

        node2.removeFromColumn();

        // Act
        node2.restoreInColumn();

        // Assert
        assertEquals(node2, node1.getDown());
        assertEquals(node2, node3.getUp());
    }
}
