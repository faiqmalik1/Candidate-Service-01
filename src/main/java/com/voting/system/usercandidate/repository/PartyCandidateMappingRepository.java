package com.voting.system.usercandidate.repository;

import com.voting.system.usercandidate.model.PartyCandidateMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyCandidateMappingRepository extends JpaRepository<PartyCandidateMapping, Long> {
}
