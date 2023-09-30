package com.github.kacperpotapczyk.pvoptimizer.model.sourcesink;

import com.github.kacperpotapczyk.pvoptimizer.model.utils.Profile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
abstract class FixedSourceSink {

    /**
     * Profile id
     */
    private final int id;
    /**
     * Profile name
     */
    private final String name;
    /**
     * Values profile
     */
    private final Profile profile;
}
