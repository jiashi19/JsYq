package com.ruoyi.monitor.handler;


import com.ruoyi.monitor.entities.BaseException;
import com.ruoyi.monitor.entities.Re;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger= LoggerFactory.getLogger(GlobalExceptionHandler.class);
    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Re baseExceptionHandler(BaseException ex){
        logger.error("异常信息：{}", ex.getMessage());
        return Re.fail(ex.getMessage());
    }

    @ExceptionHandler
    public Re IllegalArgumentExceptionHandler(IllegalArgumentException ex){
        logger.error("参数异常：{}", ex.getMessage());
        return Re.fail(ex.getMessage());
    }

}
