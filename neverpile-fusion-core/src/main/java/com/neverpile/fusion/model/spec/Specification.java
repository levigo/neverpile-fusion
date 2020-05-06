package com.neverpile.fusion.model.spec;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A Specification describes the nature of a collection element. Different subclasses are used for
 * different element types.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = Artifact.class, name = "artifact"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = CompositePaged.class, name = "compositePaged"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = CollectionReference.class, name = "collectionReference"),
    @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = Query.class, name = "query")
})
public class Specification {
}
