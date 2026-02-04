package com.in.novopay.hdfc.mf.pos;

import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class LoggingOutputStream extends OutputStream {
    private final Logger logger;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public LoggingOutputStream(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void write(int b) {
        if (b == '\n') {
            flush();
        } else {
            buffer.write(b);
        }
    }

    @Override
    public void flush() {
        String message = buffer.toString().trim();
        if (!message.isEmpty()) {
            logger.error(message);
        }
        buffer.reset();
    }
}

