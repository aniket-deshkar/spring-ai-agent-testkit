package io.github.aniketdeshkar.agenttestkit.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

/** Imports the deterministic agent test context into a Spring Boot test. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ImportAutoConfiguration(AgentTestkitAutoConfiguration.class)
public @interface AutoConfigureAgentTestkit {}
