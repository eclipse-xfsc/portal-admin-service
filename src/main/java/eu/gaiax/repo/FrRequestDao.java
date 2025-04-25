package eu.gaiax.repo;

//import eu.gaiax.repo.dto.LocationCount;
//import eu.gaiax.repo.dto.RequestTypeCount;

import eu.gaiax.repo.dto.LocationCount;
import eu.gaiax.repo.dto.RequestTypeCount;
import eu.gaiax.repo.entities.FrRequest;
import eu.gaiax.repo.entities.FrRequestTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface FrRequestDao extends JpaRepository<FrRequest, Long> {
  Optional<FrRequest> findByEmailAndRequestType(@NonNull String email, @NonNull FrRequestTypeEntity requestType);

  @Query(value =
          "select rt.name as requestType, " +
                  "       count(1) as requestTypeCount " +
                  "  from fr_request r, fr_request_type rt" +
                  " where r.request_type_id = rt.id" +
                  "   and rt.name not in ('VC_NP_NOT_CONFIRMED', 'VC_PPR_NOT_CONFIRMED')" +
                  " group by rt.name",
          nativeQuery = true)
  List<RequestTypeCount> getRequestTypeCount();

  @Query(value =
          "select r.location," +
                  " count(1) as locationCount " +
                  "from " +
                  "     fr_request r, " +
                  "     fr_request_type rt " +
                  "where r.request_type_id = rt.id " +
                  "and rt.name not in ('VC_NP_NOT_CONFIRMED', 'VC_PPR_NOT_CONFIRMED') " +
                  "group by r.location ",
          nativeQuery = true)
  List<LocationCount> getLocationCount();

  @Query(value = "select sd.*" +
          "  from fr_request sd, fr_request_type rt" +
          " where sd.request_type_id = rt.id\n" +
          "   and rt.name not in ('VC_NP_NOT_CONFIRMED', 'VC_PPR_NOT_CONFIRMED')" +
          "   and sd.location in (:locationLst)" +
          "   and rt.name in (:rqTypeLst)",
          nativeQuery = true)
  @NonNull
  Page<FrRequest> filter(
          @NonNull Pageable pageable,
          @NonNull @Param("locationLst") List<String> locationLst,
          @NonNull @Param("rqTypeLst") List<String> rqTypeLst
  );

  @Query(value = "select sd.*" +
          "  from fr_request sd, fr_request_type rt" +
          " where sd.request_type_id = rt.id\n" +
          "   and rt.name not in ('VC_NP_NOT_CONFIRMED', 'VC_PPR_NOT_CONFIRMED')" +
          "   and rt.name in (:rqTypeLst)",
          nativeQuery = true)
  @NonNull
  Page<FrRequest> filterForAllLocations(
          @NonNull Pageable pageable,
          @NonNull @Param("rqTypeLst") List<String> rqTypeLst
  );

  @Query(value = "select sd.*" +
          "  from fr_request sd, fr_request_type rt" +
          " where sd.request_type_id = rt.id\n" +
          "   and rt.name not in ('VC_NP_NOT_CONFIRMED', 'VC_PPR_NOT_CONFIRMED')" +
          "   and sd.location in (:locationLst)",
          nativeQuery = true)
  @NonNull
  Page<FrRequest> filterForAllRequestTypes(
          @NonNull Pageable pageable,
          @NonNull @Param("locationLst") List<String> locationLst
  );

  @Query(value = "select sd.*" +
          "  from fr_request sd, fr_request_type rt" +
          " where sd.request_type_id = rt.id\n" +
          "   and rt.name not in ('VC_NP_NOT_CONFIRMED', 'VC_PPR_NOT_CONFIRMED')",
          nativeQuery = true)
  @NonNull
  Page<FrRequest> filterForAllRequestTypesAndAllLocations(
          @NonNull Pageable pageable
  );

  @Override
  @NonNull
  Optional<FrRequest> findById(@NonNull Long aLong);
}
