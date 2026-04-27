package com.ticketmaster.ticketmastereventservice.mapper;

import com.ticketmaster.ticketmastereventservice.dto.request.CreateEventRequest;
import com.ticketmaster.ticketmastereventservice.dto.request.UpdateEventRequest;
import com.ticketmaster.ticketmastereventservice.dto.response.EventListResponse;
import com.ticketmaster.ticketmastereventservice.dto.response.EventResponse;
import com.ticketmaster.ticketmastereventservice.model.Event;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(target = "venueName", source = "venue.name")
    @Mapping(target = "city", source = "venue.city")
    @Mapping(target = "address", source = "venue.address")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "eventStatus", source = "eventStatus")
    EventResponse toResponse(Event event);

    @Mapping(target = "venueName", source = "venue.name")
    @Mapping(target = "city", source = "venue.city")
    @Mapping(target = "categoryName", source = "category.name")
    EventListResponse toListResponse(Event event);

    List<EventListResponse> toListResponseList(List<Event> events);

    @Mapping(target = "venue", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    Event toEntity(CreateEventRequest request);

    @Mapping(target = "venue", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEntityFromDto(UpdateEventRequest request, @MappingTarget Event event);
}