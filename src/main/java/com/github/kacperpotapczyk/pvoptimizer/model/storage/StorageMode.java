package com.github.kacperpotapczyk.pvoptimizer.model.storage;

/**
 * Available energy storage operation modes.
 */
public enum StorageMode {
    /**
     * Storage is disabled.
     */
    DISABLED,
    /**
     * Storage is charging
     */
    CHARGING,
    /**
     * Storage is discharging
     */
    DISCHARGING
}
