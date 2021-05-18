package com.neverpile.fusion.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neverpile.fusion.api.ContentLoader;

import de.nuernberger.caa.io.MultiPartOutputStream;

@RestController()
@RequestMapping(path = "/content", produces = "multipart/mixed")
public class ContentResource {

  private final List<ContentLoader> loaders;
  private static final String MULTIPART_CONTENT_TYPE = "multipart/mixed";

  @Autowired
  public ContentResource(List<ContentLoader> loaders) {
    this.loaders = loaders;
  }

  @GetMapping("get/{uri}")
  public void get(HttpServletResponse response, @PathVariable String uri) throws IOException {
    URI properURI = URI.create(URLDecoder.decode(uri, StandardCharsets.UTF_8.toString()));

    ContentLoader loader = loaders.stream() //
        .filter(l -> l.canRead(properURI)) //
        .findFirst() //
        .orElseThrow(() -> new IOException("Unsupported URI: " + properURI));

    final MultiPartOutputStream mos = new MultiPartOutputStream(response.getOutputStream());

    response.setContentType(MULTIPART_CONTENT_TYPE + "; boundary=" + mos.getBoundary());

    loader.read(properURI, is -> {
      try {
        mos.append(is);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
    mos.flush();
  }
}
