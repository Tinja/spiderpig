package br.ufmg.dcc.vod.spiderpig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import br.ufmg.dcc.vod.spiderpig.common.ServiceIDUtils;
import br.ufmg.dcc.vod.spiderpig.distributed.RemoteMessageSender;
import br.ufmg.dcc.vod.spiderpig.distributed.fd.FDClientActor;
import br.ufmg.dcc.vod.spiderpig.distributed.fd.FDServerActor;
import br.ufmg.dcc.vod.spiderpig.filesaver.FileSaver;
import br.ufmg.dcc.vod.spiderpig.filesaver.FileSaverActor;
import br.ufmg.dcc.vod.spiderpig.master.DecoratorInterested;
import br.ufmg.dcc.vod.spiderpig.master.Master;
import br.ufmg.dcc.vod.spiderpig.master.ResultActor;
import br.ufmg.dcc.vod.spiderpig.master.processor.ProcessorActor;
import br.ufmg.dcc.vod.spiderpig.master.processor.manager.RemoteResolver;
import br.ufmg.dcc.vod.spiderpig.master.processor.manager.Resolver;
import br.ufmg.dcc.vod.spiderpig.master.processor.manager.WorkerManager;
import br.ufmg.dcc.vod.spiderpig.master.processor.manager.WorkerManagerImpl;
import br.ufmg.dcc.vod.spiderpig.master.walker.Walker;
import br.ufmg.dcc.vod.spiderpig.protocol_buffers.Ids.CrawlID;
import br.ufmg.dcc.vod.spiderpig.protocol_buffers.Ids.ServiceID;
import br.ufmg.dcc.vod.spiderpig.queue.QueueService;
import br.ufmg.dcc.vod.spiderpig.worker.WorkerActor;

import com.google.common.cache.Cache;

/**
 * Contains auxiliary methods for starting crawlers.
 * 
 * @author Flavio Figueiredo - flaviovdf 'at' gmail.com
 */
public class CrawlerFactory {

	public static Crawler createDistributedCrawler(String hostname, int port, 
			Set<InetSocketAddress> workerAddrs, File queueDir, FileSaver saver,
			Walker walker, Cache<CrawlID, List<CrawlID>> resultCache) 
					throws FileNotFoundException, IOException {
		
		QueueService service = new QueueService(hostname, port);
		int numThreads = workerAddrs.size();
		Set<ServiceID> workerIDs = new HashSet<>();
		
		ServiceID callBackID = ServiceIDUtils.
				toResolvedServiceID(hostname, port, ResultActor.HANDLE);
		ServiceID fileSaverID = ServiceIDUtils.
				toResolvedServiceID(hostname, port, FileSaverActor.HANDLE);
		
		for (InetSocketAddress socketAddr : workerAddrs) {
			ServiceID workerID = 
					ServiceIDUtils.toResolvedServiceID(
					socketAddr.getHostName(), 
					socketAddr.getPort(), WorkerActor.HANDLE);
			workerIDs.add(workerID);
		}
		
		RemoteMessageSender sender = new RemoteMessageSender();
		
		WorkerManager workerManager = new WorkerManagerImpl(workerIDs);
		Resolver resolver = new RemoteResolver(callBackID, fileSaverID, sender);
		DecoratorInterested workerInterested = new DecoratorInterested();
		
		ProcessorActor processorActor = new ProcessorActor(workerManager, 
				service, resolver, workerInterested, saver, resultCache);
		Master master = new Master(walker, processorActor, 
				workerManager, resultCache);
		
		workerInterested.setLoopBack(master);
		
		ResultActor resultActor = new ResultActor(master);
		FileSaverActor fileSaverActor = new FileSaverActor(saver);
		FDClientActor fd = new FDClientActor(30, 5, TimeUnit.SECONDS, 
				master, sender);
		
		fd.withSimpleQueue(service);
		processorActor.withFileQueue(service, queueDir);
		fileSaverActor.withSimpleQueue(service);
		resultActor.withSimpleQueue(service);
		
		for (ServiceID sid : workerIDs)
			fd.watch(
					ServiceIDUtils
					.toResolvedServiceID(sid.getHostname(), sid.getPort(), 
					 FDServerActor.HANDLE), false);
		
		return new Crawler(processorActor, service, master, resultActor, 
				fileSaverActor, fd, numThreads);
	}
}
