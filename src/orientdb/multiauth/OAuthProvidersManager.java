/**
 * @author Alex Vangelov (email--at--data.bg)
 *
 */
package orientdb.multiauth;

import java.util.HashMap;
import java.util.Map;

import orientdb.multiauth.OAuthProviderInterface;

public class OAuthProvidersManager {
	private static Map<String, Object> providers = new HashMap<String, Object>();

	public static void register(String providerName,
	    OAuthProviderInterface oAuthProvider) {
		providers.put(providerName, oAuthProvider);
	}

	public static OAuthProviderInterface get(String providerName) {
		return (OAuthProviderInterface) providers.get(providerName);
	}

}
