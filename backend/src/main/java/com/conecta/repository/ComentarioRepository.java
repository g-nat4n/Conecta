package com.conecta.repository;

import com.conecta.entity.Comentario;
import com.conecta.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

	@EntityGraph(attributePaths = "autor")
	List<Comentario> findByPostOrderByDataCriacaoAsc(Post post);

	long countByPost(Post post);

	void deleteByPost(Post post);
}
