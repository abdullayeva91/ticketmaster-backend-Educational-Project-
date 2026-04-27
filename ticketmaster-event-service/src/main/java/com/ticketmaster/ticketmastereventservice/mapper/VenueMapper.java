package com.ticketmaster.ticketmastereventservice.mapper;

import com.ticketmaster.ticketmastereventservice.dto.request.VenueRequest;
import com.ticketmaster.ticketmastereventservice.dto.response.VenueResponse;
import com.ticketmaster.ticketmastereventservice.model.Venue;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VenueMapper {

    Venue toEntity(VenueRequest request);

    VenueResponse toResponse(Venue venue);

    List<VenueResponse> toResponseList(List<Venue> venues);
}