package bluepumpkin.service;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;

import org.junit.Test;

public class ServiceTests {

	@Test
	public void convertingFromLocalDateTimeToDate() {
		LocalDateTime idt = LocalDateTime.of(2015, 3, 1, 9, 30); //(2015, 5, 1, 9, 30)
		
		Date d = Date.from(idt.toInstant(ZoneOffset.of("+01:00")));
		System.out.println(d);
		d = Date.from(idt.toInstant(ZoneOffset.UTC));
		System.out.println(d);
		
		d = Date.from(idt.atZone(ZoneId.systemDefault()).toInstant());
		System.out.println(d);
		d = Date.from(idt.atZone(ZoneId.of("+1")).toInstant());
		System.out.println(d);
		d = Date.from(idt.atZone(ZoneId.of("Z")).toInstant());
		System.out.println(d);
		
		System.out.println(ZoneId.systemDefault());
		Map<String,String> s_ids = ZoneId.SHORT_IDS;
		s_ids.forEach((k,v) -> System.out.println(k + " " + v));	
	}
	
	@Test
	public void convertingFromDateToLocalDateTime() {
		LocalDateTime d = LocalDateTime.from(new Date().toInstant()
				.atZone(ZoneId.systemDefault()));
		System.out.println(d);
	}
	
	@Test
	public void convertingFromLocalDateToDate() {
		LocalDate ld = LocalDate.of(2015, 3, 1);
		Date d = Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
		System.out.println(d);
	}

}
