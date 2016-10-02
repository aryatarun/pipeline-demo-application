package org.pongmatcher.repositories;

import org.pongmatcher.domain.MatchRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@SuppressWarnings(value = "")
public interface MatchRequestRepository extends JpaRepository<MatchRequest, String> {

    MatchRequest findByUuid(String uuid);
    List<MatchRequest> findByOrderByIdAsc();

}
