package com.github.kacperpotapczyk.pvoptimizer.solver;

import com.github.kacperpotapczyk.pvoptimizer.solver.enums.ObjectiveDirection;
import com.github.kacperpotapczyk.pvoptimizer.solver.enums.SolutionStatus;
import com.github.kacperpotapczyk.pvoptimizer.solver.exceptions.SolverException;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Class implements {@link Solver Solver} interface using LpSolve library. For more information regarding LpSolve library
 * and its usage in Java see links below.
 * @see <a href="https://lpsolve.sourceforge.net/5.5">LpSolve</a>
 * @see <a href="https://lpsolve.sourceforge.net/5.5/Java/README.html">LpSolve Java API</a>
 */
public class LpSolveSolver implements Solver {

    private final LpSolve solver;

    /**
     * Initiates solver with model containing 0 variables and 0 constraints. Default optimization goal is to minimize objective function.
     * Sets up pre-solver methods. Sets up LpSolve log output to IMPORTANT level.
     * @throws SolverException if model can not be initiated
     */
    public LpSolveSolver() throws SolverException {
        try {
            solver = LpSolve.makeLp(0, 0);
            setObjectiveDirection(ObjectiveDirection.MIN);
            solver.setPresolve(
                    LpSolve.PRESOLVE_ROWS + LpSolve.PRESOLVE_COLS + LpSolve.PRESOLVE_LINDEP + LpSolve.PRESOLVE_SOS + LpSolve.PRESOLVE_KNAPSACK,
                    solver.getPresolveloops());
            solver.setVerbose(LpSolve.IMPORTANT);
        }
        catch (LpSolveException e) {
            throw new SolverException(e.getMessage());
        }
    }

    @Override
    public int addVariables(int numberOfVariables) throws SolverException {

        int firstVariableIndex = this.solver.getNcolumns() + 1;

        try {
            double[] column = {0};
            int[] rowNumber = {0};
            for (int i = 0; i < numberOfVariables; i++) {
                solver.addColumnex(1, column, rowNumber);
            }
        }
        catch (LpSolveException e) {
            throw new SolverException(e.getMessage());
        }

        return firstVariableIndex;
    }

    @Override
    public int addBinaryVariables(int numberOfVariables) throws SolverException {
        int firstVariableIndex = this.solver.getNcolumns() + 1;

        try {
            double[] column = {0};
            int[] rowNumber = {0};
            for (int i = 0; i < numberOfVariables; i++) {
                solver.addColumnex(1, column, rowNumber);
                solver.setBinary(firstVariableIndex + i, true);
            }
        }
        catch (LpSolveException e) {
            throw new SolverException(e.getMessage());
        }

        return firstVariableIndex;
    }

    @Override
    public void fixVariables(Map<Integer, Double> fixedValues) throws SolverException {

        double[] value = new double[1];
        int[] index = new int[1];

        for (Entry<Integer, Double> fixedValue : fixedValues.entrySet()) {
            value[0] = 1;
            index[0] = fixedValue.getKey();
            try {
                solver.addConstraintex(1, value, index, LpSolve.EQ, fixedValue.getValue());
            } catch (LpSolveException e) {
                throw new SolverException(e.getMessage());
            }
        }
    }

    @Override
    public void addEqWeightedSumConstraint(Map<Integer, Double> weights, Double value) throws SolverException {

        addWeightedSumConstraint(weights, LpSolve.EQ, value);
    }

    @Override
    public void addEqSumConstraint(Set<Integer> indexes, Double value) throws SolverException {

        Map<Integer, Double> row = indexes.stream()
                .collect(Collectors.toMap(index -> index, val -> 1.0));

        addEqWeightedSumConstraint(row, value);
    }

    @Override
    public void addUpperBounds(Map<Integer, Double> upperBounds) throws SolverException {

        for(Entry<Integer, Double> upperBound : upperBounds.entrySet()) {
            try {
                solver.setUpbo(upperBound.getKey(), upperBound.getValue());
            } catch (LpSolveException e) {
                throw new SolverException(e.getMessage());
            }
        }
    }

    @Override
    public void addLeqWeightedSumConstraint(Map<Integer, Double> weights, Double max) throws SolverException {

        addWeightedSumConstraint(weights, LpSolve.LE, max);
    }

    @Override
    public void addLeqSumConstraint(Set<Integer> indexes, Double max) throws SolverException {

        Map<Integer, Double> row = indexes.stream()
                .collect(Collectors.toMap(index -> index, val -> 1.0));

        addLeqWeightedSumConstraint(row, max);
    }

    @Override
    public void addLowerBounds(Map<Integer, Double> lowerBounds) throws SolverException {

        for (Entry<Integer, Double> lowerBound : lowerBounds.entrySet()) {
            try {
                solver.setLowbo(lowerBound.getKey(), lowerBound.getValue());
            } catch (LpSolveException e) {
                throw new SolverException(e.getMessage());
            }
        }
    }

    @Override
    public void addGeqWeightedSumConstraint(Map<Integer, Double> weights, Double min) throws SolverException {

        addWeightedSumConstraint(weights, LpSolve.GE, min);
    }

    @Override
    public void addGeqSumConstraint(List<Integer> indexes, Double min) throws SolverException {

        Map<Integer, Double> row = indexes.stream()
                .collect(Collectors.toMap(index -> index, val -> 1.0));

        addGeqWeightedSumConstraint(row, min);
    }

    @Override
    public void addImplication(int continuousVariableIndex, int binaryVariableIndex, double bigM) throws SolverException {

        Map<Integer, Double> indexedValues = new HashMap<>(2);
        indexedValues.put(continuousVariableIndex, 1.0);
        indexedValues.put(binaryVariableIndex, -1.0*bigM);

        addLeqWeightedSumConstraint(indexedValues, 0.0);
    }

    @Override
    public void addSumImplication(List<Integer> continuousVariableIndexes, int binaryVariableIndex, double bigM) throws SolverException {

        Map<Integer, Double> indexedValues = continuousVariableIndexes.stream()
                        .collect(Collectors.toMap(index -> index, val -> 1.0));
        indexedValues.put(binaryVariableIndex, -1.0*bigM);

        addLeqWeightedSumConstraint(indexedValues, 0.0);
    }

    @Override
    public void setObjectiveFunction(Map<Integer, Double> coefficients) throws SolverException {

        int size = coefficients.size();
        double[] coefficientsArray = new double[size];
        int[] columnIndexesArray = new int[size];
        int i = 0;

        for (Entry<Integer, Double> coefficient : coefficients.entrySet()) {
            columnIndexesArray[i] = coefficient.getKey();
            coefficientsArray[i] = coefficient.getValue();
            i++;
        }
        try {
            solver.setObjFnex(size, coefficientsArray, columnIndexesArray);
        }
        catch (LpSolveException e) {
            throw new SolverException(e.getMessage());
        }
    }

    @Override
    public void setObjectiveDirection(ObjectiveDirection objectiveDirection) {

        if (objectiveDirection == ObjectiveDirection.MAX) {
            solver.setMaxim();
        }
        else {
            solver.setMinim();
        }
    }

    @Override
    public ObjectiveDirection getObjectiveDirection() {

        if (solver.isMaxim()) {
            return ObjectiveDirection.MAX;
        }
        else {
            return ObjectiveDirection.MIN;
        }
    }

    @Override
    public void setRelativeGap(double relativeGap) {

        solver.setMipGap(false, relativeGap);
    }

    @Override
    public void setTimeOut(long timeOutSeconds) {

        solver.setTimeout(timeOutSeconds);
    }

    @Override
    public SolutionStatus solve() throws SolverException {

        try {
            int code = solver.solve();
            return switch (code) {
                case 0 -> SolutionStatus.OPTIMAL;
                case 1 -> SolutionStatus.SUBOPTIMAL;
                case 2 -> SolutionStatus.INFEASIBLE;
                case 3 -> SolutionStatus.UNBOUNDED;
                case 7 -> SolutionStatus.TIMEOUT;
                default -> SolutionStatus.ERROR;
            };
        }
        catch (LpSolveException e) {
            throw new SolverException(e.getMessage());
        }
    }

    @Override
    public double getObjectiveValue() throws SolverException {
        try {
            return solver.getObjective();
        }
        catch (LpSolveException e) {
            throw new SolverException(e.getMessage());
        }
    }

    @Override
    public Map<Integer, Double> getSolution() throws SolverException {

        Map<Integer, Double>  solution = new TreeMap<>();
        try {
            int numberOfColumns = solver.getNorigColumns();
            int numberOfRows = solver.getNorigRows();
            for (int i = 1; i <= numberOfColumns; i++) {
                solution.put(i, solver.getVarPrimalresult(numberOfRows + i));
            }
        }
        catch (LpSolveException e) {
            throw new SolverException(e.getMessage());
        }
        return solution;
    }

    @Override
    public double getSolutionRelativeGap() {
        return solver.getMipGap(false);
    }

    @Override
    public double getSolutionElapsedTime() {
        return solver.timeElapsed();
    }

    @Override
    public void printModel() {

        solver.printLp();
    }

    @Override
    public void free() {

        solver.deleteLp();
    }

    private void addWeightedSumConstraint(Map<Integer, Double> weights, int sign, Double value) throws SolverException {

        int size = weights.size();
        int maxIndex = solver.getNcolumns();
        double[] weightValues = new double[size];
        int[] weightIndexes = new int[size];
        int i = 0;

        for (Entry<Integer, Double> weight : weights.entrySet()) {

            if (weight.getKey() > maxIndex) {
                throw new SolverException("Constraint index: " + weight.getKey() + " out of range: " + maxIndex);
            }

            weightIndexes[i] = weight.getKey();
            weightValues[i] = weight.getValue();
            i++;
        }

        try {
            solver.addConstraintex(size, weightValues, weightIndexes, sign, value);
        }
        catch (LpSolveException e) {
            throw new SolverException(e.getMessage());
        }
    }
}
