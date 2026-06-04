package com.admission.common;

import lombok.Data;

@Data
public class Result {
    private int code;
    private String msg;
    private Object data;

    public static Result success(Object data) {
        Result r = new Result();
        r.setCode(200);
        r.setMsg("成功");
        r.setData(data);
        return r;
    }

    public static Result fail(String msg) {
        Result r = new Result();
        r.setCode(500);
        r.setMsg(msg);
        return r;
    }
}