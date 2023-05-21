package com.chua.example.template

import com.chua.common.support.collection.ImmutableCollection
import com.chua.common.support.lang.template.Template
import com.chua.common.support.lang.template.basis.DelegateTemplate
import org.apache.commons.codec.binary.StringUtils

class TemplateExample {

    static void main(String[] args) throws IOException {
//        example1();
//        example2()
//        example3()
//        example4()
        example5()
    }

    static def example1() {
        Template template = new DelegateTemplate();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        template.resolve("Hello {{name}}.",
                outputStream,
                ImmutableCollection.<String, Object>newMap().put("name", "Hotzenplotz").newHashMap());

        println 'Basic Usage\n'
        println StringUtils.newStringUtf8(outputStream.toByteArray())
        println '===================================================='
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
                        .put("a", 123)
                        .put("customer", "Mr. Hotzenplotz")
                        .put("license", new License("Hotzenplotz", new String[] {"3ba34234bcffe", "5bbe77f879000", "dd3ee54324bf3"}))
                .newHashMap());
        println 'Text and code spans\n'
        println StringUtils.newStringUtf8(outputStream.toByteArray())
        println '===================================================='
    }

    static def example3() {
        Template template = new DelegateTemplate();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        template.resolve('''
{{true}} fake news is {{false}}.
This is an integer {{123}}, and this is a float {{123.456}}.
A byte {{123b}}.
A short {{123s}}.
An int {{123}}.
A long {{123l}}.
A float {{123f}}.
A double {{123d}}.
The character {{'a'}} is included in the string {{"a-team"}}.
            ''',
                outputStream,
                ImmutableCollection.<String, Object>newMap()
                        .put("name", "Hotzenplotz")
                        .put("customer", "Mr. Hotzenplotz")
                        .put("license", new License("Hotzenplotz", new String[] {"3ba34234bcffe", "5bbe77f879000", "dd3ee54324bf3"}))
                .newHashMap());
        println 'Literals\n'
        println StringUtils.newStringUtf8(outputStream.toByteArray())
        println '===================================================='
    }

    static void example4() {
        Template template = new DelegateTemplate();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        template.resolve("Basis-template can access private {{myObject.privateField}}, package private {{myObject.packagePrivateField}}, protected {{myObject.protectedField}}, and public {{myObject.publicField}} fields. It can also access static {{myClass.STATIC_FIELD}} fields.\n",
                outputStream,
                ImmutableCollection.<String, Object>newMap()
                        .put("myObject", new MyObject())
                        .put("myClass", MyObject.class)
                        .newHashMap());

        println 'Accessing fields\n'
        println StringUtils.newStringUtf8(outputStream.toByteArray())
        println '===================================================='
    }

    static void example5() {
        Template template = new DelegateTemplate();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        template.resolve('''{{myObject.add(1, 2)}} {{myObject.add(1f, 2f)}} {{String.format("%010d", 93)}}''',
                outputStream,
                ImmutableCollection.<String, Object>newMap()
                        .put("myObject", new MyObject())
                        .put("myClass", MyObject.class)
                        .put("String", String.class)
                        .newHashMap());

        println 'Calling methods\n'
        println StringUtils.newStringUtf8(outputStream.toByteArray())
        println '===================================================='
    }

    static class License {
        public final String productName;
        public final String[] activationCodes;

        License (String productName, String[] activationCodes) {
            this.productName = productName;
            this.activationCodes = activationCodes;
        }
    }

    public static class MyObject {
        public static String STATIC_FIELD = "I'm static";
        private int privateField = 123;
        boolean packagePrivateField = true;
        protected float protectedField = 123.456f;
        public String publicField = "ello";

        private def add (def a, def b) { return a + b; }
        public static String staticMethod () { return "Hello"; }
    }
}
