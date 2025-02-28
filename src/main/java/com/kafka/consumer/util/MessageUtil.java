package com.kafka.consumer.util;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

@Component
public class MessageUtil {

    private Properties msgProperties;

    public MessageUtil() {

        msgProperties = new Properties();
        try {
            msgProperties.load(getClass().getResourceAsStream("/message.properties"));
        } catch (final IOException ex) {
            Util.exceptionToString(ex);
        }
    }

    /**
     * This method is used for Get the Value w.r.t Key.
     *
     * @param key
     * @return
     */
    public String getBundle(final String key) {

        return msgProperties.getProperty(key);
    }
}
