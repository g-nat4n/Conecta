package com.conecta.repository;

import com.conecta.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	@EntityGraph(attributePaths = "autor")
	Page<Post> findAllByOrderByDataCriacaoDesc(Pageable pageable);

	long countByAutor(com.conecta.entity.Usuario autor);
}
