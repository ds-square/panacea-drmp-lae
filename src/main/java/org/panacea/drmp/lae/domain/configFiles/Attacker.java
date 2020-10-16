
package org.panacea.drmp.lae.domain.configFiles;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@SuppressWarnings("unused")
public class Attacker {

    private AttackerType attackerType;
    private double attackComplexity;
    private double attackVector;
    private double privRequired;
    private double codeMaturity;
    private double reportConfidence;

    public List<Double> getThresholdsAsList() {
        List<Double> thresholds = new ArrayList<>();

        thresholds.add(this.attackComplexity);
        thresholds.add(this.attackVector);
        thresholds.add(this.privRequired);
        thresholds.add(this.codeMaturity);
        thresholds.add(this.reportConfidence);

        return thresholds;
    }

}
