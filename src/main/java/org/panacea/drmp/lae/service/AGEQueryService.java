package org.panacea.drmp.lae.service;

import org.panacea.drmp.lae.domain.query.age.AGESourceTargetQuery;
import org.panacea.drmp.lae.domain.query.age.response.AGESourceTargetQueryResponse;

public interface AGEQueryService {
    AGESourceTargetQueryResponse performAGEquery(AGESourceTargetQuery query);
}
