package com.neverpile.fusion.jpa.seen;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import com.neverpile.fusion.configuration.FusionModelMapperConfiguration;
import com.neverpile.fusion.configuration.JacksonConfiguration;
import com.neverpile.fusion.model.seen.SeenUnseenInfo;

@DataJpaTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {
    JacksonAutoConfiguration.class, JacksonConfiguration.class, FusionModelMapperConfiguration.class,
    JPASeenUnseenServiceConfiguration.class
})
public class JPASeenUnseenServiceTest {
  @Autowired
  private JPASeenUnseenService seenUnseenService;

  @Test
  public void testThat_accessingNonexistingInfoYieldsAllUnseen() {
    isAllUnseen(seenUnseenService.get("foo", "bar"));
  }


  private void isAllUnseen(final SeenUnseenInfo doesNotExist) {
    assertThat(doesNotExist.getSeenAllBefore()).isNull();
    assertThat(doesNotExist.getSeenKeys()).isEmpty();
    assertThat(doesNotExist.getUnseenKeys()).isEmpty();
  }

  
  @Test
  public void testThat_infoCanBeSaved() {
    SeenUnseenInfo seenUnseenInfo = new SeenUnseenInfo();
    seenUnseenInfo.setSeenAllBefore(Instant.ofEpochMilli(12345L));
    seenUnseenInfo.setSeenKeys(Set.of("foo", "bar", "baz", "yada;yada", ";yada", "yada;"));
    seenUnseenInfo.setUnseenKeys(Set.of("un", "seen", "keys"));
    
    seenUnseenService.save("aContext", "aPrincipal", seenUnseenInfo);
    
    SeenUnseenInfo reloaded = seenUnseenService.get("aContext", "aPrincipal");
    
    assertThat(reloaded.getSeenAllBefore()).isEqualTo(Instant.ofEpochMilli(12345L));
    assertThat(reloaded.getSeenKeys()).containsExactlyInAnyOrder("foo", "bar", "baz", "yada;yada", ";yada", "yada;");
    assertThat(reloaded.getUnseenKeys()).containsExactlyInAnyOrder("un", "seen", "keys");
  }   
  
  @Test
  public void testThat_infoCanBeDeleted() {
    SeenUnseenInfo seenUnseenInfo = new SeenUnseenInfo();
    seenUnseenInfo.setSeenAllBefore(Instant.ofEpochMilli(12345L));
    
    // save something
    seenUnseenService.save("aContext", "aPrincipal", seenUnseenInfo);
    SeenUnseenInfo reloaded = seenUnseenService.get("aContext", "aPrincipal");
    assertThat(reloaded.getSeenAllBefore()).isEqualTo(Instant.ofEpochMilli(12345L));
    
    // delete and verify it is gone
    seenUnseenService.delete("aContext", "aPrincipal");
    isAllUnseen(seenUnseenService.get("aContext", "aPrincipal"));
  }    
}
