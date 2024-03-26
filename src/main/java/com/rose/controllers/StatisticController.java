package com.rose.controllers;


import com.rose.entities.Order;
import com.rose.entities.Product;
import com.rose.models.ResponseObject;
import com.rose.models.product.ProductDto;
import com.rose.repositories.ProductRepository;
import com.rose.services.impl.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/")
public class StatisticController {
    @Autowired private ProductServiceImpl productService;

    @Operation(summary = "API for get TOP 10 product best sold")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class))
            }),
    })
    @GetMapping("/products/top-10-best-sale")
    public ResponseEntity<?> getTop10ProductBestSale(){
        List<ProductDto> productList = productService.getTop10Sale()
                .stream().filter(Product::getAvailable)
                .map(ProductDto::new).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", productList, productList.size())
        );
    }
}
