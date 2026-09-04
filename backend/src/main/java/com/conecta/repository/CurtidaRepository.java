package com.conecta.repository;

import com.conecta.entity.Curtida;
import com.conecta.entity.Post;
import com.conecta.entity.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurtidaRepository extends JpaRepository<Curtida, Long> {

	long countByPost(Post post);

	boolean existsByPostAndUsuario(Post post, Usuario usuario);

	Optional<Curtida> findByPostAndUsuario(Post post, Usuario usuario);

	void deleteByPost(Post post);
}
