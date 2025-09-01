package nexxus.shared.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;

import nexxus.shared.constants.ChargeFeeType;
import nexxus.shared.constants.FeeComponentType;
import nexxus.shared.constants.PSPSelectionMode;
import nexxus.shared.constants.Scope;
import nexxus.shared.constants.Status;

import io.r2dbc.spi.ConnectionFactory;

@Configuration
public class R2dbcConfig extends AbstractR2dbcConfiguration {

  private final ConnectionFactory connectionFactory;

  public R2dbcConfig(ConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  @Override
  public ConnectionFactory connectionFactory() {
    return connectionFactory;
  }

  @Bean
  public R2dbcCustomConversions customConversions() {
    List<Converter<?, ?>> converters = new ArrayList<>();

    // Scope enum converters
    converters.add(new ScopeReadingConverter());
    converters.add(new ScopeWritingConverter());

    // Status enum converters
    converters.add(new StatusReadingConverter());
    converters.add(new StatusWritingConverter());

    // PSPSelectionMode enum converters
    converters.add(new PSPSelectionModeReadingConverter());
    converters.add(new PSPSelectionModeWritingConverter());

    // ChargeFeeType enum converters
    converters.add(new ChargeFeeTypeReadingConverter());
    converters.add(new ChargeFeeTypeWritingConverter());

    // FeeComponentType enum converters
    converters.add(new FeeComponentTypeReadingConverter());
    converters.add(new FeeComponentTypeWritingConverter());

    @SuppressWarnings("unused")
    R2dbcDialect dialect = DialectResolver.getDialect(connectionFactory);
    return new R2dbcCustomConversions(getStoreConversions(), converters);
  }

  @ReadingConverter
  public static class ScopeReadingConverter implements Converter<String, Scope> {
    @Override
    public Scope convert(String source) {
      if (source == null) {
        return null;
      }
      return Scope.valueOf(source);
    }
  }

  @WritingConverter
  public static class ScopeWritingConverter implements Converter<Scope, String> {
    @Override
    public String convert(Scope source) {
      if (source == null) {
        return null;
      }
      return source.name();
    }
  }

  @ReadingConverter
  public static class StatusReadingConverter implements Converter<String, Status> {
    @Override
    public Status convert(String source) {
      if (source == null) {
        return null;
      }
      return Status.valueOf(source);
    }
  }

  @WritingConverter
  public static class StatusWritingConverter implements Converter<Status, String> {
    @Override
    public String convert(Status source) {
      if (source == null) {
        return null;
      }
      return source.name();
    }
  }

  @ReadingConverter
  public static class PSPSelectionModeReadingConverter
      implements Converter<String, PSPSelectionMode> {
    @Override
    public PSPSelectionMode convert(String source) {
      if (source == null) {
        return null;
      }
      return PSPSelectionMode.valueOf(source);
    }
  }

  @WritingConverter
  public static class PSPSelectionModeWritingConverter
      implements Converter<PSPSelectionMode, String> {
    @Override
    public String convert(PSPSelectionMode source) {
      if (source == null) {
        return null;
      }
      return source.name();
    }
  }

  @ReadingConverter
  public static class ChargeFeeTypeReadingConverter implements Converter<String, ChargeFeeType> {
    @Override
    public ChargeFeeType convert(String source) {
      if (source == null) {
        return null;
      }
      return ChargeFeeType.valueOf(source);
    }
  }

  @WritingConverter
  public static class ChargeFeeTypeWritingConverter implements Converter<ChargeFeeType, String> {
    @Override
    public String convert(ChargeFeeType source) {
      if (source == null) {
        return null;
      }
      return source.name();
    }
  }

  @ReadingConverter
  public static class FeeComponentTypeReadingConverter
      implements Converter<String, FeeComponentType> {
    @Override
    public FeeComponentType convert(String source) {
      if (source == null) {
        return null;
      }
      return FeeComponentType.valueOf(source);
    }
  }

  @WritingConverter
  public static class FeeComponentTypeWritingConverter
      implements Converter<FeeComponentType, String> {
    @Override
    public String convert(FeeComponentType source) {
      if (source == null) {
        return null;
      }
      return source.name();
    }
  }
}
