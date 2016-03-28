/**
  * @author Alex Vangelov (email--at--data.bg)
  *
  */
package orientdb.multiauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.config.OServerParameterConfiguration;
import com.orientechnologies.orient.server.network.protocol.http.OHttpRequest;
import com.orientechnologies.orient.server.network.protocol.http.OHttpResponse;
import com.orientechnologies.orient.server.network.protocol.http.OHttpUtils;
import com.orientechnologies.orient.server.plugin.OServerPluginAbstract;

public class OAuth2Provider extends OServerPluginAbstract implements OAuthProviderInterface {
	private String providerName = UUID.randomUUID().toString();
  private String appId;
  private String appSecret;
  private String scope = "profile";
  private String callbackUrl = null;
  private String authUrl;
  private String tokenUrl;
  
	@Override
  public void config(final OServer oServer, final OServerParameterConfiguration[] iParams) {
  	for (OServerParameterConfiguration p : iParams) {
  		if (p.name.equals("name")) {
  			providerName = p.value;
      } else if (p.name.equals("appId")) {
        appId = p.value;
      } else if (p.name.equals("appSecret")) {
      	appSecret = p.value;
      } else if (p.name.equals("scope")) {
        scope = p.value;
      } else if (p.name.equals("customCallbackUrl")) {
      	callbackUrl = p.value;
      } else if (p.name.equals("authUrl")) {
      	authUrl = p.value;
      } else if (p.name.equals("tokenUrl")) {
      	tokenUrl = p.value;
      }
    }
  	OAuthProvidersManager.register(providerName, this);
  }
	
	@Override
  public String getName() {
		return "OAuth2"+providerName+"Provider";
  }

	@Override
	public void setCallbackUrl(String defaultCallbackUrl) {
	  if (callbackUrl == null) {
	  	callbackUrl = defaultCallbackUrl;
	  }
  }

	@Override
	public void authRequest(OHttpRequest iRequest, OHttpResponse iResponse) throws IOException {
		String location = authUrl +
				"?client_id=" + appId +
				"&redirect_uri=" + callbackUrl +
				"&scope=" + scope +
				"&response_type=code";
		iResponse.send(302, null, null, OHttpUtils.CONTENT_TEXT_PLAIN, "Location: " + location);
  }

	@Override
  public void callback(OHttpRequest iRequest, OHttpResponse iResponse) throws IOException {
		URL url = new URL(tokenUrl);
		String payload = "code=" + iRequest.getParameter("code") +
		    "&client_id=" + appId +
		    "&client_secret=" + appSecret +
		    "&redirect_uri=" + callbackUrl +
		    "&grant_type=authorization_code";
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Accept", "application/json; charset=UTF-8");
    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    conn.setRequestProperty("Content-Length", Integer.toString(payload.length()));
    conn.setDoOutput(true);
    
    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
    writer.write(payload);
    writer.flush();
    
    // TODO development, just show response for now
    InputStream stream = (conn.getResponseCode() == OHttpUtils.STATUS_OK_CODE) ? conn.getInputStream() : conn.getErrorStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    StringBuilder sb = new StringBuilder();
    String line = null;
    while ((line = reader.readLine()) != null) {
      sb.append(line + "\n");
    }
    reader.close();
    iResponse.send(OHttpUtils.STATUS_OK_CODE, "OK", null, sb.toString(), OHttpUtils.CONTENT_TEXT_PLAIN);
  }

}
