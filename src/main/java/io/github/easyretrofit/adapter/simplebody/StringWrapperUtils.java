package io.github.easyretrofit.adapter.simplebody;

public class StringWrapperUtils {

    // 方法1：使用String.format()
    public static String formatWithStringFormat(String template, String value) {
        if (!template.contains("{}")) {
            return value;
        }
        return String.format(template.replace("{}", "%s"), value);
    }
}
