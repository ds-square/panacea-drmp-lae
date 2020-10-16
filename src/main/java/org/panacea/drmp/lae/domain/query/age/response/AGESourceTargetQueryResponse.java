
package org.panacea.drmp.lae.domain.query.age.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@SuppressWarnings("unused")
@JsonDeserialize(using = AGESourceTargetQueryResponseDeserializer.class)
public class AGESourceTargetQueryResponse {

    private List<AttackPath> paths;

    public AGESourceTargetQueryResponse() {
        this.paths = new ArrayList<>();
    }

    public void addPath(AttackPath path) {
        this.paths.add(path);
    }

}
