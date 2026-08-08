package com.odisha.floodrelief.controller;

import com.odisha.floodrelief.dto.response.ApiResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "API Info")
@RestController
public class HomeController {

    @ApiOperation("API root / health info")
    @GetMapping({"/", ""})
    public ResponseEntity<ApiResponse<Map<String, Object>>> home() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "Odisha Flood Relief & NGO Management System");
        info.put("status", "UP");
        info.put("apiBase", "/api");
        info.put("swagger", "/api/swagger-ui/");
        info.put("auth", "/api/auth/login");
        return ResponseEntity.ok(ApiResponse.success("API is running", info));
    }
}
