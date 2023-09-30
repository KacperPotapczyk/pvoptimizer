package com.github.kacperpotapczyk.pvoptimizer.model.sourcesink;

import com.github.kacperpotapczyk.pvoptimizer.model.utils.Profile;

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
    public Demand(int id, String name, Profile demandProfile) {
        super(id, name, demandProfile);
    }
}
