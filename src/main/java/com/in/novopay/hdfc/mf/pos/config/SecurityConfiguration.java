package com.in.novopay.hdfc.mf.pos.config;


import com.in.novopay.hdfc.mf.pos.filter.PayloadFilter;
import com.in.novopay.hdfc.mf.pos.payload.AesUtil;
import com.in.novopay.hdfc.mf.pos.util.RSAEcbUtil;
import jakarta.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SecurityConfiguration {
    // Your security configuration

    @Autowired
    //RSAUtil rsaUtil;
    //RSAECBOAEPUtil rsaUtil;
    RSAEcbUtil rsaUtil;

    @Autowired
    AesUtil aesUtil;

    private static final String URL_PATTERN_ALL = "/*";

    private <T extends Filter> FilterRegistrationBean<T> createFilterRegistrationBean(T filter, String urlPattern, int order) {
        FilterRegistrationBean<T> registrationBean = new FilterRegistrationBean<>();
        String name = filter.getClass().getName();
        registrationBean.setName(name);
        registrationBean.setFilter(filter);
        registrationBean.setOrder(order);
        registrationBean.addUrlPatterns(urlPattern);
        log.info("Creating FilterRegistrationBean with name: {}, order: {}, URL Pattern: {}", name, order, urlPattern);
        return registrationBean;
    }

    @Bean
    FilterRegistrationBean<PayloadFilter> sanitizeFilterRegistrationBean() {
        return createFilterRegistrationBean(new PayloadFilter(rsaUtil, aesUtil), URL_PATTERN_ALL, 1);
    }
}
