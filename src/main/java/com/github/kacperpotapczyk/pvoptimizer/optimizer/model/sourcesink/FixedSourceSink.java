package com.github.kacperpotapczyk.pvoptimizer.optimizer.model.sourcesink;

import com.github.kacperpotapczyk.pvoptimizer.optimizer.model.utils.Profile;
import lombok.Getter;

@Getter
public class FixedSourceSink {

    /**
     * Profile id
     */
    private final long id;
    /**
     * Profile name
     */
    private final String name;
    /**
     * Values profile
     */
    private final Profile profile;

    protected FixedSourceSink(long id, String name, Profile profile) {
        this.id = id;
        this.name = name;
        this.profile = profile;
    }
}
