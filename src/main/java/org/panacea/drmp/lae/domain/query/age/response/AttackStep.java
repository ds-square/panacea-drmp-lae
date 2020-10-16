package org.panacea.drmp.lae.domain.query.age.response;

import lombok.Data;

import java.util.List;

@Data
@SuppressWarnings("unused")
public class AttackStep {
    private String source;
    private List<String> vulnerabilityList;
    private String destination;
}
