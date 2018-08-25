package org.onap.aai.sparky.viewandinspect;

public interface VisualizationContextBuilder {

  public VisualizationContext getVisualizationContext() throws Exception;

  public void shutdown();

}
