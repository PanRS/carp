package com.github.rxyor.redis.redisson.factory;

import com.github.rxyor.common.core.exception.ReadFileException;
import com.github.rxyor.redis.redisson.config.RedisDataSource;
import com.github.rxyor.redis.redisson.exception.RedissonLackConfigExecption;
import java.io.IOException;
import java.util.Objects;
import lombok.Getter;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-06-04 Tue 10:37:00
 * @since 1.0.0
 */
public class CarpRedissonFactory {

    @Getter
    private Config config;

    @Getter
    private RedisDataSource dataSource;

    @Getter
    private String yaml;

    @Getter
    private String json;

    private CarpRedissonFactory(Config config, RedisDataSource dataSource, String yaml, String json) {
        this.config = config;
        this.dataSource = dataSource;
        this.yaml = yaml;
        this.json = json;
    }

    public RedissonClient createRedissonClient() {
        return Redisson.create(buildConfig());
    }

    private Config buildConfig() {
        if (config != null) {
            return config;
        }
        if (dataSource != null) {
            return buildDataSourceConfig();
        }
        if (yaml != null) {
            return buildYamlConfig();
        }
        if (json != null) {
            return buildJsonConfig();
        }
        throw new RedissonLackConfigExecption("you must config redis for factory");
    }

    private Config buildDataSourceConfig() {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress(this.buildAddress(dataSource.getHost(), dataSource.getPort()));
        return config;
    }

    private Config buildYamlConfig() {
        Objects.requireNonNull(this.yaml, "yaml can't be null");
        try {
            return Config.fromYAML(this.yaml);
        } catch (IOException e) {
            throw new ReadFileException(e);
        }
    }

    private Config buildJsonConfig() {
        Objects.requireNonNull(this.json, "json can't be null");
        try {
            return Config.fromJSON(this.json);
        } catch (IOException e) {
            throw new ReadFileException(e);
        }
    }

    private String buildAddress(String host, Integer port) {
        Objects.requireNonNull(host, "host can't be null");
        Objects.requireNonNull(port, "port can't be null");
        return "redis://" + host + ":" + port;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Config config;

        private RedisDataSource dataSource;

        private String yaml;

        private String json;

        public Builder config(Config config) {
            Objects.requireNonNull(config, "config can't be null");
            this.config = config;
            return this;
        }

        public Builder dataSource(RedisDataSource redisDataSource) {
            Objects.requireNonNull(redisDataSource, "redisDataSource can't be null");
            this.dataSource = redisDataSource;
            return this;
        }

        public Builder yaml(String yamlPath) {
            Objects.requireNonNull(yamlPath, "yaml path can't be null");
            this.yaml = yamlPath;
            return this;
        }

        public Builder json(String jsonPath) {
            Objects.requireNonNull(jsonPath, "json path can't be null");
            this.json = jsonPath;
            return this;
        }

        public CarpRedissonFactory build() {
            return new CarpRedissonFactory(config, dataSource, yaml, json);
        }
    }
}