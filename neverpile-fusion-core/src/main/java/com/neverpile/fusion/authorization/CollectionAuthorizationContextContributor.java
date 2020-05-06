package com.neverpile.fusion.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.neverpile.common.authorization.api.AuthorizationContext;
import com.neverpile.common.authorization.api.AuthorizationContextContributor;
import com.neverpile.common.specifier.Specifier;
import com.neverpile.fusion.model.Collection;

/**
 * An AuthorizationContextContributor contributing relevant context information for collections. The provided context information is:
 * <dl>
 * <dt><code>type</code>
 * <dd>the collection type
 * <dt><code>metadata...</code>
 * <dd>The collection metadata. Arbitrary elements within the metadata may be addressed by suffixing <code>metadata.</code> with a JsonPath expression. 
 * <dt><code>state</code>
 * <dd>the collection state
 * <dt><code>createdBy</code>
 * <dd>the user who created the collection
 * <dt><code>dateCreated</code>
 * <dd>the date when the collection was created
 * <dt><code>dateModified</code>
 * <dd>the date when the collection was last modified
 * </dl>
 */
@Component
public class CollectionAuthorizationContextContributor implements AuthorizationContextContributor<Collection> {
  private final Configuration configuration;

  @Autowired
  public CollectionAuthorizationContextContributor(final ObjectMapper mapper) {
    configuration = Configuration.builder().jsonProvider(new JacksonJsonNodeJsonProvider(mapper)).build();
  }
  
  @Override
  public AuthorizationContext contributeAuthorizationContext(final Collection source) {
    return new AuthorizationContext() {
      @Override
      public Object resolveValue(final Specifier elementKey) {
        if (elementKey.empty())
          return null;

        switch(elementKey.head()) {
          case "type":
            return source.getTypeId();
            
          case "metadata":
            JsonPath jsonPath = JsonPath.compile(elementKey.suffix().asString());
            try {
              Object result = JsonPath.using(configuration).parse(source.getMetadata()).read(jsonPath);
              if(result instanceof JsonNode) {
                return jsonNodeToPrimitive((JsonNode) result);
              }
              return result;
            } catch (PathNotFoundException e) {
              return null;
            }
            
          case "state":
            return source.getState().name();
            
          case "createdBy":
            return source.getCreatedBy();
            
          case "dateCreated":
            return source.getDateCreated();
            
          case "dateModified":
            return source.getDateModified();
        }

        return null;
      }

      /**
       * Convert a JsonNode to its equivalent primitive if possible - otherwise return the node as-is.
       *  
       * @param n
       * @return
       */
      private Object jsonNodeToPrimitive(final JsonNode n) {
        if(n.isTextual())
          return n.asText();
        if(n.isBoolean())
          return n.asBoolean();
        if(n.isFloatingPointNumber())
          return n.asDouble();
        if(n.isIntegralNumber())
          return n.asLong();
        if(n.isEmpty())
          return null;
        return n;
      }
    };
  }

}
