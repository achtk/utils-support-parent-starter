package com.alibaba.json.serializer;

import com.alibaba.json.serializer.AfterFilter;
import com.alibaba.json.serializer.BeanContext;
import com.alibaba.json.serializer.BeforeFilter;
import com.alibaba.json.serializer.ContextValueFilter;
import com.alibaba.json.serializer.JSONSerializer;
import com.alibaba.json.serializer.LabelFilter;
import com.alibaba.json.serializer.PropertyPreFilter;
import com.alibaba.json.serializer.SerializerFeature;
import com.alibaba.json.JSON;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class SerializeFilterable {

    protected List<com.alibaba.json.serializer.BeforeFilter> beforeFilters = null;
    protected List<com.alibaba.json.serializer.AfterFilter> afterFilters = null;
    protected List<PropertyFilter> propertyFilters = null;
    protected List<ValueFilter> valueFilters = null;
    protected List<NameFilter> nameFilters = null;
    protected List<com.alibaba.json.serializer.PropertyPreFilter> propertyPreFilters = null;
    protected List<com.alibaba.json.serializer.LabelFilter> labelFilters = null;
    protected List<com.alibaba.json.serializer.ContextValueFilter> contextValueFilters = null;

    protected boolean writeDirect = true;

    public List<com.alibaba.json.serializer.BeforeFilter> getBeforeFilters() {
        if (beforeFilters == null) {
            beforeFilters = new ArrayList<com.alibaba.json.serializer.BeforeFilter>();
            writeDirect = false;
        }

        return beforeFilters;
    }

    public List<com.alibaba.json.serializer.AfterFilter> getAfterFilters() {
        if (afterFilters == null) {
            afterFilters = new ArrayList<com.alibaba.json.serializer.AfterFilter>();
            writeDirect = false;
        }

        return afterFilters;
    }

    public List<NameFilter> getNameFilters() {
        if (nameFilters == null) {
            nameFilters = new ArrayList<NameFilter>();
            writeDirect = false;
        }

        return nameFilters;
    }

    public List<com.alibaba.json.serializer.PropertyPreFilter> getPropertyPreFilters() {
        if (propertyPreFilters == null) {
            propertyPreFilters = new ArrayList<com.alibaba.json.serializer.PropertyPreFilter>();
            writeDirect = false;
        }

        return propertyPreFilters;
    }

    public List<com.alibaba.json.serializer.LabelFilter> getLabelFilters() {
        if (labelFilters == null) {
            labelFilters = new ArrayList<com.alibaba.json.serializer.LabelFilter>();
            writeDirect = false;
        }

        return labelFilters;
    }

    public List<PropertyFilter> getPropertyFilters() {
        if (propertyFilters == null) {
            propertyFilters = new ArrayList<PropertyFilter>();
            writeDirect = false;
        }

        return propertyFilters;
    }

    public List<com.alibaba.json.serializer.ContextValueFilter> getContextValueFilters() {
        if (contextValueFilters == null) {
            contextValueFilters = new ArrayList<com.alibaba.json.serializer.ContextValueFilter>();
            writeDirect = false;
        }

        return contextValueFilters;
    }

    public List<ValueFilter> getValueFilters() {
        if (valueFilters == null) {
            valueFilters = new ArrayList<ValueFilter>();
            writeDirect = false;
        }

        return valueFilters;
    }

    public void addFilter(SerializeFilter filter) {
        if (filter == null) {
            return;
        }

        if (filter instanceof com.alibaba.json.serializer.PropertyPreFilter) {
            this.getPropertyPreFilters().add((com.alibaba.json.serializer.PropertyPreFilter) filter);
        }

        if (filter instanceof NameFilter) {
            this.getNameFilters().add((NameFilter) filter);
        }

        if (filter instanceof ValueFilter) {
            this.getValueFilters().add((ValueFilter) filter);
        }

        if (filter instanceof com.alibaba.json.serializer.ContextValueFilter) {
            this.getContextValueFilters().add((com.alibaba.json.serializer.ContextValueFilter) filter);
        }

        if (filter instanceof PropertyFilter) {
            this.getPropertyFilters().add((PropertyFilter) filter);
        }

        if (filter instanceof com.alibaba.json.serializer.BeforeFilter) {
            this.getBeforeFilters().add((BeforeFilter) filter);
        }

        if (filter instanceof com.alibaba.json.serializer.AfterFilter) {
            this.getAfterFilters().add((AfterFilter) filter);
        }

        if (filter instanceof com.alibaba.json.serializer.LabelFilter) {
            this.getLabelFilters().add((LabelFilter) filter);
        }
    }

    public boolean applyName(com.alibaba.json.serializer.JSONSerializer jsonBeanDeser, //
                             Object object, String key) {

        if (jsonBeanDeser.propertyPreFilters != null) {
            for (com.alibaba.json.serializer.PropertyPreFilter filter : jsonBeanDeser.propertyPreFilters) {
                if (!filter.apply(jsonBeanDeser, object, key)) {
                    return false;
                }
            }
        }
        
        if (this.propertyPreFilters != null) {
            for (PropertyPreFilter filter : this.propertyPreFilters) {
                if (!filter.apply(jsonBeanDeser, object, key)) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean apply(com.alibaba.json.serializer.JSONSerializer jsonBeanDeser, //
                         Object object, //
                         String key, Object propertyValue) {

        if (jsonBeanDeser.propertyFilters != null) {
            for (PropertyFilter propertyFilter : jsonBeanDeser.propertyFilters) {
                if (!propertyFilter.apply(object, key, propertyValue)) {
                    return false;
                }
            }
        }
        
        if (this.propertyFilters != null) {
            for (PropertyFilter propertyFilter : this.propertyFilters) {
                if (!propertyFilter.apply(object, key, propertyValue)) {
                    return false;
                }
            }
        }

        return true;
    }

    protected String processKey(com.alibaba.json.serializer.JSONSerializer jsonBeanDeser, //
                                Object object, //
                                String key, //
                                Object propertyValue) {

        if (jsonBeanDeser.nameFilters != null) {
            for (NameFilter nameFilter : jsonBeanDeser.nameFilters) {
                key = nameFilter.process(object, key, propertyValue);
            }
        }

        if (this.nameFilters != null) {
            for (NameFilter nameFilter : this.nameFilters) {
                key = nameFilter.process(object, key, propertyValue);
            }
        }

        return key;
    }

    protected Object processValue(com.alibaba.json.serializer.JSONSerializer jsonBeanDeser, //
                                  com.alibaba.json.serializer.BeanContext beanContext,
                                  Object object, //
                                  String key, //
                                  Object propertyValue) {
        return processValue(jsonBeanDeser, beanContext, object, key, propertyValue, 0);
    }

    protected Object processValue(com.alibaba.json.serializer.JSONSerializer jsonBeanDeser, //
                                  BeanContext beanContext,
                                  Object object, //
                                  String key, //
                                  Object propertyValue, //
                                  int features) {

        if (propertyValue != null) {
            if ((com.alibaba.json.serializer.SerializerFeature.isEnabled(jsonBeanDeser.out.features, features, com.alibaba.json.serializer.SerializerFeature.WriteNonStringValueAsString)  //
                    || (beanContext != null && (beanContext.getFeatures() & SerializerFeature.WriteNonStringValueAsString.mask) != 0))
                    && (propertyValue instanceof Number || propertyValue instanceof Boolean)) {
                String format = null;
                if (propertyValue instanceof Number
                        && beanContext != null) {
                    format = beanContext.getFormat();
                }

                if (format != null) {
                    propertyValue = new DecimalFormat(format).format(propertyValue);
                } else {
                    propertyValue = propertyValue.toString();
                }
            } else if (beanContext != null && beanContext.isJsonDirect()) {
                String jsonStr = (String) propertyValue;
                propertyValue = JSON.parse(jsonStr);
            }
        }
        
        if (jsonBeanDeser.valueFilters != null) {
            for (ValueFilter valueFilter : jsonBeanDeser.valueFilters) {
                propertyValue = valueFilter.process(object, key, propertyValue);
            }
        }

        List<ValueFilter> valueFilters = this.valueFilters;
        if (valueFilters != null) {
            for (ValueFilter valueFilter : valueFilters) {
                propertyValue = valueFilter.process(object, key, propertyValue);
            }
        }

        if (jsonBeanDeser.contextValueFilters != null) {
            for (com.alibaba.json.serializer.ContextValueFilter valueFilter : jsonBeanDeser.contextValueFilters) {
                propertyValue = valueFilter.process(beanContext, object, key, propertyValue);
            }
        }

        if (this.contextValueFilters != null) {
            for (ContextValueFilter valueFilter : this.contextValueFilters) {
                propertyValue = valueFilter.process(beanContext, object, key, propertyValue);
            }
        }

        return propertyValue;
    }
    
    /**
     * only invoke by asm byte
     * 
     * @return
     */
    protected boolean writeDirect(JSONSerializer jsonBeanDeser) {
        return jsonBeanDeser.out.writeDirect //
               && this.writeDirect //
               && jsonBeanDeser.writeDirect;
    }
}
