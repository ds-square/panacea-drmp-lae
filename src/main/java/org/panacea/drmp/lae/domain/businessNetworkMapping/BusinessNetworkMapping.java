
package org.panacea.drmp.lae.domain.businessNetworkMapping;

import com.google.common.collect.HashBasedTable;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@SuppressWarnings("unused")
public class BusinessNetworkMapping {

    private String environment;
    private String fileType;
    private List<Mapping> mappings;
    private String snapshotId;
    private String snapshotTime;

    private HashBasedTable<String, String, String> mappingMap;

    public void computeMappingMap() {
        this.mappingMap = HashBasedTable.create();
        for (Mapping m : this.mappings) {
//            this.mappingMap.put(m.getServiceLevelId(), m);
            String id = m.getServiceLevelId();
            String privilegeNode = m.getDeviceId();
            String[] splittedPrivilege = privilegeNode.split("@");
            this.mappingMap.put(id, splittedPrivilege[0], privilegeNode);
        }
    }

    public Map<String, String> getMappingById(String id) {
        return this.mappingMap.row(id);
    }
}
