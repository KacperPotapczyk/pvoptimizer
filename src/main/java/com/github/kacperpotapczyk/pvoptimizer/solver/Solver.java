package com.github.kacperpotapczyk.pvoptimizer.solver;

import com.github.kacperpotapczyk.pvoptimizer.solver.enums.ObjectiveDirection;
import com.github.kacperpotapczyk.pvoptimizer.solver.enums.SolutionStatus;
import com.github.kacperpotapczyk.pvoptimizer.solver.exceptions.SolverException;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Solver {

    int addVariables(int numberOfVariables) throws SolverException;

    int addBinaryVariables(int numberOfVariables) throws SolverException ;

    void fixVariables(Map<Integer, Double> fixedValues) throws SolverException;

    void addEqWeightedSumConstraint(Map<Integer, Double> weights, Double value) throws SolverException;

    void addEqSumConstraint(Set<Integer> indexes, Double value) throws SolverException;

    void addUpperBounds(Map<Integer, Double> upperBounds) throws SolverException;

    void addLeqWeightedSumConstraint(Map<Integer, Double> weights, Double max) throws SolverException;

    void addLeqSumConstraint(Set<Integer> indexes, Double max) throws SolverException;

    void addLowerBounds(Map<Integer, Double> lowerBounds) throws SolverException;

    void addGeqWeightedSumConstraint(Map<Integer, Double> weights, Double min) throws SolverException;

    void addGeqSumConstraint(List<Integer> indexes, Double min) throws SolverException;

    void addImplication(int continuousVariableIndex, int binaryVariableIndex, double bigM) throws SolverException;

    void addSumImplication(List<Integer> continuousVariableIndexes, int binaryVariableIndex, double bigM) throws SolverException;

    void setObjectiveFunction(Map<Integer, Double> coefficients) throws SolverException;

    void setObjectiveDirection(ObjectiveDirection objectiveDirection);

    ObjectiveDirection getObjectiveDirection();

    void setRelativeGap(double relativeGap);

    void setTimeOut(Long timeOutSeconds);

    SolutionStatus solve() throws SolverException;

    double getObjectiveValue() throws SolverException;

    Map<Integer, Double> getSolution() throws SolverException;

    void printModel();

    void free();
}
