package com.wumin.common.misc;

import org.junit.Test;

public class IdGeneratorTest {

	@Test
	public void demo() {
		System.out.println("uuid: " + IdGenerator.uuid());
		System.out.println("uuid2:" + IdGenerator.uuid2());
		System.out.println("randomLong:  " + IdGenerator.randomLong());
		System.out.println("randomBase64:" + IdGenerator.randomBase64(7));
	}
}
