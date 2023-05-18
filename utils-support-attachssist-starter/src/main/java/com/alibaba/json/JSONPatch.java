package com.alibaba.json;

import com.alibaba.json.JSON;
import com.alibaba.json.JSONException;
import com.alibaba.json.JSONPath;
import com.alibaba.json.annotation.JSONField;
import com.alibaba.json.annotation.JSONType;
import com.alibaba.json.parser.Feature;
import com.alibaba.json.parser.JSONScanner;

public class JSONPatch {
    public static String apply(String original, String patch) {
        Object object
                = apply(
                com.alibaba.json.JSON.parse(original, Feature.OrderedField), patch);
        return com.alibaba.json.JSON.toJSONString(object);
    }

    public static Object apply(Object object, String patch) {
        Operation[] operations;
        if (isObject(patch)) {
            operations = new Operation[]{
                    com.alibaba.json.JSON.parseObject(patch, Operation.class)};
        } else {
            operations = JSON.parseObject(patch, Operation[].class);
        }

        for (Operation op : operations) {
            com.alibaba.json.JSONPath path = com.alibaba.json.JSONPath.compile(op.path);
            switch (op.type) {
                case add:
                    path.patchAdd(object, op.value, false);
                    break;
                case replace:
                    path.patchAdd(object, op.value, true);
                    break;
                case remove:
                    path.remove(object);
                    break;
                case copy:
                case move:
                    com.alibaba.json.JSONPath from = JSONPath.compile(op.from);
                    Object fromValue = from.eval(object);
                    if (op.type == OperationType.move) {
                        boolean success = from.remove(object);
                        if (!success) {
                            throw new JSONException("json patch move error : " + op.from + " -> " + op.path);
                        }
                    }
                    path.set(object, fromValue);
                    break;
                case test:
                    Object result = path.eval(object);
                    if (result == null) {
                        return op.value == null;
                    }
                    return result.equals(op.value);
                default:
                    break;
            }
        }

        return object;
    }

    private static boolean isObject(String patch) {
        if (patch == null) {
            return false;
        }

        for (int i = 0; i < patch.length(); ++i) {
            char ch = patch.charAt(i);
            if (JSONScanner.isWhitespace(ch)) {
                continue;
            }
            return ch == '{';
        }

        return false;
    }

    @JSONType(orders = {"op", "from", "path", "value"})
    public static class Operation {
        @JSONField(name = "op")
        public OperationType type;
        public String from;
        public String path;
        public Object value;
    }

    public enum OperationType {
        add, remove, replace, move, copy, test
    }
}
