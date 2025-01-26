package taskmanagement.shared.dto;

import lombok.Value;
import org.springframework.data.domain.Page;

@Value(staticConstructor = "of")
public class PaginationResponse {
    long pageNumber;
    long pageSize;
    long totalPages;
    long totalElements;

    public static PaginationResponse of(Page<?> page) {
        return new PaginationResponse(page.getNumber(), page.getSize(), page.getTotalPages(), page.getTotalElements());
    }
}
