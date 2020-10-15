package com.neverpile.fusion.rest;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.eq;

import java.time.Instant;
import java.util.HashSet;

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
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neverpile.fusion.api.SeenUnseenService;
import com.neverpile.fusion.model.seen.SeenUnseenInfo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = "server.error.include-message=always")
public class SeenUnseenServiceTest extends AbstractRestAssuredTest {

  @TestConfiguration
  public static class ServiceConfig {
    @Bean
    SeenUnseenResource resource() {
      return new SeenUnseenResource();
    }
  }

  @MockBean
  SeenUnseenService mockSeenUnseenService;

  @Autowired
  ObjectMapper objectMapper;

  @BeforeEach
  public void reset() {

  }

  @Test
  public void testThat_emptySeenUnseenInfoCanBeRetrieved() throws Exception {
    // @formatter:off
    // retrieve it
    BDDMockito
      .given(mockSeenUnseenService.get("aKey", "user"))
      .willAnswer((a) -> new SeenUnseenInfo());

    RestAssured
      .given()
        .accept(ContentType.JSON)
        .auth().preemptive().basic("user", "password")
      .when()
        .log().all()
        .get("/api/v1/seen/aKey")
      .then()
        .log().all()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("seenAllBefore", nullValue())
        .body("seenKeys", empty())
        .body("unseenKeys", empty());
    // @formatter:on
  }

  @Test
  public void testThat_existingSeenUnseenInfoCanBeRetrieved() throws Exception {
    // @formatter:off
    // retrieve it
    BDDMockito
      .given(mockSeenUnseenService.get("aKey", "user"))
      .willAnswer((a) -> {
        SeenUnseenInfo i = new SeenUnseenInfo();
        i.setSeenAllBefore(Instant.parse("2020-01-01T00:00:00Z"));
        i.setSeenKeys(new HashSet<String>(asList("foo","bar")));
        i.setUnseenKeys(new HashSet<String>(asList("baz", "yada")));
        return i;
      });
    
    RestAssured
      .given()
        .accept(ContentType.JSON)
        .auth().preemptive().basic("user", "password")
      .when()
        .log().all()
        .get("/api/v1/seen/aKey")
      .then()
        .log().all()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("seenAllBefore", equalTo("2020-01-01T00:00:00Z"))
        .body("seenKeys", containsInAnyOrder("foo", "bar"))
        .body("unseenKeys", containsInAnyOrder("baz", "yada"));
    // @formatter:on
  }
  
  @Test
  public void testThat_seenUnseenInfoCanBeUpdated() throws Exception {
    // @formatter:off
    ArgumentCaptor<SeenUnseenInfo> seenInfoC = ArgumentCaptor.forClass(SeenUnseenInfo.class);

    BDDMockito
      .willDoNothing()
      .given(mockSeenUnseenService)
        .save(eq("aKey"), eq("user"), seenInfoC.capture());
    
    SeenUnseenInfo i = new SeenUnseenInfo();
    i.setSeenAllBefore(Instant.parse("2020-01-01T00:00:00Z"));
    i.setSeenKeys(new HashSet<String>(asList("foo","bar")));
    i.setUnseenKeys(new HashSet<String>(asList("baz", "yada")));
    
    RestAssured
      .given()
        .accept(ContentType.JSON)
        .body(i).contentType(ContentType.JSON)
        .auth().preemptive().basic("user", "password")
      .when()
        .log().all()
        .put("/api/v1/seen/aKey")
      .then()
        .log().all()
        .statusCode(204);
    // @formatter:on
    
    SeenUnseenInfo info = seenInfoC.getValue();
    assertThat(info.getSeenAllBefore()).isEqualTo(Instant.parse("2020-01-01T00:00:00Z"));
    assertThat(info.getSeenKeys()).containsExactlyInAnyOrder("foo","bar");
    assertThat(info.getUnseenKeys()).containsExactlyInAnyOrder("baz","yada");
  }
  
  @Test
  public void testThat_seenUnseenInfoCanBeDeleted() throws Exception {
    // @formatter:off
    BDDMockito
      .willDoNothing()
      .given(mockSeenUnseenService)
        .delete(eq("aKey"), eq("user"));
    
    SeenUnseenInfo i = new SeenUnseenInfo();
    i.setSeenAllBefore(Instant.parse("2020-01-01T00:00:00Z"));
    i.setSeenKeys(new HashSet<String>(asList("foo","bar")));
    i.setUnseenKeys(new HashSet<String>(asList("baz", "yada")));
    
    RestAssured
      .given()
        .accept(ContentType.JSON)
        .auth().preemptive().basic("user", "password")
      .when()
        .log().all()
        .delete("/api/v1/seen/aKey")
      .then()
        .log().all()
        .statusCode(204);
    
    BDDMockito.verify(mockSeenUnseenService)
      .delete(eq("aKey"), eq("user"));
    // @formatter:on
  }
}
