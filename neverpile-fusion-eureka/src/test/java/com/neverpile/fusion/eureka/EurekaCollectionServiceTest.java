package com.neverpile.fusion.eureka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neverpile.eureka.client.EurekaClient;
import com.neverpile.eureka.client.core.NeverpileEurekaClient;
import com.neverpile.eureka.util.EnableNeverpileEurekaSpringApplication;
import com.neverpile.fusion.api.exception.VersionMismatchException;
import com.neverpile.fusion.configuration.FusionModelMapperConfiguration;
import com.neverpile.fusion.configuration.JacksonConfiguration;
import com.neverpile.fusion.eureka.EurekaCollectionServiceTest.TestSecurityConfig;
import com.neverpile.fusion.model.Collection;
import com.neverpile.fusion.model.Collection.State;
import com.neverpile.fusion.model.Element;
import com.neverpile.fusion.model.spec.Artifact;
import com.neverpile.fusion.model.spec.CompositePaged;
import com.neverpile.fusion.model.spec.CompositePaged.PageSequence;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = {
    JacksonAutoConfiguration.class, JacksonConfiguration.class, FusionModelMapperConfiguration.class,
    TestSecurityConfig.class
}, properties = {
    "neverpile-eureka.document-service.enable-multi-versioning=true", "logging.level.org.springframework.web=DEBUG",
    "logging.level.org.springframework.security=DEBUG",
    "logging.level.org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping=TRACE"
})
@EnableAutoConfiguration
@EnableNeverpileEurekaSpringApplication
@EnableTransactionManagement
public class EurekaCollectionServiceTest {
  @EnableWebSecurity
  @Configuration
  @Order(SecurityProperties.BASIC_AUTH_ORDER)
  public static class TestSecurityConfig extends WebSecurityConfigurerAdapter {
    // @Bean
    // public AuthorizationService authorizationService() {
    // return new AllowAllAuthorizationService();
    // }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
      http.csrf().disable().authorizeRequests().anyRequest().anonymous();
    }
  }

  @LocalServerPort
  int port;

  // @Autowired
  private EurekaCollectionService collectionService;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private Clock clock;

  private NeverpileEurekaClient client;

  @AfterEach
  public void clearFileSystemObjectStore() throws IOException {
    Files.walk(Path.of("./neverpile-eureka_default")).sorted(Comparator.reverseOrder()).map(Path::toFile).filter(
        f -> !f.getName().endsWith("neverpile-eureka_default")).forEach(File::delete);
  }

  @BeforeEach
  public void init() {
    client = EurekaClient.builder().baseURL("http://localhost:" + port + "/").build();
    collectionService = new EurekaCollectionService(objectMapper, client, clock);
  }

  @Test
  public void testThat_collectionServiceIsAvailable() {
    assertThat(collectionService.getCurrent("nonexisting")).isEmpty();
  }

  @Test
  public void testThat_nonexistingCollectionIsntFound() {
    assertThat(collectionService.getCurrent(UUID.randomUUID().toString())).isNotPresent();
    assertThat(collectionService.getVersion(UUID.randomUUID().toString(), Instant.now())).isNotPresent();
  }

  @Test
  public void testThat_collectionCanBeSaved() {
    Collection tf = createTestCollection();
    tf.setId(UUID.randomUUID().toString());

    when(clock.instant()).thenReturn(Instant.ofEpochMilli(1));

    Collection saved = collectionService.save(tf);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getVersionTimestamp()).isEqualTo(Instant.ofEpochMilli(1));

    // reload and verify contents
    Optional<Collection> reloaded = collectionService.getVersion(saved.getId(), saved.getVersionTimestamp());

    assertThat(reloaded).isNotEmpty();
    verifyTestCollection(reloaded.get());
  }

  @Test
  public void testThat_newVersionCanBeSaved() {
    Collection tf = createTestCollection();
    tf.setId(UUID.randomUUID().toString());

    // V1
    Instant v1 = Instant.ofEpochMilli(1);
    when(clock.instant()).thenReturn(v1);
    tf.setVersionTimestamp(null);
    assertThat(collectionService.save(tf).getVersionTimestamp()).isEqualTo(v1);

    // V2
    Instant v2 = Instant.ofEpochMilli(2);
    when(clock.instant()).thenReturn(v2);
    tf.setVersionTimestamp(v1);
    assertThat(collectionService.save(tf).getVersionTimestamp()).isEqualTo(v2);

    // both versions must exist
    assertThat(collectionService.getVersion(tf.getId(), v1)).isNotEmpty();
    assertThat(collectionService.getVersion(tf.getId(), v2)).isNotEmpty();
  }
  
  @Test
  public void testThat_versionListCanBeRetrieved() {
    Collection tf = createTestCollection();
    tf.setId(UUID.randomUUID().toString());
    
    // V1
    Instant v1 = Instant.ofEpochMilli(1);
    when(clock.instant()).thenReturn(v1);
    tf.setVersionTimestamp(null);
    assertThat(collectionService.save(tf).getVersionTimestamp()).isEqualTo(v1);
    
    // V2
    Instant v2 = Instant.ofEpochMilli(2);
    when(clock.instant()).thenReturn(v2);
    tf.setVersionTimestamp(v1);
    assertThat(collectionService.save(tf).getVersionTimestamp()).isEqualTo(v2);
    
    // both versions must exist
    List<Instant> versions = collectionService.getVersions(tf.getId());
    assertThat(versions).containsExactly(v1, v2);
  }

  @Test
  public void testThat_newCollectionRequiresNullVersion() {
    Collection tf = createTestCollection();
    tf.setId(UUID.randomUUID().toString());

    // V1
    Instant v1 = Instant.ofEpochMilli(1);
    when(clock.instant()).thenReturn(v1);
    tf.setVersionTimestamp(v1);

    assertThrows(VersionMismatchException.class, () -> collectionService.save(tf));
  }

  @Test
  public void testThat_savingPreventsLostUpdates() {
    Collection tf = createTestCollection();
    tf.setId(UUID.randomUUID().toString());

    Instant v1 = Instant.ofEpochMilli(1);
    Instant v2 = Instant.ofEpochMilli(2);
    Instant v3 = Instant.ofEpochMilli(3);

    // save V1
    tf.setVersionTimestamp(null);
    when(clock.instant()).thenReturn(v1);
    collectionService.save(tf);
    assertThat(tf.getVersionTimestamp()).isEqualTo(v1);

    // now save v2
    when(clock.instant()).thenReturn(v2);
    collectionService.save(tf);
    assertThat(tf.getVersionTimestamp()).isEqualTo(v2);

    // save v3 but referencing the wrong version v1
    tf.setVersionTimestamp(v1);
    when(clock.instant()).thenReturn(v3);
    assertThrows(VersionMismatchException.class, () -> collectionService.save(tf));
  }

  @Test
  public void testThat_savingDetectsTheClockRunningBackwards() {
    Collection tf = createTestCollection();
    tf.setId(UUID.randomUUID().toString());

    Instant v1 = Instant.ofEpochMilli(1);
    Instant v2 = Instant.ofEpochMilli(2);
    Instant v3 = Instant.ofEpochMilli(3);

    // save V1
    tf.setVersionTimestamp(null);
    when(clock.instant()).thenReturn(v1);
    collectionService.save(tf);

    // now save v3 (!)
    tf.setVersionTimestamp(v1);
    when(clock.instant()).thenReturn(v3);
    collectionService.save(tf);

    // save v3 but referencing the wrong version v1
    tf.setVersionTimestamp(v3);
    when(clock.instant()).thenReturn(v2);
    assertThrows(VersionMismatchException.class, () -> collectionService.save(tf));
  }

  @Test
  public void testThat_collectionCanBeRetrieved() {
    Collection tf = createTestCollection();
    tf.setId(UUID.randomUUID().toString());
    
    // Save two versions, as in tests above...
    // V1
    Instant v1 = Instant.ofEpochMilli(1);
    when(clock.instant()).thenReturn(v1);
    tf.setVersionTimestamp(null);
    assertThat(collectionService.save(tf).getVersionTimestamp()).isEqualTo(v1);
    
    // V2
    Instant v2 = Instant.ofEpochMilli(2);
    when(clock.instant()).thenReturn(v2);
    tf.setVersionTimestamp(v1);
    assertThat(collectionService.save(tf).getVersionTimestamp()).isEqualTo(v2);
    
    // ...but have a closed look at the results
    // retrieve current version
    Optional<Collection> currentOpt = collectionService.getCurrent(tf.getId());
    assertThat(currentOpt).isNotEmpty();
    Collection current = currentOpt.get();
    assertThat(current.getId()).isEqualTo(tf.getId());
    assertThat(current.getVersionTimestamp()).isEqualTo(v2);
    assertThat(current.getCreatedBy()).isEqualTo("user");
    
    // retrieve old version
    Optional<Collection> oldOpt = collectionService.getVersion(tf.getId(), v1);
    assertThat(oldOpt).isNotEmpty();
    Collection old = oldOpt.get();
    assertThat(old.getId()).isEqualTo(tf.getId());
    assertThat(old.getVersionTimestamp()).isEqualTo(v1);
    assertThat(old.getCreatedBy()).isEqualTo("user");
  }
  
  private Collection createTestCollection() {
    Collection f = new Collection();
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
    ps1.setPageIndices(new int[]{
        0, 1, 2
    });
    cp.getPageSequences().add(ps1);

    PageSequence ps2 = new PageSequence();
    ps2.setMediaType(MediaType.IMAGE_JPEG);
    ps2.setContentURI("jpg:from://some/where");
    ps2.setPageIndices(new int[]{
        0
    });
    cp.getPageSequences().add(ps2);

    PageSequence ps3 = new PageSequence();
    ps3.setMediaType(MediaType.valueOf("image/tiff"));
    ps3.setContentURI("tiff:from://some/where");
    ps3.setPageIndices(new int[]{
        3, 4, 5
    });
    cp.getPageSequences().add(ps3);

    e2.setSpecification(cp);
    f.getElements().add(e2);
    return f;
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
