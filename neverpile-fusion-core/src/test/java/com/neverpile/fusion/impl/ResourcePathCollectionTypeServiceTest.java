package com.neverpile.fusion.impl;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.StreamUtils.copyToString;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neverpile.fusion.configuration.JacksonConfiguration;
import com.neverpile.fusion.model.CollectionType;
import com.neverpile.fusion.model.View;
import com.neverpile.fusion.model.rules.javascript.JavascriptRule;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, classes = {
    JacksonAutoConfiguration.class, JacksonConfiguration.class, ResourcePathCollectionTypeService.class
}, properties = {
    "neverpile-fusion.resource-path-collection-type-service.enabled=true",
    "neverpile-fusion.resource-path-collection-type-service.base-path=classpath:/collectionTypes/"
})
public class ResourcePathCollectionTypeServiceTest {

  @Autowired
  private ResourcePathCollectionTypeService service;

  @Autowired
  private ObjectMapper jsonMapper;

  @Autowired
  @Qualifier("yaml")
  private Supplier<ObjectMapper> yamlMapper;

  @Test
  public void testThat_collectionTypeCanBeReadFromJson() {
    verifyTestType(service.get("aJsonCollectionType"));
  }

  @Test
  public void testThat_collectionTypeCanBeReadFromYaml() {
    verifyTestType(service.get("aYamlCollectionType"));
  }

  @Test
  public void testThat_collectionTypeCanBeMarshalledToJson() throws IOException {
    assertThat(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(makeTestType())) //
        .isEqualToNormalizingWhitespace(
            copyToString(getClass().getResourceAsStream("/collectionTypes/aJsonCollectionType.json"), UTF_8));
  }

  @Test
  public void testThat_collectionTypeCanBeMarshalledToYaml() throws IOException {
    assertThat(yamlMapper.get().writerWithDefaultPrettyPrinter().writeValueAsString(makeTestType())) //
        .isEqualToNormalizingWhitespace(
            copyToString(getClass().getResourceAsStream("/collectionTypes/aYamlCollectionType.yaml"), UTF_8));
  }

  private CollectionType makeTestType() {
    CollectionType t = new CollectionType();

    t.setId("aCollectionType");
    t.setPermittedTags(Arrays.asList("foo", "bar", "baz"));
    t.setName("A very basic collection type");
    t.setDescription("A collection type used to test fusion");

    View v1 = new View();
    v1.setName("Default");

    t.getViews().add(v1);

    JavascriptRule cr1 = new JavascriptRule();
    cr1.setName("Put elements with foo metadata under a foo node");
    cr1.setScriptCode("if(element.metadata.foo) {" + "  createElement(element, 'All foos')"
        + "    .withProperty('foo-type', element.metadata.foo)" + "    .withVisualization('html', '<b>FOO</b>');"
        + "  createNode('All bars');" + "}");
    v1.getElementRules().add(cr1);

    JavascriptRule cr2 = new JavascriptRule();
    cr2.setName("Put elements tagged yada under a yada node");
    cr2.setScriptCode("if(element.tags.includes('yada'))" + "  createElement(element, 'All yadas')"
        + "    .withVisualization('html', '<b>YADA ' + element.metadata.foo + '</b>');");
    v1.getElementRules().add(cr2);

    JavascriptRule cr3 = new JavascriptRule();
    cr3.setName("All elements chronologically");
    cr3.setScriptCode("createNode('Chronological', element.dateCreated.toISOString())"
        + "  .withProperty('sort-by-date', element.dateCreated.getTime())" + "  .withElement(element);");
    v1.getElementRules().add(cr3);

    JavascriptRule cr4 = new JavascriptRule();
    cr4.setName("Put elements under nodes by their first tag");
    cr4.setScriptCode(
        "element.tags.forEach(" + "  function(t){" + "    createElement(element, 'By tag', 'Tag: ' + t)" + "  }" + ")");
    v1.getElementRules().add(cr4);

    JavascriptRule nr1 = new JavascriptRule();
    nr1.setName("Sort chronological branch chronologically");
    nr1.setScriptCode("withNode(" + "function(n) {" + "  n.children.sort(function(a,b){"
        + "    return a.properties['sort-by-date'] - b.properties['sort-by-date'];" + "  })" + "}, "
        + "'Chronological')");
    v1.getTreeRules().add(nr1);

    View v2 = new View();
    v2.setName("Error");

    t.getViews().add(v2);

    JavascriptRule cr5 = new JavascriptRule();
    cr5.setName("This rule always errors");
    cr5.setScriptCode("barf();");
    v2.getElementRules().add(cr5);

    return t;
  }

  private void verifyTestType(final Optional<CollectionType> ctOptional) {
    assertThat(ctOptional).isNotEmpty();

    CollectionType ct = ctOptional.get();
    assertThat(ct.getPermittedTags()).contains("foo", "bar", "baz");
    assertThat(ct.getViews()).hasSize(2);
    assertThat(ct.getViews().get(0).getName()).isEqualTo("Default");
    assertThat(ct.getViews().get(0).getElementRules()).hasSize(4);
    assertThat(ct.getViews().get(0).getElementRules().get(2).getName()).contains("chronologically");
    assertThat(ct.getViews().get(0).getElementRules().get(2)).isInstanceOf(JavascriptRule.class);
    assertThat(((JavascriptRule) ct.getViews().get(0).getElementRules().get(2)).getScriptCode()).contains(
        "createNode('Chronological'");
    assertThat(ct.getViews().get(0).getTreeRules()).hasSize(1);
    assertThat(ct.getViews().get(0).getTreeRules().get(0).getName()).contains("chronologically");
    assertThat(ct.getViews().get(0).getTreeRules().get(0)).isInstanceOf(JavascriptRule.class);
    assertThat(((JavascriptRule) ct.getViews().get(0).getTreeRules().get(0)).getScriptCode()).contains(
        "withNode(function(n)");
  }
}
