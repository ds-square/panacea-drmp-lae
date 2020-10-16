package org.panacea.drmp.lae.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.panacea.drmp.lae.domain.businessEntity.BusinessEntityInventory;
import org.panacea.drmp.lae.domain.businessNetworkMapping.BusinessNetworkMapping;
import org.panacea.drmp.lae.domain.configFiles.ConfigurationSpecification;
import org.panacea.drmp.lae.domain.notification.DataNotification;
import org.panacea.drmp.lae.domain.query.input.QueryInput;
import org.panacea.drmp.lae.domain.serviceLevel.ServiceLevelInventory;
import org.panacea.drmp.lae.domain.vulnerability.human.HumanVulnerabilityCatalog;
import org.panacea.drmp.lae.domain.vulnerability.network.VulnerabilityCatalog;
import org.panacea.drmp.lae.service.LAEInputRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class LAEInputRequestServiceImpl implements LAEInputRequestService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${businessEntityInventory.endpoint}")
    private String businessEntityInventoryURL;
    @Value("${businessEntityInventory.fn}")
    private String businessEntityInventoryFn;

    @Value("${serviceLevelInventory.endpoint}")
    private String serviceLevelInventoryURL;
    @Value("${serviceLevelInventory.fn}")
    private String serviceLevelInventoryFn;

    @Value("${businessNetworkMapping.endpoint}")
    private String businessNetworkMappingURL;
    @Value("${businessNetworkMapping.fn}")
    private String businessNetworkMappingFn;

    @Value("${queryInput.endpoint}")
    private String queryInputURL;

    @Value("${vulnerabilityCatalog.endpoint}")
    private String vulnerabilityCatalogURL;
    @Value("${vulnerabilityCatalog.fn}")
    private String vulnerabilityCatalogFn;

    @Value("${humanVulnerabilityCatalog.endpoint}")
    private String humanVulnerabilityCatalogURL;
    @Value("${humanVulnerabilityCatalog.fn}")
    private String humanVulnerabilityCatalogFn;

    @Value("${configurationSpecification.endpoint}")
    private String configurationSpecificationURL;
    @Value("${configurationSpecification.fn}")
    private String configurationSpecificationFn;


    @Override
    public BusinessEntityInventory getBusinessEntityInventoryFile(String snapshotId) {
        ResponseEntity<BusinessEntityInventory> responseEntity = restTemplate.exchange(
                businessEntityInventoryURL + '/' + snapshotId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<BusinessEntityInventory>() {
                });
        BusinessEntityInventory businessEntityInventory = responseEntity.getBody();

        return businessEntityInventory;
    }

    @Override
    public ServiceLevelInventory getServiceLevelInventoryFile(String snapshotId) {
        ResponseEntity<ServiceLevelInventory> responseEntity = restTemplate.exchange(
                serviceLevelInventoryURL + '/' + snapshotId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ServiceLevelInventory>() {
                });
        ServiceLevelInventory serviceLevelInventory = responseEntity.getBody();

        return serviceLevelInventory;
    }

    @Override
    public BusinessNetworkMapping getBusinessNetworkMappingFile(String snapshotId) {
        ResponseEntity<BusinessNetworkMapping> responseEntity = restTemplate.exchange(
                businessNetworkMappingURL + '/' + snapshotId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<BusinessNetworkMapping>() {
                });
        BusinessNetworkMapping businessNetworkMapping = responseEntity.getBody();

        return businessNetworkMapping;
    }

    @Override
    public QueryInput getQueryInputFile(DataNotification notification) {
        ResponseEntity<QueryInput> responseEntity = restTemplate.exchange(
                queryInputURL + '/' + notification.getSnapshotId() + '/' + notification.getQueryId(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<QueryInput>() {
                });
        QueryInput queryInput = responseEntity.getBody();

        return queryInput;
    }

    @Override
    public VulnerabilityCatalog getVulnerabilityCatalog(String snapshotId) {
        ResponseEntity<VulnerabilityCatalog> responseEntity = restTemplate.exchange(
                vulnerabilityCatalogURL + '/' + snapshotId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<VulnerabilityCatalog>() {
                });
        VulnerabilityCatalog vulnerabilityCatalog = responseEntity.getBody();

        return vulnerabilityCatalog;
    }

    @Override
    public HumanVulnerabilityCatalog getHumanVulnerabilityCatalog(String snapshotId) {
        ResponseEntity<HumanVulnerabilityCatalog> responseEntity = restTemplate.exchange(
                humanVulnerabilityCatalogURL + '/' + snapshotId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<HumanVulnerabilityCatalog>() {
                });
        HumanVulnerabilityCatalog humanVulnerabilityCatalog = responseEntity.getBody();

        return humanVulnerabilityCatalog;
    }

    @Override
    public ConfigurationSpecification getConfigurationSpecificationFile(String snapshotId) {
        ResponseEntity<ConfigurationSpecification> responseEntity = restTemplate.exchange(
                configurationSpecificationURL + '/' + snapshotId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<ConfigurationSpecification>() {
                });
        ConfigurationSpecification configurationSpecification = responseEntity.getBody();

        return configurationSpecification;
    }
}
