package dev.elysion.fwa.converter;

import org.junit.Test;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class ConverterTest {

	@Test
	public void localDate_sqlDate() {
		Date expected = Date.valueOf("2018-03-05");
		Date target = Converter.convert(LocalDate.of(2018, 3, 5));
		assertEquals(expected, target);
	}

	@Test
	public void sqlDate_localDate() {
		LocalDate expected = LocalDate.of(2017, 5, 6);
		LocalDate target = Converter.convert(Date.valueOf("2017-05-06"));
		assertEquals(expected, target);
	}
}
