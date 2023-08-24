package com.voting.system.usercandidate.repository;

import com.voting.system.usercandidate.model.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

  Page<Candidate> findAllByStatus(String status, Pageable pageable);

  Page<Candidate> findAllByStatusAndConstituencyId(String status, long constituencyId, Pageable pageable);

}