package com.conecta.repository;

import com.conecta.entity.Notificacao;
import com.conecta.entity.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

	@EntityGraph(attributePaths = "origemUsuario")
	List<Notificacao> findByUsuarioOrderByDataHoraDesc(Usuario usuario);

	long countByUsuarioAndLidaFalse(Usuario usuario);

	List<Notificacao> findByUsuarioAndLidaFalseOrderByDataHoraDesc(Usuario usuario);
}
