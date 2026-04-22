package com.in.novopay.hdfc.mf.pos.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.in.novopay.hdfc.mf.pos.payload.AesUtil;
import com.in.novopay.hdfc.mf.pos.payload.DecryptedHttpServletRequest;
import com.in.novopay.hdfc.mf.pos.util.LogUtility;
import com.in.novopay.hdfc.mf.pos.util.RSAEcbUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class PayloadFilter implements Filter {

    //private final RSAUtil rsaUtil;
    private final RSAEcbUtil rsaUtil;
    private final AesUtil aesUtil;

    public PayloadFilter(RSAEcbUtil rsaUtil, AesUtil aesUtil) {
        this.rsaUtil = rsaUtil;
        this.aesUtil = aesUtil;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest wrappedRequest = (HttpServletRequest) servletRequest;
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper((HttpServletResponse) servletResponse);
        String uri = wrappedRequest.getRequestURI();
        Map<String,String> configMap = new HashMap<>();
        configMap.put("uri", uri);
        if("POST".equalsIgnoreCase(wrappedRequest.getMethod()) && uri.startsWith("/mp63/txn")) {
            String encryptedSecretKey = wrappedRequest.getHeader("X-skey");
            String clientIV = wrappedRequest.getHeader("X-iv");
            byte[] decryptedKey = null;
            try {
               decryptedKey = rsaUtil.decrypt(encryptedSecretKey);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            //log.info("decryptedKey : {}", new String(Base64.getEncoder().encode(decryptedKey)));
            byte[] decryptedIv = null;
            try {
                decryptedIv = rsaUtil.decrypt(clientIV);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            //log.info("decryptedIv : {}", new String(Base64.getEncoder().encode(decryptedIv)));
            // Get the request payload
            byte[] payload = wrappedRequest.getInputStream().readAllBytes();

            String decryptedRequestData = null;
            // Decrypt the payload
            try {
                aesUtil.initFromStrings(new String(Base64.getEncoder().encode(decryptedKey)), new String(Base64.getEncoder().encode(decryptedIv)));
                decryptedRequestData = aesUtil.decrypt(new String(payload));
                extractRequestParams(decryptedRequestData, configMap);
                //log.info("decryptedRequestData : {}", decryptedRequestData);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            wrappedRequest = new DecryptedHttpServletRequest(wrappedRequest, decryptedRequestData.getBytes());
            filterChain.doFilter(wrappedRequest, wrappedResponse);

            if(rsaUtil.getPrivateKey() != null) {
                String finalResponseBody = null;
                // encrypt the response
                try {
                    String responseBody = IOUtils.toString(wrappedResponse.getContentInputStream(), UTF_8);
                    //log.info("actual response : {}", responseBody);
                    finalResponseBody = aesUtil.encrypt(responseBody);
                    wrappedResponse.reset();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                writeResponse(wrappedResponse, finalResponseBody);
                wrappedResponse.copyBodyToResponse();
            }
        } else {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
            wrappedResponse.copyBodyToResponse();
        }
        LogUtility.shipAndClearLogFile(configMap);
        MDC.clear();
    }

    private Map<String,String> extractRequestParams(String decryptedRequestData, Map<String,String> configMap) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = null;
        try {
            // userid,terminalId,txnRef#,flagToShipLogs
            root = mapper.readTree(decryptedRequestData);
            String userId = root.path("userId").asText();
            String terminalId = root.path("terminalId").asText();
            String txnRefId = root.path("txnRefId").asText();
            String captureDeviceLogs = root.path("captureDeviceLogs").asText();
            String baseUrl = root.path("baseUrl").asText();
            String tenantCode = root.path("tenantCode").asText();
            MDC.put("txnId",txnRefId);
            MDC.put("terminalId",terminalId);
            MDC.put("apiName",fetchApiName(configMap));
            configMap.put("txn_id",txnRefId);
            configMap.put("terminal_id",terminalId);
            configMap.put("capture_device_logs",captureDeviceLogs);
            configMap.put("user_id",userId);
            configMap.put("base_url",baseUrl);
            configMap.put("tenant_code",tenantCode);
        } catch (JsonProcessingException e) {
            log.error("error while extracting request params: ",e);
        }
        return configMap;
    }

    private static String fetchApiName(Map<String, String> configMap) {
        String path = configMap.get("uri");
        return StringUtils.isNotBlank(path) ? path.substring(path.lastIndexOf('/') + 1) : "";
    }

    private void writeResponse(ContentCachingResponseWrapper response, String responseBody) throws IOException {
        response.setContentLength(responseBody.length());
        response.setContentType("text/plain");
        response.getWriter().write(responseBody);
        response.setHeader("Access-Control-Allow-Origin", "*");
    }
}
