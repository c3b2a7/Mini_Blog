package me.lolico.blog.web.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lolico
 */
@Slf4j
public class StringToDateConverter implements Converter<String, Date> {
    protected static Date parse(String pattern, String source) throws ParseException {
        return new SimpleDateFormat(pattern).parse(source);
    }

    private static String format(String pattern, Date date) {
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * Convert the source object of type {@code S} to target type {@code T}.
     *
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     */
    @Override
    public Date convert(String source) {
        if (!StringUtils.hasText(source)) {
            return null;
        }
        source = source.trim();
        try {
            if (source.matches("\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}")) {
                return StringToDateConverter.parse("yyyy-MM-dd HH:mm:ss", source);
            } else if (source.matches("\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}")) {
                return StringToDateConverter.parse("yyyy/MM/dd HH:mm:ss", source);
            } else if (source.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
                return StringToDateConverter.parse("yyyy-MM-dd", source);
            } else if (source.matches("\\d{4}/\\d{1,2}/\\d{1,2}")) {
                return StringToDateConverter.parse("yyyy/MM/dd", source);
            } else {
                throw new IllegalArgumentException(source);
            }
        } catch (ParseException ex) {
            log.info("Cant convert: {}", source);
            throw new RuntimeException(ex);
        }
    }
}
