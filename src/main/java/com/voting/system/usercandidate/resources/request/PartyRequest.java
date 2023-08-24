package com.voting.system.usercandidate.resources.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PartyRequest {

  private String partyName;
  private long userId;

}