package com.jhome.user.property;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "properties.datetime")
public class DatetimeProperty {

    private final String format;

    public DateTimeFormatter getFormatter() {
        return DateTimeFormatter.ofPattern(format);
    }

    public String formatToString(final LocalDateTime dateTime) {
        return dateTime.format(getFormatter());
    }
}
