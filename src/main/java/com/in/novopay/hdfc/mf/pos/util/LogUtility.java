package com.in.novopay.hdfc.mf.pos.util;

import com.in.novopay.hdfc.mf.pos.model.LogShipingRequest;
import com.in.novopay.hdfc.mf.pos.model.ReqHeader;

import com.in.novopay.hdfc.mf.pos.model.ShipLogRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class LogUtility {

    public static void shipAndClearLogFile(Map<String,String> configMap) {

        String filePath = "logs\\app_mp63.log";
        Path path = Paths.get(filePath);

        try {
            String logsFlag = configMap.get("capture_device_logs");
            log.info("base url :{}, Device capture logs flag : {}, uri : {}", configMap.get("base_url"), logsFlag, configMap.get("uri"));
            if(StringUtils.isNotBlank(logsFlag) && logsFlag.equalsIgnoreCase("Y")) {
                // Read file content
                List<String> lines = new ArrayList<>();
                lines.add("PAX Device logs for url : "+ configMap.get("uri"));
                lines.addAll(Files.readAllLines(path));
                shipLogs(lines, configMap);
            }

            // Truncate the file (clear its contents)
            Files.write(path, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            log.error("exception while file truncation : ", e);
        }
    }

    private static void shipLogs(List<String> lines,Map<String,String> configMap) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            LogShipingRequest logRequest = new LogShipingRequest();
            ShipLogRequest reqRequest = getReqRequest(lines);
            ReqHeader reqHeader = getReqHeader(configMap);
            logRequest.setHeaders(reqHeader);
            logRequest.setRequest(reqRequest);

            HttpEntity<LogShipingRequest> entity = new HttpEntity<>(logRequest, headers);
            ResponseEntity<String> response = new RestTemplate().exchange(
                    configMap.get("base_url") + "/api-gateway/api/novopay/v1/shipPaxDeviceLogs", HttpMethod.POST, entity, String.class);
            //log.info("response body=== {}",response.getBody());
        }catch (Exception e){
            log.error("error while shipping logs: ",e);
        }

    }

    private static ReqHeader getReqHeader(Map<String,String> configMap) {
        ReqHeader reqHeader = new ReqHeader();
        reqHeader.setActor_type("CUSTOMER");
        reqHeader.setOperation_mode("SELF");
        reqHeader.setChannel_code("WEB");
        reqHeader.setFunction_sub_code("DEFAULT");
        reqHeader.setFunction_code("DEFAULT");
        reqHeader.setUser_id(configMap.get("user_id"));
        reqHeader.setEnd_channel_code("ADMIN_WEB");
        reqHeader.setRun_mode("REAL");
        Date date = new Date();
        reqHeader.setStan(String.valueOf(date.getTime()));
        reqHeader.setUser_handle_value("bankmaker");
        reqHeader.setClient_ip("127.0.0.1");
        reqHeader.setLocation("44.968046;-94.420307");
        reqHeader.setUser_handle_type("EMAIL");
        reqHeader.setTenant_code(configMap.get("tenant_code"));
        reqHeader.setClient_code("ddp_admin_app");
        reqHeader.setTransmission_datetime(String.valueOf(date.getTime()));
        reqHeader.setApp_version("1.0.12");
        reqHeader.setPlatform("WEB");
        return reqHeader;
    }

    private static ShipLogRequest getReqRequest(List<String> lines){
        ShipLogRequest request = new ShipLogRequest();
        List<String> encodedList = lines.stream()
                .map(line -> Base64.getEncoder().encodeToString(line.getBytes()))
                .collect(Collectors.toList());
        request.setDevice_logs(encodedList);
        return request;
    }
}
