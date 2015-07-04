package redis.clients.jedis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class JedisPoolConfig extends GenericObjectPoolConfig {
  private String maxActive;
  private String maxWait;
  private String whenExhaustedAction;

  public JedisPoolConfig() {
    // defaults to make your life with connection pool easier :)
    setTestWhileIdle(true);
    setMinEvictableIdleTimeMillis(60000);
    setTimeBetweenEvictionRunsMillis(30000);
    setNumTestsPerEvictionRun(-1);
  }

  public void setMaxActive(String maxActive) {
    this.maxActive = maxActive;
  }

  public String getMaxActive() {
    return maxActive;
  }

  public void setMaxWait(String maxWait) {
    this.maxWait = maxWait;
  }

  public String getMaxWait() {
    return maxWait;
  }

  public void setWhenExhaustedAction(String whenExhaustedAction) {
    this.whenExhaustedAction = whenExhaustedAction;
  }

  public String getWhenExhaustedAction() {
    return whenExhaustedAction;
  }
}
