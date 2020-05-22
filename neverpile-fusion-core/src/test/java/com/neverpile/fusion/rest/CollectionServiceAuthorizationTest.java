package com.neverpile.fusion.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neverpile.common.authorization.api.CoreActions;
import com.neverpile.fusion.api.CollectionIdStrategy;
import com.neverpile.fusion.api.CollectionService;
import com.neverpile.fusion.api.CollectionTypeService;
import com.neverpile.fusion.authorization.CollectionAuthorizationService;
import com.neverpile.fusion.model.Collection;
import com.neverpile.fusion.model.CollectionType;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CollectionServiceAuthorizationTest extends AbstractRestAssuredTest {
  private static final String F = "aCollection";

  @TestConfiguration
  @Import(CollectionResource.class)
  public static class ServiceConfig {

  }

  @MockBean
  CollectionService mockCollectionService;

  @MockBean
  CollectionTypeService mockCollectionTypeService;

  @MockBean
  CollectionIdStrategy idGenerationStrategy;

  @MockBean
  CollectionAuthorizationService collectionAuthorizationService;

  @Autowired
  ObjectMapper objectMapper;

  @BeforeEach
  public void reset() {
    AtomicInteger docIdGenerator = new AtomicInteger(42);
    when(idGenerationStrategy.creatcollectionId()).thenAnswer((i) -> "TheAnswerIs" + docIdGenerator.getAndIncrement());
    when(idGenerationStrategy.validateCollectionId(any())).thenReturn(true);
    when(mockCollectionTypeService.get(any())).thenReturn(Optional.of(new CollectionType()));
  }

  @Test
  public void testThat_authorizationOnCreationIsVerified() throws Exception {
    // @formatter:off
    given(mockCollectionService.save(any())).willAnswer(i -> i.getArgument(0));
    
    // allow
    given(collectionAuthorizationService.authorizeCollectionAction(any(), any())).willReturn(true);
    
    // store collection
    RestAssured.given()
      .accept(ContentType.JSON)
      .body(createTestCollection()).contentType(ContentType.JSON)
      .auth().preemptive().basic("user", "password")
    .when()
      .post("/api/v1/collections")
    .then()
      .statusCode(201);
    
    verify(collectionAuthorizationService).authorizeCollectionAction(isNotNull(), eq(CoreActions.CREATE));
    
    // now deny
    given(collectionAuthorizationService.authorizeCollectionAction(any(), any())).willReturn(false);
    
    // store collection
    RestAssured.given()
      .accept(ContentType.JSON)
      .body(createTestCollection()).contentType(ContentType.JSON)
      .auth().preemptive().basic("user", "password")
    .when()
      .post("/api/v1/collections")
    .then()
      .statusCode(403); // expect permission denied
    
    // @formatter:on
  }

  @Test
  public void testThat_authorizationOnUpdateIsVerified() throws Exception {
    // @formatter:off
    given(mockCollectionService.getCurrent(F))
      .willAnswer(a -> Optional.of(createTestCollection()));
    given(mockCollectionService.save(any())).willAnswer(i -> i.getArgument(0));
    
    // allow
    given(collectionAuthorizationService.authorizeCollectionAction(any(), any())).willReturn(true);
    
    // store collection
    RestAssured.given()
        .accept(ContentType.JSON)
        .body(createTestCollection()).contentType(ContentType.JSON)
        .auth().preemptive().basic("user", "password")
      .when()
        .put("/api/v1/collections/{id}", F)
      .then()
        .statusCode(201);

    verify(collectionAuthorizationService).authorizeCollectionAction(isNotNull(), eq(CoreActions.UPDATE));
    
    // now deny
    given(collectionAuthorizationService.authorizeCollectionAction(any(), any())).willReturn(false);
    
    // store collection
    RestAssured.given()
        .accept(ContentType.JSON)
        .body(createTestCollection()).contentType(ContentType.JSON)
        .auth().preemptive().basic("user", "password")
      .when()
        .put("/api/v1/collections/{id}", F)
      .then()
        .statusCode(403);
    // @formatter:on
  }

  private Collection createTestCollection() throws JsonProcessingException {
    Collection collection = new Collection();
    collection.setTypeId("foo");
    return collection;
  }

  @Test
  public void testThat_authorizationOnRetrieveIsVerified() throws Exception {
    // @formatter:off
    // retrieve it
    given(mockCollectionService.getCurrent(F)).willAnswer(a -> Optional.of(createTestCollection()));

    // allow
    given(collectionAuthorizationService.authorizeCollectionAction(any(), any())).willReturn(true);
    
    RestAssured
      .given()
        .accept(ContentType.JSON)
        .body(createTestCollection()).contentType(ContentType.JSON)
        .auth().preemptive().basic("user", "password")
      .when()
        .get("/api/v1/collections/{id}", F)
      .then()
        .statusCode(200);

    verify(collectionAuthorizationService).authorizeCollectionAction(isNotNull(), eq(CoreActions.GET));
    
    // now deny
    given(collectionAuthorizationService.authorizeCollectionAction(any(), any())).willReturn(false);
    
    RestAssured
      .given()
        .accept(ContentType.JSON)
        .body(createTestCollection()).contentType(ContentType.JSON)
        .auth().preemptive().basic("user", "password")
      .when()
        .get("/api/v1/collections/{id}", F)
      .then()
        .statusCode(403);
    // @formatter:on
  }
}
