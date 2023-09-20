package com.github.kacperpotapczyk.pvoptimizer.model.storage;

import com.github.kacperpotapczyk.pvoptimizer.model.utils.Profile;

public record StorageResult(int id, String name, Profile charge, Profile discharge, Profile energy, StorageModeProfile storageMode) {
}
