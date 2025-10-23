package fr._3il.ticketron;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:/config")
public class EnvGetter {

  @Value("${MODEL_URL}")
  private String modelUrl;
  @Value("${MODEL_NAME}")
  private String modelName;

  public String getModelName() {
    return modelName;
  }

  public String getModelUrl() {
    return modelUrl;
  }
}
