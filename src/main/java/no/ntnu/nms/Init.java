package no.ntnu.nms;

import no.ntnu.nms.domainModel.PoolRegistry;
import no.ntnu.nms.license.LicenseLedger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Base64;


public class Init {
    public static void main(String[] args) {

        try (FileWriter fw = new FileWriter("intermediate.cert")) {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            InputStream is = App.class.getClassLoader().getResourceAsStream("keystore.jks");
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

        PoolRegistry.init("data/pool/poolreg.ser");
        LicenseLedger.init("data/ledger/top_license_ledger.txt");

    }
}
