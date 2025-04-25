package eu.gaiax.repo;

public enum FrRequestType {
  VC_NP_NOT_CONFIRMED, // Not confirmed email for NP
  VC_NP, // VC Natural person with confirmed email
  VC_PPR_NOT_CONFIRMED, // Not confirmed email for PPR
  VC_PPR, // VC Provider with confirmed email
  PR_PPR, // PC credentials
  SD_SERVICE, SD_DATA, SD_NODE, // SD requests for service, data, node
  SD_PPR // SD request for organization
}
