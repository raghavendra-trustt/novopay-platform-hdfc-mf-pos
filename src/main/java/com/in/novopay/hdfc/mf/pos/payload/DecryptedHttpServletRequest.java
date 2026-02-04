package com.in.novopay.hdfc.mf.pos.payload;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class DecryptedHttpServletRequest extends HttpServletRequestWrapper {
    private final byte[] decryptedPayload;


    public DecryptedHttpServletRequest(HttpServletRequest request, byte[] decryptedPayload) {
        super(request);
        this.decryptedPayload = decryptedPayload;
    }

    @Override
    public String getHeader(String name) {
        if (decryptedPayload != null && name.equalsIgnoreCase("Content-Length")) {
            return decryptedPayload.length + "";
        } else {
            return super.getHeader(name);
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decryptedPayload);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }
}
