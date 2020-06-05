package com.github.dbchar.zoomapi.clients;

import com.github.dbchar.zoomapi.components.BaseComponent;

import java.util.List;

/**
 * Created by Junxian Chen on 2020-04-20.
 */
public class JWTZoomClient extends ZoomClient {
  enum DataType {
    JSON, XML
  }

  private String apiKey;
  private String apiSecret;
  private final DataType dataType;
  private String token;
  // All components
  private final List<BaseComponent> components;

  public JWTZoomClient(String apiKey, String apiSecret, DataType dataType) {
    super();

    this.apiKey = apiKey;
    this.apiSecret = apiSecret;
    this.dataType = dataType;
    this.token = generateJwtToken(apiKey, apiSecret);

    // add all components to the list
    this.components = List.of(
            getUsersComponent(), getMeetingsComponent(), getReportsComponent(),
            getWebinarsComponent(), getRecordingsComponent());

    // set access token to every component if you have it on the database
    components.forEach(component -> component.setAccessToken(token));
  }

  private String generateJwtToken(String apiKey, String apiSecret) {
    return "";
  }

  private void refreshJwtToken() {
    this.token = generateJwtToken(apiKey, apiSecret);
    // set access token to every component if you have it on the database
    components.forEach(component -> component.setAccessToken(token));
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
    refreshJwtToken();
  }

  public String getApiSecret() {
    return apiSecret;
  }

  public void setApiSecret(String apiSecret) {
    this.apiSecret = apiSecret;
    refreshJwtToken();
  }

  @Override
  protected void setAccessToken() throws Exception {
    this.token = "JWT Token";
  }

  @Override
  public void refreshAccessToken() throws Exception {
  }
}
