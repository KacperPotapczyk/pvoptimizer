package com.github.kacperpotapczyk.pvoptimizer.model.sourcesink;

import com.github.kacperpotapczyk.pvoptimizer.model.utils.Profile;

public class Production extends FixedSourceSink {

    public Production(int id, String name, Profile generationProfile) {
        super(id, name, generationProfile);
    }
}
