package org.panacea.drmp.lae.domain.query.age.response;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AGESourceTargetQueryResponseDeserializer extends StdDeserializer<AGESourceTargetQueryResponse> {
    private static final long serialVersionUID = 1L;

    public AGESourceTargetQueryResponseDeserializer() {
        super(AGESourceTargetQueryResponseDeserializer.class);
    }

    protected AGESourceTargetQueryResponseDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public AGESourceTargetQueryResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        AGESourceTargetQueryResponse ageSourceTargetQueryResponse = new AGESourceTargetQueryResponse();
        TreeNode tn = p.readValueAsTree();
        JsonNode pathsNode = (JsonNode) tn.get("paths");
        for (JsonNode attackPathNode : pathsNode) {
            AttackPath attackPath = new AttackPath();
            for (JsonNode attackStepNode : attackPathNode) {
                AttackStep step = new AttackStep();
                step.setSource(attackStepNode.get(0).toString().replace("\"", ""));
                List<String> vulnList = new ArrayList<>();
                for (JsonNode vulnNode : attackStepNode.get(1)) {
                    vulnList.add(vulnNode.toString().replace("\"", ""));
                }
                step.setVulnerabilityList(vulnList);
                step.setDestination(attackStepNode.get(2).toString().replace("\"", ""));
                attackPath.addAttackStep(step);
            }
            ageSourceTargetQueryResponse.addPath(attackPath);
        }
        return ageSourceTargetQueryResponse;
    }
}
