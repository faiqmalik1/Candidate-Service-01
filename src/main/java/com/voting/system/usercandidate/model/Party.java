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
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private long partyId;
  private String partyName;
  @Lob
  @Column(columnDefinition = "LONGBLOB")
  private byte[] partySymbol;
  private Date createdAt;

}