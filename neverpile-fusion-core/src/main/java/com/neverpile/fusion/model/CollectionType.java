package com.neverpile.fusion.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A type definition for a collection. Each collection belongs to one of the defined collection
 * types. The type describes the tags permitted for elements of a collection and contains rules that
 * are used to generate views.
 */
public class CollectionType {
  /**
   * The type's unique id.
   */
  private String id;

  /**
   * The type's name.
   */
  private String name;

  /**
   * A possibly longer description of the type.
   */
  private String description;

  /**
   * The list of tags permitted for elements of this collection type.
   */
  private List<String> permittedTags;

  /**
   * Whether to allow arbitrary tags in addition to the ones defined in {@link #permittedTags}.
   */
  private boolean allowAllTags;

  /**
   * The list of views defined for this collection type.
   */
  private List<View> views = new ArrayList<>();

  public String getId() {
    return id;
  }

  public void setId(final String name) {
    this.id = name;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(final String description) {
    this.description = description;
  }

  public List<String> getPermittedTags() {
    return permittedTags;
  }

  public void setPermittedTags(final List<String> tags) {
    this.permittedTags = tags;
  }

  public boolean isAllowAllTags() {
    return allowAllTags;
  }

  public void setAllowAllTags(final boolean allowAllTags) {
    this.allowAllTags = allowAllTags;
  }

  public List<View> getViews() {
    return views;
  }

  public void setViews(final List<View> views) {
    this.views = Objects.requireNonNull(views, "views");
  }

}
