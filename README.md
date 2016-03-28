#OrientDB Multi-Provider Authentication Plugin

Copy jar to `<orientb>/lib/`

Server (HTTP API) command: `http://<server>:<port>/multiauth/:database/:provider`

Register it to the server configuration in `<orientb>/config/orientdb-server-config.xml` file:
    
(Section `<listeners>` -> `<listener protocol="http" ...` -> `<commands>`)

    <command implementation="orientdb.multiauth.OServerCommandMultiauth" pattern="GET|multiauth/*" stateful="false">
      <parameters>
        <entry name="enabled" value="true"/>
        <entry name="oauthUser" value="username"/>
        <entry name="oauthPassword" value="password"/>
      </parameters>
    </command>

##Providers

Register one or more OAuth2 ready providers (Section `<handlers>`):

Example:

Syntax: `http://<server>:<port>/multiauth/<database>/google`

Syntax: `http://<server>:<port>/multiauth/<database>/facebook`

    <handler class="orientdb.multiauth.OAuth2Provider">
        <parameters>
            <parameter name="name" value="google"/>
            <parameter name="appId" value="<APP-ID>"/>
            <parameter name="appSecret" value="<APP-SECRET>"/>
            <parameter name="scope" value="email profile"/>
            <parameter name="authUrl" value="https://accounts.google.com/o/oauth2/auth"/>
            <parameter name="tokenUrl" value="https://www.googleapis.com/oauth2/v4/token"/>
        </parameters>
    </handler>
    <handler class="orientdb.multiauth.OAuth2Provider">
        <parameters>
            <parameter name="name" value="facebook"/>
            <parameter name="appId" value="<APP-ID>"/>
            <parameter name="appSecret" value="<APP-SECRET>"/>
            <parameter name="scope" value="public_profile,email"/>
            <parameter name="authUrl" value="https://www.facebook.com/dialog/oauth"/>
            <parameter name="tokenUrl" value="https://graph.facebook.com/oauth/access_token"/>
        </parameters>
    </handler>
      
Or create your authentication strategy by implementing `OAuthProviderInterface`

\* Development stage
