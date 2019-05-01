package com.logiware.rates.repository;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.logiware.rates.datasource.DataSourceManager;
import com.logiware.rates.dto.KeyValue;
import com.mysql.cj.jdbc.StatementImpl;

@Service
public class DynamicRepository {

	@Autowired
	private DataSourceManager dataSourceManager;

	public List<KeyValue> getOptionResults(String dbUrl, String dbUser, String dbPassword, String query, Map<String, Object> params) throws SQLException {
		DataSource dataSource = dataSourceManager.dataSource(dbUrl, dbUser, dbPassword);
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		if (params != null && !params.isEmpty()) {
			params.forEach((key, value) -> {
				parameterSource.addValue(key, value);
			});
		}
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, parameterSource);
		List<KeyValue> results = new ArrayList<>();
		for (Map<String, Object> row : rows) {
			results.add(new KeyValue((String) row.get("col1"), (String) row.get("col2")));
		}
		return results;
	}

	public List<KeyValue> getTypeaheadResults(String dbUrl, String dbUser, String dbPassword, String query, String input) throws SQLException {
		DataSource dataSource = dataSourceManager.dataSource(dbUrl, dbUser, dbPassword);
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("input", input + "%");
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, parameterSource);
		List<KeyValue> results = new ArrayList<>();
		for (Map<String, Object> row : rows) {
			results.add(new KeyValue((String) row.get("col1"), (String) row.get("col2")));
		}
		return results;
	}

	public int executeUpdate(String dbUrl, String dbUser, String dbPassword, String query, MapSqlParameterSource parameterSource) {
		DataSource dataSource = dataSourceManager.dataSource(dbUrl, dbUser, dbPassword);
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		parameterSource.getValues().forEach((k,v) -> {
			System.out.println(k+" == "+v);
		});
		return jdbcTemplate.update(query, parameterSource);
	}

	public Integer executeUpdate(String dbUrl, String dbUser, String dbPassword, String query, InputStream is) throws SQLException {
		if (!dbUrl.contains("allowLoadLocalInfile")) {
			dbUrl += (dbUrl.contains("?") ? "" : "?") + "allowLoadLocalInfile=true";
		}
		Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
		PreparedStatement stmt = conn.prepareStatement(query);
		stmt.unwrap(StatementImpl.class).setLocalInfileInputStream(is);
		int count = stmt.executeUpdate();
		stmt.close();
		conn.close();
		return count;
	}

}
