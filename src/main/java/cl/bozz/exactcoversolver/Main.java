package cl.bozz.exactcoversolver;

import cl.bozz.exactcoversolver.model.DlxColumn;
import cl.bozz.exactcoversolver.model.DlxNode;
import cl.bozz.exactcoversolver.translator.LinkedInQueensTranslator;
import cl.bozz.exactcoversolver.translator.SudokuTranslator;
import cl.bozz.exactcoversolver.utils.ExactCoverSolver;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(final String[] args) {
        final InputStream stream = Main.class.getClassLoader().getResourceAsStream("examples/liqueens/1.csv");

        final DlxColumn problem = LinkedInQueensTranslator.parseFromInputStream(stream);
        //System.out.println(ExactCoverPrinter.printProblem(problem));

        final Set<DlxNode> solution = ExactCoverSolver.solve(problem, new HashSet<>());
        System.out.println(solution);
    }
}
