package com.google.android.gms.samples.vision.inner.bink.sslpinning;

/**
 * Created by hi on 26/05/16.
 */

import android.content.Context;

import org.apache.http.conn.scheme.HostNameResolver;
import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class KeyPinStore {

    private static KeyPinStore instance = null;
    private SSLContext sslContext;
    private SSLSocketFactory sslSocketFactory;

    public static synchronized KeyPinStore getInstance(Context context) {
        if (instance == null){
            instance = new KeyPinStore(context);
        }
        return instance;
    }

    private KeyPinStore(Context context) {
        // https://developer.android.com/training/articles/security-ssl.html
        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        InputStream caInput = null;
        Certificate ca = null;

        try {
            sslContext = SSLContext.getInstance("TLS");

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            // randomCA.crt should be in the Assets directory (tip from here http://littlesvr.ca/grumble/2014/07/21/android-programming-connect-to-an-https-server-with-self-signed-certificate/)
            caInput = new BufferedInputStream(context.getAssets().open("chingrewards_com.der"));
            ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            caInput.close();


        // Create a KeyStore containing our trusted CAs
           String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            // SSLContext context = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            sslSocketFactory = new SSLSocketFactory("TLS", keyStore, null,
                    null, null, new HostNameResolver() {
                @Override
                public InetAddress resolve(String s) throws IOException {
                    return null;
                }
            });
        }catch (CertificateException ce){

        }catch (KeyStoreException ke){

        }catch (KeyManagementException km){

        }catch (NoSuchAlgorithmException ne){

        }catch (IOException ie){
           ie.printStackTrace();
        }catch (UnrecoverableKeyException ue){
           ue.printStackTrace();
       }

    }

    public SSLContext getContext(){
        return sslContext;
    }

    public SSLSocketFactory getSSLSocketFactory(){
        return sslSocketFactory;
    }
}

