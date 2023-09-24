package com.github.kacperpotapczyk.pvoptimizer.solver;

import com.github.kacperpotapczyk.pvoptimizer.solver.enums.ObjectiveDirection;
import com.github.kacperpotapczyk.pvoptimizer.solver.enums.SolutionStatus;
import com.github.kacperpotapczyk.pvoptimizer.solver.exceptions.SolverException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LpSolveSolverTest {

    @Test
    void optimizationGoal() {
        try {
            Solver solver = new LpSolveSolver();
            assertEquals(ObjectiveDirection.MIN, solver.getObjectiveDirection(), "Default goal should be MIN");

            solver.setObjectiveDirection(ObjectiveDirection.MAX);
            assertEquals(ObjectiveDirection.MAX, solver.getObjectiveDirection(), "Should be MAX");

            solver.setObjectiveDirection(ObjectiveDirection.MIN);
            assertEquals(ObjectiveDirection.MIN, solver.getObjectiveDirection(), "Should be MIN");

            solver.free();
        }
        catch (SolverException solverException) {
            System.out.println(solverException.getMessage());
        }
    }

    @Test
    void lowerBoundTest() {
        try {
            Solver solver = new LpSolveSolver();

            solver.addVariables(1);

            Map<Integer, Double> lowerBound = new HashMap<>();
            lowerBound.put(1, 4.0);
            solver.addLowerBounds(lowerBound);

            Map<Integer, Double> objectiveCoefficients = new HashMap<>();
            objectiveCoefficients.put(1, 1.0);
            solver.setObjectiveFunction(objectiveCoefficients);

            SolutionStatus status = solver.solve();

            SolutionStatus expectedStatus = SolutionStatus.OPTIMAL;
            Map<Integer, Double> expectedSolution = new HashMap<>();
            expectedSolution.put(1, 4.0);
            double expectedObjectiveValue = 4.0;

            assertEquals(expectedStatus, status, "Solution status should be OPTIMAL");
            assertEquals(expectedObjectiveValue, solver.getObjectiveValue(), "Objective value should be 4");
            assertEquals(expectedSolution, solver.getSolution(), "Invalid solution");

            solver.free();
        }
        catch (SolverException solverException) {
            System.out.println(solverException.getMessage());
        }
    }

    @Test
    void upperBoundTest() {
        try {
            Solver solver = new LpSolveSolver();

            solver.addVariables(1);

            HashMap<Integer, Double> upperBound = new HashMap<>();
            upperBound.put(1, 4.0);
            solver.addUpperBounds(upperBound);

            Map<Integer, Double> objectiveCoefficients = new HashMap<>();
            objectiveCoefficients.put(1, -1.0);
            solver.setObjectiveFunction(objectiveCoefficients);

            SolutionStatus status = solver.solve();

            SolutionStatus expectedStatus = SolutionStatus.OPTIMAL;
            Map<Integer, Double> expectedSolution = new HashMap<>();
            expectedSolution.put(1, 4.0);
            double expectedObjectiveValue = -4.0;

            assertEquals(expectedStatus, status, "Solution status should be OPTIMAL");
            assertEquals(expectedObjectiveValue, solver.getObjectiveValue(), "Objective value should be -4");
            assertEquals(expectedSolution, solver.getSolution(), "Invalid solution");

            solver.free();
        }
        catch (SolverException solverException) {
            System.out.println(solverException.getMessage());
        }
    }

    @Test
    void fixedValueTest() {

        try {
            Solver solver = new LpSolveSolver();

            solver.addVariables(2);

            Map<Integer, Double> fixedValues = new HashMap<>(2);
            fixedValues.put(1, 4.0);
            fixedValues.put(2, 2.0);
            solver.fixVariables(fixedValues);

            Map<Integer, Double> objectiveCoefficients = new HashMap<>();
            objectiveCoefficients.put(1, -1.0);
            objectiveCoefficients.put(2, -1.0);
            solver.setObjectiveFunction(objectiveCoefficients);

            SolutionStatus status = solver.solve();

            SolutionStatus expectedStatus = SolutionStatus.OPTIMAL;
            Map<Integer, Double> expectedSolution = new HashMap<>();
            expectedSolution.put(1, 4.0);
            expectedSolution.put(2, 2.0);
            double expectedObjectiveValue = -6.0;

            assertEquals(expectedStatus, status, "Solution status should be OPTIMAL");
            assertEquals(expectedObjectiveValue, solver.getObjectiveValue(), "Objective value should be -6");
            assertEquals(expectedSolution, solver.getSolution(), "Invalid solution");

            solver.free();
        }
        catch (SolverException solverException) {
            System.out.println(solverException.getMessage());
        }
    }

    @Test
    void sumLeqTest() {
        try {
            Solver solver = new LpSolveSolver();

            solver.addVariables(2);

            Set<Integer> sumLeqConstraint = new HashSet<>();
            sumLeqConstraint.add(1);
            sumLeqConstraint.add(2);
            solver.addLeqSumConstraint(sumLeqConstraint, 4.0);

            HashMap<Integer, Double> upperBound = new HashMap<>();
            upperBound.put(2, 3.0);
            solver.addUpperBounds(upperBound);

            Map<Integer, Double> objectiveCoefficients = new HashMap<>();
            objectiveCoefficients.put(1, -1.0);
            objectiveCoefficients.put(2, -2.0);
            solver.setObjectiveFunction(objectiveCoefficients);

            SolutionStatus status = solver.solve();

            SolutionStatus expectedStatus = SolutionStatus.OPTIMAL;
            Map<Integer, Double> expectedSolution = new HashMap<>();
            expectedSolution.put(1, 1.0);
            expectedSolution.put(2, 3.0);
            double expectedObjectiveValue = -7.0;

            assertEquals(expectedStatus, status, "Solution status should be OPTIMAL");
            assertEquals(expectedObjectiveValue, solver.getObjectiveValue(), "Objective value should be -7");

            Map<Integer, Double> solution = solver.getSolution();
            
            assertEquals(expectedSolution.size(), solution.size());
            assertTrue(mapEqualsEps(expectedSolution, solution, 1e-6));
            
            solver.free();
        }
        catch (SolverException solverException) {
            System.out.println(solverException.getMessage());
        }
    }

    @Test
    void sumGeqTest() {
        try {
            Solver solver = new LpSolveSolver();

            solver.addVariables(2);

            List<Integer> sumGeqConstraint = new ArrayList<>();
            sumGeqConstraint.add(1);
            sumGeqConstraint.add(2);
            solver.addGeqSumConstraint(sumGeqConstraint, 4.0);

            HashMap<Integer, Double> upperBound = new HashMap<>();
            upperBound.put(1, 3.0);
            solver.addUpperBounds(upperBound);

            Map<Integer, Double> objectiveCoefficients = new HashMap<>();
            objectiveCoefficients.put(1, 1.0);
            objectiveCoefficients.put(2, 2.0);
            solver.setObjectiveFunction(objectiveCoefficients);

            SolutionStatus status = solver.solve();

            SolutionStatus expectedStatus = SolutionStatus.OPTIMAL;
            Map<Integer, Double> expectedSolution = new HashMap<>();
            expectedSolution.put(1, 3.0);
            expectedSolution.put(2, 1.0);
            double expectedObjectiveValue = 5.0;

            assertEquals(expectedStatus, status, "Solution status should be OPTIMAL");
            assertEquals(expectedObjectiveValue, solver.getObjectiveValue(), "Objective value should be 5");

            Map<Integer, Double> solution = solver.getSolution();

            assertEquals(expectedSolution.size(), solution.size());
            assertTrue(mapEqualsEps(expectedSolution, solution, 1.1e-6));


            solver.free();
        }
        catch (SolverException solverException) {
            System.out.println(solverException.getMessage());
        }
    }

    @Test
    void sumEqTest() {

        try {
            Solver solver = new LpSolveSolver();

            solver.addVariables(2);

            Set<Integer> sumEqConstraint = new HashSet<>();
            sumEqConstraint.add(1);
            sumEqConstraint.add(2);
            solver.addEqSumConstraint(sumEqConstraint, 4.0);

            Map<Integer, Double> objectiveCoefficients = new HashMap<>();
            objectiveCoefficients.put(1, -1.0);
            objectiveCoefficients.put(2, 2.0);
            solver.setObjectiveFunction(objectiveCoefficients);

            SolutionStatus status = solver.solve();

            SolutionStatus expectedStatus = SolutionStatus.OPTIMAL;
            Map<Integer, Double> expectedSolution = new HashMap<>();
            expectedSolution.put(1, 4.0);
            expectedSolution.put(2, 0.0);
            double expectedObjectiveValue = -4.0;

            assertEquals(expectedStatus, status, "Solution status should be OPTIMAL");
            assertEquals(expectedObjectiveValue, solver.getObjectiveValue(), "Objective value should be -4");

            Map<Integer, Double> solution = solver.getSolution();

            assertEquals(expectedSolution.size(), solution.size());
            assertTrue(mapEqualsEps(expectedSolution, solution, 1e-6));
            solver.free();
        }
        catch (SolverException solverException) {
            System.out.println(solverException.getMessage());
        }
    }

    @Test
    void invalidConstraint() {

        try {
            Solver solver = new LpSolveSolver();

            solver.addVariables(1);

            Set<Integer> sumEqConstraint = new HashSet<>();
            sumEqConstraint.add(1);
            sumEqConstraint.add(2);

            assertThrows(SolverException.class, () -> solver.addEqSumConstraint(sumEqConstraint, 4.0));

            solver.free();
        }
        catch (SolverException solverException) {
            System.out.println(solverException.getMessage());
        }
    }

    @Test
    void infeasibleTest() {

        try {
            Solver solver = new LpSolveSolver();

            solver.addVariables(2);

            Set<Integer> sumEqConstraint = new HashSet<>();
            sumEqConstraint.add(1);
            sumEqConstraint.add(2);
            solver.addEqSumConstraint(sumEqConstraint, 4.0);

            List<Integer> sumGeqConstraint = new ArrayList<>();
            sumGeqConstraint.add(1);
            sumGeqConstraint.add(2);
            solver.addGeqSumConstraint(sumGeqConstraint, 8.0);

            Map<Integer, Double> objectiveCoefficients = new HashMap<>();
            objectiveCoefficients.put(1, -1.0);
            objectiveCoefficients.put(2, 2.0);
            solver.setObjectiveFunction(objectiveCoefficients);

            SolutionStatus status = solver.solve();

            SolutionStatus expectedStatus = SolutionStatus.INFEASIBLE;

            assertEquals(expectedStatus, status, "Solution status should be INFEASIBLE");

            solver.free();
        }
        catch (SolverException solverException) {
            System.out.println(solverException.getMessage());
        }
    }

    @Test
    void addVariablesTwoTimes() {

        try {
            Solver solver = new LpSolveSolver();

            solver.addVariables(2);
            Map<Integer, Double> lowerBound1 = new HashMap<>();
            lowerBound1.put(1, 4.0);
            lowerBound1.put(2, 3.0);
            solver.addLowerBounds(lowerBound1);

            solver.addVariables(2);
            Map<Integer, Double> lowerBound2 = new HashMap<>();
            lowerBound2.put(3, 2.0);
            lowerBound2.put(4, 1.0);
            solver.addLowerBounds(lowerBound2);

            Map<Integer, Double> objectiveCoefficients = new HashMap<>();
            objectiveCoefficients.put(1, 1.0);
            objectiveCoefficients.put(2, 1.0);
            objectiveCoefficients.put(3, 1.0);
            objectiveCoefficients.put(4, 1.0);
            solver.setObjectiveFunction(objectiveCoefficients);

            SolutionStatus expectedStatus = SolutionStatus.OPTIMAL;
            Map<Integer, Double> expectedSolution = new TreeMap<>();
            expectedSolution.put(1, 4.0);
            expectedSolution.put(2, 3.0);
            expectedSolution.put(3, 2.0);
            expectedSolution.put(4, 1.0);
            double expectedObjectiveValue = 10.0;

            SolutionStatus status = solver.solve();

            assertEquals(expectedStatus, status, "Solution status should be OPTIMAL");
            assertEquals(expectedObjectiveValue, solver.getObjectiveValue(), "Objective value should be 10");

            Map<Integer, Double> solution = solver.getSolution();

            assertEquals(expectedSolution.size(), solution.size());
            assertTrue(mapEqualsEps(expectedSolution, solution, 1e-6));

            solver.free();
        }
        catch (SolverException solverException) {
            System.out.println(solverException.getMessage());
        }
    }

    @Test
    void addImplication() {

        try {
            Solver solver = new LpSolveSolver();

            int index = solver.addVariables(1);
            Map<Integer, Double> lowerBound1 = new HashMap<>();
            lowerBound1.put(1, 4.0);
            solver.addLowerBounds(lowerBound1);

            int binaryIndex = solver.addBinaryVariables(1);
            solver.addImplication(index, binaryIndex, 1e3);

            Map<Integer, Double> objectiveCoefficients = new HashMap<>();
            objectiveCoefficients.put(1, 1.0);
            objectiveCoefficients.put(2, 10.0);
            solver.setObjectiveFunction(objectiveCoefficients);

            SolutionStatus expectedStatus = SolutionStatus.OPTIMAL;
            Map<Integer, Double> expectedSolution = new HashMap<>();
            expectedSolution.put(1, 4.0);
            expectedSolution.put(2, 1.0);
            double expectedObjectiveValue = 14.0;

            SolutionStatus status = solver.solve();

            assertEquals(expectedStatus, status, "Solution status should be OPTIMAL");
            assertEquals(expectedObjectiveValue, solver.getObjectiveValue(), 1e-6, "Objective value should be 14");

            Map<Integer, Double> solution = solver.getSolution();

            assertEquals(expectedSolution.size(), solution.size());

            assertEquals(expectedSolution.size(), solution.size());
            assertTrue(mapEqualsEps(expectedSolution, solution, 1e-6));

            solver.free();
        }
        catch (SolverException solverException) {
            System.out.println(solverException.getMessage());
        }
    }

    @Test
    void addSumImplication() {

        try {
            Solver solver = new LpSolveSolver();

            solver.addVariables(2);
            HashMap<Integer, Double> upperBound = new HashMap<>();
            upperBound.put(2, 12.0);
            solver.addUpperBounds(upperBound);

            int binaryIndex = solver.addBinaryVariables(1);
            List<Integer> continuousIndexes = Arrays.asList(1, 2);
            solver.addSumImplication(continuousIndexes, binaryIndex, 1e3);

            Map<Integer, Double> objectiveCoefficients = new HashMap<>();
            objectiveCoefficients.put(1, 1.0);
            objectiveCoefficients.put(2, -1.0);
            objectiveCoefficients.put(3, 10.0);
            solver.setObjectiveFunction(objectiveCoefficients);

            Map<Integer, Double> expectedSolution = new HashMap<>();
            expectedSolution.put(1, 0.0);
            expectedSolution.put(2, 12.0);
            expectedSolution.put(3, 1.0);
            double expectedObjectiveValue = -2.0;

            SolutionStatus status = solver.solve();
            assertEquals(SolutionStatus.OPTIMAL, status, "Solution status should be OPTIMAL");
            assertEquals(expectedObjectiveValue, solver.getObjectiveValue(), 1e-6);

            Map<Integer, Double> solution = solver.getSolution();
            assertEquals(expectedSolution.size(), solution.size());

            assertEquals(expectedSolution.size(), solution.size());
            assertTrue(mapEqualsEps(expectedSolution, solution, 1e-6));

            solver.free();
        }
        catch (SolverException solverException) {
            System.out.println(solverException.getMessage());
        }
    }

    @Test
    void LpSolveExample() {
        try {
            Solver solver = setUpSolverExample();

            SolutionStatus expectedStatus = SolutionStatus.OPTIMAL;
            Map<Integer, Double> expectedSolution = new HashMap<>();
            expectedSolution.put(1, 0.0);
            expectedSolution.put(2, 0.0);
            expectedSolution.put(3, 2.0);
            expectedSolution.put(4, 0.0);
            double expectedObjectiveValue = -4.0;

            SolutionStatus status = solver.solve();

            assertEquals(expectedStatus, status, "Solution status should be OPTIMAL");
            assertEquals(expectedObjectiveValue, solver.getObjectiveValue(), "Objective value should be 4");
            assertEquals(expectedSolution, solver.getSolution(), "Invalid solution");

            solver.free();
        }
        catch (SolverException solverException) {
            System.out.println(solverException.getMessage());
        }
    }

    private Solver setUpSolverExample() throws SolverException {

        Solver solver = new LpSolveSolver();
        solver.printModel();

        solver.addVariables(4);

        Map<Integer, Double> leqConstraint = new HashMap<>();
        leqConstraint.put(1, 3.0);
        leqConstraint.put(2, 2.0);
        leqConstraint.put(3, 2.0);
        leqConstraint.put(4, 1.0);
        solver.addLeqWeightedSumConstraint(leqConstraint, 4.0);

        Map<Integer, Double> geqConstraint = new HashMap<>();
        geqConstraint.put(2, 4.0);
        geqConstraint.put(3, 3.0);
        geqConstraint.put(4, 1.0);
        solver.addGeqWeightedSumConstraint(geqConstraint, 3.0);

        Map<Integer, Double> objectiveCoefficients = new HashMap<>();
        objectiveCoefficients.put(1, 2.0);
        objectiveCoefficients.put(2, 3.0);
        objectiveCoefficients.put(3, -2.0);
        objectiveCoefficients.put(4, 3.0);
        solver.setObjectiveFunction(objectiveCoefficients);

        solver.setRelativeGap(1e-6);
        solver.setTimeOut((long) (60*5));

        return solver;
    }

    private boolean mapEqualsEps(Map<Integer, Double> expectedSolution, Map<Integer, Double> solution, double eps) {
        return expectedSolution.entrySet().stream()
                .allMatch(entry -> Math.abs(entry.getValue() - solution.get(entry.getKey())) < eps);
    }
}