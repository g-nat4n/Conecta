package com.conecta.repository;

import com.conecta.entity.SolicitacaoAmizade;
import com.conecta.entity.StatusSolicitacao;
import com.conecta.entity.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SolicitacaoAmizadeRepository extends JpaRepository<SolicitacaoAmizade, Long> {

	@EntityGraph(attributePaths = {"remetente", "destinatario"})
	Optional<SolicitacaoAmizade> findById(Long id);

	@EntityGraph(attributePaths = {"remetente", "destinatario"})
	List<SolicitacaoAmizade> findByDestinatarioAndStatusOrderByDataCriacaoDesc(
			Usuario destinatario, StatusSolicitacao status);

	@EntityGraph(attributePaths = {"remetente", "destinatario"})
	List<SolicitacaoAmizade> findByRemetenteAndStatusOrderByDataCriacaoDesc(
			Usuario remetente, StatusSolicitacao status);

	@Query("""
			select s from SolicitacaoAmizade s
			where s.status = :status and
			((s.remetente = :a and s.destinatario = :b) or (s.remetente = :b and s.destinatario = :a))
			""")
	Optional<SolicitacaoAmizade> findEntreUsuarios(
			@Param("a") Usuario a,
			@Param("b") Usuario b,
			@Param("status") StatusSolicitacao status);

	Optional<SolicitacaoAmizade> findByRemetenteAndDestinatario(Usuario remetente, Usuario destinatario);
}
