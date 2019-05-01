package com.logiware.rates.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Service;

@Service
public class DataSourceManager {

	private static final Map<String, DataSource> DATA_SOURCES = new HashMap<>();
    private DataSource dataSource;

    public DataSourceManager() {
    }

    public DataSourceManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void add(String name, DataSource dataSource) {
    	DATA_SOURCES.put(name, dataSource);
    }

    private void switchDataSource(String name) {
        dataSource = DATA_SOURCES.get(name);
    }
    
    private DataSource makeDataSource(String url, String username, String password) {
		return DataSourceBuilder.create().driverClassName("com.mysql.cj.jdbc.Driver").url(url).username(username).password(password).build();
	}

	public DataSource dataSource(String url, String username, String password) {
		String name = url + username + password;
    	if(!DATA_SOURCES.containsKey(name)) {
    		add(name, makeDataSource(url, username, password));
    	}
		switchDataSource(name);
		return dataSource;
	}
}
