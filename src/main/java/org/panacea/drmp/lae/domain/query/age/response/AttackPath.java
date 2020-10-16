package org.panacea.drmp.lae.domain.query.age.response;

import lombok.Data;
import org.panacea.drmp.lae.domain.configFiles.AttackerType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@SuppressWarnings("unused")
public class AttackPath {
    private List<AttackStep> steps;
    private Map<AttackerType, Double> likelihood;

    public AttackPath() {
        this.steps = new ArrayList<>();
        this.likelihood = new HashMap<>();
    }

    public void addAttackStep(AttackStep step) {
        this.steps.add(step);
    }

    public String getSource() {
        return this.steps.get(0).getSource();
    }

    public String getTarget() {
        return this.steps.get(this.steps.size() - 1).getDestination();
    }

    public void addLikelihood(AttackerType type, Double likelihoodValue) {
        this.likelihood.put(type, likelihoodValue);
    }
}
