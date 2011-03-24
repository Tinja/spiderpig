package br.ufmg.dcc.vod.ncrawler.distributed.common.server;

import org.apache.log4j.Logger;

import br.ufmg.dcc.vod.ncrawler.CrawlJob;
import br.ufmg.dcc.vod.ncrawler.ui.EXIT_CODES;

public class JobExecutorImpl {

	private static final Logger LOG = Logger.getLogger(JobExecutorImpl.class);
	
	private static final long serialVersionUID = 1L;

	public void collect(CrawlJob c) {
		LOG.info("Received JOB");
		c.collect();
		LOG.info("Finished JOB");
	}

	public void kill()  {
		LOG.info("Exiting!");
		System.exit(EXIT_CODES.OK);
	}
}
