
package org.panacea.drmp.lae.domain.query.input;

import lombok.Data;

@Data
@SuppressWarnings("unused")
public class Source {

    private String sourceId;
    private String sourceType;
    private Double sourceProbability;

    public Source() {
    }

    public Source(String sourceId, String sourceType) {
        this.sourceId = sourceId;
        this.sourceType = sourceType;
        this.sourceProbability = 1.0;
    }

}
