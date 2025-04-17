package com.jhome.user.dto;

import com.jhome.user.exception.CustomException;
import com.jhome.user.response.ApiResponseCode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Getter
@Builder
@ToString
public final class SearchRequest {

    @Min(value = 0, message = "Size must be at least 0")
    private final Integer page = 0;

    @Min(value = 1, message = "Size must be at least 1")
    private final Integer size = 10;

    private final String sortBy = "createdAt";

    @Pattern(regexp = "^(asc|desc)$", message = "Sort direction must be 'asc' or 'desc'.")
    private final String sortDirection = "desc";

    private final String searchKeyword;

    public Pageable createPageable() {
        final List<String> ALLOWED_SORT_FIELDS = List.of("createdAt", "username", "email");

        if(!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new CustomException(ApiResponseCode.REQUEST_SORT_FIELD_INVALID);
        }

        return PageRequest.of(
                page, size,
                Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
    }
}
