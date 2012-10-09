package br.ufmg.dcc.vod.ncrawler.distributed.rmi.client;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;

import org.junit.Test;

import br.ufmg.dcc.vod.ncrawler.distributed.rmi.client.EvaluatorProxy;
import br.ufmg.dcc.vod.ncrawler.distributed.rmi.client.EvaluatorProxyBuilder;

public class EvaluatorProxyBuilderTest {

	@Test
	public void testAll() 
			throws RemoteException, AlreadyBoundException, MalformedURLException, 
					NotBoundException {
		EvaluatorProxyBuilder<?, ?> f = new EvaluatorProxyBuilder<>(9090);
		
		String[] list = Naming.list("rmi://localhost:9090");
		System.out.println(Arrays.toString(list));
		
		assertTrue(list.length == 0);
		f.createAndBind();

		list = Naming.list("rmi://localhost:9090");
		
		assertTrue(list.length == 1);
		assertTrue(list[0].contains(EvaluatorProxy.NAME));
		System.out.println(Arrays.toString(list));
		
		f.shutdown();
	}
	
}
