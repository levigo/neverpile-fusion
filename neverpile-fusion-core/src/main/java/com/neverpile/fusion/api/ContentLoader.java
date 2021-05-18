package com.neverpile.fusion.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.function.Consumer;

public interface ContentLoader {
  boolean canRead(URI contentURI);

  void read(URI uri, Consumer<InputStream> consumer) throws IOException;
}
