package io.github.aniketdeshkar.agenttestkit.junit;

import io.github.aniketdeshkar.agenttestkit.AgentTestContext;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/** Creates one isolated {@link AgentTestContext} per JUnit test method. */
public final class AgentTestExtension implements ParameterResolver {
  private static final ExtensionContext.Namespace NAMESPACE =
      ExtensionContext.Namespace.create(AgentTestExtension.class);

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext) {
    return parameterContext.getParameter().getType().equals(AgentTestContext.class);
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return extensionContext
        .getStore(NAMESPACE)
        .getOrComputeIfAbsent(extensionContext.getUniqueId(), ignored -> new AgentTestContext());
  }
}
