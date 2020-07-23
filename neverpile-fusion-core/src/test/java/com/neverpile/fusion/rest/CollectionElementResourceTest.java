package com.neverpile.fusion.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neverpile.fusion.api.CollectionIdStrategy;
import com.neverpile.fusion.api.CollectionService;
import com.neverpile.fusion.api.CollectionTypeService;
import com.neverpile.fusion.model.Collection;
import com.neverpile.fusion.model.CollectionType;
import com.neverpile.fusion.model.Element;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = "server.error.include-message=always")
public class CollectionElementResourceTest extends AbstractRestAssuredTest {
  private static final String C = "aCollectionId";

  private static final String F = "aCollection";

  @TestConfiguration
  @Import(CollectionElementResource.class)
  public static class ServiceConfig {

  }

  @MockBean
  CollectionService mockCollectionService;

  @MockBean
  CollectionTypeService mockCollectionTypeService;

  @MockBean
  CollectionIdStrategy idGenerationStrategy;

  @Autowired
  ObjectMapper objectMapper;

  @BeforeEach
  public void reset() throws JsonProcessingException {
    AtomicInteger docIdGenerator = new AtomicInteger(42);
    BDDMockito.when(idGenerationStrategy.creatcollectionId()).thenAnswer(
        (i) -> "TheAnswerIs" + docIdGenerator.getAndIncrement());
    BDDMockito.when(idGenerationStrategy.validateCollectionId(any())).thenReturn(true);
    BDDMockito.when(mockCollectionTypeService.get(any())).thenReturn(Optional.of(new CollectionType()));

    Optional<Collection> tc = Optional.of(createTestCollection());
    BDDMockito.when(mockCollectionService.getCurrent(C)).thenReturn(tc);
  }

  @Test
  public void testThat_collectionElementCanBeAdded() throws Exception {
    // @formatter:off
    ArgumentCaptor<Collection> storedCollectionC = ArgumentCaptor.forClass(Collection.class);

    BDDMockito
      .given(mockCollectionService.save(storedCollectionC.capture()))
        .willAnswer(i -> { 
          Collection collection = i.getArgument(0);
          collection.setVersionTimestamp(Instant.now());
          collection.setDateCreated(Instant.now());
          collection.setDateModified(Instant.now());
          return collection; 
        });
    
    Instant then = Instant.now();
    
    Element toBeAdded = new Element();
    toBeAdded.getTags().add("added");
    
    // add element
    Element resElement = RestAssured.given()
      .accept(ContentType.JSON)
      .body(toBeAdded).contentType(ContentType.JSON)
      .auth().preemptive().basic("user", "password")
    .when()
      .log().all()
      .post("/api/v1/collections/{c}/elements", C)
    .then()
      .log().all()
      .statusCode(201)
      .header("Location", Matchers.equalTo(C + "/elements/TheAnswerIs42"))
      .contentType(ContentType.JSON)
      .extract().as(Element.class);

    assertThat(resElement.getId()).isEqualTo("TheAnswerIs42");
    assertThat(resElement.getDateModified()).isBetween(then, Instant.now());

    Collection stored = storedCollectionC.getValue();
    assertThat(stored.getElements()).hasSize(3);
    assertThat(stored.getElements()).anyMatch(e -> e.getId().equals("TheAnswerIs42"));
    assertThat(stored.getElements().get(2).getId()).isEqualTo("TheAnswerIs42");
    assertThat(stored.getElements().get(2).getTags()).contains("added");
    // @formatter:on
  }
  

  @Test
  public void testThat_collectionElementCanBeAddedWithId() throws Exception {
    // @formatter:off
    ArgumentCaptor<Collection> storedCollectionC = ArgumentCaptor.forClass(Collection.class);

    BDDMockito
      .given(mockCollectionService.save(storedCollectionC.capture()))
        .willAnswer(i -> { 
          Collection collection = i.getArgument(0);
          collection.setVersionTimestamp(Instant.now());
          collection.setDateCreated(Instant.now());
          collection.setDateModified(Instant.now());
          return collection; 
        });
    
    Instant then = Instant.now();
    
    Element toBeAdded = new Element();
    toBeAdded.setId("element-c");
    toBeAdded.getTags().add("added");
    
    // add element
    Element resElement = RestAssured.given()
      .accept(ContentType.JSON)
      .body(toBeAdded).contentType(ContentType.JSON)
      .auth().preemptive().basic("user", "password")
    .when()
      .log().all()
      .post("/api/v1/collections/{c}/elements", C)
    .then()
      .log().all()
      .statusCode(201)
      .header("Location", Matchers.equalTo(C + "/elements/element-c"))
      .contentType(ContentType.JSON)
      .extract().as(Element.class);

    assertThat(resElement.getId()).isEqualTo("element-c");
    assertThat(resElement.getDateModified()).isBetween(then, Instant.now());

    Collection stored = storedCollectionC.getValue();
    assertThat(stored.getElements()).hasSize(3);
    assertThat(stored.getElements()).anyMatch(e -> e.getId().equals("element-c"));
    assertThat(stored.getElements().get(2).getId()).isEqualTo("element-c");
    // @formatter:on
  }
  
  @Test
  public void testThat_addRejectsDuplicateId() throws Exception {
    // @formatter:off
    Element toBeAdded = new Element();
    toBeAdded.setId("element-a");
    toBeAdded.getTags().add("added");
    
    // expect rejection
    RestAssured.given()
        .accept(ContentType.JSON)
        .body(toBeAdded).contentType(ContentType.JSON)
        .auth().preemptive().basic("user", "password")
      .when()
        .log().all()
        .post("/api/v1/collections/{c}/elements", C)
      .then()
        .log().all()
        .statusCode(406);
    // @formatter:on
  }

  @Test
  public void testThat_collectionElementCanBeUpdated() throws Exception {
    // @formatter:off
    ArgumentCaptor<Collection> storedCollectionC = ArgumentCaptor.forClass(Collection.class);
    
    BDDMockito
    .given(mockCollectionService.save(storedCollectionC.capture()))
    .willAnswer(i -> { 
      Collection collection = i.getArgument(0);
      collection.setVersionTimestamp(Instant.now());
      collection.setDateCreated(Instant.now());
      collection.setDateModified(Instant.now());
      return collection; 
    });
    
    Instant then = Instant.now();
    
    Element toBeUpdated = new Element();
    toBeUpdated.getTags().add("updated");
    
    // update element
    Element resElement = RestAssured.given()
        .accept(ContentType.JSON)
        .body(toBeUpdated).contentType(ContentType.JSON)
        .auth().preemptive().basic("user", "password")
      .when()
        .log().all()
        .put("/api/v1/collections/{c}/elements/element-a", C)
      .then()
        .log().all()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .extract().as(Element.class);
    
    assertThat(resElement.getId()).isEqualTo("element-a");
    assertThat(resElement.getDateModified()).isBetween(then, Instant.now());
    
    Collection stored = storedCollectionC.getValue();
    assertThat(stored.getElements()).hasSize(2);
    assertThat(stored.getElements()).anyMatch(e -> e.getId().equals("element-a"));
    assertThat(stored.getElements().get(0).getId()).isEqualTo("element-a");
    // @formatter:on
  }

  @Test
  public void testThat_updateRejectsIdMismatch() throws Exception {
    // @formatter:off
    Element toBeUpdated = new Element();
    toBeUpdated.setId("element-foo");
    
    // expect rejection
    RestAssured.given()
        .accept(ContentType.JSON)
        .body(toBeUpdated).contentType(ContentType.JSON)
        .auth().preemptive().basic("user", "password")
      .when()
        .log().all()
        .post("/api/v1/collections/{c}/elements/element-a", C)
      .then()
        .log().all()
        .statusCode(405);
    // @formatter:on
  }

  
  @Test
  public void testThat_collectionElementCanBeDeleted() throws Exception {
    // @formatter:off
    ArgumentCaptor<Collection> storedCollectionC = ArgumentCaptor.forClass(Collection.class);
    
    BDDMockito
    .given(mockCollectionService.save(storedCollectionC.capture()))
    .willAnswer(i -> { 
      Collection collection = i.getArgument(0);
      collection.setVersionTimestamp(Instant.now());
      collection.setDateCreated(Instant.now());
      collection.setDateModified(Instant.now());
      return collection; 
    });
    
    // delete element
    RestAssured.given()
        .auth().preemptive().basic("user", "password")
      .when()
        .log().all()
        .delete("/api/v1/collections/{c}/elements/element-a", C)
      .then()
        .log().all()
        .statusCode(204);
    
    Collection stored = storedCollectionC.getValue();
    assertThat(stored.getElements()).hasSize(1);
    assertThat(stored.getElements()).noneMatch(e -> e.getId().equals("element-a"));
    assertThat(stored.getElements().get(0).getId()).isEqualTo("element-b");
    // @formatter:on
  }

  private Collection createTestCollection() throws JsonProcessingException {
    Collection collection = new Collection();

    collection.setId(C);
    collection.setTypeId("foo");
    collection.setMetadata(objectMapper.createObjectNode().put("foo", "bar"));

    Element a = new Element();
    a.setId("element-a");
    a.getTags().add("a");

    collection.getElements().add(a);

    Element b = new Element();
    b.setId("element-b");
    b.getTags().add("b");

    collection.getElements().add(b);

    return collection;
  }
}
