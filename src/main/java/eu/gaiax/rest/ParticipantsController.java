package eu.gaiax.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.gaiax.model.*;
import eu.gaiax.proxy.RequestCall;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Api("List of Participants")
@ApiOperation("List of Participants")
@RestController
@RequestMapping("/api/admin/pr/")
public class ParticipantsController {

    @Autowired
    @Qualifier("demoSrv")
    WebClient demoSrv;

    @ApiOperation("Get List of Participants")
    @GetMapping("/registrations/search")
    public ResponseEntity<?> getParticipantsList(HttpServletRequest request) {
        log.info("getParticipantsList");
        ResponseEntity<Map<String, ?>> rs = RequestCall.doGet(demoSrv, request);
        if (!rs.getStatusCode().is2xxSuccessful()) {
            return rs;
        }
        Map<String, ?> map = rs.getBody();
        if (map == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        FilterResult body = new FilterResult(
                (List) map.get("data"),
                (int) map.get("page_count"),
                (String) map.get("prev"),
                (String) map.get("next")
        );
        return ResponseEntity.ok(body);
    }

    @ApiOperation("Get Registration Types for Filter")
    @GetMapping("/registration-types")
    public ResponseEntity<?> getPrRegistrationTypes(HttpServletRequest request) {
        log.info("getPrRegistrationTypes");
        ResponseEntity<Map<String, List<Map<String, ?>>>> rs = RequestCall.doGet(demoSrv, request);
        if (!rs.getStatusCode().is2xxSuccessful()) {
            return rs;
        }
        Map<String, List<Map<String, ?>>> map = rs.getBody();
        if (map == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        List<Map<String, ?>> items = map.get("items");
        List<FilterItem> result = new ArrayList<>();
        for (Map<String, ?> item : items) {
            result.add(new FilterItem((String) item.get("name"), (int) item.get("qty")));
        }
        return ResponseEntity.ok(new FilterDto(result));
    }

    @ApiOperation("Get PR Details by ID")
    @GetMapping("/registrations/{id}/details")
    public ResponseEntity<?> getPrDetails(HttpServletRequest request,
                                                   @PathVariable
                                                   @ApiParam(name = "id", value = "PR Identifier", example = "asdf39eijf") String id) {
        log.info("getPrDetails");
        ResponseEntity<Map<String, ?>> rse = RequestCall.doGet(demoSrv, request);
        if (!rse.getStatusCode().is2xxSuccessful()) {
            return rse;
        }
        Map<String, ?> map = rse.getBody();
        if (map == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        ObjectMapper mapper = new ObjectMapper();
        DetailsDto rs = mapper.convertValue(map, DetailsDto.class);
        return ResponseEntity.ok(rs);
    }

    @ApiOperation("Get Locations for Filter")
    @GetMapping("/locations")
    public ResponseEntity<?> getPrLocations(HttpServletRequest request) {
        log.info("getPrLocations");
        ResponseEntity<Map<String, List<Map<String, ?>>>> rs = RequestCall.doGet(demoSrv, request);
        if (!rs.getStatusCode().is2xxSuccessful()) {
            return rs;
        }
        Map<String, List<Map<String, ?>>> map = rs.getBody();
        if (map == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        List<Map<String, ?>> items = map.get("items");
        List<PrLocationFilterItem> result = new ArrayList<>();
        for (Map<String, ?> item : items) {
            result.add(new PrLocationFilterItem(
                    (String) item.get("loc_code"),
                    (String) item.get("name"),
                    (int) item.get("qty")
            ));
        }
        return ResponseEntity.ok(new LocationFilterDto(result));
    }

}
