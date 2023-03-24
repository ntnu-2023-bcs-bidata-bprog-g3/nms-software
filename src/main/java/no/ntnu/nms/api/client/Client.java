package no.ntnu.nms.api.client;

import no.ntnu.nms.logging.Logging;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import java.io.File;
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
                    .setHostnameVerifier((hostname, session) -> true)
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

    public static void consumeLicense(JSONObject body) throws HttpException {

        String ip;
        String mediaFunction;
        int duration;

        try {
            ip = body.getString("ip");
            mediaFunction = body.getString("mediaFunction");
            duration = body.getInt("duration");
        } catch (Exception e) {
            throw new HttpException(e.getMessage());
        }

        String url = "https://" + ip + "/api/v1/consume";

        final HttpEntity entity = EntityBuilder.create().setContentType(ContentType.APPLICATION_JSON)
                .setText("{\"mediaFunction\": \"" + mediaFunction + "\", \"duration\": " + duration + "}")
                .build();

        try (CloseableHttpClient httpClient = getHttpClient()) {
            if (httpClient == null) {
                throw new HttpException("Failed to create http client");
            }
            boolean returnCode = httpClient.execute(ClassicRequestBuilder.delete(url)
                            .setEntity(entity)
                            .build(),
                    (ClassicHttpResponse response) -> response.getCode() == 200);
            if (!returnCode) {
                throw new HttpException("LFA did not return 200");
            }
        } catch (Exception e) {
            throw new HttpException(e.getMessage());
        }
    }

    /**
     * Checks if the LFA is alive.
     * @param ip The ip of the LFA.
     * @return True if the LFA is alive, false otherwise.
     */
    public static boolean lfaIsAlive(String ip) {
        String url = "https://" + ip;
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

    /**
     * Gets the licenses from the LFA.
     * @param ip The ip of the LFA.
     * @return A {@link JSONArray} with the licenses of the given LFA.
     */
    public static JSONArray getLfaLicenses(String ip) {
        String url = "https://" + ip + "/api/v1/licenses";
        String body;
        try (CloseableHttpClient httpClient = getHttpClient()) {
            if (httpClient == null) {
                Logging.getLogger().warning("Failed to create http client");
                return null;
            }
            body = httpClient.execute(ClassicRequestBuilder.get(url).build(),
                    (ClassicHttpResponse response) -> {
                        if (response.getCode() != 200) {
                            Logging.getLogger().warning("Failed to connect to LFA: " + response.getCode());
                            return null;
                        } else {
                            return EntityUtils.toString(response.getEntity(), "UTF-8");
                        }
                    });
        } catch (Exception e) {
            Logging.getLogger().warning(e.getMessage());
            return null;
        }
        if (body == null) {
            return null;
        }
        return new JSONObject(body).getJSONArray("licenses");
    }

    /**
     * Uploads a license to the LFA.
     * @param ip The ip of the LFA.
     * @param path The path to the license files.
     */
    public static void uploadLicense(String ip, String path) throws HttpException {
        String url = "https://" + ip + "/api/v1/upload";
        String body;

        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.LEGACY);
        builder.addPart("license", new FileBody(new File(path + "/license.json")));
        builder.addPart("signature", new FileBody(new File(path + "/license.json.signature")));
        builder.addPart("intermediate", new FileBody(new File("intermediate.cert")));
        final HttpEntity entity = builder.build();

        try (CloseableHttpClient httpClient = getHttpClient()) {
            if (httpClient == null) {
                Logging.getLogger().warning("Failed to create http client");
                return;
            }
            body = httpClient.execute(ClassicRequestBuilder.post(url).setEntity(entity).build(),
                    (ClassicHttpResponse response) -> {
                        if (response.getCode() != 200) {
                            Logging.getLogger().warning("Failed to connect to LFA: " + response.getCode());
                            return EntityUtils.toString(response.getEntity(), "UTF-8");
                        } else {
                            return null;
                        }
                    });
        } catch (Exception e) {
            Logging.getLogger().warning(e.getMessage());
            throw new HttpException(e.getMessage());
        }

        if (body != null) {
            throw new HttpException(body);
        }
    }
}
