package com.github.dbchar.zoomapi.utils.services;

import com.github.dbchar.zoomapi.network.ApiClient;
import com.github.dbchar.zoomapi.network.ApiRequest;
import com.github.dbchar.zoomapi.network.HttpMethod;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import static com.github.dbchar.zoomapi.utils.Logger.logi;

public class NgrokService {
    private final int localServicePort;
    private String publicUrl;

    private static class TunnelsResponse {

        private static class Tunnel {
            private String name;
            private String uri;
            private String public_url;
            private String proto;

            public String getName() {
                return name;
            }

            public String getUri() {
                return uri;
            }

            public String getPublicUrl() {
                return public_url;
            }

            public String getProto() {
                return proto;
            }
        }

        private List<Tunnel> tunnels;
        private String uri;

        public List<Tunnel> getTunnels() {
            return tunnels;
        }

        public String getUri() {
            return uri;
        }
    }

    public NgrokService(int localServicePort) {
        this.localServicePort = localServicePort;
    }

    public void start() throws Exception {
        // "ngrok start --none"
        // "ngrok http " + port

        // please manually start ngrok
//    Runtime.getRuntime().exec("ngrok http " + localServicePort);
//    Logger.logi("Starting ngrok...");
//    Thread.sleep(2000);

        var request = new ApiRequest("http://localhost:4040/api", "tunnels", HttpMethod.GET);
        var response = ApiClient.INSTANCE.request(request);
        if (!response.isSuccess()) {
//      stop();
            throw new Exception("Cannot get tunnels. Please make sure ngrok is running.");
        }
        try {
            this.publicUrl = new Gson().fromJson(response.getJson(), TunnelsResponse.class).getTunnels().get(0).getPublicUrl();
            logi("Tunnel established: " + publicUrl + " <-> http://localhost:" + localServicePort);
        } catch (Exception e) {
            stop();
            throw new Exception("Cannot get tunnels. Tunnels may be empty.");
        }
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public void stop() throws IOException {
        var killNgrok = (String) null;
        var osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("windows")) {
            killNgrok = "taskkill /f /im ngrok.exe";
        } else if (osName.contains("mac")) {
            killNgrok = "pkill ngrok";
        } else if (osName.contains("nix") || osName.contains("nux")) {
            killNgrok = "killall ngrok";
        } else {
            return;
        }

        Runtime.getRuntime().exec(killNgrok);
    }
}
