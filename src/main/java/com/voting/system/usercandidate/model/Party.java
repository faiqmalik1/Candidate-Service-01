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
public class Party {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long partyId;

  private String partyName;

  private String partySymbol;

  private Date createdAt;

}