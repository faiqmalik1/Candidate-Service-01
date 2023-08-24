package com.voting.system.usercandidate.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Candidate {

  @Id
  private long userId;

  private String post;

  @ManyToOne
  @JoinColumn(name = "partyId")
  private Party party;
  private long constituencyId;
  private Date createdAt;
  private String status;

}