package com.neverpile.fusion.rest;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;


public abstract class AbstractRestAssuredTest {
  @LocalServerPort
  int port;

  @Autowired
  protected ObjectMapper objectMapper;

  @BeforeEach
  public void setupRestAssured() {
    RestAssured.port = port;
  }
}