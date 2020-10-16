package org.panacea.drmp.lae.service;

import com.google.common.collect.HashBasedTable;
import org.panacea.drmp.lae.domain.notification.DataNotification;
import org.panacea.drmp.lae.domain.query.age.response.AGESourceTargetQueryResponse;

public interface LAEOutputRequestService {

    void postQueryOutputFile(DataNotification notification, HashBasedTable pathWithLikelihood);

    void postAGEQueryOutputFile(DataNotification notification, AGESourceTargetQueryResponse path);
}
