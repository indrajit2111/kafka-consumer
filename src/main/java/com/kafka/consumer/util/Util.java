package com.kafka.consumer.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

@Component
public class Util {

    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);
    private static final Random RANDOM = new Random();

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    @Lazy
    private MessageSource messageSource;

    /**
     * This method is used to convert Objects using ModelMapper
     *
     * @param from
     * @param valueType
     * @param <T>
     * @return T
     */
    public <T> T transform(final Object from, final Class<T> valueType) {

        @SuppressWarnings("unchecked")
        var trans = (T) Optional.empty();
        if (Objects.nonNull(from)) {
            trans = modelMapper.map(from, valueType);
        }
        return trans;
    }

    /**
     * It is used to print the Root Cause of the Whole Error Stacktrace
     *
     * @param throwable
     * @return String
     */
    public String getCauseMessage(final Throwable throwable) {
        var remarks = throwable.getMessage();
        var check = 0;
        while (!Optional.ofNullable(throwable.getCause()).isEmpty()) {
            remarks = new StringBuilder(remarks + " :: " + throwable.getCause().getLocalizedMessage()).toString();
            if (check > 2) {
                break;
            }
            check += 1;
        }
        return remarks;
    }

    /**
     * This method is used to convert Objects To String using ObjectMapper
     *
     * @param object
     * @return String
     */
    public String objectToString(final Object object) {
        var output = StringUtils.EMPTY;
        try {
            output = objectMapper.writeValueAsString(object);
        } catch (final JsonProcessingException exception) {
            LOGGER.error("Exception in objectToString : {}", getCauseMessage(exception));
        }
        return output;
    }

    /**
     * This method is used to convert String To Object using ObjectMapper
     *
     * @param input
     * @param valueType
     * @param <T>
     * @return T
     */
    public <T> T stringToObject(final String input, final Class<T> valueType) {
        @SuppressWarnings("unchecked")
        var trans = (T) Optional.empty();
        try {
            if (StringUtils.isNotBlank(input)) {
                trans = objectMapper.readValue(input, valueType);
            }
        } catch (final JsonProcessingException exception) {
            LOGGER.error("Exception in stringToObject : {}", getCauseMessage(exception));
        }
        return trans;
    }

    /**
     * This method is used to trim a string to particular length
     *
     * @param input
     * @param length
     * @return String
     */
    public String trimString(final String input, final int length) {
        var result = removeSpecialCharacters(input);
        if (input.length() > length) {
            result = input.substring(0, length);
        }
        return result;
    }

    private String removeSpecialCharacters(final String input) {
        final var stringBuilder = new StringBuilder();
        for (var count = 0; count < input.length(); count++) {
            final var ch = input.charAt(count);
            if (!Character.isHighSurrogate(ch) && !Character.isLowSurrogate(ch)) {
                stringBuilder.append(ch);
            }
        }
        return stringBuilder.toString();
    }

    public static void exceptionToString(final Exception ex) {
        LOGGER.error("Exception in exceptionToString : {}", ExceptionUtils.getStackTrace(ex));
    }

    /**
     * This method is used to Get the Current DateTime in specific format.
     *
     * @return Supplier<String>
     */
    public Supplier<String> getCurrentDateTime() {

        return () -> {
            final DateFormat dateFormat = new SimpleDateFormat(messageUtil.getBundle("datetime.format"));
            final Calendar cal = Calendar.getInstance();
            return dateFormat.format(cal.getTime());
        };
    }

    /**
     * This method is used to Get the OTP.
     *
     * @return Supplier<String>
     */
    public Supplier<String> getOtp() {

        return () -> {
            final int number = RANDOM.nextInt(999999);
            return String.format("%06d", number);
        };
    }
}
