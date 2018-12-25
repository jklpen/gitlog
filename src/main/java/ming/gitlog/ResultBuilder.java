package ming.gitlog;

import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang3.StringUtils;

public class ResultBuilder {

    private ResultBuilder() {
    }

    public static <T> Result<T> build(int status, String message, T data) {
        return new Result<T>(status, message, data);
    }

    public static <T> Result<T> build(int status, String message) {
        return new Result<T>(status, message);
    }

    public static <T> Result<T> success(T data) {
        return new Result<T>(200, "success", data);
    }

    public static <T> Result<T> success() {
        return new Result<T>(200, "ok");
    }

    public static <T> Result<T> error(int status, String message, T data) {
        return new Result<T>(status, message, data);
    }

    public static <T> Result<T> error(int status, String message) {
        return new Result<T>(status, message, null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<T>(-1, message, null);
    }

    public static <T> Result<T> error() {
        return new Result<T>(-1, "System is busy");
    }

    private static <T> String toJsonString(Result<T> result) {
        return JSONObject.toJSONString(result);
    }

    public static <T> String toJsonpString(Result<T> result, String callback) {
        return StringUtils.isEmpty(callback) ? "callback" : callback + "(" + toJsonString(result) + ")";
    }

    public static <T> String toJsonpString(int status, String message, T data, String callback) {
        Result<T> result = ResultBuilder.build(status, message, data);
        return toJsonpString(result, callback);
    }

    public static <T> String toJsonpString(int status, String message, T data) {
        Result<T> result = ResultBuilder.build(status, message, data);
        return toJsonpString(result, null);
    }

}
