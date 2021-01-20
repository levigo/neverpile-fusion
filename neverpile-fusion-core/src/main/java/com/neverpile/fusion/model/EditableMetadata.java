package com.neverpile.fusion.model;

/**
 * Defined editable metadata for creating and editing of elements.
 */
public class EditableMetadata {
  /**
   * The list of editable metadata when creating new elements.
   */
  EditableMetadataEntry[] create;

  /**
   * The list of editable metadata when editing existing elements.
   */
  EditableMetadataEntry[] edit;

  public EditableMetadataEntry[] getCreate() {
    return create;
  }

  public void setCreate(EditableMetadataEntry[] create) {
    this.create = create;
  }

  public EditableMetadataEntry[] getEdit() {
    return edit;
  }

  public void setEdit(EditableMetadataEntry[] edit) {
    this.edit = edit;
  }

  /**
   * Metadata element containing the property key and a corresponding label.
   */
  public static class EditableMetadataEntry {
    String key;
    String label;

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public String getLabel() {
      return label;
    }

    public void setLabel(String label) {
      this.label = label;
    }
  }
}
