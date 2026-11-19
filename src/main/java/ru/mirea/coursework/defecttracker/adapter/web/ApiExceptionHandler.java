package ru.mirea.coursework.defecttracker.adapter.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.mirea.coursework.defecttracker.application.ConflictException;
import ru.mirea.coursework.defecttracker.application.NotFoundException;
import java.net.URI;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    ProblemDetail notFound(NotFoundException exception, HttpServletRequest request) {
        return problem(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }
    @ExceptionHandler(ConflictException.class)
    ProblemDetail conflict(ConflictException exception, HttpServletRequest request) {
        return problem(HttpStatus.CONFLICT, exception.getMessage(), request);
    }
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    ProblemDetail invalid(Exception exception, HttpServletRequest request) {
        String message = exception instanceof MethodArgumentNotValidException
                ? "Проверьте обязательные поля и их формат" : exception.getMessage();
        return problem(HttpStatus.BAD_REQUEST, message, request);
    }
    @ExceptionHandler(Exception.class)
    ProblemDetail unexpected(Exception exception, HttpServletRequest request) {
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера", request);
    }
    private ProblemDetail problem(HttpStatus status, String detail, HttpServletRequest request) {
        ProblemDetail result = ProblemDetail.forStatusAndDetail(status, detail);
        result.setType(URI.create("about:blank"));
        result.setTitle(status.getReasonPhrase());
        result.setInstance(URI.create(request.getRequestURI()));
        return result;
    }
}
