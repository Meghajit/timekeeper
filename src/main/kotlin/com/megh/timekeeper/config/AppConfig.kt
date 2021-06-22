package com.megh.timekeeper.config

import com.megh.timekeeper.instrumentation.MetricRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope

@Configuration
class AppConfig {
    @Bean
    @Scope("singleton")
    fun getMetricRegistry(): MetricRegistry {
        return MetricRegistry()
    }
}