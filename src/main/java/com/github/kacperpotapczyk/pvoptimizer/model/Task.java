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

@Builder
@Getter
public class Task {

    private Profile intervals;
    private Production production;
    private Demand demand;
    @Singular
    private Set<Contract> contracts;
    @Singular
    private Set<Storage> storages;
    @Singular
    private Set<MovableDemand> movableDemands;

    public int optimizationHorizonLength() {

        return intervals.getLength();
    }
}
