package com.chua.mms.support.mas;

import com.chua.common.support.sms.*;
import com.chua.common.support.task.cache.CacheConfiguration;
import com.chua.common.support.task.cache.Cacheable;
import com.chua.common.support.task.cache.JdkCacheable;
import com.chua.common.support.utils.DigestUtils;
import com.mascloud.sdkclient.Client;

import java.util.LinkedList;
import java.util.List;

/**
 * 移动云mas
 *
 * @author CH
 */
public class YunMasSmsHandler implements SmsHandler {

    final Client client = Client.getInstance();
    private final SmsConfiguration smsConfiguration;

    private final Cacheable cacheable;

    public YunMasSmsHandler() {
        this(SmsConfiguration.newDefault());
    }

    public YunMasSmsHandler(SmsConfiguration smsConfiguration) {
        this(smsConfiguration, new JdkCacheable());
    }

    public YunMasSmsHandler(SmsConfiguration smsConfiguration, Cacheable cacheable) {
        this.cacheable = cacheable;
        this.smsConfiguration = smsConfiguration;
        this.cacheable.configuration(CacheConfiguration.builder().expireAfterWrite(smsConfiguration.getIntervals()).build());
        // 登录地址需另外提供
        boolean login = client.login(smsConfiguration.getAddress(), smsConfiguration.getAppKey(), smsConfiguration.getAppSecure(), smsConfiguration.getEcName());
        if (!login) {
            throw new IllegalArgumentException("账号或密码错误");
        }
    }

    //		// 获取状态报告——开始
//		List<StatusReportModel> statusReportlist = client.getReport( );
//		System.out.println( "getReport : " + JsonUtil.toJsonString( statusReportlist ) );
//		// 获取状态报告——结束
//
//		// 获取上行短信——开始
//		List<MoModel> deliverList = client.getMO( );
//		System.out.println( "getMO : " + JsonUtil.toJsonString( deliverList ) );
//		// 获取上行短信——结束

    @Override
    public List<SmsResult> send(SmsRequest request) {
        List<SmsResult> rs = new LinkedList<>();
        String content = request.getContent();
        String serial = request.getSerial();
        for (String phone : request.getPhone()) {
            String newKey = getKey(phone, content, serial);
            if (isPass(newKey)) {
                continue;
            }

            SmsType smsType = request.getSmsType();
            if (smsType == SmsType.NORMAL) {
                rs.add(sendOneToOne(newKey, content, serial, phone));
                continue;
            }

            if (smsType == SmsType.TEMPLATE) {
                rs.add(sendOneToOneTemplate(newKey, content, serial, phone, request.getParams()));
            }
        }
        return rs;
    }

    /**
     * 模板短信
     *
     * @param newKey  缓存Key
     * @param content 模板ID
     * @param serial  序列号
     * @param phone   手机
     * @param params  模板参数
     * @return 结果
     */
    private SmsResult sendOneToOneTemplate(String newKey, String content, String serial, String phone, String[] params) {
        int sendDSMS = 0;
        try {
            sendDSMS = client.sendTSMS(new String[]{phone}, content, params, serial, 0, smsConfiguration.getSign(), null);
        } catch (Exception ignored) {
        }
        if (sendDSMS == 1) {
            cacheable.put(newKey, newKey);
        }
        return new SmsResult(phone, sendDSMS);
    }
    /**
     * 普通短信
     *
     * @param newKey  缓存Key
     * @param content 短信内容
     * @param serial  序列号
     * @param mobile   手机
     * @return 结果
     */
    private SmsResult sendOneToOne(String newKey, String content, String serial, String mobile) {
        int sendDSMS = 0;
        try {
            sendDSMS = client.sendDSMS(new String[]{mobile}, content, serial, 1, smsConfiguration.getSign(), null, true);
        } catch (Exception ignored) {
        }
        if (sendDSMS == 1) {
            cacheable.put(newKey, newKey);
        }
        return new SmsResult(mobile, sendDSMS);
    }

    private boolean isPass(String newKey) {
        if (null == smsConfiguration.getIntervals() || smsConfiguration.getIntervals() <= 0) {
            return false;
        }
        return cacheable.exist(newKey);
    }

    private String getKey(String mobile, String content, String serial) {
        return DigestUtils.md5Hex(mobile + content + serial);
    }

}
