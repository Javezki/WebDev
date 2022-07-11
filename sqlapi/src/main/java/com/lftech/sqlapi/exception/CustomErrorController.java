package com.lftech.sqlapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Slf4j
public class CustomErrorController extends BasicErrorController {
    public CustomErrorController(ErrorAttributes errorAttributes,
                                 ServerProperties serverProperties,
                                 List<ErrorViewResolver> errorViewResolvers) {

        super(errorAttributes, serverProperties.getError(), errorViewResolvers);
        log.info("Created");
    }

    /**
     * Overrides the base method to add our custom logic
     */
    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {

        Map<String, Object> body = getErrorAttributes(request,
                getErrorAttributeOptions(request, MediaType.ALL));


        HttpStatus status = getStatus(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(body, headers, status);
    }

    @Override
    @RequestMapping(produces="text/html")
    public ModelAndView errorHtml(HttpServletRequest request,
                                  HttpServletResponse response) {

        return null;
    }
}
