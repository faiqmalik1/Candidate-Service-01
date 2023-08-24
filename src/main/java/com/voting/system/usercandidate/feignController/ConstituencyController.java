package com.voting.system.usercandidate.feignController;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import resources.constituency.ConstituencyListResponseDTO;
import resources.constituency.ConstituencyResponseDTO;
import resources.constituency.PoolingResponseDTO;

@FeignClient(name = "CONSTITUENCY-SERVICE")
public interface ConstituencyController {

  /**
   * to retrieve constituency by id
   *
   * @param constituencyId:id of constituency
   * @return :return constituency if found else return null
   */
  @GetMapping("/constituency/id/{constituencyId}")
  public ConstituencyResponseDTO retrieveConstituencyById(@PathVariable(name = "constituencyId") long constituencyId);

  /**
   * to retrieve all the constituencies
   *
   * @return :return the list of available constituencies if found else return null
   */
  @GetMapping("/constituency")
  public ConstituencyListResponseDTO retrieveConstituencies();


  /**
   * to retrieve started polling or the last polling
   *
   * @return : return the polling response else return null
   */
  @GetMapping("/polling/status")
  public PoolingResponseDTO isPoolingStarted();

}