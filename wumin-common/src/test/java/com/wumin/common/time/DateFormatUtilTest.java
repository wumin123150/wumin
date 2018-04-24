package com.wumin.common.time;

import static org.assertj.core.api.Assertions.*;

import java.text.ParseException;
import java.util.Date;

import org.junit.Test;

public class DateFormatUtilTest {

	@Test
	public void isoDateFormat() {
		Date date = new Date(116, 10, 1, 12, 23, 44);
		assertThat(DateFormatUtil.FORMAT_ISO.format(date)).contains("2016-11-01T12:23:44.000");
		assertThat(DateFormatUtil.FORAMT_ISO_ON_SECOND.format(date)).contains("2016-11-01T12:23:44");
		assertThat(DateFormatUtil.FORMAT_SHORT.format(date)).isEqualTo("2016-11-01");
	}

	@Test
	public void defaultDateFormat() {
		Date date = new Date(116, 10, 1, 12, 23, 44);
		assertThat(DateFormatUtil.FORMAT_DEFAULT.format(date)).isEqualTo("2016-11-01 12:23:44.000");
		assertThat(DateFormatUtil.FORMAT_STANDARD.format(date)).isEqualTo("2016-11-01 12:23:44");
	}

	@Test
	public void formatWithPattern() {
		Date date = new Date(116, 10, 1, 12, 23, 44);
		assertThat(DateFormatUtil.formatDate(date, DateFormatUtil.PATTERN_DEFAULT))
				.isEqualTo("2016-11-01 12:23:44.000");
		assertThat(DateFormatUtil.formatDate(date.getTime(), DateFormatUtil.PATTERN_DEFAULT))
				.isEqualTo("2016-11-01 12:23:44.000");
	}

	@Test
	public void parseWithPattern() throws ParseException {
		Date date = new Date(116, 10, 1, 12, 23, 44);
		Date resultDate = DateFormatUtil.parseDate("2016-11-01 12:23:44.000", DateFormatUtil.PATTERN_DEFAULT);
		assertThat(resultDate.getTime() == date.getTime()).isTrue();
	}

	@Test
	public void formatDuration() {
		assertThat(DateFormatUtil.formatDuration(100)).isEqualTo("00:00:00.100");

		assertThat(DateFormatUtil.formatDuration(new Date(100), new Date(3000))).isEqualTo("00:00:02.900");

		assertThat(DateFormatUtil.formatDuration(DateUtil.MILLIS_PER_DAY * 2 + DateUtil.MILLIS_PER_HOUR * 4))
				.isEqualTo("52:00:00.000");

		assertThat(DateFormatUtil.formatDurationOnSecond(new Date(100), new Date(3000))).isEqualTo("00:00:02");

		assertThat(DateFormatUtil.formatDurationOnSecond(2000)).isEqualTo("00:00:02");

		assertThat(DateFormatUtil.formatDurationOnSecond(DateUtil.MILLIS_PER_DAY * 2 + DateUtil.MILLIS_PER_HOUR * 4))
				.isEqualTo("52:00:00");
	}

	@Test
	public void formatFriendlyTimeSpanByNow() throws ParseException {
		try {
			Date now = DateFormatUtil.FORMAT_STANDARD.parse("2016-12-11 23:30:00");

			ClockUtil.useDummyClock(now);

			Date lessOneSecond = DateFormatUtil.FORMAT_DEFAULT.parse("2016-12-11 23:29:59.500");
			assertThat(DateFormatUtil.formatFriendlyTimeSpanByNow(lessOneSecond)).isEqualTo("刚刚");

			Date lessOneMinute = DateFormatUtil.FORMAT_DEFAULT.parse("2016-12-11 23:29:55.000");
			assertThat(DateFormatUtil.formatFriendlyTimeSpanByNow(lessOneMinute)).isEqualTo("5秒前");

			Date lessOneHour = DateFormatUtil.FORMAT_STANDARD.parse("2016-12-11 23:00:00");
			assertThat(DateFormatUtil.formatFriendlyTimeSpanByNow(lessOneHour)).isEqualTo("30分钟前");

			Date today = DateFormatUtil.FORMAT_STANDARD.parse("2016-12-11 1:00:00");
			assertThat(DateFormatUtil.formatFriendlyTimeSpanByNow(today)).isEqualTo("今天01:00");

			Date yesterday = DateFormatUtil.FORMAT_STANDARD.parse("2016-12-10 1:00:00");
			assertThat(DateFormatUtil.formatFriendlyTimeSpanByNow(yesterday)).isEqualTo("昨天01:00");

			Date threeDayBefore = DateFormatUtil.FORMAT_STANDARD.parse("2016-12-09 1:00:00");
			assertThat(DateFormatUtil.formatFriendlyTimeSpanByNow(threeDayBefore)).isEqualTo("2016-12-09");

		} finally {

			ClockUtil.useDefaultClock();
		}
	}

}
