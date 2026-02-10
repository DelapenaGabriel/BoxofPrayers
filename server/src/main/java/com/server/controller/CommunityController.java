package com.server.controller;

import com.server.dao.PostCommentDao;
import com.server.dao.PostDao;
import com.server.dao.ReactionDao;
import com.server.dao.UserDao;
import com.server.model.Post;
import com.server.model.PostComment;
import com.server.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/community")
public class CommunityController {

    private final PostDao postDao;
    private final PostCommentDao commentDao;
    private final ReactionDao reactionDao;
    private final UserDao userDao;

    public CommunityController(PostDao postDao, PostCommentDao commentDao, ReactionDao reactionDao, UserDao userDao) {
        this.postDao = postDao;
        this.commentDao = commentDao;
        this.reactionDao = reactionDao;
        this.userDao = userDao;
    }

    @GetMapping("/posts")
    public List<Post> getFeed(Principal principal, @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int offset) {
        int userId = getCurrentUserId(principal);
        return postDao.getAllPosts(userId, limit, offset);
    }

    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public Post createPost(@RequestBody Post post, Principal principal) {
        int userId = getCurrentUserId(principal);
        post.setUserId(userId);
        
        // Validate that at least content, media, or originalPostId is provided
        // Reposts can have empty content if they reference an original post
        boolean hasContent = post.getContent() != null && !post.getContent().trim().isEmpty();
        boolean hasMedia = (post.getImageUrl() != null && !post.getImageUrl().trim().isEmpty()) ||
                          (post.getVideoUrl() != null && !post.getVideoUrl().trim().isEmpty());
        boolean isRepost = post.getOriginalPostId() != null;
        
        if (!hasContent && !hasMedia && !isRepost) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post must have content, image, video, or be a repost");
        }
        
        return postDao.createPost(post);
    }

    @PutMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public Post updatePost(@PathVariable int postId, @RequestBody Post post, Principal principal) {
        int userId = getCurrentUserId(principal);
        post.setId(postId);
        return postDao.updatePost(post, userId);
    }

    @DeleteMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable int postId, Principal principal) {
        int userId = getCurrentUserId(principal);
        // Add check for admin or owner
        postDao.deletePost(postId, userId);
    }

    @GetMapping("/posts/{postId}/comments")
    public List<PostComment> getComments(@PathVariable int postId) {
        return commentDao.getCommentsByPostId(postId);
    }

    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public PostComment createComment(@PathVariable int postId, @RequestBody PostComment comment, Principal principal) {
        int userId = getCurrentUserId(principal);
        comment.setUserId(userId);
        comment.setPostId(postId);
        return commentDao.createComment(comment);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable int commentId, Principal principal) {
        int userId = getCurrentUserId(principal);
        User user = userDao.getUserByEmail(principal.getName());
        boolean isAdmin = "ROLE_ADMIN".equals(user.getRole());

        if (isAdmin) {
             commentDao.deleteComment(commentId);
        } else {
             commentDao.deleteComment(commentId, userId);
        }
    }

    @PostMapping("/posts/{postId}/react")
    @ResponseStatus(HttpStatus.OK)
    public void toggleReaction(@PathVariable int postId, @RequestParam(defaultValue = "AMEN") String type, Principal principal) {
        int userId = getCurrentUserId(principal);
        // Simplistic toggle logic can be handled here or in frontend. For now, we'll assume the frontend calls add/remove appropriately, 
        // or we can implement a toggle check.
        // But for "Amen", it's usually a toggle. 
        // Let's assume this endpoint is for ADDING. 
        // To remove, users should probably call DELETE or we check existence.
        // Actually, let's make it idempotent ADD.
        reactionDao.addReaction(postId, userId, type);
    }

    @DeleteMapping("/posts/{postId}/react")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeReaction(@PathVariable int postId, @RequestParam(defaultValue = "AMEN") String type, Principal principal) {
        int userId = getCurrentUserId(principal);
        reactionDao.removeReaction(postId, userId, type);
    }

    private int getCurrentUserId(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        User user = userDao.getUserByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found for email: " + principal.getName());
        }
        return user.getId();
    }
}
