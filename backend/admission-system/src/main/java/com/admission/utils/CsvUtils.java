package com.admission.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * CSV 导出工具类（UTF-8 BOM，解决 Excel 中文乱码问题）
 */
public class CsvUtils {

    /**
     * 写出 CSV 文件到 HTTP 响应
     *
     * @param response HttpServletResponse
     * @param fileName 文件名（不含扩展名）
     * @param headers  CSV 表头
     * @param rows     数据行（每行是一个 String[]）
     */
    public static void writeCsv(HttpServletResponse response, String fileName,
                                String[] headers, List<String[]> rows) throws IOException {
        // 设置响应头
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=" + java.net.URLEncoder.encode(fileName + ".csv", "UTF-8"));

        PrintWriter writer = response.getWriter();

        // 写入 UTF-8 BOM（Excel 识别 UTF-8 的关键）
        writer.write('﻿');

        // 写入表头
        writer.println(String.join(",", quoteFields(headers)));

        // 写入数据行
        for (String[] row : rows) {
            writer.println(String.join(",", quoteFields(row)));
        }

        writer.flush();
    }

    /**
     * 对每个字段加双引号包裹，内部引号转义
     */
    private static String[] quoteFields(String[] fields) {
        String[] result = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            String val = fields[i] == null ? "" : fields[i];
            // CSV 标准：字段内双引号需转义为两个双引号
            result[i] = "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return result;
    }
}
