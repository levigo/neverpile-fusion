package com.neverpile.fusion.api;

import java.util.List;
import java.util.Optional;

import com.neverpile.fusion.model.CollectionType;

/**
 * A CollectionTypeService is responsible for managing the collection types defined in the system.
 */
public interface CollectionTypeService {
  /**
   * Return the collection type with the given id.
   * 
   * @param id the id
   * @return the collection type as an {@link Optional}
   */
  Optional<CollectionType> get(String id);

  /**
   * Return all defined collection types.
   * 
   * @return a list of types 
   */
  List<CollectionType> getAllTypes();
  
  /**
   * Save a collection type. Serves both as a method for adding new types as well as for updating
   * existing ones.
   * 
   * @param type the type to add or update
   */
  void save(CollectionType type);

  /**
   * Delete the collection type with the given id. Returns <code>false</code> if no matching type
   * exists.
   * 
   * @param id the id
   * @return <code>true</code> if the delete succeeded, <code>false</code> otherwise
   */
  boolean delete(String id);

}
