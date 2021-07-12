package com.neverpile.fusion.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.neverpile.common.util.MultiPartOutputStream;
import com.neverpile.fusion.api.ContentLoader;
import com.neverpile.fusion.rest.exception.NotFoundException;

@RestController()
@RequestMapping(path = "/content",
    produces = "multipart/mixed")
public class ContentResource {

  private final List<ContentLoader> loaders;
  private static final String MULTIPART_CONTENT_TYPE = "multipart/mixed";

  @Autowired
  public ContentResource(List<ContentLoader> loaders) {
    this.loaders = loaders;
  }

  @PostMapping(path = "stream",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.TEXT_PLAIN_VALUE)
  public void get(HttpServletResponse response, @RequestBody String uri,
      @RequestHeader("X-Authorization") String authInfo) throws IOException {
    URI properURI = URI.create(URLDecoder.decode(uri, StandardCharsets.UTF_8.toString()));

    ContentLoader loader = loaders.stream() //
        .filter(l -> l.canRead(properURI)) //
        .findFirst() //
        .orElseThrow(() -> new NotFoundException("Unsupported URI: " + properURI));

    final MultiPartOutputStream mos = new MultiPartOutputStream(response.getOutputStream());

    response.setContentType(MULTIPART_CONTENT_TYPE + "; boundary=" + mos.getBoundary());

    loader.read(properURI, is -> {
      try {
        mos.append(is);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }, authInfo);
    mos.flush();
  }
}
