package com.neverpile.fusion.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neverpile.fusion.configuration.JacksonConfiguration;
import com.neverpile.fusion.model.Collection.State;
import com.neverpile.fusion.model.spec.Artifact;
import com.neverpile.fusion.model.spec.CompositePaged;
import com.neverpile.fusion.model.spec.CompositePaged.PageSequence;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE,
    classes = {JacksonAutoConfiguration.class, JacksonConfiguration.class})
public class JsonMappingTest {
  @Autowired
  private ObjectMapper objectMapper;

  private final String REF = "{\r\n" + //
      "  \"id\" : \"anId\",\r\n" + //
      "  \"versionTimestamp\" : \"1970-01-01T00:00:00.001Z\",\r\n" + //
      "  \"state\" : \"Active\",\r\n" + //
      "  \"dateCreated\" : \"1970-01-01T00:00:00.001Z\",\r\n" + //
      "  \"dateModified\" : \"1970-01-01T00:00:00.001Z\",\r\n" + //
      "  \"createdBy\" : \"user\",\r\n" + //
      "  \"metadata\" : {\r\n" + //
      "    \"foo\" : \"bar\"\r\n" + //
      "  },\r\n" + //
      "  \"elements\" : [ {\r\n" + //
      "    \"id\" : \"anElementId\",\r\n" + //
      "    \"dateCreated\" : \"1970-01-01T00:00:00.001Z\",\r\n" + //
      "    \"dateModified\" : \"1970-01-01T00:00:00.001Z\",\r\n" + //
      "    \"tags\" : [ \"foo\", \"bar\" ],\r\n" + //
      "    \"metadata\" : {\r\n" + //
      "      \"foo\" : \"baz\"\r\n" + //
      "    },\r\n" + //
      "    \"specification\" : {\r\n" + //
      "      \"type\" : \"artifact\",\r\n" + //
      "      \"contentURI\" : \"text:collection:from://some/where\",\r\n" + //
      "      \"mediaType\" : \"text/plain\"\r\n" + //
      "    }\r\n" + //
      "  }, {\r\n" + //
      "    \"id\" : \"anotherElementId\",\r\n" + //
      "    \"dateCreated\" : \"1970-01-01T00:00:00.001Z\",\r\n" + //
      "    \"dateModified\" : \"1970-01-01T00:00:00.001Z\",\r\n" + //
      "    \"tags\" : [ \"foo\", \"bar\" ],\r\n" + //
      "    \"metadata\" : {\r\n" + //
      "      \"foo\" : \"baz\"\r\n" + //
      "    },\r\n" + //
      "    \"specification\" : {\r\n" + //
      "      \"type\" : \"compositePaged\",\r\n" + //
      "      \"pageSequences\" : [ {\r\n" + //
      "        \"mediaType\" : \"application/pdf\",\r\n" + //
      "        \"contentURI\" : \"pdf:from://some/where\",\r\n" + //
      "        \"pageIndices\" : [ 0, 1, 2 ],\r\n" + //
      "        \"annotationData\" : null,\r\n" + //
      "        \"renderSettingsData\" : null\r\n" + //
      "      }, {\r\n" + //
      "        \"mediaType\" : \"image/jpeg\",\r\n" + //
      "        \"contentURI\" : \"jpg:from://some/where\",\r\n" + //
      "        \"pageIndices\" : [ 0 ],\r\n" + //
      "        \"annotationData\" : null,\r\n" + //
      "        \"renderSettingsData\" : null\r\n" + //
      "      }, {\r\n" + //
      "        \"mediaType\" : \"image/tiff\",\r\n" + //
      "        \"contentURI\" : \"tiff:from://some/where\",\r\n" + //
      "        \"pageIndices\" : [ 3, 4, 5 ],\r\n" + //
      "        \"annotationData\" : null,\r\n" + //
      "        \"renderSettingsData\" : null\r\n" + //
      "      } ],\r\n" + //
      "      \"annotationData\" : null\r\n" + //
      "    }\r\n" + //
      "  } ],\r\n" + //
      "  \"typeId\" : \"aCollectionType\"\r\n" + //
      "}";

  @Test
  public void testThat_collectionMarshallingWorks() throws JsonProcessingException {
    Collection f = createTestCollection();

    String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(f);

    assertThat(s).isEqualToIgnoringWhitespace(REF);
  }

  private Collection createTestCollection() {
    Collection f = new Collection();
    f.setId("anId");
    f.setVersionTimestamp(Instant.ofEpochMilli(1));
    f.setTypeId("aCollectionType");
    f.setState(State.Active);

    f.setDateCreated(Instant.ofEpochMilli(1));
    f.setDateModified(Instant.ofEpochMilli(1));
    f.setCreatedBy("user");
    f.setMetadata(objectMapper.createObjectNode().put("foo", "bar"));

    Element e1 = new Element();
    e1.setId("anElementId");
    e1.setDateCreated(Instant.ofEpochMilli(1));
    e1.setDateModified(Instant.ofEpochMilli(1));
    e1.setTags(Arrays.asList("foo", "bar"));
    e1.setMetadata(objectMapper.createObjectNode().put("foo", "baz"));

    Artifact a = new Artifact();
    a.setContentURI("text:collection:from://some/where");
    a.setMediaType(MediaType.TEXT_PLAIN);
    e1.setSpecification(a);

    f.getElements().add(e1);

    Element e2 = new Element();
    e2.setId("anotherElementId");
    e2.setDateCreated(Instant.ofEpochMilli(1));
    e2.setDateModified(Instant.ofEpochMilli(1));
    e2.setTags(Arrays.asList("foo", "bar"));
    e2.setMetadata(objectMapper.createObjectNode().put("foo", "baz"));

    CompositePaged cp = new CompositePaged();

    PageSequence ps1 = new PageSequence();
    ps1.setMediaType(MediaType.APPLICATION_PDF);
    ps1.setContentURI("pdf:from://some/where");
    ps1.setPageIndices(new int[]{0, 1, 2});
    cp.getPageSequences().add(ps1);

    PageSequence ps2 = new PageSequence();
    ps2.setMediaType(MediaType.IMAGE_JPEG);
    ps2.setContentURI("jpg:from://some/where");
    ps2.setPageIndices(new int[]{0});
    cp.getPageSequences().add(ps2);

    PageSequence ps3 = new PageSequence();
    ps3.setMediaType(MediaType.valueOf("image/tiff"));
    ps3.setContentURI("tiff:from://some/where");
    ps3.setPageIndices(new int[]{3, 4, 5});
    cp.getPageSequences().add(ps3);

    e2.setSpecification(cp);
    f.getElements().add(e2);
    return f;
  }

  @Test
  public void testThat_collectionUnmarshallingWorks() throws JsonMappingException, JsonProcessingException {
    Collection f = objectMapper.readValue(REF, Collection.class);

    assertThat(f.getId()).isEqualTo("anId");
    assertThat(f.getVersionTimestamp()).isEqualTo(Instant.ofEpochMilli(1));

    verifyTestCollection(f);
  }

  private void verifyTestCollection(final Collection f) {
    assertThat(f.getTypeId()).isEqualTo("aCollectionType");
    assertThat(f.getState()).isEqualTo(State.Active);
    assertThat(f.getDateCreated()).isEqualTo(Instant.ofEpochMilli(1));
    assertThat(f.getDateModified()).isEqualTo(Instant.ofEpochMilli(1));
    assertThat(f.getCreatedBy()).isEqualTo("user");

    assertThat(f.getMetadata().findPath("foo").asText()).isEqualTo("bar");

    assertThat(f.getElements()).hasSize(2);

    Element e1 = f.getElements().get(0);
    assertThat(e1.getId()).isEqualTo("anElementId");
    assertThat(e1.getDateCreated()).isEqualTo(Instant.ofEpochMilli(1));
    assertThat(e1.getDateModified()).isEqualTo(Instant.ofEpochMilli(1));
    assertThat(e1.getTags()).containsExactly("foo", "bar");
    assertThat(e1.getMetadata().findPath("foo").asText()).isEqualTo("baz");
    assertThat(e1.getSpecification()).isInstanceOf(Artifact.class);

    Artifact a = (Artifact) e1.getSpecification();
    assertThat(a.getContentURI()).isEqualTo("text:collection:from://some/where");
    assertThat(a.getMediaType()).isEqualTo(MediaType.TEXT_PLAIN);

    Element e2 = f.getElements().get(1);
    assertThat(e2.getSpecification()).isInstanceOf(CompositePaged.class);

    CompositePaged cp = (CompositePaged) e2.getSpecification();
    assertThat(cp.getPageSequences()).hasSize(3);

    assertThat(cp.getPageSequences().get(0).getContentURI()).isEqualTo("pdf:from://some/where");
    assertThat(cp.getPageSequences().get(0).getMediaType()).isEqualTo(MediaType.APPLICATION_PDF);
    assertThat(cp.getPageSequences().get(0).getPageIndices()).containsExactly(0, 1, 2);

    assertThat(cp.getPageSequences().get(2).getContentURI()).isEqualTo("tiff:from://some/where");
    assertThat(cp.getPageSequences().get(2).getPageIndices()).containsExactly(3, 4, 5);
  }
}
