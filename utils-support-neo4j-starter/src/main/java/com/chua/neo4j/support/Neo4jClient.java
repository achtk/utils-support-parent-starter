package com.chua.neo4j.support;

import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.ClientOption;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.data.neo4j.core.Neo4jTemplate;

import java.util.concurrent.TimeUnit;


/**
 * neo4j
 * <p>
 *     构建关系：
 *     CEO
 *          -设计部
 *              - 设计1组
 *              - 设计2组
 *          -技术部
 *              - 前端技术部
 *              - 后端技术部
 *              - 测试技术部
 * </p>
 * <p>
 *     部门实体：
 *      @NodeEntity(label = "dept")
 *      @Data
 *      @Builder
 *      public class Dept {
 *
 *          @Id
 *          @GeneratedValue
 *          private Long id;
 *
 *          @Property(name = "deptName")
 *          private String deptName;
 *      }
 * </p>
 * <p>
 *     关系实体:
 *      @RelationshipEntity(type = "relationShip")
 *      @Data
 *      @Builder
 *      public class RelationShip {
 *
 *          @Id
 *          @GeneratedValue
 *          private Long id;
 *
 *          @StartNode
 *          private Dept parent;
 *
 *          @EndNode
 *          private Dept child;
 *      }
 * </p>
 * <p>注解说明:</p>
 * <p>
 *      @NodeEntity 标明是一个节点实体
 *      @RelationshipEntity 标明是一个关系实体
 *      @Id 实体主键
 *      @Property 实体属性
 *      @GeneratedValue 实体属性值自增
 *      @StartNode 开始节点（可以理解为父节点）
 *      @EndNode 结束节点（可以理解为子节点）
 * </p>
 * @author CH
 * @version 1.0.0
 * @since 2021/6/5
 */
public class Neo4jClient extends AbstractClient<Neo4jTemplate> {
    protected Neo4jTemplate neo4jTemplate;
    private Driver driver;
    private Config config;

    public Neo4jClient(ClientOption clientOption) {
        super(clientOption);
    }

    @Override
    public Neo4jTemplate getClient() {
        return neo4jTemplate;
    }

    @Override
    public void closeClient(Neo4jTemplate client) {

    }

    @Override
    public void afterPropertiesSet() {
        this.config = Config.builder()
                .withConnectionTimeout(clientOption.connectionTimeoutMillis(), TimeUnit.MILLISECONDS)
                .withEncryption()
                .withMaxConnectionPoolSize(clientOption.maxTotal())
                .build();
    }

    @Override
    public void connectClient() {
        this.driver = GraphDatabase
                .driver(url,
                        AuthTokens.basic(clientOption.username(),
                                clientOption.password()), config);
        this.neo4jTemplate = new Neo4jTemplate(org.springframework.data.neo4j.core.Neo4jClient.create(driver));

    }

    @Override
    public void close() {
        driver.close();
    }
}
