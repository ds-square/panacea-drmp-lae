package org.panacea.drmp.lae;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.panacea.drmp.lae.domain.attackGraph.privLevel.HumanPrivLevel;
import org.panacea.drmp.lae.domain.attackGraph.privLevel.NetworkPrivLevel;
import org.panacea.drmp.lae.domain.businessEntity.BusinessEntityInventory;
import org.panacea.drmp.lae.domain.businessEntity.BusinessEntityType;
import org.panacea.drmp.lae.domain.businessNetworkMapping.BusinessNetworkMapping;
import org.panacea.drmp.lae.domain.configFiles.Attacker;
import org.panacea.drmp.lae.domain.configFiles.AttackerType;
import org.panacea.drmp.lae.domain.configFiles.ConfigurationSpecification;
import org.panacea.drmp.lae.domain.exception.LAEException;
import org.panacea.drmp.lae.domain.notification.DataNotification;
import org.panacea.drmp.lae.domain.query.age.AGESourceTargetQuery;
import org.panacea.drmp.lae.domain.query.age.response.AGESourceTargetQueryResponse;
import org.panacea.drmp.lae.domain.query.age.response.AttackPath;
import org.panacea.drmp.lae.domain.query.age.response.AttackStep;
import org.panacea.drmp.lae.domain.query.input.QueryInput;
import org.panacea.drmp.lae.domain.query.input.Source;
import org.panacea.drmp.lae.domain.serviceLevel.Dependency;
import org.panacea.drmp.lae.domain.serviceLevel.ServiceLevelInventory;
import org.panacea.drmp.lae.domain.vulnerability.human.HumanVulnerability;
import org.panacea.drmp.lae.domain.vulnerability.human.HumanVulnerabilityCatalog;
import org.panacea.drmp.lae.domain.vulnerability.network.Vulnerability;
import org.panacea.drmp.lae.domain.vulnerability.network.VulnerabilityCatalog;
import org.panacea.drmp.lae.service.AGEQueryService;
import org.panacea.drmp.lae.service.LAEInputRequestService;
import org.panacea.drmp.lae.service.LAEOutputRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class LAECalculator {

    @Autowired
    LAEInputRequestService laeInputRequestService;
    @Autowired
    LAEOutputRequestService laeOutputRequestService;
    @Autowired
    AGEQueryService ageQueryService;

    private BusinessEntityInventory businessEntityInventory;
    private ServiceLevelInventory serviceLevelInventory;
    private BusinessNetworkMapping businessNetworkMapping;
    private VulnerabilityCatalog vulnerabilityCatalog;
    private HumanVulnerabilityCatalog humanVulnerabilityCatalog;
    private ConfigurationSpecification configurationSpecification;
    private QueryInput queryInput;

    private Table<String, AttackerType, Double> vuln2exitRate;
    private Map<String, NodeType> node2type;
    private Map<String,Double> srcProbability;

    @Synchronized
    public void computeLikelihood(DataNotification notification) {
        try {
            this.getInput(notification);
        } catch (LAEException e) {
            log.error(e.getMessage());
        }

        List<String> targetList = new ArrayList<>();
        List<String> sourceStringList = new ArrayList<>();
        List<Source> sourceList = queryInput.getSources();
        for (Source s : sourceList) {
            String sourceId = s.getSourceId();
            if (sourceId.contains("@")) {
                sourceStringList.add(sourceId);
            } else {
                Double srcProb = this.srcProbability.get(sourceId);
                if (s.getSourceType().equalsIgnoreCase("human")) {
                    sourceStringList.add("OWN@" + sourceId);
                    this.srcProbability.put("OWN@" + sourceId, srcProb);
                    sourceStringList.add("USE@" + sourceId);
                    this.srcProbability.put("USE@" + sourceId, srcProb);
                    sourceStringList.add("EXECUTE@" + sourceId);
                    this.srcProbability.put("EXECUTE@" + sourceId, srcProb);
                } else if (s.getSourceType().equalsIgnoreCase("network")) {
                    sourceStringList.add("ROOT@" + sourceId);
                    this.srcProbability.put("ROOT@" + sourceId, srcProb);
                    sourceStringList.add("USER@" + sourceId);
                    this.srcProbability.put("USER@" + sourceId, srcProb);
                    sourceStringList.add("NONE@" + sourceId);
                    this.srcProbability.put("NONE@" + sourceId, srcProb);
                }
            }
        }

        if (queryInput.getType().equalsIgnoreCase("business")) {
            targetList = computeAsset(queryInput.getTargetIds());
        } else if (queryInput.getType().equalsIgnoreCase("asset")) {
            for (String targetId : queryInput.getTargetIds()) {
                if (targetId.contains("@")) {
                    targetList.add(targetId);
                } else {
                    targetList.add("ROOT@" + targetId);
                    targetList.add("USER@" + targetId);
                    targetList.add("NONE@" + targetId);
                }
            }
        }

        AGESourceTargetQuery ageSourceTargetQuery = new AGESourceTargetQuery(sourceStringList, targetList);
        AGESourceTargetQueryResponse ageSourceTargetQueryAnswer = ageQueryService.performAGEquery(ageSourceTargetQuery);

        this.mapNodes(ageSourceTargetQueryAnswer); // populates this.node2type


        // compute the likelihood for each attackPath and attacker.
        // the markov chain is represented as a list of doubles, the exit rates
        List<Double> markovChain;
        double likelihood, currentLik;
        HashBasedTable<String, AttackerType, Double> target2likelihood = HashBasedTable.create();
        for (AttackPath ap : ageSourceTargetQueryAnswer.getPaths()) {
            String target = ap.getTarget();
            HashMap<AttackerType, Double> likelihoodMap = new HashMap<>();
            for (Attacker a : this.configurationSpecification.getAttackers()) {
                markovChain = this.buildMarkovChain(ap, a);
                likelihood = this.computePathLikelihood(markovChain);
                ap.addLikelihood(a.getAttackerType(), likelihood);
                if (target2likelihood.get(target, a.getAttackerType()) == null) {
                    currentLik = 0.0;
                } else if (target2likelihood.get(target, a.getAttackerType()).isNaN()) {
                    currentLik = 0.0;
                } else {
                    currentLik = target2likelihood.get(target, a.getAttackerType());
                }
                Double newLikelihood = 0.0;
                Double maxLikelihood = Math.max(currentLik, likelihood);
                if (!maxLikelihood.isNaN()) {
                    newLikelihood = maxLikelihood;
                }
                target2likelihood.put(target, a.getAttackerType(), newLikelihood);
                likelihoodMap.put(a.getAttackerType(), newLikelihood);
//                log.info(currentLik + " - " + likelihood);
            }
            ap.setLikelihood(likelihoodMap);
        }
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(target2likelihood);
//            log.info(json);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
        this.laeOutputRequestService.postAGEQueryOutputFile(notification, ageSourceTargetQueryAnswer);
        this.laeOutputRequestService.postQueryOutputFile(notification, target2likelihood);
    }

    private void getInput(DataNotification notification) {
        this.businessEntityInventory = laeInputRequestService.getBusinessEntityInventoryFile(notification.getSnapshotId());
        log.info("[LAE] GET BusinessEntityInventory from http://172.16.100.131:8107/business-layer/businessEntityInventory");
        this.serviceLevelInventory = laeInputRequestService.getServiceLevelInventoryFile(notification.getSnapshotId());
        log.info("[LAE] GET ServiceLevelInventory from http://172.16.100.131:8107/business-layer/serviceLevelInventory");
        this.businessNetworkMapping = laeInputRequestService.getBusinessNetworkMappingFile(notification.getSnapshotId());
        log.info("[LAE] GET BusinessNetworkMapping from http://172.16.100.131:8107/business-layer/businessNetworkMapping");
        this.vulnerabilityCatalog = laeInputRequestService.getVulnerabilityCatalog(notification.getSnapshotId());
        log.info("[LAE] GET VulnerabilityCatalog from http://172.16.100.131:8096/aggregator/vulnerabilityInventory");
        this.humanVulnerabilityCatalog = laeInputRequestService.getHumanVulnerabilityCatalog(notification.getSnapshotId());
        log.info("[LAE] GET humanVulnerabilityCatalog from http://172.16.100.131:8102/human/humanVulnerabilityInventory");
        this.configurationSpecification = laeInputRequestService.getConfigurationSpecificationFile(notification.getSnapshotId());
        log.info("[LAE] GET attacker parameters from http://172.16.100.131:8107/business-layer/config/");

        //TODO improve vulnerability map creation with custom deserializer
        this.vulnerabilityCatalog.computeHumanVulnerabilityMap();
        this.humanVulnerabilityCatalog.computeHumanVulnerabilityMap();
        this.serviceLevelInventory.computeServiceLevelMap();
        this.businessNetworkMapping.computeMappingMap();

        this.queryInput = laeInputRequestService.getQueryInputFile(notification);
        log.info("[LAE] GET Query with ID \"" + notification.getQueryId() + "\" from http://172.16.100.131:8108/persistence/query/input");
        this.srcProbability = new HashMap<>();
        for (Source s : this.queryInput.getSources()) {
            Double prob = s.getSourceProbability();
            if (prob == null) {
                prob = 1.0;
            }
            this.srcProbability.put(s.getSourceId(), prob);
        }
        this.vuln2exitRate = HashBasedTable.create();
        this.node2type = new HashMap<>();

    }

    private List<String> computeAsset(List<String> serviceLevelList) {
        Set<String> serviceLevelSet = new HashSet<>();
        for (String slId : serviceLevelList) {
//            Dependency dep = this.slInventory.getSL(slId).dependency;
            Dependency dep = this.serviceLevelInventory.getServiceLevelById(slId).getDependency();
            ArrayList<String> route = new ArrayList<>();
            route.add(slId);
            serviceLevelSet.addAll(this.getAssetsFromBP(dep, route));
        }
        List<String> assetList = new ArrayList<>();
        for (String slId : serviceLevelSet) {
            Map<String, String> privNodeMap = this.businessNetworkMapping.getMappingById(slId);
            for (String assetId : privNodeMap.values()) {
                assetList.add(assetId);
            }
        }
        return assetList;
    }

    private Set<String> getAssetsFromBP(Dependency dep, ArrayList<String> route) {
        Set<String> assets = new HashSet<>();
        route = (ArrayList<String>) route.clone();
        if (dep != null) {
            if (dep.getDependencyType().equals("ServiceLevelDependencyNode")) {
                String slId = dep.getServiceLevelId();
                if (!route.contains(slId)) { // cycle detection
                    route.add(slId);
                    BusinessEntityType slType = this.businessEntityInventory.getSlType(slId);
                    if (slType == BusinessEntityType.ASSET) assets.add(slId);
                    Dependency nextDep = this.serviceLevelInventory.getServiceLevelById(slId).getDependency();
                    assets.addAll(this.getAssetsFromBP(nextDep, route));
                }
//        } else if (dep.getDependencyType().equals("MultiDependencyNode")) {
            } else {
                for (Dependency nextDep : dep.getDependencies()) {
                    assets.addAll(this.getAssetsFromBP(nextDep, route));
                }
            }
        }
        return assets;
    }


    //TODO improve mapNodes building
    private void mapNodes(AGESourceTargetQueryResponse ageSourceTargetQueryAnswer) {
        for (AttackPath ap : ageSourceTargetQueryAnswer.getPaths()) {
            for (AttackStep step : ap.getSteps()) {
                String source = step.getSource();
                if (!this.node2type.containsKey(step.getSource())) {
                    this.node2type.put(source, this.getNodeType(source));
                }
            }
            String target = ap.getTarget();
            if (!this.node2type.containsKey(target)) {
                this.node2type.put(target, this.getNodeType(target));
            }
        }
    }

    private double computePathLikelihood(List<Double> markovChain) {
        double likelihood;
        double MTAO = 0;
        for (Double exitRate : markovChain) {
            if (exitRate == 0) return 0;
            MTAO += 1 / exitRate;
        }
        MTAO = (MTAO - 1) / MTAO;
        likelihood = (-20) * Math.log10(MTAO);
        return likelihood;
    }

    private List<Double> buildMarkovChain(AttackPath ap, Attacker a) {
        List<Double> markovChain = new ArrayList<>();
        List<String> vulns;
        Double srcProb = this.srcProbability.get(ap.getSource());
        if (srcProb == 0.0) return markovChain;
        if (srcProb < 0.0) markovChain.add(srcProb);
        for (AttackStep step : ap.getSteps()) {
            if (this.node2type.get(step.getDestination()) == NodeType.HUMAN) {                // H -> H
                vulns = step.getVulnerabilityList();
                markovChain.add(this.computeExitRateHuman(vulns));
            } else if (this.node2type.get(step.getDestination()) == NodeType.ACCESS ||
                    this.node2type.get(step.getSource()) == NodeType.ACCESS) {            // A
                // Note that in a full AP we'll get "1.0" two times, is this correct?
                markovChain.add(1.0);
            } else if (this.node2type.get(step.getSource()) == NodeType.NETWORK) {        // N -> N
                vulns = step.getVulnerabilityList();
                markovChain.add(this.computeExitRateNetwork(vulns, a));
            }
        }
        return markovChain;
    }


    private NodeType getNodeType(String id) {
        //TODO modify node type recognition
        String nodePrefix = id.split("@")[0];
        if (HumanPrivLevel.contains(nodePrefix)) return NodeType.HUMAN;
        if (NetworkPrivLevel.contains(nodePrefix)) return NodeType.NETWORK;
        return NodeType.ACCESS;
    }

    private double computeExitRateNetwork(List<String> vulnerabilities, Attacker a) {
        List<Double> aThresholds = a.getThresholdsAsList();
        double stepExitRate = 0;
        Double vulnExitRate;
        for (String vulnId : vulnerabilities) {
            vulnExitRate = this.vuln2exitRate.get(vulnId, a.getAttackerType()); // optimization
            if (vulnExitRate == null) {
                vulnExitRate = 1.0;
                Vulnerability v = this.vulnerabilityCatalog.getVulnerabilityById(vulnId);
                List<Double> metrics = v.getMetricsAsList();
                double m;
                for (int i = 0; i < 5; i++) {
                    m = metrics.get(i);
                    if (aThresholds.get(i) > m) {
                        vulnExitRate = 0.0;
                        break;
                    }
                    vulnExitRate *= m;
                }
            }
            if (vulnExitRate > stepExitRate) {
                // if there are multiple vulns, I take the easiest to exploit
                stepExitRate = vulnExitRate;
            }
        }
        return stepExitRate;
    }

    private double computeExitRateHuman(List<String> vulnerabilities) {
        return this.computeExitRateHuman(vulnerabilities, 0.0);
    }

    private double computeExitRateHuman(List<String> vulnerabilities, Double robustness) {
        double pathExitRate = 1;
        for (String vulnId : vulnerabilities) {
            HumanVulnerability v = this.humanVulnerabilityCatalog.getVulnerabilityById(vulnId);
            Double vAC = v.getAttackComplexityScore();
            Double vAV = v.getAccessVectorScore();
            double vulnExitRate = vAC * vAV * (1 - robustness);
            if (vulnExitRate < pathExitRate) {
                // if there are multiple vulns, I take the easiest to exploit
                pathExitRate = vulnExitRate;
            }
        }
        return pathExitRate;
    }

    private enum NodeType {
        HUMAN,
        ACCESS,
        NETWORK
    }

//    private DoubleKeyMap<Double> aggregateLikelihoodForTargets(List<AttackPath> paths) {
//        DoubleKeyMap<Double> target2likelihood = new DoubleKeyMap<>();
//        String target;
//        double currentLik, pathLik;
//        for (AttackPath ap: paths) {
//            target = ap.getTarget();
//            for (AttackerType a: AttackerType.values()) {
//                currentLik = target2likelihood.get(target, a, 0.0);
//                pathLik = ap.likelihood.get(a);
//                target2likelihood.put(target, a, Math.max(currentLik, pathLik));
//            }
//        }
//        return target2likelihood;
//    }
}


