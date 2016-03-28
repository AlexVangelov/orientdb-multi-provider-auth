/**
  * @author Alex Vangelov (email--at--data.bg)
  *
  */
package orientdb.multiauth;

import java.io.IOException;

import com.orientechnologies.orient.server.network.protocol.http.OHttpRequest;
import com.orientechnologies.orient.server.network.protocol.http.OHttpResponse;

public interface OAuthProviderInterface {

	void setCallbackUrl(String string);
	
	void authRequest(OHttpRequest iRequest, OHttpResponse iResponse) throws IOException;

	void callback(OHttpRequest iRequest, OHttpResponse iResponse) throws IOException;

}
