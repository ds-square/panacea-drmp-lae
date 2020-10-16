package org.panacea.drmp.lae.service;

import org.panacea.drmp.lae.domain.notification.DataNotification;
import org.panacea.drmp.lae.domain.notification.DataNotificationResponse;

public interface OrchestratorNotificationHandlerService {

    DataNotificationResponse perform(DataNotification notification);

}
