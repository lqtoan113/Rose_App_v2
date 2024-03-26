package com.rose.controllers;

import com.rose.models.ResponseObject;
import com.rose.models.product.ProductDto;
import com.rose.services.impl.CloudServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v2/firebase/")
public class CloudController {
    @Autowired private CloudServiceImpl cloudService;

    @Operation(summary = "API for upload Multipart File")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @PostMapping("/upload")
    public ResponseEntity<ResponseObject> uploadFile(@RequestParam MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("OK", "Upload successfully...", cloudService.getFileUrl(cloudService.saveMultipartFile(file)), null)
        );
    }
}
