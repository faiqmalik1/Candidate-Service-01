package com.voting.system.usercandidate.controller;

import com.voting.system.usercandidate.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import resources.ResponseDTO;
import resources.candidate.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/candidate")
public class CandidateController {

  private final CandidateService candidateService;

  /**
   * returns the list of parties available
   *
   * @param detail: to get parties detail including party image and creation date
   * @return :list of parties
   */
  @GetMapping("/party")
  public PartyListResponseDTO retrieveAllParties(@RequestParam(value = "detail") boolean detail) {
    return candidateService.retrieveParties(detail);
  }

  /**
   * to register the user as candidate and to update its role
   *
   * @param token             : to validate user request
   * @param candidateRequest: json containing data for creating candidate
   * @return :success response if every thing goes fine else failure response
   */
  @PostMapping("/register")
  public ResponseDTO registerUserAsCandidate(@CookieValue("Authorization") String token, @RequestParam(value = "candidate") String candidateRequest) {
    return candidateService.registerUserAsCandidate(token, candidateRequest);
  }

  /**
   * it will retrieve candidate with its details
   *
   * @param token:       token to validate request
   * @param candidateId: id of candidate
   * @return :return candidate response if candidate present else return null
   */
  @GetMapping("/{candidateId}")
  public CandidateResponseDTO retrieveCandidateDetail(@CookieValue("Authorization") String token, @PathVariable(name = "candidateId") long candidateId) {
    return candidateService.retrieveCandidateDetail(token, candidateId);
  }

  /**
   * to retrieve the basic info of candidate
   *
   * @param candidateId: id of candidate
   * @return :return the candidate basic response else return null
   */
  @GetMapping("/{candidateId}/get")
  public CandidateBasicResponseDTO retrieveCandidate(@PathVariable(name = "candidateId") long candidateId) {
    return candidateService.retrieveCandidate(candidateId);
  }

  /**
   * it will retrieve the list of candidate having basic info of each candidate
   *
   * @param token:    to validate the request
   * @param status:   to find active or inactive candidate list
   * @param pageable: to retrieve the custom size pageable objects of candidates
   * @return :return the object having pageable object of candidates if present else return null
   */
  @GetMapping("/candidates")
  public CandidatePageResponseDTO retrieveCandidates(@CookieValue("Authorization") String token, @RequestParam(value = "status", required = true) boolean status, Pageable pageable) {
    return candidateService.retrieveCandidates(token, status, pageable);
  }

  /**
   * approve the request of voter to become candidate
   *
   * @param id: id of candidate
   * @return :return the success response if everything goes fine else return failure reason
   */
  @PostMapping("/{candidate_id}/approve")
  public ResponseDTO approveCandidate(@PathVariable("candidate_id") long id) {
    return candidateService.approveCandidate(id);
  }

  /**
   * decline the request of voter to become candidate
   *
   * @param id: id of candidate
   * @return :return the success response if everything goes fine else return failure reason
   */
  @PostMapping("/{candidate_id}/decline")
  public ResponseDTO declineCandidate(@PathVariable("candidate_id") long id) {
    return candidateService.declineCandidate(id);
  }

  /**
   * to create a party
   *
   * @param candidateId: id of candidate who want to create party
   * @param name:        name of party to be created
   * @param partyLogo:   image/logo of party
   * @return :return the success response if everything goes fine else return failure reason
   */
  @PostMapping("/{candidateId}/party/{name}")
  public PartyResponseDTO createParty(@PathVariable("candidateId") long candidateId, @PathVariable("name") String name, @RequestParam(value = "partyLogo", required = true) MultipartFile partyLogo) {
    return candidateService.createParty(candidateId, name, partyLogo);
  }

  /**
   * it will retrieve the pageable object of candidates who are in specific constituency
   *
   * @param token:          to validate request
   * @param constituencyId: id of constituency
   * @param pageable:       to give custom size of pageable object
   * @return :will return the object having list of candidate else return null
   */
  @GetMapping("/{constituencyId}/constituency")
  public CandidatePageResponseDTO retrieveAllCandidatesInConstituency(@CookieValue("Authorization") String token, @PathVariable("constituencyId") long constituencyId, Pageable pageable) {
    return candidateService.retrieveCandidatesInConstituency(token, constituencyId, pageable);
  }

  /**
   * Retrieve party by party id
   *
   * @param partyId: if of party
   * @return :return party response if found else return null
   */
  @GetMapping("/party/get")
  public PartyResponseDTO retrieveParty(@RequestParam long partyId) {
    return candidateService.retrieveParty(partyId);
  }

  /**
   * to register candidate by admin
   *
   * @param token:               Token for validation of admin
   * @param candidateRequestDTO: Resources to create the candidate
   * @return : return the success response if candidate created else return failure reason
   */
  @PostMapping("/register/admin")
  public ResponseDTO createCandidateByAdmin(@CookieValue("Authorization") String token, @RequestBody CandidateRequestDTO candidateRequestDTO) {
    return candidateService.createCandidateByAdmin(token, candidateRequestDTO);
  }

  /**
   * retrieve the party by id in form of list
   *
   * @param partyId: id of party
   * @return :List of parties if found by id only one party is in list else return all the parties
   */
  @GetMapping("/party/{partyId}")
  public PartyListResponseDTO retrievePartiesById(@PathVariable long partyId) {
    return candidateService.retrievePartiesById(partyId);
  }

}