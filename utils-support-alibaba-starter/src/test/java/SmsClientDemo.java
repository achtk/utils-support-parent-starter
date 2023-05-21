import com.chua.alibaba.support.sms.BatchSmsTemplate;
import com.chua.alibaba.support.sms.SmsClient;
import com.chua.alibaba.support.sms.SmsTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cn-src
 */
public class SmsClientDemo {
    private SmsClient smsClient;

    public void setUp() throws Exception {
        final String accessKeyId = System.getenv("aliyun.sms.accessKeyId");
        final String accessKeySecret = System.getenv("aliyun.sms.accessKeySecret");
        this.smsClient = new SmsClient(accessKeyId, accessKeySecret);
    }

    public void sendAuthenticationCode() {
        final String signName = System.getenv("aliyun.sms.authentication.signName");
        final String templateCode = System.getenv("aliyun.sms.authentication.templateCode");
        final SmsTemplate smsTemplate = SmsTemplate.builder()
                .signName(signName)
                .templateCode(templateCode)
                .addTemplateParam("code", "123456")
                .phoneNumbers(Collections.singletonList(System.getenv("aliyun.sms.authentication.phoneNumber")))
                .build();

        this.smsClient.send(smsTemplate);
    }

    public void send() {
        final Map<String, String> param = new HashMap<>();
        param.put("code", "123456");
        final BatchSmsTemplate batchSmsTemplate = BatchSmsTemplate.builder()
                .phoneNumbers(Arrays.asList("", ""))
                .signNames(Arrays.asList("", ""))
                .templateCode("")
                .templateParams((Arrays.asList(param, param)))
                .build();
        this.smsClient.send(batchSmsTemplate);
    }
}