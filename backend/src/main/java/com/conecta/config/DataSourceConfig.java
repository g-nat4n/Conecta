package com.conecta.config;

import java.net.URI;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

	@Bean
	@Primary
	public DataSource dataSource() {
		String databaseUrl = System.getenv("DATABASE_URL");
		DataSourceBuilder<?> builder = DataSourceBuilder.create().driverClassName("org.postgresql.Driver");

		if (databaseUrl != null && !databaseUrl.isBlank() && !databaseUrl.startsWith("jdbc:")) {
			URI uri = URI.create(databaseUrl);
			String userInfo = uri.getUserInfo();
			if (userInfo == null || userInfo.isBlank()) {
				throw new IllegalStateException("DATABASE_URL sem credenciais");
			}
			String[] userPass = userInfo.split(":", 2);
			int port = uri.getPort() > 0 ? uri.getPort() : 5432;
			String path = uri.getPath() == null ? "" : uri.getPath();
			String query = (uri.getQuery() == null || uri.getQuery().isBlank()) ? "" : "?" + uri.getQuery();
			String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port + path + query;
			return builder
					.url(jdbcUrl)
					.username(userPass[0])
					.password(userPass.length > 1 ? userPass[1] : "")
					.build();
		}

		if (databaseUrl != null && databaseUrl.startsWith("jdbc:")) {
			return builder
					.url(databaseUrl)
					.username(envOr("DB_USERNAME", "postgres"))
					.password(envOr("DB_PASSWORD", "040501"))
					.build();
		}

		return builder
				.url(envOr("SPRING_DATASOURCE_URL", "jdbc:postgresql://localhost:5433/rede"))
				.username(envOr("SPRING_DATASOURCE_USERNAME", envOr("DB_USERNAME", "postgres")))
				.password(envOr("SPRING_DATASOURCE_PASSWORD", envOr("DB_PASSWORD", "040501")))
				.build();
	}

	private static String envOr(String key, String fallback) {
		String value = System.getenv(key);
		return value == null || value.isBlank() ? fallback : value;
	}
}
