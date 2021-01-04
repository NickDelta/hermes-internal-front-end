package org.hua.hermes.frontend.converter;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;


/**
 * Vaadin converter that converts between LocalDate and String
 * @author <a href="mailto:nikosdelta@protonmail.com">Nick Dimitrakopoulos</a>
 */
public class LocalDateToStringConverter implements Converter<LocalDate, String>
{

	private final String format;

	public LocalDateToStringConverter(String format)
	{
		this.format = format;
	}

	@Override
	public Result<String> convertToModel(LocalDate value, ValueContext context)
	{
		if (value == null) {
			return Result.ok(null);
		}

		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return Result.ok(value.format(formatter));
	}

	@Override
	public LocalDate convertToPresentation(String value, ValueContext context)
	{
		if (value == null) {
			return null;
		}

		// Remove leading and trailing whitespace
		value = value.trim();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return LocalDate.parse(value, formatter);
	}
}
