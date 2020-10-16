package org.panacea.drmp.lae.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.HashBasedTable;
import lombok.extern.slf4j.Slf4j;
import org.panacea.drmp.lae.domain.notification.DataNotification;
import org.panacea.drmp.lae.domain.query.age.response.AGESourceTargetQueryResponse;
import org.panacea.drmp.lae.service.LAEOutputRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@Service
public class LAEOutputRequestServiceImpl implements LAEOutputRequestService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${queryOutput.endpoint}")
    private String queryOutputURL;


    @Override
    public void postQueryOutputFile(DataNotification notification, HashBasedTable pathWithLikelihood) {
        ObjectMapper mapper = JsonMapper.builder().addModule(new GuavaModule()).build();
        ResponseEntity<String> response = null;
        try {
            String result = mapper.writeValueAsString(pathWithLikelihood);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity
                    = new HttpEntity<>(result, headers);
            String endPointUrl = queryOutputURL + '/' + notification.getSnapshotId() + "/"+ notification.getQueryId() + "/likelihood";
            log.info("[LAE] POST query likelihood for query ID \"" + notification.getQueryId() + "\" to http://172.16.100.131:8108/persistence/output/likelihood");

            RestTemplate restTemplate = new RestTemplate();
            response = restTemplate
                    .postForEntity(endPointUrl, requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            System.out.println("Response from storage service: " + response);
            byte[] bytes = e.getResponseBodyAsByteArray();
            //Convert byte[] to String
            String s = new String(bytes);
            log.error(s);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void postAGEQueryOutputFile(DataNotification notification, AGESourceTargetQueryResponse path) {

        HttpEntity<AGESourceTargetQueryResponse> requestEntity
                = new HttpEntity<>(path);

        String endPointUrl = queryOutputURL + '/' + notification.getSnapshotId() + '/' + notification.getQueryId() + "/path";
//        log.info("POST query path with id " + notification.getQueryId() + " to " + endPointUrl);
        log.info("[LAE] POST query paths for query ID \"" + notification.getQueryId() + "\" to http://172.16.100.131:8108/persistence/query/output");
        ResponseEntity<String> response = null;
        RestTemplate restTemplate = new RestTemplate();
        try {
            response = restTemplate
                    .postForEntity(endPointUrl, requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            System.out.println("Response from storage service: " + response);
            byte[] bytes = e.getResponseBodyAsByteArray();

            //Convert byte[] to String
            String s = new String(bytes);

            log.error(s);
            e.printStackTrace();
        }

    }
}
