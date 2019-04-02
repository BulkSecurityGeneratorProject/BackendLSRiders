package com.lsriders.backend.repository;

import com.lsriders.backend.domain.Event;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Event entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select event from Event event where event.creador.login = ?#{principal.username}")
    List<Event> findByCreadorIsCurrentUser();

}
