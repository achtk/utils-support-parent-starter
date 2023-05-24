//package com.chua.alibaba.support.sms;
//
//import com.aliyuncs.CommonRequest;
//import com.aliyuncs.CommonResponse;
//import com.aliyuncs.DefaultAcsClient;
//import com.aliyuncs.IAcsClient;
//import com.aliyuncs.exceptions.ClientException;
//import com.aliyuncs.http.MethodType;
//import com.aliyuncs.profile.DefaultProfile;
//import com.aliyuncs.profile.IClientProfile;
//import com.google.gson.Gson;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Map;
//import java.util.Objects;
//
//import static com.chua.alibaba.support.sms.Utils.*;
//
//
///**
// * 阿里云 SMS 客户端.
// *
// * @author cn-src
// */
//public class AlibabaSmsClient extends AbstractSmsClient<Object> {
//
//    private IAcsClient acsClient;
//    private final Gson gson = new Gson();
//
//
//    public AlibabaSmsClient(String accessKeyId, String accessKeySecret) {
//        super(accessKeyId, accessKeySecret, null);
//    }
//
//    public AlibabaSmsClient(String accessKeyId, String accessKeySecret, Map<String, SmsTemplate> smsTemplates) {
//        super(accessKeyId, accessKeySecret, smsTemplates, null);
//        final IClientProfile clientProfile = DefaultProfile.getProfile(
//                "default", accessKeyId, accessKeySecret);
//
//        this.acsClient = new DefaultAcsClient(clientProfile);
//    }
//
//    /**
//     * Instantiates a new SmsClient.
//     *
//     * @param acsClient    IAcsClient
//     * @param smsTemplates 预置短信模板
//     */
//    public AlibabaSmsClient(final IAcsClient acsClient, final Map<String, SmsTemplate> smsTemplates) {
//        super(null, null, smsTemplates, null);
//        this.acsClient = acsClient;
//    }
//
//    /**
//     * 发送短信验证码.
//     *
//     * @param phoneNumber 手机号码(中国)
//     *
//     * @return 6 位数的随机码
//     */
//    public int sendVerificationCode(final String smsTemplateKey, final String phoneNumber) {
//        checkPhoneNumber(phoneNumber);
//        final SmsTemplate smsTemplate = this.smsTemplates.get(smsTemplateKey);
//        Objects.requireNonNull(smsTemplate, () -> "SmsTemplate must be not null, key:" + smsTemplateKey);
//
//        final int code = Utils.randomCode();
//        smsTemplate.setTemplateParam(Collections.singletonMap("code", String.valueOf(code)));
//        smsTemplate.setPhoneNumbers(Collections.singletonList(phoneNumber));
//        send(smsTemplate);
//        return code;
//    }
//
//    /**
//     * 发送短信.
//     *
//     * @param smsTemplateKey 预置短信模板 key
//     */
//    public void send(final String smsTemplateKey) {
//        final SmsTemplate smsTemplate = this.smsTemplates.get(smsTemplateKey);
//        Objects.requireNonNull(smsTemplate, () -> "SmsTemplate must be not null, key:" + smsTemplateKey);
//
//        send(smsTemplate);
//    }
//
//    /**
//     * 发送短信.
//     *
//     * @param smsTemplateKey 预置短信模板 key
//     * @param phoneNumbers 手机号码，优先于预置短信模板中配置的手机号码
//     */
//    public void send(final String smsTemplateKey, final String... phoneNumbers) {
//        final SmsTemplate smsTemplate = this.smsTemplates.get(smsTemplateKey);
//        Objects.requireNonNull(smsTemplate, () -> "SmsTemplate must be not null, key:" + smsTemplateKey);
//
//        smsTemplate.setPhoneNumbers(Arrays.asList(phoneNumbers));
//        send(smsTemplate);
//    }
//
//    /**
//     * 发送短信.
//     *
//     * @param smsTemplate 短信模板
//     * @return
//     */
//    @Override
//    public Map<String, String> send(final SmsTemplate smsTemplate) {
//        Objects.requireNonNull(smsTemplate);
//        checkSmsTemplate(smsTemplate);
//
//        final CommonRequest request = new CommonRequest();
//        request.setSysMethod(MethodType.POST);
//        request.setSysDomain("dysmsapi.aliyuncs.com");
//        request.setSysVersion("2017-05-25");
//        request.setSysAction("SendSms");
//        request.putQueryParameter("PhoneNumbers", String.join(",", smsTemplate.getPhoneNumbers()));
//        request.putQueryParameter("SignName", smsTemplate.getSignName());
//        request.putQueryParameter("TemplateCode", smsTemplate.getTemplateCode());
//        request.putQueryParameter("TemplateParam", Utils.toJsonStr(smsTemplate.getTemplateParam()));
//        try {
//            final CommonResponse response = this.acsClient.getCommonResponse(request);
//            return checkSmsResponse(response);
//        }
//        catch (final ClientException e) {
//            throw new SmsException(e);
//        }
//    }
//
//    /**
//     * 批量发送短信.
//     *
//     * <p>
//     * 批量发送短信接口，支持在一次请求中分别向多个不同的手机号码发送不同签名的短信。
//     * 手机号码，签名，模板参数字段个数相同，一一对应，短信服务根据字段的顺序判断发往指定手机号码的签名。
//     *
//     * <p>
//     * 如果您需要往多个手机号码中发送同样签名的短信，请使用 {@link #send(SmsTemplate)}。
//     *
//     * @param batchSmsTemplate 批量发送短信模板
//     */
//    public void send(final BatchSmsTemplate batchSmsTemplate) {
//        Objects.requireNonNull(batchSmsTemplate);
//        checkBatchSmsTemplate(batchSmsTemplate);
//
//        final CommonRequest request = new CommonRequest();
//        request.setSysMethod(MethodType.POST);
//        request.setSysDomain("dysmsapi.aliyuncs.com");
//        request.setSysVersion("2017-05-25");
//        request.setSysAction("SendBatchSms");
//        request.putQueryParameter("PhoneNumberJson", this.gson.toJson(batchSmsTemplate.getPhoneNumbers()));
//        request.putQueryParameter("SignNameJson", this.gson.toJson(batchSmsTemplate.getSignNames()));
//        request.putQueryParameter("TemplateCode", batchSmsTemplate.getTemplateCode());
//        request.putQueryParameter("TemplateParamJson", this.gson.toJson(batchSmsTemplate.getTemplateParams()));
//        try {
//            final CommonResponse response = this.acsClient.getCommonResponse(request);
//            checkSmsResponse(response);
//        }
//        catch (final ClientException e) {
//            throw new SmsException(e);
//        }
//    }
//}
