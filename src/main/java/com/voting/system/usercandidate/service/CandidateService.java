package com.voting.system.usercandidate.service;

import CustomException.CommonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.voting.system.usercandidate.feignController.ConstituencyController;
import com.voting.system.usercandidate.feignController.UserController;
import com.voting.system.usercandidate.feignController.VoterController;
import com.voting.system.usercandidate.model.Candidate;
import com.voting.system.usercandidate.model.Party;
import com.voting.system.usercandidate.repository.CandidateRepository;
import com.voting.system.usercandidate.repository.PartyCandidateMappingRepository;
import com.voting.system.usercandidate.repository.PartyRepository;
import com.voting.system.usercandidate.resources.response.ModelToResponse;
import constants.ReturnMessage;
import constants.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import resources.BaseService;
import resources.ResponseDTO;
import resources.candidate.*;
import resources.constituency.ConstituencyResponseDTO;
import resources.constituency.PoolingResponseDTO;
import resources.user.UserListRequestDTO;
import resources.user.UserListResponseDTO;
import resources.user.UserResponseDTO;
import resources.user.ValidateResponseDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateService extends BaseService {

  private final CandidateRepository candidateRepository;
  private final PartyRepository partyRepository;
  private final PartyCandidateMappingRepository partyCandidateMappingRepository;
  private final UserController userController;
  private final ConstituencyController constituencyController;
  private final VoterController voterController;

  ObjectMapper objectMapper = new ObjectMapper();
  private static String token;

  /**
   * returns the list of parties available
   *
   * @param detail: to get parties detail including party image and creation date
   * @return :list of parties
   */
  public PartyListResponseDTO retrieveParties(boolean detail) {
    List<Party> partyList = partyRepository.findAll();
    if (partyList.isEmpty()) {
      return new PartyListResponseDTO(null);
    }
    return ModelToResponse.parsePartyListToPartyResponse(partyList, detail);
  }

  /**
   * to register the user as candidate and to update its role
   *
   * @param token      : to validate user request
   * @param candidate: json containing data for creating candidate
   * @return :success response if every thing goes fine else failure response
   */
  @Transactional
  public ResponseDTO registerUserAsCandidate(String token, String candidate) {

    if (candidate == null) {
      return generateFailureResponse(ReturnMessage.INVALID_REQUEST.getValue());
    }
    CandidateRequestDTO candidateRequestDTO;
    try {
      candidateRequestDTO = objectMapper.readValue(candidate, CandidateRequestDTO.class);
    } catch (JsonProcessingException e) {
      return generateFailureResponse(ReturnMessage.INVALID_REQUEST.getValue());
    }
    if (!userController.isUserExists(token, candidateRequestDTO.getUserId())) {
      return generateFailureResponse(ReturnMessage.USER_NOT_EXISTS.getValue());
    }
    Optional<Party> optionalParty = partyRepository.findById(candidateRequestDTO.getPartyId());
    if (optionalParty.isEmpty()) {
      return generateFailureResponse(ReturnMessage.PARTY_NOT_EXISTS.getValue());
    }
    Party party = optionalParty.get();
    Optional<Candidate> optionalCandidate = candidateRepository.findById(candidateRequestDTO.getUserId());
    if (optionalCandidate.isPresent()) {
      return generateFailureResponse(ReturnMessage.USER_ALREADY_CANDIDATE.getValue());
    }
    Candidate newCandidate = new Candidate();
    newCandidate.setParty(party);
    newCandidate.setPost(candidateRequestDTO.getPost());
    newCandidate.setStatus(Status.PENDING.getValue());
    newCandidate.setCreatedAt(new Date());
    if (checkConstituency(candidateRequestDTO.getConstituencyId())) {
      return generateFailureResponse(ReturnMessage.CONSTITUENCY_NOT_EXISTS.getValue());
    }
    newCandidate.setConstituencyId(candidateRequestDTO.getConstituencyId());
    newCandidate.setUserId(candidateRequestDTO.getUserId());
    candidateRepository.save(newCandidate);
    return generateSuccessResponse();
  }

  /**
   * it will check if any constituency with constituencyId exists or not
   *
   * @param constituencyId: id of constituency
   * @return :return true if constituency exists else false
   */
  public boolean checkConstituency(long constituencyId) {
    ConstituencyResponseDTO response = constituencyController.retrieveConstituencyById(constituencyId);
    return response.getResponseCode() != null;
  }

  /**
   * it will retrieve candidate with its details
   *
   * @param token:       token to validate request
   * @param candidateId: id of candidate
   * @return :return candidate response if candidate present else return null
   */
  public CandidateResponseDTO retrieveCandidateDetail(String token, long candidateId) {
    Optional<Candidate> optionalCandidate = candidateRepository.findById(candidateId);
    ValidateResponseDTO validateResponseDTO = userController.validateToken(token);
    if (optionalCandidate.isEmpty()) return null;
    UserResponseDTO userResponseDTO = userController.getUser(token, candidateId);
    ConstituencyResponseDTO constituencyResponseDTO = constituencyController.retrieveConstituencyById(optionalCandidate.get().getConstituencyId());
    Candidate verifidCandidate = optionalCandidate.get();
    return ModelToResponse.parseCandidateToCandidateResponse(verifidCandidate, userResponseDTO, constituencyResponseDTO, 0);
  }

  /**
   * it will retrieve the list of candidate having basic info of each candidate
   *
   * @param token:    to validate the request
   * @param active:   to find active or inactive candidate list
   * @param pageable: to retrieve the custom size pageable objects of candidates
   * @return :return the object having pageable object of candidates if present else return null
   */
  public CandidatePageResponseDTO retrieveCandidates(String token, boolean active, Pageable pageable) {
    Page<Candidate> candidatePage;
    if (active) {
      candidatePage = candidateRepository.findAllByStatus(Status.ACTIVE.getValue(), pageable);
    } else {
      candidatePage = candidateRepository.findAllByStatus(Status.PENDING.getValue(), pageable);
    }
    if (candidatePage.isEmpty()) {
      return null;
    }
    List<Long> userIdList = candidatePage.getContent()
            .stream()
            .map(Candidate::getUserId).toList();
    UserListRequestDTO userListRequestDTO = new UserListRequestDTO(userIdList);
    UserListResponseDTO userResponseList = userController.findUsers(token, userListRequestDTO);
    Page<CandidateResponseDTO> candidateResponsePage = candidatePage.map(candidate -> {
      Optional<UserResponseDTO> matchingUserResponse = userResponseList.getUserResponseDTOList().stream()
              .filter(userResponse -> userResponse.getId() == candidate.getUserId())
              .findFirst();
      ConstituencyResponseDTO constituencyResponseDTO = constituencyController.retrieveConstituencyById(candidate.getConstituencyId());
      return matchingUserResponse.map(userResponse -> ModelToResponse.parseCandidateToCandidateResponse(candidate, userResponse, constituencyResponseDTO, voterController.retrieveCounts(token, candidate.getUserId(), 1))
      ).orElse(null);
    });
    return new CandidatePageResponseDTO(candidateResponsePage);
  }

  /**
   * approve the request of voter to become candidate
   *
   * @param candidateId: id of candidate
   * @return :return the success response if everything goes fine else return failure reason
   */
  @Transactional
  public ResponseDTO approveCandidate(long candidateId) {
    Optional<Candidate> optionalCandidate = candidateRepository.findById(candidateId);
    if (optionalCandidate.isEmpty()) {
      throw new CommonException(ReturnMessage.CANDIDATE_NOT_EXISTS.getValue(), HttpStatus.NOT_FOUND);
    }
    Candidate verifiedCandidate = optionalCandidate.get();
    if (!verifiedCandidate.getStatus().equals(Status.PENDING.getValue())) {
      throw new CommonException(ReturnMessage.CANDIDATE_STATUS_INVALID.getValue(), HttpStatus.BAD_REQUEST);
    }
    verifiedCandidate.setStatus(Status.ACTIVE.getValue());
    candidateRepository.save(verifiedCandidate);

    return generateSuccessResponse();
  }

  /**
   * decline the request of voter to become candidate
   *
   * @param id: id of candidate
   * @return :return the success response if everything goes fine else return failure reason
   */
  @Transactional
  public ResponseDTO declineCandidate(long id) {
    Optional<Candidate> optionalCandidate = candidateRepository.findById(id);
    if (optionalCandidate.isEmpty()) {
      throw new CommonException(ReturnMessage.CANDIDATE_NOT_EXISTS.getValue(), HttpStatus.NOT_FOUND);
    }
    Candidate verifiedCandidate = optionalCandidate.get();
    if (!verifiedCandidate.getStatus().equals(Status.PENDING.getValue())) {
      throw new CommonException(ReturnMessage.CANDIDATE_STATUS_INVALID.getValue(), HttpStatus.BAD_REQUEST);
    }
    verifiedCandidate.setStatus(Status.DECLINED.getValue());
    candidateRepository.save(verifiedCandidate);
    return generateSuccessResponse();
  }

  /**
   * to create a party
   *
   * @param candidateId: id of candidate who want to create party
   * @param partyName:   name of party to be created
   * @param image:       image/logo of party
   * @return :return the success response if everything goes fine else return failure reason
   */
  @Transactional
  public PartyResponseDTO createParty(long candidateId, String partyName, MultipartFile image) {
    Optional<Party> optionalParty = partyRepository.findByPartyName(partyName);
    if (optionalParty.isPresent()) {
      return new PartyResponseDTO(generateFailureResponse(ReturnMessage.PARTY_NAME_ALREADY_EXISTS.getValue()));
    }
    Party party = new Party();
    party.setPartyName(partyName);
    try {
      party.setPartySymbol(image.getBytes());
    } catch (IOException e) {
      return new PartyResponseDTO(generateFailureResponse(ReturnMessage.PARTY_SYMBOL_ERROR.getValue()));
    }
    party.setCreatedAt(new Date());
    party = partyRepository.save(party);
    return ModelToResponse.parsePartyToPartyResponse(party, false);
  }

  /**
   * it will retrieve the pageable object of candidates who are in specific constituency
   *
   * @param token:          to validate request
   * @param constituencyId: id of constituency
   * @param pageable:       to give custom size of pageable object
   * @return :will return the object having list of candidate else return null
   */
  public CandidatePageResponseDTO retrieveCandidatesInConstituency(String token, long constituencyId, Pageable pageable) {
    Page<Candidate> candidatePage = candidateRepository.findAllByStatusAndConstituencyId(Status.ACTIVE.getValue(), constituencyId, pageable);
    if (candidatePage.isEmpty()) {
      return null;
    }
    List<Long> userIdList = candidatePage.getContent()
            .stream()
            .map(Candidate::getUserId).toList();
    UserListRequestDTO userListRequestDTO = new UserListRequestDTO(userIdList);
    UserListResponseDTO userResponseList = userController.findUsers(token, userListRequestDTO);
    PoolingResponseDTO poolingResponseDTO = constituencyController.isPoolingStarted();
    Page<CandidateResponseDTO> candidateResponsePage = candidatePage.map(candidate -> {
      Optional<UserResponseDTO> matchingUserResponse = userResponseList.getUserResponseDTOList().stream()
              .filter(userResponse -> userResponse.getId() == candidate.getUserId())
              .findFirst();
      ConstituencyResponseDTO constituencyResponseDTO = constituencyController.retrieveConstituencyById(candidate.getConstituencyId());
      long candidateVotes = voterController.retrieveCounts(token, candidate.getUserId(), poolingResponseDTO.getPoolingId());
      return matchingUserResponse.map(userResponse -> ModelToResponse.parseCandidateToCandidateResponse(candidate, userResponse, constituencyResponseDTO, candidateVotes)
      ).orElse(null);
    });
    return new CandidatePageResponseDTO(candidateResponsePage);
  }

  /**
   * to retrieve the basic info of candidate
   *
   * @param candidateId: id of candidate
   * @return :return the candidate basic response else return null
   */
  public CandidateBasicResponseDTO retrieveCandidate(long candidateId) {
    Optional<Candidate> optionalCandidate = candidateRepository.findById(candidateId);
    return optionalCandidate.map(ModelToResponse::parseCandidateToResponse).orElse(null);
  }

  public PartyResponseDTO retrieveParty(long partyId) {
    Optional<Party> optionalParty = partyRepository.findById(partyId);
    return optionalParty.map(party -> ModelToResponse.parsePartyToPartyResponse(party, false)).orElse(null);
  }

  @Transactional
  public ResponseDTO createCandidateByAdmin(String token, CandidateRequestDTO candidateRequestDTO) {
    Candidate newCandidate = new Candidate();
    Optional<Party> party = partyRepository.findById(candidateRequestDTO.getPartyId());
    if (party.isEmpty()) {
      throw new CommonException(ReturnMessage.PARTY_NOT_EXISTS.getValue(), HttpStatus.BAD_REQUEST);
    }
    newCandidate.setParty(party.get());
    newCandidate.setPost(candidateRequestDTO.getPost());
    newCandidate.setStatus(Status.ACTIVE.getValue());
    newCandidate.setCreatedAt(new Date());
    if (checkConstituency(candidateRequestDTO.getConstituencyId())) {
      throw new CommonException(ReturnMessage.CONSTITUENCY_NOT_EXISTS.getValue(), HttpStatus.NOT_FOUND);
    }
    newCandidate.setConstituencyId(candidateRequestDTO.getConstituencyId());
    newCandidate.setUserId(candidateRequestDTO.getUserId());
    candidateRepository.save(newCandidate);
    return generateSuccessResponse();
  }

  public PartyListResponseDTO retrievePartiesById(long partyId) {
    Optional<Party> optionalParty = partyRepository.findById(partyId);
    List<Party> partyList = new ArrayList<>();
    if (optionalParty.isEmpty()) {
      partyList = partyRepository.findAll();
    } else {
      partyList.add(optionalParty.get());
    }
    return ModelToResponse.parsePartyListToPartyResponse(partyList, false);
  }
}