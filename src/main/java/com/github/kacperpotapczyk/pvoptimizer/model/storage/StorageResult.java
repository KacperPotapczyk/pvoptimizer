package com.github.kacperpotapczyk.pvoptimizer.model.storage;

import com.github.kacperpotapczyk.pvoptimizer.model.utils.Profile;

/**
 * Optimization results of storage operation.
 * @param id storage identifier
 * @param name storage name
 * @param charge charging profile
 * @param discharge discharging profile
 * @param energy energy stored profile
 * @param storageMode storage mode profile
 */
public record StorageResult(long id, String name, Profile charge, Profile discharge, Profile energy, StorageModeProfile storageMode) {
}
