package com.megh.timekeeper.instrumentation

import io.micrometer.core.instrument.Counter
import io.micrometer.prometheus.PrometheusConfig

import io.micrometer.prometheus.PrometheusMeterRegistry

class MetricRegistry {
    private val registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    val formatRequestCounter: Counter = registry.counter("request.format.count")
}