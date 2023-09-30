package com.github.kacperpotapczyk.pvoptimizer.model.contract;

import com.github.kacperpotapczyk.pvoptimizer.model.utils.Profile;

/**
 * Optimization results of contract utilization.
 * @param id contract id
 * @param name contract name
 * @param power contract power intervals profile
 * @param energy contract energy intervals profile
 * @param cost contract cost/profit intervals profile
 */
public record ContractResult(int id, String name, Profile power, Profile energy, Profile cost) {
}
