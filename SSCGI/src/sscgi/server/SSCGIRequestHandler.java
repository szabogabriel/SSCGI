package sscgi.server;

import sscgi.SSCGIMessage;

public interface SSCGIRequestHandler {

	SSCGIMessage handle(SSCGIMessage request);
	
}
