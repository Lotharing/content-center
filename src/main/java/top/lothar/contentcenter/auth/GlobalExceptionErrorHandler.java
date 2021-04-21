package top.lothar.contentcenter.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <h1>全局异常处理</h1>
 *
 * @author LuTong.Zhao
 * @Date 2021/4/21 22:14
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionErrorHandler {
    /**
     * SecurityException对自定义对异常进行全局捕捉
     * @param e
     * @return
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorBody> error(SecurityException e){
        log.warn("发生SecurityException异常", e);
        return new ResponseEntity<ErrorBody>(
                ErrorBody.builder()
                        .body("Token非法,用户不允许访问！")
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .build(),
                HttpStatus.UNAUTHORIZED
        );
    }
}

/**
 * 返回构造体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class ErrorBody{
    private String body;
    private int status;
}
