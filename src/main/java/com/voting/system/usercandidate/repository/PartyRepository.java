package com.voting.system.usercandidate.repository;

import com.voting.system.usercandidate.model.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long> {

  Optional<Party> findByPartyName(String partyName);
}
