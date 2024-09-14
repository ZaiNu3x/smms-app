package group.intelliboys.smms.configs;

import android.content.Context;
import android.content.res.Resources;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import group.intelliboys.smms.R;
import okhttp3.OkHttpClient;

public class CustomOkHttpClient {
    public static OkHttpClient getOkHttpClient(Context context) {
        try {
            Resources resources = context.getResources();
            InputStream sslCertInputStream = resources.openRawResource(R.raw.sslcert);

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(sslCertInputStream);

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("my_server_cert", cert);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            X509TrustManager trustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .sslSocketFactory(sslContext.getSocketFactory(), trustManager);

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
