package org.mconf.bbb.video;

import static org.junit.Assert.*;

import org.junit.Test;

public class IVideoListenerTest {
	@Test
	public void getAspectRatioTest() {
		assertEquals(IVideoListener.getAspectRatio(99, "320x24099"), 320/(float)240, 0.0001);
		assertEquals(IVideoListener.getAspectRatio(24, "320x24024"), 320/(float)240, 0.0001);
		assertEquals(IVideoListener.getAspectRatio(99, "320x24024"), -1, 0.0001);
	}
}
