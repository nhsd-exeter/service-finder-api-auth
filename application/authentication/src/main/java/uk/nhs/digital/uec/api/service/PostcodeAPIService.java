package uk.nhs.digital.uec.api.service;

import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.model.PostcodeMapping;

public interface PostcodeAPIService {

  PostcodeMapping getRegionDetails(String postcode) throws InvalidParameterException;
}
