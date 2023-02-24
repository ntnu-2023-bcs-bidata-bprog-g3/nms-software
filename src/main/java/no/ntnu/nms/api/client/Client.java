package no.ntnu.nms.api.client;

import no.ntnu.nms.logging.Logging;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Service
public class Client {

    /**
     * Creates a http client with a custom ssl context.
     * @return {@link CloseableHttpClient} with a custom ssl context.
     */
    private static CloseableHttpClient getHttpClient() {
        try {
            final SSLContext sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                    .build();
            final SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                    .setSslContext(sslcontext)
                    .build();
            final HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(sslSocketFactory)
                    .build();
            return HttpClients.custom()
                    .setConnectionManager(cm)
                    .evictExpiredConnections()
                    .build();

        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            Logging.getLogger().warning(e.getMessage());
            return null;
        }
    }

    /**
     * Checks if the LFA is alive.
     * @param ip The ip of the LFA.
     * @return True if the LFA is alive, false otherwise.
     */
    public static boolean lfaIsAlive(String ip) {
        String url = "https://" + ip + ":8443/";
        try (CloseableHttpClient httpClient = getHttpClient()) {
            if (httpClient == null) {
                Logging.getLogger().warning("Failed to create http client");
                return false;
            }
            return httpClient.execute(ClassicRequestBuilder.get(url).build(),
                    (ClassicHttpResponse response) -> {
                        if (response.getCode() == 200) {
                            return true;
                        } else {
                            Logging.getLogger().warning("Failed to connect to LFA: " + response.getCode());
                            return false;
                        }
                    });
        } catch (Exception e) {
            Logging.getLogger().warning(e.getMessage());
            return false;
        }
    }
}
