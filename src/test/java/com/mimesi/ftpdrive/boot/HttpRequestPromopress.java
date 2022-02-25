package com.mimesi.ftpdrive.boot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@SpringBootTest
public class HttpRequestPromopress {

    @Test
    void requestPromopress() {
        try {
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            String authData = "pp-mimesi" + ":" + "5ZnPlZC-BYfl";
            String basicAuth = "Basic " + new String(Base64.getEncoder().encode(authData.getBytes()));

            URL urlParent = new URL("https://promopress.kataweb.it/download");
            URL url = concatenate(urlParent, "ilpiccolo/20211118.zip");
            URLConnection request = url.openConnection();
            request.setRequestProperty("Authorization", basicAuth);
            System.out.println("URL to invoke: "+url.toURI().toString());
            System.out.println("Path to invoke: "+url.getPath());
            // System.setProperty("http.maxRedirects", "100");

            InputStream in = request.getInputStream();
            System.out.println("Start download /mattinopadova/20211118.zip at "+datePatternFolder("dd-MM-yyyy HH:mm:ss"));
            FileOutputStream out = new FileOutputStream("C:\\tmp_pdf\\quotidianiEspresso\\ilpiccolo\\20211118.zip");
            byte[] buffer = new byte[2048];
            int len = in.read(buffer);
            while (len != -1) {
                out.write(buffer, 0, len);
                len = in.read(buffer);
                if (Thread.interrupted()) {
                    try {
                        throw new InterruptedException();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

            System.out.println("End download /mattinopadova/20211118.zip at "+datePatternFolder("dd-MM-yyyy HH:mm:ss"));
            in.close();
            out.close();
        } catch (Exception e) {
            System.out.println("Error in httpRequestPromopress: "+e.getMessage());
        }
    }

    private static String datePatternFolder (final String pattern) {
        SimpleDateFormat formFile = new SimpleDateFormat(pattern);
        Date date = new Date();
        String datePatternFolder = formFile.format(date);

        return datePatternFolder;
    }

    public static URL concatenate(URL baseUrl, String extraPath) throws URISyntaxException,
            MalformedURLException {
        URI uri = baseUrl.toURI();
        String newPath = uri.getPath() + '/' + extraPath;
        URI newUri = uri.resolve(newPath);
        return newUri.toURL();
    }

}
