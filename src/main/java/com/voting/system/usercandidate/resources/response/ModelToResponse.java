package com.voting.system.usercandidate.resources.response;

import com.cloudinary.Cloudinary;
import com.voting.system.usercandidate.constants.Constants;
import com.voting.system.usercandidate.model.Candidate;
import com.voting.system.usercandidate.model.Party;
import com.voting.system.usercandidate.service.CandidateService;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import resources.candidate.*;
import resources.candidate.CandidateResponseDTO;
import resources.candidate.PartyResponseDTO;
import resources.constituency.ConstituencyResponseDTO;
import resources.user.UserResponseDTO;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ModelToResponse {

  public static PartyResponseDTO parsePartyToPartyResponse(Cloudinary cloudinary, Party party, boolean detail) {
    PartyResponseDTO response = new PartyResponseDTO();
    response.setPartyId(party.getPartyId());
    response.setPartyName(party.getPartyName());
    if (detail) {
      response.setPartySymbol(getImageFromCloud(cloudinary, party.getPartySymbol()));
      response.setCreatedAt(party.getCreatedAt().toString());
    }
    response.setResponseCode(Constants.SUCCESS_CODE);
    return response;
  }

  public static PartyListResponseDTO parsePartyListToPartyResponse(Cloudinary cloudinary, List<Party> partyList, boolean detail) {
    List<PartyResponseDTO> partyResponseDTOList = new ArrayList<>();
    PartyListResponseDTO partyListResponseDTO = new PartyListResponseDTO();
    for (Party party : partyList) {
      partyResponseDTOList.add(parsePartyToPartyResponse(cloudinary, party, detail));
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

  public static byte[] getImageFromCloud(Cloudinary cloudinary, String publicID) {

    String cloudUrl = cloudinary.url()
            .publicId(publicID)
            .generate();
    try {
      URL url = new URL(cloudUrl);
      InputStream inputStream = url.openStream();
      byte[] out = IOUtils.toByteArray(inputStream);
      ByteArrayResource resource = new ByteArrayResource(out);
      return resource.getByteArray();

    } catch (Exception ex) {
      return null;
    }
  }

}