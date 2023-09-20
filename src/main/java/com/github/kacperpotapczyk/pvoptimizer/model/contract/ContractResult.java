package com.github.kacperpotapczyk.pvoptimizer.model.contract;

import com.github.kacperpotapczyk.pvoptimizer.model.utils.Profile;

public record ContractResult(int id, String name, Profile power, Profile energy, Profile cost) {
}
