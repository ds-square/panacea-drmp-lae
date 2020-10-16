
package org.panacea.drmp.lae.domain.serviceLevel;

import lombok.Data;

@Data
@SuppressWarnings("unused")
public class ServiceLevel {

    private String availabilityLevel;
    private String confidentialityLevel;
    private Dependency dependency;
    private String id;
    private double impact;
    private String integrityLevel;

}
