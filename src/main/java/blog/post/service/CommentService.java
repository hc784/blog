package blog.post.service;

import blog.post.dto.CommentDto;
import blog.post.dto.CommentRequestDto;
import blog.post.model.Comment;
import blog.post.model.Post;
import blog.post.repository.CommentRepository;
import blog.post.repository.PostRepository;
import blog.security.model.User;
import blog.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화를 위해 주입

    /**
     * 댓글 및 답글 생성
     */
    
    @Transactional
    public CommentDto createComment(CommentRequestDto requestDto) {
        Post post = postRepository.findById(requestDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + requestDto.getPostId()));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(requestDto.getContent());

        // 답글인 경우 부모 댓글 설정
        if (requestDto.getParentId() != null) {
            Comment parent = commentRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다. ID: " + requestDto.getParentId()));
            comment.setParent(parent);
        }

        // 사용자 유형에 따라 작성자 정보 설정
        if (requestDto.getUserId() != null) {
            User user = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + requestDto.getUserId()));
            comment.setUser(user);
        } else {
            comment.setAnonymousAuthor(requestDto.getAnonymousAuthor());
            if (requestDto.getAnonymousPassword() != null && !requestDto.getAnonymousPassword().isEmpty()) {
                // 비밀번호를 암호화하여 저장
                comment.setAnonymousPassword(passwordEncoder.encode(requestDto.getAnonymousPassword()));
            }
        }

        Comment savedComment = commentRepository.save(comment);
        return new CommentDto(savedComment);
    }

    /**
     * 특정 게시글의 댓글 목록 조회 (DTO로 변환)
     */
    @Transactional(readOnly = true)
    public Page<CommentDto> getCommentsByPost(Long postId, Pageable pageable) {
        Page<Comment> parentCommentsPage = commentRepository.findParentCommentsByPostId(postId, pageable);
        List<Comment> parentComments = parentCommentsPage.getContent();

        // if (parentComments.isEmpty()) 블록을 제거해도 아래 로직이 정상적으로 빈 페이지를 처리합니다.

        List<Long> parentIds = parentComments.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        // parentIds가 비어있으면 replies도 비어있게 됩니다.
        List<Comment> replies = parentIds.isEmpty() ? List.of() : commentRepository.findRepliesByParentIds(parentIds);

        Map<Long, List<Comment>> repliesMap = replies.stream()
                .collect(Collectors.groupingBy(reply -> reply.getParent().getId()));

        parentComments.forEach(parent -> parent.setReplies(repliesMap.getOrDefault(parent.getId(), List.of())));
        
        List<CommentDto> dtoList = parentComments.stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, parentCommentsPage.getTotalElements());
    }
    

    /**
     * 댓글 삭제
     */
    public void deleteComment(Long commentId, String anonymousPassword, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. ID: " + commentId));

        // 익명 댓글인 경우 비밀번호 확인
        if (comment.isAnonymous()) {
            if (anonymousPassword == null || !passwordEncoder.matches(anonymousPassword, comment.getAnonymousPassword())) {
                throw new IllegalArgumentException("익명 댓글의 비밀번호가 일치하지 않습니다.");
            }
        } else {
        	
            if (user == null || !comment.getUser().getId().equals(user.getId())) {
            	throw new IllegalArgumentException("댓글 작성자가 일치하지 않습니다."); // 본인이 아니면 삭제 불가
            }

        }

        commentRepository.delete(comment);
    }
}

