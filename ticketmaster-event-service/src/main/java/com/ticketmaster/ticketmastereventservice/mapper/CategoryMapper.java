package com.ticketmaster.ticketmastereventservice.mapper;

import com.ticketmaster.ticketmastereventservice.dto.request.CategoryRequest;
import com.ticketmaster.ticketmastereventservice.dto.response.CategoryResponse;
import com.ticketmaster.ticketmastereventservice.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    Category toEntity(CategoryRequest request);

    CategoryResponse toResponse(Category category);

    List<CategoryResponse> toResponseList(List<Category> categories);
}