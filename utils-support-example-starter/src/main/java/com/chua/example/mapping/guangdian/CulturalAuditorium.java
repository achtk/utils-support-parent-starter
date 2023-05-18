package com.chua.example.mapping.guangdian;

import com.chua.common.support.mapping.annotation.*;
import com.chua.common.support.mapping.condition.SystemTimeMappingCondition;

import java.util.List;

/**
 * 文化礼堂数据
 *
 * @author CH
 */
@MappingAddress("https://luqiao.mi.culaud.com")
public interface CulturalAuditorium {
    /**
     * 文化礼堂广电 Token
     *
     * @return token
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingRequest("POST /provide/oauth/open/access-token")
    @MappingResponse("$.data.token")
    default String getToken() {
        return getToken(null, null, null, null);
    }

    /**
     * 文化礼堂广电 Token
     *
     * @param appKey    应用ID
     * @param code      授权码。授权模式为code时，应传。
     * @param grantType 授权模式。client_credentials、code
     * @param secret    凭证密钥。授权模式为client_credentials时，应传。
     * @return token
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingRequest("POST /provide/oauth/open/access-token")
    @MappingResponse("$.data.token")
    String getToken(
            @MappingParam(value = "appKey", defaultValue = "67422331") String appKey,
            @MappingParam(value = "grant_type", defaultValue = "client_credentials") String grantType,
            @MappingParam(value = "code", defaultValue = "52cbea61efc4ac0e") String code,
            @MappingParam(value = "secret", defaultValue = "81c31913c49843ecfd4e35005cddf61f") String secret
    );


    /**
     * 礼堂列表
     *
     * @return 测评指数
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)
    @MappingRequest("POST /provide/open/place/place-list")
    default List<PlaceAuditorium> listPlace(
            @MappingParam(value = "page", defaultValue = "0") int page,
            @MappingParam(value = "size", defaultValue = "10") int size
    ) {
        return listPlace(null, null, null, null, page, size);
    }


    /**
     * 礼堂列表
     *
     * @param appKey    应用ID
     * @param code      授权码。授权模式为code时，应传。
     * @param grantType 授权模式。client_credentials、code
     * @param secret    凭证密钥。授权模式为client_credentials时，应传。
     * @param page      页码。从1开始
     * @param size      页大小，可选，默认30，最大90。
     * @return 测评指数
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)
    @MappingResponse(value = "$.data.query", target = PlaceAuditorium.class)
    @MappingRequest("POST /provide/open/place/place-list")
    List<PlaceAuditorium> listPlace(
            @MappingParam(value = "appKey", defaultValue = "67422331", ignore = true) String appKey,
            @MappingParam(value = "grant_type", defaultValue = "client_credentials", ignore = true) String grantType,
            @MappingParam(value = "code", defaultValue = "52cbea61efc4ac0e", ignore = true) String code,
            @MappingParam(value = "secret", defaultValue = "81c31913c49843ecfd4e35005cddf61f", ignore = true) String secret,
            @MappingParam(value = "page", defaultValue = "0") int page,
            @MappingParam(value = "size", defaultValue = "10") int size
    );

    /**
     * 指数排名
     *
     * @return 测评指数
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)
    @MappingResponse(value = "$.data")
    @MappingRequest("POST /provide/open/place/assess-ranking")
    default AssessRanking getAssessRanking(
            @MappingParam(value = "startDate") Long startDate,
            @MappingParam(value = "endDate") Long endDate,
            @MappingParam(value = "size", defaultValue = "1000") int size
    ) {

        return getAssessRanking(null, null, null, null, startDate, endDate, size);
    }

    /**
     * 指数排名
     *
     * @param appKey    应用ID
     * @param code      授权码。授权模式为code时，应传。
     * @param grantType 授权模式。client_credentials、code
     * @param secret    凭证密钥。授权模式为client_credentials时，应传。
     * @param startDate 开始时间（可选），13位Unix时间戳。非查询状态时，默认不要传，由服务端决定起止时间
     * @param endDate   结束时间（可选），13位Unix时间戳。非查询状态时，默认不要传，由服务端决定起止时间
     * @param size      页大小（调试用，实际开发不传递）
     * @return 测评指数
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)
    @MappingResponse(value = "$.data")
    @MappingRequest("POST /provide/open/place/assess-ranking")
    AssessRanking getAssessRanking(
            @MappingParam(value = "appKey", defaultValue = "67422331", ignore = true) String appKey,
            @MappingParam(value = "grant_type", defaultValue = "client_credentials", ignore = true) String grantType,
            @MappingParam(value = "code", defaultValue = "52cbea61efc4ac0e", ignore = true) String code,
            @MappingParam(value = "secret", defaultValue = "81c31913c49843ecfd4e35005cddf61f", ignore = true) String secret,
            @MappingParam(value = "startDate") Long startDate,
            @MappingParam(value = "endDate") Long endDate,
            @MappingParam(value = "size", defaultValue = "1000") int size
    );

    /**
     * 活动记录
     *
     * @return 测评指数
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)
    @MappingResponse(value = "$.data")
    @MappingRequest("POST /provide/open/place/assess-list")
    default AssessList listAssess(
            @MappingParam(value = "placeId") String placeId,
            @MappingParam(value = "before") Long before,
            @MappingParam(value = "size", defaultValue = "1000") int size,
            @MappingParam(value = "startDate") Long startDate,
            @MappingParam(value = "endDate") Long endDate,
            @MappingParam(value = "status", defaultValue = "0") int status
    ) {
        return listAssess(null, null, null, null, placeId, before, size, startDate, endDate, status);
    }

    /**
     * 活动记录
     *
     * @param appKey    应用ID
     * @param code      授权码。授权模式为code时，应传。
     * @param grantType 授权模式。client_credentials、code
     * @param secret    凭证密钥。授权模式为client_credentials时，应传。
     * @param placeId   礼堂ID（可选）
     * @param before    游标。获取最新传0或不传，之后传同字段返回值；
     * @param size      页大小，可选，默认30。
     * @param startDate 统计开始时间
     * @param endDate   统计结束时间
     * @param status    审核状态。0为全部
     * @return 测评指数
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)
    @MappingResponse(value = "$.data")
    @MappingRequest("POST /provide/open/place/assess-list")
    AssessList listAssess(
            @MappingParam(value = "appKey", defaultValue = "67422331", ignore = true) String appKey,
            @MappingParam(value = "grant_type", defaultValue = "client_credentials", ignore = true) String grantType,
            @MappingParam(value = "code", defaultValue = "52cbea61efc4ac0e", ignore = true) String code,
            @MappingParam(value = "secret", defaultValue = "81c31913c49843ecfd4e35005cddf61f", ignore = true) String secret,
            @MappingParam(value = "placeId") String placeId,
            @MappingParam(value = "before") Long before,
            @MappingParam(value = "size", defaultValue = "1000") int size,
            @MappingParam(value = "startDate") Long startDate,
            @MappingParam(value = "endDate") Long endDate,
            @MappingParam(value = "status", defaultValue = "0") int status
    );

    /**
     * 主题数据
     *
     * @return 测评指数
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)

    @MappingRequest("POST /provide/open/datav/topic")
    default Topic topic() {
        return topic(null, null, null, null);
    }

    /**
     * 主题数据
     *
     * @param appKey    应用ID
     * @param code      授权码。授权模式为code时，应传。
     * @param grantType 授权模式。client_credentials、code
     * @param secret    凭证密钥。授权模式为client_credentials时，应传。
     * @return 测评指数
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)

    @MappingResponse(value = "$.data.query")
    @MappingRequest("POST /provide/open/datav/topic")
    Topic topic(
            @MappingParam(value = "appKey", defaultValue = "67422331", ignore = true) String appKey,
            @MappingParam(value = "grant_type", defaultValue = "client_credentials", ignore = true) String grantType,
            @MappingParam(value = "code", defaultValue = "52cbea61efc4ac0e", ignore = true) String code,
            @MappingParam(value = "secret", defaultValue = "81c31913c49843ecfd4e35005cddf61f", ignore = true) String secret
    );

    /**
     * 最热的活动类别
     *
     * @param type 时间范围。1本年；2去年；3本月；4上月；5本周；6上周
     * @return 测评指数
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)

    @MappingResponse(value = "$.data.query")
    @MappingRequest("POST /provide/open/datav/assess-hot-cats")
    default List<AssessHotCats> assessHotCats(
            @MappingParam(value = "type", defaultValue = "1") int type
    ) {
        return assessHotCats(null, null, null, null, type);
    }

    /**
     * 最热的活动类别
     *
     * @param appKey    应用ID
     * @param code      授权码。授权模式为code时，应传。
     * @param grantType 授权模式。client_credentials、code
     * @param secret    凭证密钥。授权模式为client_credentials时，应传。
     * @param type      时间范围。1本年；2去年；3本月；4上月；5本周；6上周
     * @return 测评指数
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)

    @MappingResponse(value = "$.data.query", target = AssessHotCats.class)
    @MappingRequest("POST /provide/open/datav/assess-hot-cats")
    List<AssessHotCats> assessHotCats(
            @MappingParam(value = "appKey", defaultValue = "67422331", ignore = true) String appKey,
            @MappingParam(value = "grant_type", defaultValue = "client_credentials", ignore = true) String grantType,
            @MappingParam(value = "code", defaultValue = "52cbea61efc4ac0e", ignore = true) String code,
            @MappingParam(value = "secret", defaultValue = "81c31913c49843ecfd4e35005cddf61f", ignore = true) String secret,
            @MappingParam(value = "type", defaultValue = "1") int type
    );

    /**
     * 测评指数
     *
     * @param type 时间范围。1本年；2去年；3本月；4上月；5本周；6上周
     * @return 测评指数
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)

    @MappingResponse(value = "$.data.query", target = Assess.class)
    @MappingRequest("POST /provide/open/datav/assess")
    default List<Assess> assess(
            @MappingParam(value = "type", defaultValue = "1") int type
    ) {
        return assess(null, null, null, null, type);
    }

    /**
     * 测评指数
     *
     * @param appKey    应用ID
     * @param code      授权码。授权模式为code时，应传。
     * @param grantType 授权模式。client_credentials、code
     * @param secret    凭证密钥。授权模式为client_credentials时，应传。
     * @param type      时间范围。1本年；2去年；3本月；4上月；5本周；6上周
     * @return 测评指数
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)

    @MappingResponse(value = "$.data.query", target = Assess.class)
    @MappingRequest("POST /provide/open/datav/assess")
    List<Assess> assess(
            @MappingParam(value = "appKey", defaultValue = "67422331", ignore = true) String appKey,
            @MappingParam(value = "grant_type", defaultValue = "client_credentials", ignore = true) String grantType,
            @MappingParam(value = "code", defaultValue = "52cbea61efc4ac0e", ignore = true) String code,
            @MappingParam(value = "secret", defaultValue = "81c31913c49843ecfd4e35005cddf61f", ignore = true) String secret,
            @MappingParam(value = "type", defaultValue = "1") int type
    );

    /**
     * 文章列表
     *
     * @param page    页码。从1开始
     * @param size    页大小，可选，默认30，最大90。
     * @param catId   栏目ID。可选
     * @param feature 文章特征。可选 1为推荐；2为栏目固顶；3为评论多；4为点赞多；5为收藏多；可设置多个，如“1|3” 文章首页默认请求需留空，由服务器决定信息特征
     * @return 测评指数
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)

    @MappingResponse(value = "$.data", target = Assess.class)
    @MappingRequest("POST /provide/open/article/article-list")
    default List<Article> listArticle(
            @MappingParam(value = "page", defaultValue = "1") int page,
            @MappingParam(value = "size", defaultValue = "90") int size,
            @MappingParam(value = "catId") String catId,
            @MappingParam(value = "feature") String feature
    ) {
        return listArticle(null, null, null, null, page, size, catId, feature);
    }

    /**
     * 文章列表
     *
     * @param appKey    应用ID
     * @param code      授权码。授权模式为code时，应传。
     * @param grantType 授权模式。client_credentials、code
     * @param secret    凭证密钥。授权模式为client_credentials时，应传。
     * @param page      页码。从1开始
     * @param size      页大小，可选，默认30，最大90。
     * @param catId     栏目ID。可选
     * @param feature   文章特征。可选 1为推荐；2为栏目固顶；3为评论多；4为点赞多；5为收藏多；可设置多个，如“1|3” 文章首页默认请求需留空，由服务器决定信息特征
     * @return 测评指数
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)

    @MappingResponse(value = "$.data", target = Article.class)
    @MappingRequest("POST /provide/open/article/article-list")
    List<Article> listArticle(
            @MappingParam(value = "appKey", defaultValue = "67422331", ignore = true) String appKey,
            @MappingParam(value = "grant_type", defaultValue = "client_credentials", ignore = true) String grantType,
            @MappingParam(value = "code", defaultValue = "52cbea61efc4ac0e", ignore = true) String code,
            @MappingParam(value = "secret", defaultValue = "81c31913c49843ecfd4e35005cddf61f", ignore = true) String secret,
            @MappingParam(value = "page", defaultValue = "1") int page,
            @MappingParam(value = "size", defaultValue = "90") int size,
            @MappingParam(value = "catId") String catId,
            @MappingParam(value = "feature") String feature
    );

    /**
     * 文章详情
     *
     * @param articleId 文章ID
     * @return 测评指数
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)

    @MappingResponse(value = "$.data", target = Article.class)
    @MappingRequest("POST /provide/open/article/article-detail")
    default ArticleDetail getArticle(
            @MappingParam(value = "id") String articleId
    ) {
        return getArticle(null, null, null, null, articleId);
    }

    /**
     * 文章详情
     *
     * @param appKey    应用ID
     * @param code      授权码。授权模式为code时，应传。
     * @param grantType 授权模式。client_credentials、code
     * @param secret    凭证密钥。授权模式为client_credentials时，应传。
     * @param articleId 文章ID
     * @return 测评指数
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)

    @MappingResponse(value = "$.data", target = Article.class)
    @MappingRequest("POST /provide/open/article/article-detail")
    ArticleDetail getArticle(
            @MappingParam(value = "appKey", defaultValue = "67422331", ignore = true) String appKey,
            @MappingParam(value = "grant_type", defaultValue = "client_credentials", ignore = true) String grantType,
            @MappingParam(value = "code", defaultValue = "52cbea61efc4ac0e", ignore = true) String code,
            @MappingParam(value = "secret", defaultValue = "81c31913c49843ecfd4e35005cddf61f", ignore = true) String secret,
            @MappingParam(value = "id") String articleId
    );

    /**
     * 物联设备信息
     *
     * @return 物联设备信息
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)

    @MappingResponse(value = "$.data")
    @MappingRequest("POST /provide/open/datav/iot-info")
    default Iot listIot() {
        return listIot(null, null, null, null);
    }

    /**
     * 物联设备信息
     *
     * @param appKey    应用ID
     * @param code      授权码。授权模式为code时，应传。
     * @param grantType 授权模式。client_credentials、code
     * @param secret    凭证密钥。授权模式为client_credentials时，应传。
     * @return 物联设备信息
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)

    @MappingResponse(value = "$.data")
    @MappingRequest("POST /provide/open/datav/iot-info")
    Iot listIot(
            @MappingParam(value = "appKey", defaultValue = "67422331", ignore = true) String appKey,
            @MappingParam(value = "grant_type", defaultValue = "client_credentials", ignore = true) String grantType,
            @MappingParam(value = "code", defaultValue = "52cbea61efc4ac0e", ignore = true) String code,
            @MappingParam(value = "secret", defaultValue = "81c31913c49843ecfd4e35005cddf61f", ignore = true) String secret
    );

    /**
     * 物联最新推送消息
     *
     * @param after 结果集最大的时间戳，0表示查询最新
     * @return 物联设备信息
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)

    @MappingResponse(value = "$.data")
    @MappingRequest("POST /provide/open/datav/iot-push-latest")
    default IotPushLastest iotPushLatest(
            @MappingParam(value = "after", defaultValue = "0") int after,
            @MappingParam(value = "size", defaultValue = "20") int size
    ) {
        return iotPushLatest(null, null, null, null, after, size);
    }

    /**
     * 物联最新推送消息
     *
     * @param appKey    应用ID
     * @param code      授权码。授权模式为code时，应传。
     * @param grantType 授权模式。client_credentials、code
     * @param secret    凭证密钥。授权模式为client_credentials时，应传。
     * @param after     结果集最大的时间戳，0表示查询最新
     * @return 物联设备信息
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)

    @MappingResponse(value = "$.data")
    @MappingRequest("POST /provide/open/datav/iot-push-latest")
    IotPushLastest iotPushLatest(
            @MappingParam(value = "appKey", defaultValue = "67422331", ignore = true) String appKey,
            @MappingParam(value = "grant_type", defaultValue = "client_credentials", ignore = true) String grantType,
            @MappingParam(value = "code", defaultValue = "52cbea61efc4ac0e", ignore = true) String code,
            @MappingParam(value = "secret", defaultValue = "81c31913c49843ecfd4e35005cddf61f", ignore = true) String secret,
            @MappingParam(value = "after", defaultValue = "0") int after,
            @MappingParam(value = "size", defaultValue = "20") int size
    );

    /**
     * 推送消息详情
     *
     * @param iotId 推送的信息ID
     * @return 推送消息详情
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)

    @MappingResponse(value = "$.data.push")
    @MappingRequest("POST /provide/open/datav/iot-push-detail")
    default IotPushDetail iotPushDetail(
            @MappingParam(value = "id") String iotId
    ) {
        return iotPushDetail(null, null, null, null, iotId);
    }

    /**
     * 推送消息详情
     *
     * @param appKey    应用ID
     * @param code      授权码。授权模式为code时，应传。
     * @param grantType 授权模式。client_credentials、code
     * @param secret    凭证密钥。授权模式为client_credentials时，应传。
     * @param iotId     推送的信息ID
     * @return 推送消息详情
     */
    @MappingHeader(name = "Channel", value = "provide-open")
    @MappingHeader(name = "Token", script = "getToken(${appKey}, ${grant_type}, ${code}, ${secret})")
    @MappingHeader(name = "Timestamp", conditionType = SystemTimeMappingCondition.class)
    @MappingHeader(name = "Sign", conditionType = CulturalAuditoriumMappingCondition.class)

    @MappingResponse(value = "$.data.push")
    @MappingRequest("POST /provide/open/datav/iot-push-detail")
    IotPushDetail iotPushDetail(
            @MappingParam(value = "appKey", defaultValue = "67422331", ignore = true) String appKey,
            @MappingParam(value = "grant_type", defaultValue = "client_credentials", ignore = true) String grantType,
            @MappingParam(value = "code", defaultValue = "52cbea61efc4ac0e", ignore = true) String code,
            @MappingParam(value = "secret", defaultValue = "81c31913c49843ecfd4e35005cddf61f", ignore = true) String secret,
            @MappingParam(value = "id") String iotId
    );


}
