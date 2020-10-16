package org.panacea.drmp.lae.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.panacea.drmp.lae.LAECalculator;
import org.panacea.drmp.lae.domain.exception.LAEException;
import org.panacea.drmp.lae.domain.notification.DataNotification;
import org.panacea.drmp.lae.domain.notification.DataNotificationResponse;
import org.panacea.drmp.lae.service.OrchestratorNotificationHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Service
public class OrchestratorNotificationHandlerServiceImpl implements OrchestratorNotificationHandlerService {
    public static final String INVALID_NOTIFICATION_ERR_MSG = "Invalid Data Notification Body.";

    @Autowired
    LAECalculator laeCalculator;

    @Override
    @ResponseBody
    public DataNotificationResponse perform(DataNotification notification) throws LAEException {
//        log.info("Received Data Notification from Orchestrator: {}", notification);
        log.info("[LAE] Received notification from Risk Estimation Engine (REE)");
        try {
            if (notification.getEnvironment() == null) {
                throw new LAEException("No environment defined for notification.");
            }
            laeCalculator.computeLikelihood(notification);

            return new DataNotificationResponse(notification.getEnvironment(), notification.getSnapshotId(), notification.getSnapshotTime());
        } catch (LAEException e) {
            log.error("LAEException occurred: ", e);
            throw new LAEException(INVALID_NOTIFICATION_ERR_MSG, e);
        }
    }
}

