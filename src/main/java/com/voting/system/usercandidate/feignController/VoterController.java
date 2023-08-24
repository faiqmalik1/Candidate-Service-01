package com.voting.system.usercandidate.feignController;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "VOTER-SERVICE", configuration = FeignClientInterceptorConfig.class)
public interface VoterController {

  /**
   * get the count of votes of single candidate
   *
   * @param token:token  of user to be validated
   * @param candidateId: id of candidate whose votes to be retrieve
   * @param pollingId:   id of polling
   * @return : return the count of votes
   */
  @GetMapping("/votes/get")
  public long retrieveCounts(@CookieValue("Authorization") String token, @RequestParam long candidateId, @RequestParam long pollingId);
}
