package br.ufmg.dcc.vod.ncrawler.tracker;

import br.ufmg.dcc.vod.ncrawler.common.Constants;

/**
 * Raised to indicate that a {@link TrackerFactory} is unable to build
 * a {@link Tracker}.
 * 
 * @author Flavio Figueiredo - flaviovdf 'at' gmail.com
 */
public class InstatiationException extends RuntimeException {

	private static final long serialVersionUID = Constants.SERIAL_UID;
	
	public InstatiationException(String msg) {
		super(msg);
	}

}
