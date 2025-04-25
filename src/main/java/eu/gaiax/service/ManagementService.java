package eu.gaiax.service;

import eu.gaiax.model.*;
import eu.gaiax.proxy.RequestCall;
import eu.gaiax.repo.*;
import eu.gaiax.repo.dto.JsonbSdData;
import eu.gaiax.repo.dto.LocationCount;
import eu.gaiax.repo.dto.RequestTypeCount;
import eu.gaiax.repo.entities.FrRequest;
import eu.gaiax.repo.entities.FrRequestAttachment;
import eu.gaiax.repo.entities.FrRequestStatusEntity;
import eu.gaiax.repo.entities.FrRequestTypeEntity;
import eu.gaiax.util.SortingResolver;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ManagementService {

  @Autowired
  @Qualifier("notarizationSrv")
  private WebClient notarizationSrv;

  @Autowired
  @Qualifier("ocmSrv")
  private WebClient ocmSrv;

  @Autowired
  @Qualifier("sdSrv")
  private WebClient sdSrv;

  private static final String ENDPOINT = "/api/admin/management/requests/search?page=%s&%s";
  private static final String DENY = "deny";
  private static final String ACCEPT = "accept";

  @Autowired
  private FrRequestDao frRequestDao;
  @Autowired
  private
  FrRequestAttachmentDao frRequestAttachmentDao;

  @Autowired
  private
  FrRequestTypeDao frRequestTypeDao;

  @Autowired
  private
  FrRequestStatusDao frRequestStatusDao;

  private final WebClient demoSrv;
  private final EmailSendingService emailSrv;

  @Value("${services.portal.uri.external}")
  private String portalExtURL;

  FrRequestTypeEntity VC_NP_NOT_CONFIRMED, VC_NP, VC_PPR_NOT_CONFIRMED,
          VC_PPR, PR_PPR, SD_SERVICE, SD_DATA, SD_NODE, SD_PPR;

  FrRequestStatusEntity PUBLISHED, NOTIFIED;

  @Autowired
  public ManagementService(
          EmailSendingService emailSrv,
          @Qualifier("demoSrv") WebClient demoSrv
  ) {
    this.demoSrv = demoSrv;
    this.emailSrv = emailSrv;
  }

  @PostConstruct
  public void initConstants() {
    VC_NP_NOT_CONFIRMED = frRequestTypeDao.findByName(FrRequestType.VC_NP_NOT_CONFIRMED.name()).orElseThrow();
    VC_NP = frRequestTypeDao.findByName(FrRequestType.VC_NP.name()).orElseThrow();
    VC_PPR_NOT_CONFIRMED = frRequestTypeDao.findByName(FrRequestType.VC_PPR_NOT_CONFIRMED.name()).orElseThrow();
    VC_PPR = frRequestTypeDao.findByName(FrRequestType.VC_PPR.name()).orElseThrow();
    PR_PPR = frRequestTypeDao.findByName(FrRequestType.PR_PPR.name()).orElseThrow();
    SD_SERVICE = frRequestTypeDao.findByName(FrRequestType.SD_SERVICE.name()).orElseThrow();
    SD_DATA = frRequestTypeDao.findByName(FrRequestType.SD_DATA.name()).orElseThrow();
    SD_NODE = frRequestTypeDao.findByName(FrRequestType.SD_NODE.name()).orElseThrow();
    SD_PPR = frRequestTypeDao.findByName(FrRequestType.SD_PPR.name()).orElseThrow();
    PUBLISHED = frRequestStatusDao.findByName(FrRequestStatus.PUBLISHED.name()).orElseThrow();
    NOTIFIED = frRequestStatusDao.findByName(FrRequestStatus.NOTIFIED.name()).orElseThrow();
  }

  public LocationFilterDto getLocationFilterItems() {
    final List<LocationCount> locationCount = frRequestDao.getLocationCount();
    if (locationCount == null || locationCount.isEmpty()) {
      return new LocationFilterDto();
    }

    final List<PrLocationFilterItem> lst =
            locationCount.stream()
                         .map(
                                 x -> new PrLocationFilterItem(
                                         x.getLocation(),
                                         x.getLocation(), x.getLocationCount()
                                 )
                         ).collect(Collectors.toList());
    return new LocationFilterDto(lst);
  }

  private String requestTypeFromDBConvertion(final String n) {
    if (n.startsWith("SD_")) {
      return "sd";
    } else if (n.startsWith("VC_")) {
      return "v_cred";
    } else if (n.equals("PR_PPR")) {
      return "pr_cred";
    } else {
      throw new IllegalArgumentException("Incorrect request type: " + n);
    }
  }

  private List<String> requestTypeToDBConvertion(final String n) {
    /*

     */
    if ("sd".equals(n)) {
      return Arrays.asList("SD_SERVICE", "SD_DATA", "SD_NODE", "SD_PPR");
    } else if ("v_cred".equals(n)) {
      return Arrays.asList("VC_NP", "VC_PPR");
    } else if ("pr_cred".equals(n)) {
      return Arrays.asList("PR_PPR");
    } else {
      throw new IllegalArgumentException("Incorrect request type: " + n);
    }
  }

  public List<FilterItem> getRequestTypes() {
    final Map<String, Integer> filter = new HashMap<>();
    filter.put("pr_cred", 0);
    filter.put("v_cred", 0);
    filter.put("sd", 0);

    final List<RequestTypeCount> requestTypeCounts = frRequestDao.getRequestTypeCount();
    for (final RequestTypeCount rtc : requestTypeCounts) {
      filter.merge(requestTypeFromDBConvertion(rtc.getRequestType()), rtc.getRequestTypeCount(), Integer::sum);
    }
    return filter.entrySet().stream()
                 .map(e -> new FilterItem(e.getKey(), e.getValue()))
                 .collect(Collectors.toList());
  }

  private PartRequest from(FrRequest r) {
    if (VC_NP.equals(r.getRequestType())) {
      return new PartRequest(
              r.getId().toString(),
              r.getParticipantName(),
              r.getLocation(),
              requestTypeFromDBConvertion(r.getRequestType().getName())
      );
    } else if (VC_PPR.equals(r.getRequestType()) ||
            PR_PPR.equals(r.getRequestType()) ||
            SD_SERVICE.equals(r.getRequestType()) ||
            SD_DATA.equals(r.getRequestType()) ||
            SD_NODE.equals(r.getRequestType()) ||
            SD_PPR.equals(r.getRequestType())) {
      return new PartRequest(
              r.getId().toString(),
              r.getDetails().getProperties().get("prName"),
              r.getLocation(),
              requestTypeFromDBConvertion(r.getRequestType().getName())
      );
    } else {
      throw new IllegalArgumentException("Incorrect request type: " + r.getRequestType());
    }
  }

  public FilterResult getRequests(MultiValueMap<String, String> map, HttpServletRequest request) {
    final List<String> pageLst = map.getOrDefault("page", new ArrayList<>());
    int pageNum = pageLst.isEmpty() ? 0 : Integer.parseInt(pageLst.get(0));
    log.info("page parameters: " + pageLst + "; resolved: " + pageNum);

    final List<String> sizeLst = map.getOrDefault("size", new ArrayList<>());
    int sizeLen = sizeLst.isEmpty() ? 10 : Integer.parseInt(sizeLst.get(0));
    log.info("size parameters: " + sizeLst + "; resolved: " + sizeLen);

    final List<String> locations = map.getOrDefault("location", null);
    final List<String> requestType = map.getOrDefault("request_type", null);

    final Page<FrRequest> page;
    if (requestType != null) {
      final List<String> dbTypes =
              requestType.stream()
                         .flatMap(t -> requestTypeToDBConvertion(t).stream())
                         .collect(Collectors.toList());
      if (locations != null) {
        page = frRequestDao.filter(
                PageRequest.of(pageNum, sizeLen, getSortParam(map)),
                locations,
                dbTypes
        );
      } else {
        page = frRequestDao.filterForAllLocations(
                PageRequest.of(pageNum, sizeLen, getSortParam(map)),
                dbTypes
        );
      }
    } else {
      if (locations != null) {
        page = frRequestDao.filterForAllRequestTypes(
                PageRequest.of(pageNum, sizeLen, getSortParam(map)),
                locations
        );
      } else {
        page = frRequestDao.filterForAllRequestTypesAndAllLocations(
                PageRequest.of(pageNum, sizeLen, getSortParam(map))
        );
      }
    }


    final List<PartRequest> collect =
            page.getContent()
                .stream()
                .map(
                        row -> from(row)
                ).collect(Collectors.toList());

    int totalPages = page.getTotalPages();
    int nextPage = pageNum + 1;
    int prevPage = pageNum - 1;

    String nextPageLink = (nextPage < totalPages)
            ? preparePageLink(request.getQueryString(), nextPage)
            : null;
    String prevPageLink = (prevPage > -1)
            ? preparePageLink(request.getQueryString(), prevPage)
            : null;

    return new FilterResult(collect, totalPages, prevPageLink, nextPageLink);
  }

  private Sort getSortParam(MultiValueMap<String, String> map) {
    List<String> sortLst = map.getOrDefault("sort_field", new ArrayList<>());
    log.info("sort_field parameters: " + sortLst);
    if (sortLst.isEmpty()) return Sort.unsorted();

    String sortField = sortLst.get(0);
    Sort sortParam = Sort.by(SortingResolver.getDbValueByUiValue(sortField));

    List<String> sortDir = map.getOrDefault("sort_direction", new ArrayList<>());
    log.info("sort_direction parameters: " + sortDir);
    if (!sortDir.isEmpty()) {
      String sortDirParam = sortDir.get(0);
      sortParam = sortDirParam.equalsIgnoreCase("ASC")
              ? sortParam.ascending()
              : sortParam.descending();
    }
    return sortParam;
  }

  public void acceptOrDenyRequest(final AcceptDenyRq rq) {
    final String operation = rq.getStatus().toLowerCase(Locale.ROOT);
    final FrRequest request =
            frRequestDao.findById(Long.valueOf(rq.getId()))
                        .orElseThrow(() -> new IllegalArgumentException("Incorrect request id: " + rq.getId()));
    switch (operation) {
      case DENY:
        denyAndInformUser(request);
        break;
      case ACCEPT:
        acceptAndInformUser(request);
        break;
      default:
        throw new IllegalArgumentException("Incorrect operation: " + rq.getStatus());
    }
  }

  private void acceptAndInformUser(final FrRequest r) {
    log.info("in acceptAndInformUser, r: {}", r);
    if (!PUBLISHED.equals(r.getRequestStatus())) {
      log.info("Request wasn't previously published successfully, call external system");
      // Call external system if and only if it wasn't successfully called previously.
      // That should be in a separate (from email notification) transaction
      // because probably external system can be called successfully only once per request,
      // and in any exception with email notification or internal DB error we have no
      // way to call external system again
      callExternalSystemAndUpdateStatus(r);
    } else {
      log.info("Request was already published to external system");
    }
    if (!NOTIFIED.equals(r.getRequestStatus())) {
      log.info("User wasn't notified by email previously, notify the user");
      // Currently, we do not use NOTIFIED status and will just delete the accepted request
      // But it is better to reserve dictionary for possible future scenarios.
      updateStatusAndInformUser(r);
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  private void updateStatusAndInformUser(final FrRequest request) {
    // First - delete in database, because rollback will occur in case of
    // any error during email sending.
    frRequestDao.delete(request);
    try {
      emailSrv.sendEmailWithoutAttachments(
              retrieveRecipientEmailAddress(request),
              "Gaia-X: request approved by federator",
              prepareAcceptEmailBody(request)
      );
    } catch (Exception e) {
      throw new RuntimeException("Exception occurred during email sending");
    }
  }

  @Transactional(propagation = Propagation.MANDATORY)
  private HttpStatus callExternalSystem(final FrRequest request) {
    log.info("In callExternalSystem, request: {}", request);

    final MultipartBodyBuilder builder = new MultipartBodyBuilder();
    log.info("Processing attachments");
    for (final FrRequestAttachment a : request.getFrRequestAttachments()) {
      log.info("Attach: {}", a.getFileName());
      builder.part(
              a.getFileName(),
              new ByteArrayResource(a.getFileData())
      ).filename(a.getFileName());
    }
    final JSONDetails details = request.getDetails();
    builder.part("details", details, MediaType.APPLICATION_JSON);

    final HttpStatus httpStatus;
    if (VC_NP.equals(request.getRequestType()) || VC_PPR.equals(request.getRequestType())) {
      log.info("Call to notarization system");
      httpStatus =
              notarizationSrv.post()
                             .uri("/api/admin/management/notarization/requests")
                             .contentType(MediaType.MULTIPART_FORM_DATA)
                             .body(BodyInserters.fromMultipartData(builder.build()))
                             .exchangeToMono(
                                     response -> {
                                       if (response.statusCode().equals(HttpStatus.OK)) {
                                         return response.bodyToMono(HttpStatus.class).thenReturn(response.statusCode());
                                       } else {
                                         throw new ServiceException("Error uploading file");
                                       }
                                     }
                             ).block();
    } else if (PR_PPR.equals(request.getRequestType())) {
      log.info("Call to OCM system");
      httpStatus =
              ocmSrv.post()
                    .uri("/api/admin/management/ocm/requests")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .exchangeToMono(
                            response -> {
                              if (response.statusCode().equals(HttpStatus.OK)) {
                                return response.bodyToMono(HttpStatus.class).thenReturn(response.statusCode());
                              } else {
                                throw new ServiceException("Error uploading file");
                              }
                            }
                    ).block();
    } else if (SD_SERVICE.equals(request.getRequestType()) ||
            SD_DATA.equals(request.getRequestType()) ||
            SD_NODE.equals(request.getRequestType()) ||
            SD_PPR.equals(request.getRequestType())
    ) {
      log.info("Call to SD system");
      httpStatus =
              sdSrv.post()
                   .uri("/api/admin/management/sd/requests")
                   .contentType(MediaType.MULTIPART_FORM_DATA)
                   .body(BodyInserters.fromMultipartData(builder.build()))
                   .exchangeToMono(
                           response -> {
                             if (response.statusCode().equals(HttpStatus.OK)) {
                               return response.bodyToMono(HttpStatus.class).thenReturn(response.statusCode());
                             } else {
                               throw new ServiceException("Error uploading file");
                             }
                           }
                   ).block();
    } else {
      throw new IllegalArgumentException("Incorrect request type");
    }

    log.info("Call to external system done, httpStatus: {}", httpStatus);
    return httpStatus;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  private void callExternalSystemAndUpdateStatus(final FrRequest r) {
    // First - update the DB, because rollback will occur in case of error
    // during external call
    log.info("Change status to PUBLISHED in database");
    r.setRequestStatus(PUBLISHED);
    frRequestDao.save(r);
    log.info("Calling external system");
    final HttpStatus httpStatus = callExternalSystem(r);
    if (!httpStatus.is2xxSuccessful()) {
      throw new RuntimeException(
              String.format(
                      "External system error: %d %s",
                      httpStatus.value(),
                      httpStatus.getReasonPhrase()
              )
      );
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  private void denyAndInformUser(final FrRequest request) {
    log.info("in denyAndInformUser, request: {}", request);
    // It is possible that request was already published to external system but error occurred during
    // previous user notification. We shouldn't allow to deny such request, because indeed it was already
    // accepted.
    if (PUBLISHED.equals(request.getRequestStatus())) {
      throw new RuntimeException("Can't deny already published request");
    }

    // First - delete in database, because rollback will occur in case of
    // any error during email sending.
    frRequestDao.delete(request);
    try {
      emailSrv.sendEmailWithoutAttachments(
              retrieveRecipientEmailAddress(request),
              "Gaia-X: request denied by federator",
              prepareDeniedEmailBody(request)
      );
    } catch (Exception e) {
      throw new RuntimeException(
              String.format(
                      "Exception occurred during email sending: %s",
                      e.getMessage()
              )
      );
    }
  }

  private String prepareDeniedEmailBody(final FrRequest r) {
    final String genericTmpl = "<h1>Request denied by Federator</h1>" +
            "<h3>Dear Gaia-X user,</h3>" +
            "<p>your request for %s was denied by Federator. " +
            "Please, contact <a href='%s/help/support'>Gaia-X support</a> for further details.";

    if (VC_NP.equals(r.getRequestType()) || VC_PPR.equals(r.getRequestType())) {
      return "<h1>Request denied by Federator</h1>" +
              "<h3>Dear Gaia-X user,</h3>" +
              "<p>your verified credentials couldn't be issued by the AISBL. " +
              "For further information please contact the AISBL.";
    } else if (r.getRequestType().equals(PR_PPR)) {
      return "<h1>Request denied by Federator</h1>" +
              "<h3>Dear Gaia-X user,</h3>" +
              "<p>your onboarding as participant has been rejected by the AISBL. " +
              "For further information please contact the AISBL.";
    } else if (r.getRequestType().equals(SD_SERVICE)) {
      return String.format(genericTmpl, "service SD approval", portalExtURL);
    } else if (r.getRequestType().equals(SD_DATA)) {
      return String.format(genericTmpl, "data SD approval", portalExtURL);
    } else if (r.getRequestType().equals(SD_NODE)) {
      return String.format(genericTmpl, "node SD approval", portalExtURL);
    } else if (r.getRequestType().equals(SD_PPR)) {
      return String.format(genericTmpl, "provider SD approval", portalExtURL);
    } else {
      throw new IllegalArgumentException("Incorrect request type");
    }
  }

  private String prepareAcceptEmailBody(final FrRequest r) {
    final String bodyTemplate = "<h1>Request approved by Federator</h1>" +
            "<h3>Dear Gaia-X user,</h3>" +
            "<p>your request for %s was approved by Federator. %s";

    if (r.getRequestType().equals(VC_NP) || r.getRequestType().equals(VC_PPR)) {
      final String tmpl = "<h1>Request approved by Federator</h1>" +
              "<h3>Dear Gaia-X user,</h3>" +
              "<p>your verified credentials have been issued by the AISBL." +
              " Please, click <a href='%s/onboarding/%s/proof'>here</a> to proceed the onboarding.";

      return String.format(
              tmpl,
              portalExtURL,
              VC_NP.equals(r.getRequestType()) ? "user" : "organization");
    } else if (r.getRequestType().equals(PR_PPR)) {
      final String tmpl = "<h1>Request approved by Federator</h1>" +
              "<h3>Dear Gaia-X user,</h3>" +
              "<p>your onboarding as participant has been accepted by the AISBL." +
              " Please, click <a href='%s/help/support'>here</a> to add your participant credentials to your wallet.";

      return String.format(tmpl, portalExtURL);
    } else if (r.getRequestType().equals(SD_SERVICE)) {
      return String.format(bodyTemplate, "service SD approval", "Service will be published to catalog soon.");
    } else if (r.getRequestType().equals(SD_DATA)) {
      return String.format(bodyTemplate, "data SD approval", "Data will be published to catalog soon.");
    } else if (r.getRequestType().equals(SD_NODE)) {
      return String.format(bodyTemplate, "node SD approval", "Node will be published to catalog soon.");
    } else if (r.getRequestType().equals(SD_PPR)) {
      return String.format(bodyTemplate, "provider SD approval", "Organization details will be updated soon.");
    } else {
      throw new IllegalArgumentException("Incorrect request type");
    }
  }

  private String retrieveRecipientEmailAddress(final FrRequest request) {
    if (request.getRequestType().equals(PR_PPR)) {
      final String confEmail = request.getDetails().getProperties().get("conf_email");
      if (confEmail == null) {
        throw new RuntimeException("Can't retrieve confirmation email for PPR");
      }

      return confEmail;
    } else {
      return request.getEmail();
    }
  }

  public Resource getFile(Long rqId, String filename) {
    log.info("getFile, rqId: {}, filename: {}", rqId, filename);
    final FrRequestAttachment attach =
            frRequestAttachmentDao.findByFileNameAndFrRequest_Id(filename, rqId).orElseThrow();
    return new InputStreamResource(new ByteArrayInputStream(attach.getFileData()));
  }

  private String preparePageLink(String queryString, int pageNumber) {
    String result;
    if (queryString == null || queryString.isEmpty()) {
      result = String.format(ENDPOINT, 1, "");
    } else {
      queryString = queryString.replaceAll("page=\\d+&", "");
      queryString = queryString.replaceAll("&page=\\d+", "");
      queryString = queryString.replaceAll("page=\\d+", "");
      result = String.format(ENDPOINT, pageNumber, queryString)
                     .replaceAll("&&", "&");
    }
    return result.endsWith("&")
            ? result.substring(0, result.length() - 1)
            : result;
  }

  public DetailsDto getDetails(String id, HttpServletRequest request, boolean addAttachInfo) {
    final FrRequest frRequest = frRequestDao.findById(Long.valueOf(id)).orElseThrow();
    final JSONDetails details = frRequest.getDetails();
    final List<DetailsDto.Attribute> attributes =
            details.getProperties().entrySet().stream()
                   .map(
                           e -> new DetailsDto.Attribute(
                                   e.getKey(),
                                   e.getValue(),
                                   true
                           )
                   ).collect(Collectors.toList());
    final String name;
    if (VC_NP.equals(frRequest.getRequestType())) {
      name = "NP Details";
    } else if (VC_PPR.equals(frRequest.getRequestType()) ||
            PR_PPR.equals(frRequest.getRequestType())) {
      name = "PPR Details";
    } else if (SD_SERVICE.equals(frRequest.getRequestType()) ||
            SD_DATA.equals(frRequest.getRequestType()) ||
            SD_NODE.equals(frRequest.getRequestType()) ||
            SD_PPR.equals(frRequest.getRequestType())) {
      name = "SD Details";
    } else {
      throw new IllegalArgumentException("Incorrect request type: " + frRequest.getRequestType());
    }
    final List<DetailsDto.SdData> sdDataList = new ArrayList<>();
    sdDataList.add(new DetailsDto.SdData(name, attributes));

    if (!addAttachInfo) {
      return new DetailsDto(sdDataList, null);
    }

    final String url = "/api/admin/management/download?requestId=%s&filename=%s";

    final List<DetailsDto.AttachmentDto> attLst = new ArrayList<>();
    for (FrRequestAttachment attachment : frRequest.getFrRequestAttachments()) {
      // We try to add all SD attributes to display for Admin
      // We assume, that any JSON file is SD file
      if (attachment.getFileName().endsWith(".json")) {
        try {
          final JsonbSdData sd =
                  RequestCall.validateSdData(
                          demoSrv, request, attachment.getFileData(), "/api/validation/sd"
                  );
          final List<JsonbSdData.SdDetailsDto> sdJsonLst = sd.getSd();
          for (final JsonbSdData.SdDetailsDto dto : sdJsonLst) {
            final List<DetailsDto.Attribute> collect =
                    dto.getAttributes().stream()
                       .map(a -> new DetailsDto.Attribute(
                                       a.getName(), a.getValue(), a.isMandatory()
                               )
                       ).collect(Collectors.toList());
            sdDataList.add(new DetailsDto.SdData(dto.getName(), collect));
          }
        } catch (Exception e) {
          log.error("Was unable to parse data in JSON file, no additional SD attributes added");
        }
      }

      attLst.add(
              new DetailsDto.AttachmentDto(
                      attachment.getFileName(),
                      attachment.getFileName(),
                      String.format(url, id, attachment.getFileName())
              )
      );
    }
    return new DetailsDto(sdDataList, attLst);
  }
}
