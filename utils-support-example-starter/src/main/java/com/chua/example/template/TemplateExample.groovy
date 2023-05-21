package com.chua.example.template

import com.chua.common.support.collection.ImmutableCollection
import com.chua.common.support.lang.template.Template
import com.chua.common.support.lang.template.basis.DelegateTemplate
import org.apache.commons.codec.binary.StringUtils

class TemplateExample {

    static void main(String[] args) throws IOException {
        example1();
        example2()
    }

    static def example1() {
        Template template = new DelegateTemplate();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        template.resolve("Hello {{name}}.",
                outputStream,
                ImmutableCollection.<String, Object>newMap().put("name", "Hotzenplotz").newHashMap());
        System.out.println(StringUtils.newStringUtf8(outputStream.toByteArray()));
    }

    static def example2() {
        Template template = new DelegateTemplate();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        template.resolve('''
            Dear {{customer}},
            
            Thank you for purchasing {{license.productName}}. You can find your activation codes below:
            
            {{for index, activationCode in license.activationCodes}}
               {{(index + 1)}}. {{activationCode}}
            {{end}}
            
            Please let me know if there is anything else I can help you with!
            
            Kind regards,
            Your friendly neighbourhood customer service employee
            ''',
                outputStream,
                ImmutableCollection.<String, Object>newMap()
                        .put("name", "Hotzenplotz")
                        .put("customer", "Mr. Hotzenplotz")
                        .put("license", new License("Hotzenplotz", new String[] {"3ba34234bcffe", "5bbe77f879000", "dd3ee54324bf3"}))
                .newHashMap());
        System.out.println(StringUtils.newStringUtf8(outputStream.toByteArray()));
    }

    static class License {
        public final String productName;
        public final String[] activationCodes;

        License (String productName, String[] activationCodes) {
            this.productName = productName;
            this.activationCodes = activationCodes;
        }
    }
}
