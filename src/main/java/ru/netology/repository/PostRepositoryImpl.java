package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepositoryImpl implements PostRepository {
    private final Map<Long, Post> postRepository = new ConcurrentHashMap<>();
    private final Map<Long, Post> removedPosts = new ConcurrentHashMap<>();
    private final AtomicLong id = new AtomicLong();

    @Override
    public List<Post> all() {
        return new ArrayList<>(postRepository.values());
    }

    @Override
    public Optional<Post> getById(long id) {
        return Optional.ofNullable(postRepository.get(id));
    }

    @Override
    public Post save(Post post) {
        if (post.getId() != 0) {
            addAndRemovePost(post.getId());
            postRepository.put(post.getId(), post);
        }
        if (post.getId() == 0) {
            final var idPost = id.incrementAndGet();
            Post newPost = new Post(idPost, post.getContent());
            postRepository.put(idPost, newPost);
            return newPost;
        }
        return post;
    }

    @Override
    public void removeById(long id) {
        addAndRemovePost(id);
    }

    private void addAndRemovePost(long id) {
        if (postRepository.containsKey(id)) {
            removedPosts.put(postRepository.get(id).getId(), postRepository.get(id));
            postRepository.remove(id);
        } else {
            notFoundException();
        }
    }

    private void notFoundException() {
        throw new NotFoundException();
    }
}
