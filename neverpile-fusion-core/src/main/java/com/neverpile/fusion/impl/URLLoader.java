package com.neverpile.fusion.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import com.neverpile.fusion.api.ContentLoader;

@Component
public class URLLoader implements ContentLoader {
  @Override
  public boolean canRead(final URI contentURI) {
    return contentURI.getScheme().equals("http") || contentURI.getScheme().equals("https");
  }

  @Override
  public void read(final URI uri, Consumer<InputStream> consumer) throws IOException {
    URLConnection connection = uri.toURL().openConnection();

    // very quick & dirty auth hack
    String user = "admin";
    String password = "admin";
    String auth = user + ":" + password;
    byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
    String authHeaderValue = "Basic " + new String(encodedAuth);
    connection.setRequestProperty("Authorization", authHeaderValue);
    consumer.accept(connection.getInputStream());
  }

}
