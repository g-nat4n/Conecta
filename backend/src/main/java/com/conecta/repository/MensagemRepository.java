package com.conecta.repository;

import com.conecta.entity.Mensagem;
import com.conecta.entity.StatusMensagem;
import com.conecta.entity.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {

	@EntityGraph(attributePaths = {"remetente", "destinatario"})
	@Query("""
			select m from Mensagem m
			where ((m.remetente = :eu and m.destinatario = :outro)
			   or (m.remetente = :outro and m.destinatario = :eu))
			  and m.status <> com.conecta.entity.StatusMensagem.EXCLUIDA
			order by m.dataHora asc
			""")
	List<Mensagem> findConversa(@Param("eu") Usuario eu, @Param("outro") Usuario outro);

	@Query("""
			select m from Mensagem m
			where m.id in (
			  select max(m2.id) from Mensagem m2
			  where (m2.remetente = :eu or m2.destinatario = :eu)
			    and m2.status <> com.conecta.entity.StatusMensagem.EXCLUIDA
			  group by case
			    when m2.remetente = :eu then m2.destinatario.id
			    else m2.remetente.id
			  end
			)
			order by m.dataHora desc
			""")
	@EntityGraph(attributePaths = {"remetente", "destinatario"})
	List<Mensagem> findUltimasPorConversa(@Param("eu") Usuario eu);

	long countByDestinatarioAndRemetenteAndStatusNotAndStatus(
			Usuario destinatario,
			Usuario remetente,
			StatusMensagem excluida,
			StatusMensagem lida);

	@Query("""
			select count(m) from Mensagem m
			where m.destinatario = :eu and m.remetente = :outro
			  and m.status <> com.conecta.entity.StatusMensagem.EXCLUIDA
			  and m.status <> com.conecta.entity.StatusMensagem.LIDA
			""")
	long countNaoLidas(@Param("eu") Usuario eu, @Param("outro") Usuario outro);
}
