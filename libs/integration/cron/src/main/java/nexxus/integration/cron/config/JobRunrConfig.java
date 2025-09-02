package nexxus.integration.cron.config;

import javax.sql.DataSource;
import org.jobrunr.configuration.JobRunr;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.sql.common.SqlStorageProviderFactory;
import org.springframework.beans.factory.annotation.Value;
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
    return SqlStorageProviderFactory.using(dataSource);
  }

  @Bean
  @ConditionalOnMissingBean(JobScheduler.class)
  public JobScheduler jobScheduler(
      StorageProvider storageProvider,
      @Value("${jobrunr.background-job-server.enabled:true}") boolean backgroundEnabled,
      @Value("${jobrunr.dashboard.enabled:false}") boolean dashboardEnabled) {
    var config = JobRunr.configure().useStorageProvider(storageProvider);
    if (backgroundEnabled) {
      config = config.useBackgroundJobServer();
    }
    if (dashboardEnabled) {
      config = config.useDashboard();
    }
    return config.initialize().getJobScheduler();
  }
}

