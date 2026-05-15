package com.e_commerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private List<CategoryDTO> content;

    private Integer page;

    private Integer size;

    private Long totalElements;

    private Integer totalPages;

    private boolean last;
}
