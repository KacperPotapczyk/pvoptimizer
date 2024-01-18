package com.github.kacperpotapczyk.pvoptimizer.optimizer.model.storage;

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
