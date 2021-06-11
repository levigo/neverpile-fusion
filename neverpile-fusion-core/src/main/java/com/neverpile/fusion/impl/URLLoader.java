package com.neverpile.fusion.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.function.Consumer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import com.neverpile.fusion.api.ContentLoader;

@Component
@ConditionalOnMissingBean(ContentLoader.class)
public class URLLoader implements ContentLoader {
  @Override
  public boolean canRead(final URI contentURI) {
    return contentURI.getScheme().equals("http") || contentURI.getScheme().equals("https");
  }

  @Override
  public void read(final URI uri, Consumer<InputStream> consumer, String authInfo) throws IOException {
    URLConnection connection = uri.toURL().openConnection();
    if (authInfo != null) {
      connection.setRequestProperty("Authorization", authInfo);
    }
    consumer.accept(connection.getInputStream());
  }

}
