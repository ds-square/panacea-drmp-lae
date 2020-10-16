package org.panacea.drmp.lae.service;


import org.panacea.drmp.lae.domain.businessEntity.BusinessEntityInventory;
import org.panacea.drmp.lae.domain.businessNetworkMapping.BusinessNetworkMapping;
import org.panacea.drmp.lae.domain.configFiles.ConfigurationSpecification;
import org.panacea.drmp.lae.domain.notification.DataNotification;
import org.panacea.drmp.lae.domain.query.input.QueryInput;
import org.panacea.drmp.lae.domain.serviceLevel.ServiceLevelInventory;
import org.panacea.drmp.lae.domain.vulnerability.human.HumanVulnerabilityCatalog;
import org.panacea.drmp.lae.domain.vulnerability.network.VulnerabilityCatalog;

public interface LAEInputRequestService {

    BusinessEntityInventory getBusinessEntityInventoryFile(String snapshotId);

    ServiceLevelInventory getServiceLevelInventoryFile(String snapshotId);

    BusinessNetworkMapping getBusinessNetworkMappingFile(String snapshotId);

    QueryInput getQueryInputFile(DataNotification notification);

    VulnerabilityCatalog getVulnerabilityCatalog(String snapshotId);

    HumanVulnerabilityCatalog getHumanVulnerabilityCatalog(String snapshotId);

    ConfigurationSpecification getConfigurationSpecificationFile(String snapshotId);

}
