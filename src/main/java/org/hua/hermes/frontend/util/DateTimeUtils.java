package org.hua.hermes.frontend.util;

import com.vaadin.flow.server.VaadinSession;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtils
{
    public static String formatDate(FormatStyle style, TemporalAccessor date){
        return DateTimeFormatter
                .ofLocalizedDate(style)
                .withLocale(VaadinSession.getCurrent().getLocale())
                .withZone(VaadinSession.getCurrent().getAttribute(ZoneId.class))
                .format(date);
    }

    public static String formatDateTime(FormatStyle style, TemporalAccessor date){
        return DateTimeFormatter
                .ofLocalizedDateTime(style)
                .withLocale(VaadinSession.getCurrent().getLocale())
                .withZone(VaadinSession.getCurrent().getAttribute(ZoneId.class))
                .format(date);
    }

    public static String formatDateTime(Date date){
        DateFormat f = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, VaadinSession.getCurrent().getLocale());
        String pattern = ((SimpleDateFormat)f).toPattern();

        SimpleDateFormat formatter = new SimpleDateFormat(pattern,VaadinSession.getCurrent().getLocale());
        formatter.setTimeZone(TimeZone.getTimeZone(VaadinSession.getCurrent().getAttribute(ZoneId.class)));
        return formatter.format(date);
    }

    public static String getOffsetString(LocalDateTime dateTime, ZoneId id) {
        return dateTime
                .atZone(id)
                .getOffset()
                .getId()
                .replace("Z", "+00:00");

    }
}
