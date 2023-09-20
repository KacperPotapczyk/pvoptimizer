package com.github.kacperpotapczyk.pvoptimizer.model.sourcesink;

import com.github.kacperpotapczyk.pvoptimizer.model.utils.Profile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
abstract class FixedSourceSink {

    private final int id;
    private final String name;
    private final Profile profile;
}
