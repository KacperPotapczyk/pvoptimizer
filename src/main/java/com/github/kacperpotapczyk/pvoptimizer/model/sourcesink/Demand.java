package com.github.kacperpotapczyk.pvoptimizer.model.sourcesink;

import com.github.kacperpotapczyk.pvoptimizer.model.utils.Profile;

public class Demand extends FixedSourceSink {

    public Demand(int id, String name, Profile demandProfile) {
        super(id, name, demandProfile);
    }
}
