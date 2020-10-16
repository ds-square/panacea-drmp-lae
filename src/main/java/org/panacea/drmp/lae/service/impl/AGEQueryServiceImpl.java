package org.panacea.drmp.lae.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.panacea.drmp.lae.domain.query.age.AGESourceTargetQuery;
import org.panacea.drmp.lae.domain.query.age.response.AGESourceTargetQueryResponse;
import org.panacea.drmp.lae.service.AGEQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Slf4j
@Service
public class AGEQueryServiceImpl implements AGEQueryService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${ageQuery.endpoint}")
    private String ageQueryURL;

    @Override
    public AGESourceTargetQueryResponse performAGEquery(AGESourceTargetQuery query) {

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//        Map<String, String> map = new HashMap<>();
//        map.put("sources", query.getSources().toString());
//        map.put("targets", query.getTargets().toString());

        HttpEntity<AGESourceTargetQuery> requestEntity = new HttpEntity<>(query, requestHeaders);
        ResponseEntity<AGESourceTargetQueryResponse> responseEntity = restTemplate.exchange(
                ageQueryURL,
                HttpMethod.POST,
                requestEntity,
                AGESourceTargetQueryResponse.class
        );

        AGESourceTargetQueryResponse response = responseEntity.getBody();
        return response;
    }
}
