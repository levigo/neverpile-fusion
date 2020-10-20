package com.neverpile.fusion.model.rules.javascript;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neverpile.fusion.configuration.JacksonConfiguration;
import com.neverpile.fusion.model.Collection;
import com.neverpile.fusion.model.Collection.State;
import com.neverpile.fusion.model.CollectionType;
import com.neverpile.fusion.model.Element;
import com.neverpile.fusion.model.View;
import com.neverpile.fusion.model.rules.Layout;
import com.neverpile.fusion.model.spec.Artifact;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = {
    JacksonAutoConfiguration.class, JacksonConfiguration.class, JavascriptViewLayoutEngine.class
})
public class LayoutEngineTest {
  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  JavascriptViewLayoutEngine engine;

  @Test
  public void testThat_globalRulesAreApplied() throws JSONException, IOException {
    CollectionType t = new CollectionType();

    t.setId("aCollectionType");

    JavascriptRule cr1 = new JavascriptRule();
    cr1.setName("define greeter");
    cr1.setScriptCode("function greet(x) { return 'Hello, ' + x; } ");
    t.getGlobalRules().add(cr1);

    View v1 = new View();
    v1.setName("Default");

    t.getViews().add(v1);

    JavascriptRule g1 = new JavascriptRule();
    g1.setName("use greeter");
    g1.setScriptCode("createElementNode(element).withVisualization('html', greet(element.metadata.foo));");
    v1.getElementRules().add(g1);

    JavascriptRule g2 = new JavascriptRule();
    g2.setName("use greeter");
    g2.setScriptCode("titleVsualization('html', collection.metadata.title + ' (' + collection.createdBy + ')');");
    v1.getElementRules().add(g2);
    
    Collection f = new Collection();
    f.setId("anId");
    f.setTypeId("aCollectionType");
    f.setState(State.Active);
    f.setCreatedBy("user");
    f.setMetadata(objectMapper.createObjectNode().put("title", "All your base are belong to us!"));

    Element e1 = new Element();
    e1.setId("anElementId");
    e1.setTags(Arrays.asList("foo", "bar"));
    e1.setMetadata(objectMapper.createObjectNode().put("foo", "bar1"));

    f.getElements().add(e1);

    Layout root = engine.layoutTree(f, t);

    String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);

    //@formatter:off
    JSONAssert.assertEquals("{"
        + "  \"collectionTypeId\" : \"aCollectionType\","
        + "  \"titleVisualization\" : {"
        + "    \"html\" : \"All your base are belong to us! (user)\""
        + "  },"
        + "  \"viewLayouts\" : {"
        + "    \"Default\" : {"
        + "      \"view\" : \"Default\","
        + "      \"structureTree\" : {"
        + "        \"name\" : \"root\","
        + "        \"children\" : [ {"
        + "          \"visualization\" : {"
        + "            \"html\" : \"Hello, bar1\""
        + "          },"
        + "          \"name\" : \"anElementId\","
        + "          \"elementId\" : \"anElementId\","
        + "          \"initiallyExpanded\" : false"
        + "        } ],"
        + "        \"initiallyExpanded\" : false"
        + "      }"
        + "    }"
        + "  }"
        + "}", s, JSONCompareMode.LENIENT);
    //@formatter:on
  }

  @Test
  public void performTestLayout() throws JSONException, IOException {
    CollectionType t = makeTestType();
    Layout root = engine.layoutTree(createTestCollection(), t);

    String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);

    JSONAssert.assertEquals(s,
        StreamUtils.copyToString(getClass().getResourceAsStream("test-layout.json"), StandardCharsets.UTF_8),
        JSONCompareMode.LENIENT);
  }

  private CollectionType makeTestType() {
    CollectionType t = new CollectionType();

    t.setId("aCollectionType");

    View v1 = new View();
    v1.setName("Default");

    t.getViews().add(v1);

    //@formatter:off
    JavascriptRule cr1 = new JavascriptRule();
    cr1.setName("Put elements with foo metadata under a foo node");
    cr1.setScriptCode("if(element.metadata.foo) {" + "  createNode('All foos', element.id)"
        + "    .withElement(element)" 
        + "      .withProperty('foo-type', element.metadata.foo)"
        + "      .withVisualization('html', '<b>FOO</b>');" 
        + "  createNode('All bars');" + "}");
    v1.getElementRules().add(cr1);

    JavascriptRule cr2 = new JavascriptRule();
    cr2.setName("Put elements tagged yada under a yada node");
    cr2.setScriptCode("if(element.tags.includes('yada'))" 
      + "  createNode('All yadas')" 
      + "    .withElement(element)"
      + "    .withVisualization('html', '<b>YADA ' + element.metadata.foo + '</b>');");
    v1.getElementRules().add(cr2);

    JavascriptRule cr3 = new JavascriptRule();
    cr3.setName("All elements chronologically");
    cr3.setScriptCode("createNode('Chronological', element.dateCreated.toISOString())"
        + "  .withProperty('sort-by-date', element.dateCreated.getTime())" 
        + "  .withElement(element);");
    v1.getElementRules().add(cr3);

    JavascriptRule cr4 = new JavascriptRule();
    cr4.setName("Put elements under nodes by their first tag");
    cr4.setScriptCode("element.tags.forEach(" 
        + "  function(t){" 
        + "    createNode('By tag', 'Tag: ' + t)"
        + "      .withElement(element);" + "  }" 
        + ")");
    v1.getElementRules().add(cr4);

    JavascriptRule nr1 = new JavascriptRule();
    nr1.setName("Sort chronological branch chronologically");
    nr1.setScriptCode("withNode(" + "function(n) {" 
        + "  n.children.sort(function(a,b){"
        + "    return a.properties['sort-by-date'] - b.properties['sort-by-date'];" 
        + "  })" 
        + "}, "
        + "'Chronological')");
    v1.getTreeRules().add(nr1);
    //@formatter:on

    View v2 = new View();
    v2.setName("Error");

    t.getViews().add(v2);

    JavascriptRule cr5 = new JavascriptRule();
    cr5.setName("This rule always errors");
    cr5.setScriptCode("barf();");
    v2.getElementRules().add(cr5);

    return t;
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
    e1.setDateCreated(Instant.ofEpochMilli(4711));
    e1.setDateModified(Instant.ofEpochMilli(4711));
    e1.setTags(Arrays.asList("foo", "bar"));
    e1.setMetadata(objectMapper.createObjectNode().put("foo", "bar1"));

    Artifact a1 = new Artifact();
    a1.setContentURI("text:collection:from://some/where1");
    a1.setMediaType(MediaType.TEXT_PLAIN);
    e1.setSpecification(a1);

    f.getElements().add(e1);

    Element e2 = new Element();
    e2.setId("anotherElementId");
    e2.setDateCreated(Instant.ofEpochMilli(815));
    e2.setDateModified(Instant.ofEpochMilli(815));
    e2.setTags(Arrays.asList("bla", "blubb"));
    e2.setMetadata(objectMapper.createObjectNode().put("foo", "bar2"));

    Artifact a2 = new Artifact();
    a2.setContentURI("text:collection:from://some/where2");
    a2.setMediaType(MediaType.TEXT_PLAIN);
    e2.setSpecification(a2);
    f.getElements().add(e2);

    Element e3 = new Element();
    e3.setId("anotherElementId");
    e3.setDateCreated(Instant.ofEpochMilli(42));
    e3.setDateModified(Instant.ofEpochMilli(42));
    e3.setTags(Arrays.asList("yada"));
    e3.setMetadata(objectMapper.createObjectNode().put("foo", "bar2"));

    Artifact a3 = new Artifact();
    a3.setContentURI("text:collection:from://some/where3");
    a3.setMediaType(MediaType.TEXT_PLAIN);
    e3.setSpecification(a3);

    f.getElements().add(e3);
    return f;
  }
}
