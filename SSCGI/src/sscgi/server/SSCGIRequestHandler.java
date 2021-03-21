package sscgi.server;

import sscgi.data.SSCGIMessage;

public interface SSCGIRequestHandler {

	SSCGIMessage handle(SSCGIMessage request);
	
}
