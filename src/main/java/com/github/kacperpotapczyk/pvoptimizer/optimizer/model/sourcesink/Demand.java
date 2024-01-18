package com.github.kacperpotapczyk.pvoptimizer.optimizer.model.sourcesink;

import com.github.kacperpotapczyk.pvoptimizer.optimizer.model.utils.Profile;

/**
 * Fixed household demand profile. It is expected that demand is available at all task intervals
 */
public class Demand extends FixedSourceSink {

    /**
     * Creates fixed household demand profile
     * @param id demand id
     * @param name demand name
     * @param demandProfile demand profile
     */
    public Demand(long id, String name, Profile demandProfile) {
        super(id, name, demandProfile);
    }
}
