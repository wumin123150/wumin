package com.wumin.common.concurrent;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import com.wumin.common.concurrent.jsr166e.LongAdder;

public class ConcurrentToolsTest {

	@Test
	public void longAdder() {
		LongAdder counter = ConcurrentTools.longAdder();
		counter.increment();
		counter.add(2);
		assertThat(counter.longValue()).isEqualTo(3L);
	}

}
