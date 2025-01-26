package blog.post.dto;

import java.util.List;

import lombok.Data;

@Data
public class CategoryRequest {
    private List<CategoryDto> categories;
    private List<Long> deletedCategories;
}
