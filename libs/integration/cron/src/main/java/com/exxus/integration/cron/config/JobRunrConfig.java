package com.exxus.integration.cron.config;

import javax.sql.DataSource;

import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.sql.postgres.PostgresStorageProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobRunrConfig {

  @Bean
  @ConditionalOnBean(DataSource.class)
  @ConditionalOnMissingBean(StorageProvider.class)
  public StorageProvider storageProvider(DataSource dataSource) {
    return new PostgresStorageProvider(dataSource);
  }
}
