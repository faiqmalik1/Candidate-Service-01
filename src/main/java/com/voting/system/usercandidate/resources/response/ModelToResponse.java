package com.voting.system.usercandidate.resources.response;

import com.voting.system.usercandidate.constants.Constants;
import com.voting.system.usercandidate.model.Candidate;
import com.voting.system.usercandidate.model.Party;
import resources.candidate.*;
import resources.candidate.CandidateResponseDTO;
import resources.candidate.PartyResponseDTO;
import resources.constituency.ConstituencyResponseDTO;
import resources.user.UserResponseDTO;

import java.util.ArrayList;
import java.util.List;

public class ModelToResponse {

  public static PartyResponseDTO parsePartyToPartyResponse(Party party, boolean detail) {
    PartyResponseDTO response = new PartyResponseDTO();
    response.setPartyId(party.getPartyId());
    response.setPartyName(party.getPartyName());
    if (detail) {
      response.setPartySymbol(party.getPartySymbol());
      response.setCreatedAt(party.getCreatedAt().toString());
    }
    response.setResponseCode(Constants.SUCCESS_CODE);
    return response;
  }

  public static PartyListResponseDTO parsePartyListToPartyResponse(List<Party> partyList, boolean detail) {
    List<PartyResponseDTO> partyResponseDTOList = new ArrayList<>();
    PartyListResponseDTO partyListResponseDTO = new PartyListResponseDTO();
    for (Party party : partyList) {
      partyResponseDTOList.add(parsePartyToPartyResponse(party, detail));
    }
    partyListResponseDTO.setPartyResponseDTOList(partyResponseDTOList);
    return partyListResponseDTO;
  }

  public static CandidateResponseDTO parseCandidateToCandidateResponse(Candidate candidate, UserResponseDTO userResponseDTO, ConstituencyResponseDTO constituencyResponseDTO, long votes) {
    CandidateResponseDTO candidateResponseDTO = new CandidateResponseDTO();
    candidateResponseDTO.setCnic(userResponseDTO.getCNIC());
    candidateResponseDTO.setName(userResponseDTO.getName());
    candidateResponseDTO.setConstituencyName(constituencyResponseDTO.getHalkaName());
    candidateResponseDTO.setCandidateId(candidate.getUserId());
    candidateResponseDTO.setPartyName(candidate.getParty().getPartyName());
    candidateResponseDTO.setPost(candidate.getPost());
    candidateResponseDTO.setImage(userResponseDTO.getImg());
    candidateResponseDTO.setStatus(candidate.getStatus());
    candidateResponseDTO.setConstituencyId(candidate.getConstituencyId());
    candidateResponseDTO.setVotes(Math.toIntExact(votes));
    return candidateResponseDTO;
  }

  public static CandidateBasicResponseDTO parseCandidateToResponse(Candidate candidate) {
    CandidateBasicResponseDTO candidateBasicResponseDTO = new CandidateBasicResponseDTO();
    candidateBasicResponseDTO.setCandidateId(candidate.getUserId());
    candidateBasicResponseDTO.setConstituencyId(candidate.getConstituencyId());
    candidateBasicResponseDTO.setPost(candidate.getPost());
    candidateBasicResponseDTO.setStatus(candidate.getPost());
    candidateBasicResponseDTO.setPartyId(candidate.getParty().getPartyId());
    return candidateBasicResponseDTO;

  }

}