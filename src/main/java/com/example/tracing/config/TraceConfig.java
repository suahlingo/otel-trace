package com.example.tracing.config;

import com.example.tracing.service.ContextAwareExecutorService;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.ServiceAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.*;
import org.springframework.core.Ordered;

@Configuration
@AutoConfigureAfter(name = "org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration")
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
public class TraceConfig {

  @Value("${spring.application.name:default-service}")
  private String serviceName;

  @Value("${otel.exporter.otlp.endpoint:http://localhost:4317}")
  private String otlpEndpoint;

//  @Bean
//  public OtlpGrpcSpanExporter spanExporter() {
//    return OtlpGrpcSpanExporter.builder()
//        .setEndpoint(otlpEndpoint)
//        .setTimeout(Duration.ofSeconds(5))
//        .build();
//  }

//  @Bean
//  public SdkTracerProvider sdkTracerProvider(OtlpGrpcSpanExporter spanExporter) {
//    Resource resource = Resource.getDefault().merge(
//        Resource.create(Attributes.of(ServiceAttributes.SERVICE_NAME, serviceName))
//    );
//
//    return SdkTracerProvider.builder()
//        .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
//        .setResource(resource)
//        .build();
//  }

//  @Bean
//  public OpenTelemetry openTelemetry(SdkTracerProvider sdkTracerProvider) {
//    return OpenTelemetrySdk.builder()
//        .setTracerProvider(sdkTracerProvider)
//        .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
//        .buildAndRegisterGlobal();
//  }
//
//  @Bean
//  public Tracer tracer(OpenTelemetry openTelemetry) {
//    return openTelemetry.getTracer("custom-tracer");
//  }

  @Bean
  public ExecutorService contextAwareExecutorService() {
    ExecutorService delegate = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    return new ContextAwareExecutorService(delegate);
  }
}
