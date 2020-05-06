package com.neverpile.fusion.api;

/**
 * A CollectionIdStrategy is responsible for generating new and validating externally-supplied ids
 * for collections and collection elements.
 */
public interface CollectionIdStrategy {

  /**
   * Create a new collection id.
   * 
   * @return a new collection id
   */
  String creatcollectionId();

  /**
   * Validate the given id, return <code>true</code> if it is acceptable.
   * 
   * @param id the id
   * @return <code>true</code> if acceptable
   */
  boolean validateCollectionId(String id);

  /**
   * Create a new element id.
   * 
   * @return a new element id
   */
  String createElementId();

  /**
   * Validate the given id, return <code>true</code> if it is acceptable.
   * 
   * @param id the id
   * @return <code>true</code> if acceptable
   */
  boolean validateElementId(String id);

}