package com.neverpile.fusion.jpa.seen;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.neverpile.fusion.api.SeenUnseenService;
import com.neverpile.fusion.model.seen.SeenUnseenInfo;

/**
 * An implementation of {@link SeenUnseenService} which persists seen/unseen info to a SQL database
 * via JPA.
 */
@Component
public class JPASeenUnseenService implements SeenUnseenService {
  private final SeenUnseenInfoRepository repository;
  private final ModelMapper modelMapper;

  @Autowired
  public JPASeenUnseenService(final SeenUnseenInfoRepository repository, final ModelMapper modelMapper) {
    this.repository = repository;
    this.modelMapper = modelMapper;
  }

  @Override
  public SeenUnseenInfo get(final String contextKey, final String principalKey) {
    return repository //
        .findById(new ContextKeyAndPrincipal(contextKey, principalKey)) //
        .map(e -> modelMapper.map(e, SeenUnseenInfo.class)) //
        .orElseGet(() -> new SeenUnseenInfo());
  }

  @Override
  public void save(final String contextKey, final String principalKey, final SeenUnseenInfo info) {
    SeenUnseenInfoEntity e = modelMapper.map(info, SeenUnseenInfoEntity.class);
    e.setContextKey(contextKey);
    e.setPrincipalKey(principalKey);

    repository.save(e);
  }

  @Override
  public void delete(final String contextKey, final String principalKey) {
    repository.deleteById(new ContextKeyAndPrincipal(contextKey, principalKey));
  }

}
