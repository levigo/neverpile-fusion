package com.neverpile.fusion.authorization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.neverpile.common.authorization.api.AuthorizationContext;
import com.neverpile.common.authorization.api.AuthorizationService;
import com.neverpile.common.authorization.api.CoreActions;
import com.neverpile.fusion.configuration.JacksonConfiguration;
import com.neverpile.fusion.model.Collection;
import com.neverpile.fusion.model.Collection.State;


@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = {
    JacksonAutoConfiguration.class, JacksonConfiguration.class, CollectionAuthorizationService.class, CollectionAuthorizationContextContributor.class
})
public class MetadataAuthContextTest {
  @MockBean
  AuthorizationService authorizationService;

  @Autowired
  CollectionAuthorizationService collectionAuthorizationService;

  @Autowired
  ObjectMapper objectMapper;

  @Test
  public void testThat_collectionAuthorizationContextValuesAreCorrect() {
    ArgumentCaptor<AuthorizationContext> authContextC = ArgumentCaptor.forClass(AuthorizationContext.class);
    given(authorizationService.isAccessAllowed(any(), any(), authContextC.capture())).willReturn(true);

    collectionAuthorizationService.authorizeCollectionAction(createTestCollection(), CoreActions.GET);
    
    AuthorizationContext ac = authContextC.getValue();
    assertThat(ac.resolveValue("collection.type")).isEqualTo("aCollectionType");
    assertThat(ac.resolveValue("collection.createdBy")).isEqualTo("user");
    assertThat(ac.resolveValue("collection.dateCreated")).isEqualTo(Instant.ofEpochMilli(1));
    assertThat(ac.resolveValue("collection.metadata.foo")).isEqualTo("bar");
    assertThat(ac.resolveValue("collection.metadata.bar[0]")).isEqualTo("yada1");
    assertThat(ac.resolveValue("collection.metadata.bar[1]")).isEqualTo("yada2");
    assertThat(ac.resolveValue("collection.metadata.empty[0]")).isNull();
    assertThat(ac.resolveValue("collection.metadata.empty")).isNull();
    assertThat(ac.resolveValue("collection.metadata.does-not-exist")).isNull();
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
    ObjectNode metadata = objectMapper.createObjectNode();
    metadata.put("foo", "bar").putArray("bar").add("yada1").add("yada2");
    metadata.putArray("empty");
    f.setMetadata(metadata);
    return f;
  }
}
