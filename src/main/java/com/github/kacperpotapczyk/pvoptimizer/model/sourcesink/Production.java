package com.github.kacperpotapczyk.pvoptimizer.model.sourcesink;

import com.github.kacperpotapczyk.pvoptimizer.model.utils.Profile;

/**
 * Fixed pv production profile. It is expected that production is available at all task intervals
 */
public class Production extends FixedSourceSink {

    /**
     * Creates fixed household demand profile
     * @param id production id
     * @param name production name
     * @param generationProfile pv power generation profile
     */
    public Production(long id, String name, Profile generationProfile) {
        super(id, name, generationProfile);
    }
}
