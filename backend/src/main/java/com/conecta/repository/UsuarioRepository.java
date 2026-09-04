package com.conecta.repository;

import com.conecta.entity.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	java.util.Optional<Usuario> findByEmail(String email);

	boolean existsByEmail(String email);

	@Query("""
			select u from Usuario u
			where lower(u.nome) like lower(concat('%', :q, '%'))
			   or lower(u.email) like lower(concat('%', :q, '%'))
			order by u.nome asc
			""")
	List<Usuario> buscarPorNomeOuEmail(@Param("q") String q);
}
