package com.conecta.repository;

import com.conecta.entity.Amizade;
import com.conecta.entity.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AmizadeRepository extends JpaRepository<Amizade, Long> {

	@EntityGraph(attributePaths = "amigo")
	List<Amizade> findByUsuarioOrderByDataCriacaoDesc(Usuario usuario);

	boolean existsByUsuarioAndAmigo(Usuario usuario, Usuario amigo);

	Optional<Amizade> findByUsuarioAndAmigo(Usuario usuario, Usuario amigo);

	@Query("""
			select count(a) > 0 from Amizade a
			where (a.usuario = :u1 and a.amigo = :u2) or (a.usuario = :u2 and a.amigo = :u1)
			""")
	boolean saoAmigos(@Param("u1") Usuario u1, @Param("u2") Usuario u2);

	long countByUsuario(Usuario usuario);
}
