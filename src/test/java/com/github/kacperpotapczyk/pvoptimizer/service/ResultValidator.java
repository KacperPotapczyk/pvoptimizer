package com.github.kacperpotapczyk.pvoptimizer.service;

import com.github.kacperpotapczyk.pvoptimizer.model.contract.ContractResult;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.MovableDemandResult;
import com.github.kacperpotapczyk.pvoptimizer.model.storage.StorageModeProfile;
import com.github.kacperpotapczyk.pvoptimizer.model.storage.StorageResult;
import com.github.kacperpotapczyk.pvoptimizer.model.utils.Profile;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class ResultValidator {
    public ResultValidator() {
    }

    void assertContractResults(List<ContractResult> contractResults, List<Profile> expectedPowerResults, List<Profile> expectedCostResults) {

        Assertions.assertEquals(expectedPowerResults.size(), contractResults.size());

        for (int i = 0; i < expectedPowerResults.size(); i++) {

            Profile tmpExpPower = expectedPowerResults.get(i);
            Profile tmpExpCost = expectedCostResults.get(i);

            Profile powerResult = contractResults.get(i).power();
            Profile costResult = contractResults.get(i).cost();

            Assertions.assertEquals(tmpExpPower.getLength(), powerResult.getLength());
            Assertions.assertEquals(tmpExpCost.getLength(), costResult.getLength());

            Assertions.assertEquals(tmpExpPower.getStartInterval(), powerResult.getStartInterval());
            Assertions.assertEquals(tmpExpCost.getStartInterval(), costResult.getStartInterval());

            for (int j = 0; j < tmpExpPower.getLength(); j++) {
                Assertions.assertEquals(tmpExpPower.getValues().get(j), powerResult.getValues().get(j), 1e-6);
                Assertions.assertEquals(tmpExpCost.getValues().get(j), costResult.getValues().get(j), 1e-6);
            }
        }
    }

    void assertContractEnergyResults(List<ContractResult> contractResults, List<Double> expectedEnergyResults) {

        Assertions.assertEquals(expectedEnergyResults.size(), contractResults.size());

        for (int i = 0; i < expectedEnergyResults.size(); i++) {
            Assertions.assertEquals(
                    expectedEnergyResults.get(i),
                    contractResults.get(i).energy().getValues().stream().reduce(Double::sum).orElseThrow(),
                    1e-6);
        }
    }

    void assertStorageResults(List<Profile> expectedCharge, List<Profile> expectedDischarge, List<Profile> expectedEnergy, List<StorageModeProfile> expectedMode, List<StorageResult> storageResults) {

        Assertions.assertEquals(expectedCharge.size(), storageResults.size());

        for (int i = 0; i < expectedCharge.size(); i++) {

            Profile tmpCharge = expectedCharge.get(i);
            Profile tmpDischarge = expectedDischarge.get(i);
            Profile tmpEnergy = expectedEnergy.get(i);
            StorageModeProfile tmpProfile = expectedMode.get(i);

            Profile chargeResult = storageResults.get(i).charge();
            Profile dischargeResult = storageResults.get(i).discharge();
            Profile energyResult = storageResults.get(i).energy();
            StorageModeProfile modeResult = storageResults.get(i).storageMode();

            Assertions.assertEquals(tmpCharge.getLength(), chargeResult.getLength());
            Assertions.assertEquals(tmpDischarge.getLength(), dischargeResult.getLength());
            Assertions.assertEquals(tmpEnergy.getLength(), energyResult.getLength());
            Assertions.assertEquals(tmpProfile.getLength(), modeResult.getLength());

            for (int j = 0; j < tmpCharge.getLength(); j++) {

                Assertions.assertEquals(tmpCharge.getValues().get(j), chargeResult.getValues().get(j), 1e-6);
                Assertions.assertEquals(tmpDischarge.getValues().get(j), dischargeResult.getValues().get(j), 1e-6);
                Assertions.assertEquals(tmpEnergy.getValues().get(j), energyResult.getValues().get(j), 1e-6);
                Assertions.assertEquals(tmpProfile.getValues().get(j), modeResult.getValues().get(j));

            }
        }
    }

    void assertMovableDemandResults(List<Integer> expectedStartIntervals, List<MovableDemandResult> movableDemandResults) {

        Assertions.assertEquals(expectedStartIntervals.size(), movableDemandResults.size());

        for(int i=0; i<expectedStartIntervals.size(); i++) {

            Assertions.assertEquals(expectedStartIntervals.get(i), movableDemandResults.get(i).startInterval());
        }
    }
}