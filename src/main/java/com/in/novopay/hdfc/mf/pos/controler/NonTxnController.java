package com.in.novopay.hdfc.mf.pos.controler;


import com.in.novopay.hdfc.mf.pos.payload.AesUtil;
import com.in.novopay.hdfc.mf.pos.request.CryptoRequest;
import com.in.novopay.hdfc.mf.pos.util.RSAEcbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping(path = "/nontxn")
public class NonTxnController {

    @Autowired
    //RSAUtil rsaUtil;
    //RSAECBOAEPUtil rsaUtil;
    RSAEcbUtil rsaUtil;

    @Autowired
    AesUtil aesUtil;

    @GetMapping("/aesKey")
    public Map<String,String> generateAes() throws Exception {
        return aesUtil.generateAesKeys();
    }

    @GetMapping("/aesEncrypt")
    public String aesEncrypt(@RequestBody CryptoRequest request) throws Exception {
        Map<String, String> keys = aesUtil.getAesKeys();
        aesUtil.initFromStrings(keys.get("secret"), keys.get("iv"));
        return aesUtil.encrypt(request.getData());
    }

    @GetMapping("/aesDecrypt")
    public String aesDecrypt(@RequestBody CryptoRequest request) throws Exception {
        Map<String, String> keys = aesUtil.getAesKeys();
        aesUtil.initFromStrings(keys.get("secret"), keys.get("iv"));
        return aesUtil.decrypt(request.getData());
    }

    @GetMapping("/rsaEncrypt")
    public String rsaEncrypt(@RequestBody CryptoRequest request) throws Exception {
        return Base64.getEncoder().encodeToString(rsaUtil.encrypt(request.getData()));
    }

    @GetMapping("/rsaDecrypt")
    public String rsaDecrypt(@RequestBody CryptoRequest request) throws Exception {
        return new String(rsaUtil.decrypt(request.getData()));
    }
}
