package com.voting.system.usercandidate.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PartyCandidateMapping {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long id;

  @OneToOne
  @JoinColumn(name = "partyId")
  private Party party;

  @OneToOne
  @JoinColumn(name = "userId")
  private Candidate createdByCandidate;

}