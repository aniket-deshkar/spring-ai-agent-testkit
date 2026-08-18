package io.github.aniketdeshkar.agenttestkit.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

/** Enables {@link AgentTestContext} parameter injection for a JUnit Jupiter test class. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(AgentTestExtension.class)
public @interface AgentTest {}
