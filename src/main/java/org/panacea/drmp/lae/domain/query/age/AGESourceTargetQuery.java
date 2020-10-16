package org.panacea.drmp.lae.domain.query.age;

import lombok.Data;

import java.util.List;

@Data
@SuppressWarnings("unused")
public class AGESourceTargetQuery {
    private List<String> sources;
    private List<String> targets;

    public AGESourceTargetQuery() {
    }

    public AGESourceTargetQuery(List<String> sources, List<String> targets) {
        this.sources = sources;
        this.targets = targets;
    }
}
