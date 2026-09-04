package com.conecta.service;

import com.conecta.exception.ArquivoInvalidoException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Armazenamento local de imagens.
 * Preparado para futura troca por serviço externo (S3, Cloudinary, etc.).
 */
@Service
public class FileStorageService {

	private static final Set<String> TIPOS_PERMITIDOS = Set.of(
			"image/jpeg",
			"image/png",
			"image/webp",
			"image/gif");

	private final Path rootPath;

	public FileStorageService(@Value("${conecta.upload.dir:uploads}") String uploadDir) {
		this.rootPath = Paths.get(uploadDir).toAbsolutePath().normalize();
		try {
			Files.createDirectories(rootPath.resolve("perfis"));
			Files.createDirectories(rootPath.resolve("posts"));
		} catch (IOException e) {
			throw new IllegalStateException("Não foi possível criar o diretório de uploads", e);
		}
	}

	public String salvar(MultipartFile arquivo, String pasta) {
		validar(arquivo);

		String original = arquivo.getOriginalFilename() != null ? arquivo.getOriginalFilename() : "arquivo";
		String extensao = extrairExtensao(original);
		String nomeArquivo = UUID.randomUUID() + extensao;
		Path destino = rootPath.resolve(pasta).resolve(nomeArquivo).normalize();

		if (!destino.startsWith(rootPath)) {
			throw new ArquivoInvalidoException("Caminho de arquivo inválido");
		}

		try {
			Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new ArquivoInvalidoException("Falha ao salvar o arquivo");
		}

		return "/uploads/" + pasta + "/" + nomeArquivo;
	}

	public void removerSeExistir(String caminhoRelativo) {
		if (caminhoRelativo == null || caminhoRelativo.isBlank() || !caminhoRelativo.startsWith("/uploads/")) {
			return;
		}
		Path arquivo = rootPath.resolve(caminhoRelativo.replaceFirst("^/uploads/", "")).normalize();
		if (!arquivo.startsWith(rootPath)) {
			return;
		}
		try {
			Files.deleteIfExists(arquivo);
		} catch (IOException ignored) {
			// não bloqueia o fluxo principal
		}
	}

	public Path getRootPath() {
		return rootPath;
	}

	private void validar(MultipartFile arquivo) {
		if (arquivo == null || arquivo.isEmpty()) {
			throw new ArquivoInvalidoException("O arquivo é obrigatório");
		}
		String contentType = arquivo.getContentType();
		if (contentType == null || !TIPOS_PERMITIDOS.contains(contentType.toLowerCase())) {
			throw new ArquivoInvalidoException("Formato de imagem inválido. Use JPG, PNG, WEBP ou GIF");
		}
		if (arquivo.getSize() > 5 * 1024 * 1024) {
			throw new ArquivoInvalidoException("A imagem deve ter no máximo 5MB");
		}
	}

	private String extrairExtensao(String nome) {
		int idx = nome.lastIndexOf('.');
		if (idx < 0) {
			return ".jpg";
		}
		return nome.substring(idx).toLowerCase();
	}
}
