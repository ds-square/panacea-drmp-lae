
package org.panacea.drmp.lae.domain.businessEntity;

import lombok.Data;

import java.util.List;

@Data
@SuppressWarnings("unused")
public class BusinessEntityInventory {

    private List<BusinessEntity> businessEntities;
    private String environment;
    private String fileType;
    private String snapshotId;
    private String snapshotTime;


    public BusinessEntityType getSlType(String slId) {
        for (BusinessEntity be : this.businessEntities) {
            for (String id : be.getServiceLevelsList()) {
                if (id.equals(slId)) {
                    return BusinessEntityType.valueOf(be.getType());
                }
            }
        }
        return null;
    }
}
