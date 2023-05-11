package no.ntnu.nms;

import no.ntnu.nms.domainmodel.PoolRegistry;
import no.ntnu.nms.license.LicenseLedger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Base64;


/**
 * This class is used to initialize the pool registry and the license ledger.
 */
public class Init {
    /**
     * This method is used to initialize the pool registry and the license ledger.
     * @param args ignored.
     */
    public static void main(String[] args) {

        try (FileWriter fw = new FileWriter("intermediate.cert")) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            InputStream is = App.class.getClassLoader().getResourceAsStream("keystore.p12");
            keyStore.load(is, "secret".toCharArray());

            Certificate certificate = keyStore.getCertificate("keystore");

            String certPem = "-----BEGIN CERTIFICATE-----\n";
            certPem += Base64.getEncoder()
                    .encodeToString(certificate.getEncoded())
                    .replaceAll("(.{64})", "$1\n");
            certPem += "\n-----END CERTIFICATE-----\n";

            fw.write(certPem);
        } catch (KeyStoreException | CertificateException | IOException
                 | NoSuchAlgorithmException e) {
            System.exit(-1);
        }

        PoolRegistry.init("data" + File.separator + "pool" + File.separator + "poolreg.ser");
        LicenseLedger.init("data" + File.separator + "ledger" + File.separator
                + "top_license_ledger.txt");

    }
}
