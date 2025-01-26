package blog.post.dto;

import lombok.Data;

@Data
public class CategoryDto {

    // 기본 생성자
    public CategoryDto() {}

    // 생성자
    public CategoryDto(String id, String name, int order, String parent) {
        this.id = id;
        this.name = name;
        this.order = order;
        this.parent = parent;
    }

    private String id;  // `new-123456789` 같은 임시 ID도 처리 가능
    private String name;
    private int order;
    private String parent;

}
