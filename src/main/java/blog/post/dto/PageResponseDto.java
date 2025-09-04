package blog.post.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageResponseDto<T> {

    private final List<T> content;          // 데이터 목록
    private final int number;        // 현재 페이지 번호 (0부터 시작)
    private final int totalPages;         // 전체 페이지 수
    private final long totalElements;     // 전체 데이터 개수
    private final boolean isFirst;          // 첫 페이지 여부
    private final boolean isLast;           // 마지막 페이지 여부

    // Page 객체를 DTO로 변환하는 생성자
    public PageResponseDto(Page<T> page) {
        this.content = page.getContent();
        this.number = page.getNumber();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.isFirst = page.isFirst();
        this.isLast = page.isLast();
    }
}
