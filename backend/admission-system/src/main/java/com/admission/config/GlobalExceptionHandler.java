package com.admission.config;

import com.admission.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 参数校验失败（@Valid 校验）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", msg);
        return Result.fail("参数校验失败: " + msg);
    }

    /**
     * 缺少必填请求参数
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleMissingParam(MissingServletRequestParameterException ex) {
        log.warn("缺少必填参数: {}", ex.getParameterName());
        return Result.fail("缺少必填参数: " + ex.getParameterName());
    }

    /**
     * 主键重复
     */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleDuplicateKey(DuplicateKeyException ex) {
        log.warn("主键重复: {}", ex.getMessage());
        return Result.fail("数据已存在，请检查考号/专业代码是否重复");
    }

    /**
     * 数据完整性约束违反（如外键约束）
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("数据完整性约束违反: {}", ex.getMessage());
        return Result.fail("操作失败：数据违反约束，请检查关联数据是否存在");
    }

    /**
     * 兜底异常处理
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleException(Exception ex) {
        log.error("服务器内部错误", ex);
        return Result.fail("服务器内部错误: " + ex.getMessage());
    }
}
