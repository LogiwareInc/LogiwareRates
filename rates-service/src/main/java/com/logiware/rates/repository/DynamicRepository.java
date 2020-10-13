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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.logiware.rates.datasource.DataSourceManager;
import com.logiware.rates.dto.KeyValueResult;
import com.mysql.cj.jdbc.StatementImpl;

@Service
public class DynamicRepository {

	@Autowired
	private DataSourceManager dataSourceManager;

	public List<KeyValueResult> getOptionResults(String dbUrl, String dbUser, String dbPassword, String sql, Map<String, Object> params) {
		DataSource dataSource = dataSourceManager.dataSource(dbUrl, dbUser, dbPassword);
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		if (params != null && !params.isEmpty()) {
			params.forEach((key, value) -> {
				parameterSource.addValue(key, value);
			});
		}
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, parameterSource);
		List<KeyValueResult> results = new ArrayList<>();
		for (Map<String, Object> row : rows) {
			results.add(new KeyValueResult((String) row.get("col1"), (String) row.get("col2")));
		}
		return results;
	}

	public List<KeyValueResult> getTypeaheadResults(String dbUrl, String dbUser, String dbPassword, String sql, String input) {
		DataSource dataSource = dataSourceManager.dataSource(dbUrl, dbUser, dbPassword);
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("input", input + "%");
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, parameterSource);
		List<KeyValueResult> results = new ArrayList<>();
		for (Map<String, Object> row : rows) {
			results.add(new KeyValueResult((String) row.get("col1"), (String) row.get("col2")));
		}
		return results;
	}

	public int executeUpdate(String dbUrl, String dbUser, String dbPassword, String sql, MapSqlParameterSource parameterSource) {
		DataSource dataSource = dataSourceManager.dataSource(dbUrl, dbUser, dbPassword);
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		return jdbcTemplate.update(sql, parameterSource);
	}

	public Integer loadLocalInfile(String dbUrl, String dbUser, String dbPassword, String sql, InputStream is) throws SQLException {
		if (!dbUrl.contains("allowLoadLocalInfile")) {
			dbUrl += (dbUrl.contains("?") ? "" : "?") + "allowLoadLocalInfile=true";
		}
		try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword); PreparedStatement stmt = conn.prepareStatement(sql);) {
			stmt.unwrap(StatementImpl.class)
				.setLocalInfileInputStream(is);
			int count = stmt.executeUpdate();
			return count;
		}
	}

	public void loadLocalInfile(String dbUrl, String dbUser, String dbPassword, String sql) {
		if (!dbUrl.contains("allowLoadLocalInfile")) {
			dbUrl += (dbUrl.contains("?") ? "" : "?") + "allowLoadLocalInfile=true";
		}
		DataSource dataSource = dataSourceManager.dataSource(dbUrl, dbUser, dbPassword);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.execute(sql);
	}

	public void executeUpdate(String dbUrl, String dbUser, String dbPassword, String sql) {
		DataSource dataSource = dataSourceManager.dataSource(dbUrl, dbUser, dbPassword);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.execute(sql);
	}

	public String getSingleValueQuery(String dbUrl, String dbUser, String dbPassword, String sql) {
		DataSource dataSource = dataSourceManager.dataSource(dbUrl, dbUser, dbPassword);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate.queryForObject(sql, String.class);
	}

	public int executeUpdateWithoutParam(String dbUrl, String dbUser, String dbPassword, String sql) {
		DataSource dataSource = dataSourceManager.dataSource(dbUrl, dbUser, dbPassword);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate.update(sql);
	}

	public List<KeyValueResult> getKeyValueResults(String dbUrl, String dbUser, String dbPassword, String sql) {
		DataSource dataSource = dataSourceManager.dataSource(dbUrl, dbUser, dbPassword);
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		return jdbcTemplate.query(sql, ((rs, rowNo) -> new KeyValueResult(rs.getString("key"), rs.getString("value"))));
	}

	public KeyValueResult getKeyValueResult(String dbUrl, String dbUser, String dbPassword, String sql) {
		DataSource dataSource = dataSourceManager.dataSource(dbUrl, dbUser, dbPassword);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			return jdbcTemplate.queryForObject(sql, ((rs, rowNo) -> new KeyValueResult(rs.getString("key"), rs.getString("value"))));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<String> getSingleValueList(String dbUrl, String dbUser, String dbPassword, String sql) {
		DataSource dataSource = dataSourceManager.dataSource(dbUrl, dbUser, dbPassword);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return jdbcTemplate.queryForList(sql, String.class);
	}
}
