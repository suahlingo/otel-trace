package com.example.tracing.config;

import com.example.tracing.service.ContextAwareExecutorService;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.ServiceAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.*;

@Configuration
public class TraceConfig {

  @Value("${spring.application.name}")
  private String serviceName;

  @Value("${otel.exporter.otlp.endpoint}")
  private String otlpEndpoint;

  @Bean
  public OpenTelemetry openTelemetry() {
    Resource resource = Resource.getDefault()
        .merge(Resource.create(io.opentelemetry.api.common.Attributes.of(
            ServiceAttributes.SERVICE_NAME, serviceName
        )));

    OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
        .setEndpoint(otlpEndpoint)
        .setTimeout(Duration.ofSeconds(5))
        .build();

    SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
        .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
        .setResource(resource)
        .build();

    return OpenTelemetrySdk.builder()
        .setTracerProvider(tracerProvider)
        .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
        .buildAndRegisterGlobal();
  }

  /**
   * Context-aware ExecutorService for propagating OpenTelemetry context in async tasks.
   */
  @Bean
  public ExecutorService contextAwareExecutorService() {
    return new ContextAwareExecutorService(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
  }
}
