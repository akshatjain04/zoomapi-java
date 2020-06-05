package com.github.dbchar.zoomapi.clients;

import com.github.dbchar.zoomapi.components.*;
import com.github.dbchar.zoomapi.sqlite.tables.CredentialRecord;
import com.github.dbchar.zoomapi.utils.Utils;
import com.github.dbchar.zoomapi.utils.Validator;
import com.github.dbchar.zoomapi.utils.services.DatabaseService;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.List;

import static com.github.dbchar.zoomapi.utils.Logger.logi;

public class OAuthZoomClient extends ZoomClient {
  // region Private Properties

  private final String clientId;
  private final String clientSecret;
  private final int port;
  private final String redirectUri;

  // Low-level REST APIs
  private final ChatChannelsComponent chatChannelsComponent;
  private final ChatMessagesComponent chatMessagesComponent;
  private final ContactsComponent contactsComponent;

  // Compound APIs
  private final ChatComponent chatComponent;

  // All components
  private final List<BaseComponent> components;

  // endregion

  // region Constructor

  public OAuthZoomClient(String clientId, String clientSecret, int port, String redirectUri) throws Exception {
    super();
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.port = port;
    this.redirectUri = redirectUri;

    // Note: This API only supports user-managed OAuth app.
    this.chatChannelsComponent = new ChatChannelsComponent();
    this.chatMessagesComponent = new ChatMessagesComponent();
    this.contactsComponent = new ContactsComponent();

    // Compound APIs
    this.chatComponent = new ChatComponent(this.chatMessagesComponent, this.chatChannelsComponent);

    // A list that holds all components
    // We can no longer store the token in BaseComponent
    // because it does not allow multiple bots running
    this.components = List.of(
            getUsersComponent(), getMeetingsComponent(), getReportsComponent(),
            getWebinarsComponent(), getRecordingsComponent(), chatChannelsComponent,
            chatMessagesComponent, contactsComponent);

    // set client Id for caching
    components.forEach(component -> component.setClientId(clientId));

    setAccessToken();
  }

  // endregion

  // region Public APIs

  @Override
  public void refreshAccessToken() throws Exception {
    var accessToken = obtainAccessTokenFromServer();
    updateComponentsAccessToken(accessToken);
  }

  public ChatChannelsComponent getChatChannelsComponent() {
    return chatChannelsComponent;
  }

  public ChatMessagesComponent getChatMessagesComponent() {
    return chatMessagesComponent;
  }

  public ContactsComponent getContactsComponent() {
    return contactsComponent;
  }

  public ChatComponent getChatComponent() {
    return chatComponent;
  }

  // endregion

  // region Access Token Functions

  @Override
  protected void setAccessToken() throws Exception {
    var accessToken = obtainAccessTokenFromDatabase();
    if (Validator.stringIsNullOrEmpty(accessToken)) {
      accessToken = obtainAccessTokenFromServer();
    }

    updateComponentsAccessToken(accessToken);
  }

  private void updateComponentsAccessToken(String accessToken) throws Exception {
    if (Validator.stringIsNullOrEmpty(accessToken)) {
      throw new Exception("Cannot get access token either from zoom server or cache in the local database.");
    }

    // Every time we obtain the token make sure it is distributed to children components
    components.forEach(component -> component.setAccessToken(accessToken));
  }

  private String obtainAccessTokenFromDatabase() {
    try {
      var credentials = DatabaseService.INSTANCE.findCredentialRecords(clientId, clientSecret);

      if (!credentials.isEmpty()) {
        return credentials.get(0).getToken();
      }
    } catch (Exception ignored) {
    }

    return null;
  }

  private void saveAccessTokenToDatabase(String accessToken) {
    if (Validator.stringIsNullOrEmpty(accessToken)) return;

    try {
      var credentials = DatabaseService.INSTANCE.findCredentialRecords(clientId, clientSecret);
      var credential = (CredentialRecord) null;

      if (!credentials.isEmpty()) {
        // credential exists and we need its ID in table to update
        credential = credentials.get(0);
        credential.setToken(accessToken);
      } else {
        // credential does not exist and let the DB assign new ID to it
        credential = new CredentialRecord(clientId, clientSecret, accessToken);
      }

      DatabaseService.INSTANCE.save(credential);
    } catch (Exception ignored) {
    }
  }

  private String obtainAccessTokenFromServer() throws Exception {
    // open the URL and get the code
    var authorizationUri = OAuthClientRequest
            .authorizationLocation("https://zoom.us/oauth/authorize")
            .setClientId(clientId)
            .setParameter("response_type", "code")
            .setRedirectURI(redirectUri)
            .buildQueryMessage()
            .getLocationUri();
    logi("Authorization Uri: " + authorizationUri);
    Utils.openBrowser(authorizationUri);
    var code = new CodeHttpServer(this.port).getCode(); // blocking IO

    // use the code to ask for token
    var request = OAuthClientRequest
            .tokenLocation("https://zoom.us/oauth/token")
            .setGrantType(GrantType.AUTHORIZATION_CODE)
            .setCode(code)
            .setRedirectURI(redirectUri)
            .buildQueryMessage();
    var authHeader = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
    request.addHeader("Authorization", authHeader);
    logi("Request Access Token Uri: " + request.getLocationUri());
    logi("Request Access Token Uri Header: " + request.getHeader("Authorization"));

    var oAuthClient = new OAuthClient(new URLConnectionClient());
    var oAuthTokenResponse = oAuthClient.accessToken(request, OAuthJSONAccessTokenResponse.class);
    var accessToken = oAuthTokenResponse.getAccessToken();

    // cache the token
    saveAccessTokenToDatabase(accessToken);

    return accessToken;
  }

  // endregion

  // region Local Http Server

  private static class CodeHttpServer {
    private final int port;
    private final boolean ENABLE_TIMEOUT = false;
    private final int TIMEOUT = 3000; // 3 secs

    public CodeHttpServer(int port) {
      this.port = port;
    }

    public String getCode() throws IOException {
      String code = "";

      ServerSocket serverSocket = new ServerSocket(port);
      logi("Server socket listening at http://localhost:" + port);
      if (ENABLE_TIMEOUT) {
        serverSocket.setSoTimeout(TIMEOUT);
      }

      logi("Server waiting for user request...");
      Socket clientSocket = serverSocket.accept();
      logi("Client socket connected from " +
              clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

      BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      // !! Must use port 80 to display response properly !!
      // For other ports Chrome complains net::ERR_INVALID_HTTP_RESPONSE
      PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true);

      try {
        String requestLine = fromClient.readLine(); // GET /?code=xxx HTTP/1.1
        logi("requestLine: " + requestLine);
        String[] requestComponents = requestLine.split(" ");
        String requestResource = requestComponents[1];
        logi("requestResource: " + requestResource);
        code = requestResource.split("code=")[1];
        logi("Code accepted: " + code);
        toClient.println("HTTP/1.1 200 OK\r\n\r\n" +
                "<html><body style=\"font-size:40px;text-align:center;\">" +
                "<p>code=" + code + "</p>" +
                "<p>Auth success. Please close this tab.</p>" +
                "</body></html>");
      } catch (Exception e) {
        toClient.println("HTTP/1.1 500 Internal Server Error\r\n\r\n" +
                "<html><body style=\"font-size:40px;text-align:center;\">" +
                "<p>\"code\" is not contained in request queries.</p>" +
                "<p>Auth failure. Please close this tab.</p>" +
                "</body></html>");
        e.printStackTrace();
        System.err.println("Failed to get code. Please try again.");
        System.exit(1);
      } finally {
        clientSocket.close();
        serverSocket.close();
      }
      return code;
    }
  }

  // endregion
}
