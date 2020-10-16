
package org.panacea.drmp.lae.domain.configFiles;

import lombok.Data;

import java.util.List;

@Data
@SuppressWarnings("unused")
public class ConfigurationSpecification {

    private List<Attacker> attackers;
    private String environment;
    private String fileType;
    private ImpactTresholds impactThresholds;
    private String snapshotId;
    private String snapshotTime;

}
