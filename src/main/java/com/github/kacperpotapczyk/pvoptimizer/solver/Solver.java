package com.github.kacperpotapczyk.pvoptimizer.solver;

import com.github.kacperpotapczyk.pvoptimizer.solver.enums.ObjectiveDirection;
import com.github.kacperpotapczyk.pvoptimizer.solver.enums.SolutionStatus;
import com.github.kacperpotapczyk.pvoptimizer.solver.exceptions.SolverException;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Mixed Integer Programing (MIP) solver.
 * At first create model by adding variables, constraints and objective function
 * then {@link #solve() solve} created model and {@link #getSolution() receive solution} and {@link #getObjectiveValue() objective function} value.
 * Do not forget to {@link #free() free} solver after computations.
 */
public interface Solver {

    /**
     * Adds continuous variables to model.
     * @param numberOfVariables number of continuous variables to be added to model
     * @return index of firs variable
     * @throws SolverException if variables can not be added
     */
    int addVariables(int numberOfVariables) throws SolverException;

    /**
     * Adds binary variables to model.
     * @param numberOfVariables number of binary variables to be added to model
     * @return index of firs variable
     * @throws SolverException if variables can not be added
     */
    int addBinaryVariables(int numberOfVariables) throws SolverException ;

    /**
     * Constrains variables to given value.
     * @param fixedValues variable index and fix value pairs
     * @throws SolverException if constraint can not be added
     */
    void fixVariables(Map<Integer, Double> fixedValues) throws SolverException;

    /**
     * Adds constraint to model where sum of variables each multiplied by its coefficient has to be equal to given value.
     * @param weights variable index and coefficient value pairs
     * @param value right hand side value of equality constraint
     * @throws SolverException if constraint can not be added
     */
    void addEqWeightedSumConstraint(Map<Integer, Double> weights, Double value) throws SolverException;

    /**
     * Adds constraint to model where sum of variables has to be equal to given value.
     * @param indexes indexes of variables
     * @param value right hand side value of equality constraint
     * @throws SolverException if constraints can not be added
     */
    void addEqSumConstraint(Set<Integer> indexes, Double value) throws SolverException;

    /**
     * Sets upper bound constraint for specified variables.
     * @param upperBounds variable index and upper bound value pairs
     * @throws SolverException if constraints can not be added
     */
    void addUpperBounds(Map<Integer, Double> upperBounds) throws SolverException;

    /**
     * Adds constraint to model where sum of variables each multiplied by its coefficient has to be lower or equal than given value.
     * @param weights variable index and coefficient value pairs
     * @param max right hand side value of lower or equal than constraint
     * @throws SolverException if constraint can not be added
     */
    void addLeqWeightedSumConstraint(Map<Integer, Double> weights, Double max) throws SolverException;

    /**
     * Adds constraint to model where sum of variables has to be lower or equal than given value.
     * @param indexes indexes of variables
     * @param max right hand side value of lower or equal than constraint
     * @throws SolverException if constraint can not be added
     */
    void addLeqSumConstraint(Set<Integer> indexes, Double max) throws SolverException;

    /**
     * Sets lower bound constraints for specified variables.
     * @param lowerBounds variable index and lower bound value pairs
     * @throws SolverException if constraints can not be added
     */
    void addLowerBounds(Map<Integer, Double> lowerBounds) throws SolverException;

    /**
     * Adds constraint to model where sum of variables each multiplied by its coefficient has to be greater or equal than given value.
     * @param weights variable index and coefficient value pairs
     * @param min right hand side value of greater or equal than constraint
     * @throws SolverException if constraint can not be added
     */
    void addGeqWeightedSumConstraint(Map<Integer, Double> weights, Double min) throws SolverException;

    /**
     * Adds constraint to model where sum of variables has to be greater or equal than given value.
     * @param indexes indexes of variables
     * @param min right hand side value of greater or equal than constraint
     * @throws SolverException if constraint can not be added
     */
    void addGeqSumConstraint(List<Integer> indexes, Double min) throws SolverException;

    /**
     * Create implication that: if continuous variable is greater than 0 then binary variable has to be 1.
     * This implication does not enforce binary variable value of 0 when continuous variable is equal 0.
     * @param continuousVariableIndex index of continuous variable
     * @param binaryVariableIndex index of binary variable
     * @param bigM big multiplier value, has to be greater than possible value of continuous variable
     * @throws SolverException if implication can not be added
     */
    void addImplication(int continuousVariableIndex, int binaryVariableIndex, double bigM) throws SolverException;

    /**
     * Create implication that: if sum of continuous variables is greater than 0 then binary variable has to be 1.
     * This implication does not enforce binary variable value of 0 when sum of continuous variables is equal 0.
     * @param continuousVariableIndexes indexes of continuous variables
     * @param binaryVariableIndex index of binary variable
     * @param bigM big multiplier value, has to be greater than possible sum of continuous variables
     * @throws SolverException if implication can not be added
     */
    void addSumImplication(List<Integer> continuousVariableIndexes, int binaryVariableIndex, double bigM) throws SolverException;

    /**
     * Set objective function to be minimized or maximized during optimization. Objective function is sum of variable
     * multiplied by its coefficient. If variable is not specified then its coefficient is 0.
     * @param coefficients variable index and coefficient pairs
     * @throws SolverException if objective function can not be set
     */
    void setObjectiveFunction(Map<Integer, Double> coefficients) throws SolverException;

    /**
     * Defines if objective function has to be minimized or maximized by solver.
     * @param objectiveDirection optimization goal direction
     */
    void setObjectiveDirection(ObjectiveDirection objectiveDirection);

    /**
     * Returns optimization goal direction of model.
     * @return current optimization goal direction
     */
    ObjectiveDirection getObjectiveDirection();

    /**
     * Sets relative gap value between relaxed solution and current integer solution below which optimization is terminated.
     * @param relativeGap relative gap value
     */
    void setRelativeGap(double relativeGap);

    /**
     * Sets maximal time in seconds after which optimization is stopped and solution (if found) is returned.
     * @param timeOutSeconds maximal time in seconds
     */
    void setTimeOut(long timeOutSeconds);

    /**
     * Starts optimization of created model.
     * @return solution status
     * @throws SolverException if there was error during optimization
     */
    SolutionStatus solve() throws SolverException;

    /**
     * Returns best objective function value obtained during optimization.
     * @return objective function value
     * @throws SolverException if objective value is not available
     */
    double getObjectiveValue() throws SolverException;

    /**
     * Return values assigned to variables obtained during optimization.
     * @return variable index and value paris
     * @throws SolverException if solution is not available
     */
    Map<Integer, Double> getSolution() throws SolverException;

    /**
     * Return relative gap between relaxed solution and best integer solution
     * @return relative gap between relaxed solution and best integer solution
     */
    double getSolutionRelativeGap();

    /**
     * Returns optimization elapsed time
     * @return optimization elapsed time in seconds
     */
    double getSolutionElapsedTime();

    /**
     * Prints constraints matrix to standard output.
     */
    void printModel();

    /**
     * Destroys created model. Used to free resources after optimization.
     */
    void free();
}
