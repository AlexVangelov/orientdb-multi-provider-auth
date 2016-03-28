/**
  * @author Alex Vangelov (email--at--data.bg)
  *
  */
package orientdb.multiauth;

import com.orientechnologies.orient.server.config.OServerCommandConfiguration;
import com.orientechnologies.orient.server.config.OServerEntryConfiguration;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequest;
import com.orientechnologies.orient.server.network.protocol.http.OHttpResponse;
import com.orientechnologies.orient.server.network.protocol.http.command.OServerCommandAbstract;

public class OServerCommandMultiauth extends OServerCommandAbstract {
	private static final String[] NAMES = { "GET|multiauth/*" };

	@SuppressWarnings("unused") //TODO implement user identities
  private String dbUsername;
	@SuppressWarnings("unused")
  private String dbPassword;

	public OServerCommandMultiauth(final OServerCommandConfiguration iConfiguration) {
		for (OServerEntryConfiguration par : iConfiguration.parameters) {
			if (par.name.equals("oauthUser")) {
				dbUsername = par.value;
			} else if (par.name.equals("oauthPassword")) {
				dbPassword = par.value;
			}
		}
	}

	@Override
	public boolean execute(final OHttpRequest iRequest, OHttpResponse iResponse) throws Exception {
		String[] urlParts = checkSyntax(iRequest.url, 3, "Syntax error: multiauth/<database>/:provider");
		
		String providerName = urlParts[2];
		
		iRequest.data.commandInfo = "OAuth";
		OAuthProviderInterface provider = OAuthProvidersManager.get(providerName);
		provider.setCallbackUrl("http://localhost:2480/multiauth/wef/"+providerName+"/callback"); //TODO replace me / extract host
		
		if (urlParts.length == 3) {
			iRequest.data.commandDetail = "Authentication request for " + providerName;
			provider.authRequest(iRequest, iResponse);
		} else if (urlParts.length > 3 && urlParts[3].equals("callback")) {
			iRequest.data.commandDetail = "Callback for " + providerName;
			provider.callback(iRequest, iResponse);
		}
		return false;
	}

	@Override
	public String[] getNames() {
		return NAMES;
	}

}
