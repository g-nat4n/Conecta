package com.conecta.repository;

import com.conecta.entity.Denuncia;
import com.conecta.entity.StatusDenuncia;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {

	@EntityGraph(attributePaths = "usuario")
	List<Denuncia> findAll();

	@EntityGraph(attributePaths = "usuario")
	List<Denuncia> findByUsuarioId(Long usuarioId);

	@EntityGraph(attributePaths = "usuario")
	List<Denuncia> findByStatus(StatusDenuncia status);
}
