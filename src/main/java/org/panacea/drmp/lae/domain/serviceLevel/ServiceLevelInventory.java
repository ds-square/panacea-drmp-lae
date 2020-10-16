
package org.panacea.drmp.lae.domain.serviceLevel;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@SuppressWarnings("unused")
public class ServiceLevelInventory {

    private String environment;
    private String fileType;
    private List<ServiceLevel> serviceLevels;
    private String snapshotId;
    private String snapshotTime;

    private Map<String, ServiceLevel> serviceLevelMap;


    public void computeServiceLevelMap() {
        this.serviceLevelMap = new HashMap<>();
        for (ServiceLevel sl : this.serviceLevels) {
            this.serviceLevelMap.put(sl.getId(), sl);
        }
    }

    public ServiceLevel getServiceLevelById(String id) {
        return this.serviceLevelMap.get(id);
    }
}
