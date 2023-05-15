package com.chua.common.support.json.jsonpath.internal.function;

import com.chua.common.support.json.jsonpath.InvalidPathException;
import com.chua.common.support.json.jsonpath.internal.function.json.Append;
import com.chua.common.support.json.jsonpath.internal.function.json.KeySetFunction;
import com.chua.common.support.json.jsonpath.internal.function.numeric.*;
import com.chua.common.support.json.jsonpath.internal.function.text.Concatenate;
import com.chua.common.support.json.jsonpath.internal.function.text.Length;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements a factory that given a name of the function will return the Function implementation, or null
 * if the value is not obtained.
 * <p>
 * Leverages the function's name in order to determine which function to execute which is maintained internally
 * here via a static map
 *
 * @author Administrator
 */
public class PathFunctionFactory {

    public static final Map<String, Class> FUNCTIONS;

    static {
        // New functions should be added here and ensure the name is not overridden
        Map<String, Class> map = new HashMap<String, Class>();

        // Math Functions
        map.put("avg", Average.class);
        map.put("stddev", StandardDeviation.class);
        map.put("sum", Sum.class);
        map.put("min", Min.class);
        map.put("max", Max.class);

        // Text Functions
        map.put("concat", Concatenate.class);

        // JSON Entity Functions
        map.put(Length.TOKEN_NAME, Length.class);
        map.put("size", Length.class);
        map.put("append", Append.class);
        map.put("keys", KeySetFunction.class);


        FUNCTIONS = Collections.unmodifiableMap(map);
    }

    /**
     * Returns the function by name or throws InvalidPathException if function not found.
     *
     * @param name The name of the function
     * @return The implementation of a function
     * @throws InvalidPathException
     * @see #FUNCTIONS
     * @see PathFunction
     */
    public static PathFunction newFunction(String name) throws InvalidPathException {
        Class functionClazz = FUNCTIONS.get(name);
        if (functionClazz == null) {
            throw new InvalidPathException("Function with name: " + name + " does not exist.");
        } else {
            try {
                return (PathFunction) functionClazz.newInstance();
            } catch (Exception e) {
                throw new InvalidPathException("Function of name: " + name + " cannot be created", e);
            }
        }
    }
}
