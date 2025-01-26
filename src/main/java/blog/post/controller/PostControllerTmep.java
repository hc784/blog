/*
 * package blog.post.controller;
 * 
 * import blog.post.model.Post; import blog.post.service.PostService; import
 * org.springframework.stereotype.Controller; import
 * org.springframework.ui.Model; import
 * org.springframework.web.bind.annotation.*; import
 * org.springframework.web.servlet.mvc.support.RedirectAttributes;
 * 
 * import java.util.List;
 * 
 * @Controller
 * 
 * @RequestMapping("/temp") public class PostControllerTmep {
 * 
 * private final PostService postService;
 * 
 * public PostControllerTmep(PostService postService) { this.postService =
 * postService; }
 * 
 * 
 * // 글 작성 페이지를 보여줌 (뷰 렌더링)
 * 
 * @GetMapping("/create") public String showCreatePostForm() { return
 * "temp/post-form"; // post-form.html로 이동 }
 * 
 * // 글 작성 처리
 * 
 * @PostMapping("/create") public String createPost(@RequestParam String
 * title, @RequestParam String content, Model model, RedirectAttributes
 * redirectAttributes) { Post post = postService.createPost(title, content);
 * model.addAttribute("post", post);
 * redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 등록되었습니다!");
 * return "redirect:/posts/" + post.getId(); // 글 상세 페이지로 리다이렉트 }
 * 
 * // 전체 게시글 목록 페이지
 * 
 * @GetMapping public String getAllPosts(Model model) { List<Post> posts =
 * postService.getAllPosts(); model.addAttribute("posts", posts); return
 * "temp/post-list"; // post-list.html 렌더링 }
 * 
 * // 특정 게시글 상세 페이지
 * 
 * @GetMapping("/{id}") public String getPostById(@PathVariable Long id, Model
 * model) { Post post = postService.getPostById(id); model.addAttribute("post",
 * post); return "temp/post-detail"; // post-detail.html 렌더링 }
 * 
 * // 게시글 수정 페이지를 보여줌
 * 
 * @GetMapping("/edit/{id}") public String showEditForm(@PathVariable Long id,
 * Model model) { Post post = postService.getPostById(id);
 * model.addAttribute("post", post); return "temp/post-edit"; // post-edit.html
 * 렌더링 }
 * 
 * // 게시글 수정 처리
 * 
 * @PostMapping("/edit/{id}") public String updatePost(@PathVariable Long
 * id, @RequestParam String title, @RequestParam String content,
 * RedirectAttributes redirectAttributes) { postService.updatePost(id, title,
 * content); redirectAttributes.addFlashAttribute("message",
 * "게시글이 성공적으로 수정되었습니다!"); return "redirect:/posts/" + id; // 수정 후 해당 게시글 상세
 * 페이지로 이동 }
 * 
 * // 게시글 삭제 처리 후 목록 페이지로 리다이렉트
 * 
 * @DeleteMapping("/{id}") public String deletePost(@PathVariable Long id) {
 * postService.deletePost(id); return "redirect:/posts"; // 목록 페이지로 리다이렉트 } }
 */