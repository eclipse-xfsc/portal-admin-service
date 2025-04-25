package eu.gaiax.rest;

import eu.gaiax.model.*;
import eu.gaiax.service.ManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@SuppressWarnings("unused")
@Api("List of Requests")
@ApiOperation("Management Page")
@RestController
@RequestMapping("/api/admin/management")
public class ManagementController {

  @Autowired
  @Qualifier("demoSrv")
  WebClient demoSrv;

  @Autowired
  private ManagementService service;

  @ApiOperation("Get List of Requests")
  @GetMapping("/requests/search")
  public ResponseEntity<FilterResult> getRequestsList(
          @RequestParam
          @ApiParam(name = "map", value = "Associative Array of Search Parameters") MultiValueMap<String, String> map,
          HttpServletRequest request
  ) {
    log.info("getRequestsList: {}", request.getQueryString());
    FilterResult body = service.getRequests(map, request);
    return ResponseEntity.ok(body);
  }

  @ApiOperation("Get Requests Types for Filter")
  @GetMapping("/request-types")
  public ResponseEntity<FilterDto> getRequestTypesFilter(HttpServletRequest request) {
    log.info("getRequestTypesFilter: {}", request.getQueryString());
    List<FilterItem> requestTypes = service.getRequestTypes();
    return ResponseEntity.ok(new FilterDto(requestTypes));
  }

  @ApiOperation("Get Locations for Filter")
  @GetMapping("/locations")
  public ResponseEntity<?> getManagementFilterLocations(HttpServletRequest request) {
    log.info("getManagementFilterLocations: {}", request.getQueryString());
    return ResponseEntity.ok(service.getLocationFilterItems());
  }

  @ApiOperation("Get Request Details by ID")
  @GetMapping("/requests/{id}/details")
  public ResponseEntity<DetailsDto> getPrDetails(
          HttpServletRequest request,
          @PathVariable
          @ApiParam(name = "id", value = "Request Identifier", example = "asfas2asf323sdfe34") String id
  ) {
    log.info("getPrDetails: {}", request.getQueryString());
    DetailsDto details = service.getDetails(id, request, true);
    return ResponseEntity.ok(details);
  }

  @ApiOperation("Accept/Deny Request")
  @PostMapping("/requests")
  public ResponseEntity<?> acceptOrDenyRequest(
          HttpServletRequest request,
          @RequestBody
          @ApiParam(name = "rq", value = "accept/deny") AcceptDenyRq rq
  ) {
    log.info("acceptDenyRequest, requestBody: {}", rq);
    try {
      service.acceptOrDenyRequest(rq);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.badRequest()
                           .body(
                                   new ErrorDto(
                                           "/api/admin/management/requests",
                                           e.getMessage()
                                   )
                           );
    }
  }

  @ApiOperation("Download File Attached")
  @GetMapping(path = "/download")
  public ResponseEntity<?> download(
          HttpServletRequest request,
          @RequestHeader HttpHeaders headers,
          @RequestParam("requestId")
          @ApiParam(name = "id", value = "Request Identifier", example = "asfas2asf323sdfe34") String requestId,
          @RequestParam("filename")
          @ApiParam(name = "id", value = "Name of the File to be received", example = "sd.json") String filename
  ) {
    log.info("download: {}", request.getQueryString());
    final Resource resource = service.getFile(Long.valueOf(requestId), filename);
    if (resource == null)
      return ResponseEntity
              .badRequest()
              .body(new ErrorDto("/api/admin/management/download", "No such file found: " + filename));
    headers.add("content-disposition", "attachment;filename=\"" + filename + "\"");
    return ResponseEntity.ok()
                         .headers(headers)
                         .contentType(MediaType.APPLICATION_OCTET_STREAM)
                         .body(resource);
  }
}
