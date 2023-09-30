package com.github.kacperpotapczyk.pvoptimizer.model;

import com.github.kacperpotapczyk.pvoptimizer.model.contract.Contract;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.Demand;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.MovableDemand;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.Production;
import com.github.kacperpotapczyk.pvoptimizer.model.storage.Storage;
import com.github.kacperpotapczyk.pvoptimizer.model.utils.Profile;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Set;

/**
 * Task contains all information required to create optimization model by {@link com.github.kacperpotapczyk.pvoptimizer.service.Optimizer optimizer}.
 */
@Builder
@Getter
public class Task {

    /**
     * Time in seconds after which optimization solver has to stop calculations.
     */
    @Builder.Default
    private final Long timeoutSeconds = 0L;
    /**
     * Relative gap between relaxed and integer solution below which solver stops calculations.
     */
    @Builder.Default
    private final double relativeGap = 0.0;
    /**
     * List of intervals for witch input data and results are present. Each interval has its time length.
     */
    private Profile intervals;
    /**
     * Electric power from PV installation.
     */
    private Production production;
    /**
     * Household demand for electric power.
     */
    private Demand demand;
    /**
     * Electricity purchase and sell contracts.
     */
    @Singular
    private Set<Contract> contracts;
    /**
     * Electric energy storages.
     */
    @Singular
    private Set<Storage> storages;
    /**
     * Household demands witch are constant in their profile but its start date can be adjusted by optimizer.
     */
    @Singular
    private Set<MovableDemand> movableDemands;

    /**
     * Returns number of intervals.
     * @return number of intervals
     */
    public int optimizationHorizonLength() {

        return intervals.getLength();
    }
}
