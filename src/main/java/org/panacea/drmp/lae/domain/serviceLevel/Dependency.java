
package org.panacea.drmp.lae.domain.serviceLevel;

import lombok.Data;

import java.util.List;

@Data
@SuppressWarnings("unused")
public class Dependency {

    private List<Dependency> dependencies;
    private String dependencyType;
    private String serviceLevelId;

}
