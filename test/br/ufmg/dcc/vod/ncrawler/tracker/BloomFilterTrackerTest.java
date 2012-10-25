package br.ufmg.dcc.vod.ncrawler.tracker;

import junit.framework.Assert;

import org.junit.Test;

public class BloomFilterTrackerTest {

	@Test
	public void testAll() {
		Tracker<String> bf = 
				new BloomFilterTrackerFactory<String>().createTracker(
						String.class);
		
		Assert.assertTrue(bf.crawled("oi"));
		Assert.assertTrue(bf.crawled("tudo"));
		Assert.assertTrue(bf.crawled("bem"));
		
		Assert.assertEquals(3, bf.numCrawled());
		Assert.assertTrue(bf.wasCrawled("oi"));
		Assert.assertTrue(bf.wasCrawled("tudo"));
		Assert.assertTrue(bf.wasCrawled("bem"));
		Assert.assertFalse(bf.wasCrawled("bala"));
		
		Assert.assertFalse(bf.crawled("oi"));
		Assert.assertEquals(3, bf.numCrawled());
	}

}
