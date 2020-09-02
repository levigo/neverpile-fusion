package com.neverpile.fusion.jpa.seen;

import org.springframework.data.repository.CrudRepository;

/**
 * A spring CRUD repository for seen/unseen info.
 */
public interface SeenUnseenInfoRepository extends CrudRepository<SeenUnseenInfoEntity, ContextKeyAndPrincipal>  {

}
