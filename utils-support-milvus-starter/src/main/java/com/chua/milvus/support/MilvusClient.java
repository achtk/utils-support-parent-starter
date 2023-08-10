package com.chua.milvus.support;

import com.chua.common.support.protocol.client.AbstractClient;
import com.chua.common.support.protocol.client.ClientOption;
import com.chua.common.support.utils.StringUtils;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.*;
import io.milvus.param.*;
import io.milvus.param.collection.*;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.*;
import io.milvus.param.partition.CreatePartitionParam;
import io.milvus.param.partition.HasPartitionParam;
import io.milvus.param.partition.ReleasePartitionsParam;
import io.milvus.param.partition.ShowPartitionsParam;
import io.milvus.response.DescCollResponseWrapper;
import io.milvus.response.GetCollStatResponseWrapper;
import io.milvus.response.SearchResultsWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author CH
 */
public class MilvusClient extends AbstractClient<MilvusServiceClient> {

    private MilvusServiceClient milvusClient;

    protected MilvusClient(ClientOption clientOption) {
        super(clientOption);
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public void connectClient() {
        ConnectParam.Builder builder = ConnectParam.newBuilder()
                .withHost(netAddress.getHost())
                .withPort(netAddress.getPort(19530));

        if (StringUtils.isNotEmpty(clientOption.username())) {
            builder.withAuthorization(clientOption.username(), clientOption.password());
        }
        ConnectParam connectParam = builder.build();
        milvusClient = new MilvusServiceClient(connectParam);
    }

    @Override
    public MilvusServiceClient getClient() {
        return milvusClient;
    }

    @Override
    public void close() {
        milvusClient.close();
    }

    @Override
    public void closeClient(MilvusServiceClient client) {
        client.close();
    }

    /**
     * 建库
     *
     * @param createCollectionParam 建库参数
     * @param timeoutMilliseconds   超时时间(s)
     * @return 结果
     */
    public R<RpcStatus> createCollection(CreateCollectionParam createCollectionParam, long timeoutMilliseconds) {
        return milvusClient.withTimeout(timeoutMilliseconds, TimeUnit.MILLISECONDS)
                .createCollection(createCollectionParam);
    }

    /**
     * 建库
     *
     * @param collectionName      库名
     * @param description         描述
     * @param shardsNum           分片
     * @param timeoutMilliseconds 超时时间(s)
     * @return 结果
     */
    public R<RpcStatus> createCollection(String collectionName, String description, int shardsNum, List<FieldType> fieldTypeList, long timeoutMilliseconds) {
        CreateCollectionParam.Builder builder = CreateCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withDescription(description)
                .withShardsNum(shardsNum);
        for (FieldType fieldType : fieldTypeList) {
            builder.addFieldType(fieldType);
        }
        return createCollection(builder.build(), timeoutMilliseconds);
    }

    /**
     * 删库
     *
     * @param collectionName 库名
     * @return 结果
     */
    public R<RpcStatus> dropCollection(String collectionName) {
        return milvusClient.dropCollection(DropCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build());
    }

    /**
     * 数据库是否存在
     *
     * @param collectionName 库名
     * @return 结果
     */
    public boolean hasCollection(String collectionName) {
        return milvusClient.hasCollection(HasCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build()).getData().booleanValue();
    }

    /**
     * 加载数据库
     *
     * @param collectionName 库名
     * @return 结果
     */
    public R<RpcStatus> loadCollection(String collectionName) {
        return milvusClient.loadCollection(LoadCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build());
    }

    /**
     * 释放数据库
     *
     * @param collectionName 库名
     * @return 结果
     */
    public R<RpcStatus> releaseCollection(String collectionName) {
        return milvusClient.releaseCollection(ReleaseCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build());
    }

    /**
     * 数据库描述
     *
     * @param collectionName 库名
     * @return 结果
     */
    public String describeCollection(String collectionName) {
        R<DescribeCollectionResponse> response = milvusClient.describeCollection(DescribeCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build());
        DescCollResponseWrapper wrapper = new DescCollResponseWrapper(response.getData());
        return wrapper.toString();
    }

    /**
     * 数据库描述
     *
     * @param collectionName 库名
     * @return 结果
     */
    public R<GetCollectionStatisticsResponse> getCollectionStatistics(String collectionName) {
        milvusClient.flush(FlushParam.newBuilder().addCollectionName(collectionName).build());

        System.out.println("========== getCollectionStatistics() ==========");
        R<GetCollectionStatisticsResponse> response = milvusClient.getCollectionStatistics(
                GetCollectionStatisticsParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build());
        GetCollStatResponseWrapper wrapper = new GetCollStatResponseWrapper(response.getData());
        System.out.println("Collection row count: " + wrapper.getRowCount());
        return response;
    }

    /**
     * 查询所有库
     *
     * @return 所有库
     */

    private R<ShowCollectionsResponse> showCollections() {
        System.out.println("========== showCollections() ==========");
        R<ShowCollectionsResponse> response = milvusClient.showCollections(ShowCollectionsParam.newBuilder()
                .build());
        System.out.println(response);
        return response;
    }

    /**
     * 创建分区
     *
     * @param collectionName 集合名称
     * @return 所有库
     */

    private R<RpcStatus> createPartition(String collectionName, String partitionName) {
        System.out.println("========== createPartition() ==========");
        R<RpcStatus> response = milvusClient.createPartition(CreatePartitionParam.newBuilder()
                .withCollectionName(collectionName)
                .withPartitionName(partitionName)
                .build());
        System.out.println(response);
        return response;
    }

    /**
     * 删除分区
     *
     * @param collectionName 集合名称
     * @param partitionName  分区名称
     * @return 所有库
     */

    private Boolean dropPartition(String collectionName, String partitionName) {
        R<Boolean> response = milvusClient.hasPartition(HasPartitionParam.newBuilder()
                .withCollectionName(collectionName)
                .withPartitionName(partitionName)
                .build());
        return response.getData();
    }

    /**
     * 是否存在分区
     *
     * @param collectionName 集合名称
     * @param partitionName  分区名称
     * @return 所有库
     */

    private Boolean hasPartition(String collectionName, String partitionName) {
        R<Boolean> response = milvusClient.hasPartition(HasPartitionParam.newBuilder()
                .withCollectionName(collectionName)
                .withPartitionName(partitionName)
                .build());
        return response.getData();
    }

    /**
     * 释放分区
     *
     * @param collectionName 集合名称
     * @param partitionName  分区名称
     * @return 释放分区
     */

    private R<RpcStatus> releasePartition(String collectionName, String partitionName) {
        System.out.println("========== releasePartition() ==========");
        R<RpcStatus> response = milvusClient.releasePartitions(ReleasePartitionsParam.newBuilder()
                .withCollectionName(collectionName)
                .addPartitionName(partitionName)
                .build());
        System.out.println(response);
        return response;
    }

    /**
     * 显示分区
     *
     * @param collectionName 集合名称
     * @return 显示分区
     */

    private R<ShowPartitionsResponse> showPartitions(String collectionName) {
        System.out.println("========== showPartitions() ==========");
        R<ShowPartitionsResponse> response = milvusClient.showPartitions(ShowPartitionsParam.newBuilder()
                .withCollectionName(collectionName)
                .build());
        System.out.println(response);
        return response;
    }

    /**
     * 创建索引
     *
     * @return 创建索引
     */
    private R<RpcStatus> createIndex(CreateIndexParam createIndexParam) {
        System.out.println("========== createIndex() ==========");
        R<RpcStatus> response = milvusClient.createIndex(createIndexParam);
        System.out.println(response);
        return response;
    }

    /**
     * 删除索引
     *
     * @param collectionName 库名
     * @param indexName      索引名称
     * @return 索引
     */
    private R<RpcStatus> dropIndex(String collectionName, String indexName) {
        System.out.println("========== dropIndex() ==========");
        R<RpcStatus> response = milvusClient.dropIndex(DropIndexParam.newBuilder()
                .withCollectionName(collectionName)
                .withIndexName(indexName)
                .build());
        System.out.println(response);
        return response;
    }

    /**
     * 索引描述
     *
     * @param collectionName 库名
     * @param indexName      索引名称
     * @return 索引
     */
    private R<DescribeIndexResponse> describeIndex(String collectionName, String indexName) {
        System.out.println("========== describeIndex() ==========");
        R<DescribeIndexResponse> response = milvusClient.describeIndex(DescribeIndexParam.newBuilder()
                .withCollectionName(collectionName)
                .withIndexName(indexName)
                .build());
        System.out.println(response);
        return response;
    }

    /**
     * 索引状态
     *
     * @param collectionName 库名
     * @param indexName      索引名称
     * @return 索引
     */
    private R<GetIndexStateResponse> getIndexState(String collectionName, String indexName) {
        System.out.println("========== getIndexState() ==========");
        R<GetIndexStateResponse> response = milvusClient.getIndexState(GetIndexStateParam.newBuilder()
                .withCollectionName(collectionName)
                .withIndexName(indexName)
                .build());
        System.out.println(response);
        return response;
    }

    /**
     * 索引创建进度
     *
     * @param collectionName 库名
     * @param indexName      索引名称
     * @return 索引
     */
    private R<GetIndexBuildProgressResponse> getIndexBuildProgress(String collectionName, String indexName) {
        System.out.println("========== getIndexBuildProgress() ==========");
        R<GetIndexBuildProgressResponse> response = milvusClient.getIndexBuildProgress(
                GetIndexBuildProgressParam.newBuilder()
                        .withCollectionName(collectionName)
                        .withIndexName(indexName)
                        .build());
        System.out.println(response);
        return response;
    }

    /**
     * 删除分区
     *
     * @param collectionName 库
     * @param partitionName  分区名称
     * @param expr           过期时间
     * @return 结果
     */
    private R<MutationResult> delete(String collectionName, String partitionName, String expr) {
        System.out.println("========== delete() ==========");
        DeleteParam build = DeleteParam.newBuilder()
                .withCollectionName(collectionName)
                .withPartitionName(partitionName)
                .withExpr(expr)
                .build();
        R<MutationResult> response = milvusClient.delete(build);
        System.out.println(response.getData());
        return response;
    }

    /**
     * 查询人脸
     *
     * @param vectors        特征值
     * @param collectionName 库
     * @param topK           前几
     * @param vectorFields   特征值字段
     * @param params         参数
     * @param expr           表达式
     * @param outFields      输出字段
     * @return 结果
     */
    private SearchResultsWrapper searchFace(List<List<Float>> vectors, String collectionName, int topK, String vectorFields, String params, String expr, String... outFields) {
        System.out.println("========== searchFace() ==========");
        long begin = System.currentTimeMillis();

        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(collectionName)
                .withMetricType(MetricType.L2)
                .withOutFields(Arrays.asList(outFields))
                .withTopK(topK)
                .withVectors(vectors)
                .withVectorFieldName(vectorFields)
                .withExpr(expr)
                .withParams(params)
                .withGuaranteeTimestamp(Constant.GUARANTEE_EVENTUALLY_TS)
                .build();

        R<SearchResults> response = milvusClient.search(searchParam);
        long end = System.currentTimeMillis();
        long cost = (end - begin);
        System.out.println("Search time cost: " + cost + "ms");

        return new SearchResultsWrapper(response.getData().getResults());
    }

    /**
     * 添加数据
     *
     * @param collectionName 库
     * @param partitionName  分区
     * @return 结果
     */

    private R<MutationResult> addDocument(String collectionName, String partitionName, List<InsertParam.Field> fields) {
        System.out.println("========== insert() ==========");
        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(collectionName)
                .withPartitionName(partitionName)
                .withFields(fields)
                .build();

        return milvusClient.insert(insertParam);
    }

    /**
     * 删除数据
     *
     * @param collectionName 库
     * @param partitionName  分区
     * @param expr 表达式
     * @return 结果
     */

    private R<MutationResult> deleteDocument(String collectionName, String partitionName, String expr) {
        System.out.println("========== delete() ==========");
        DeleteParam insertParam = DeleteParam.newBuilder()
                .withExpr(expr)
                .withCollectionName(collectionName)
                .withPartitionName(partitionName)
                .build();

        return milvusClient.delete(insertParam);
    }



}
