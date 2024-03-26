package com.rose.controllers;

import com.rose.entities.Discount;
import com.rose.entities.Product;
import com.rose.entities.ProductEntry;
import com.rose.entities.ProductSearch;
import com.rose.exceptions.CustomException;
import com.rose.models.ResponseObject;
import com.rose.models.product.ProductDto;
import com.rose.models.product.ProductRequest;
import com.rose.repositories.ProductSearchRepository;
import com.rose.services.impl.DiscountServiceImpl;
import com.rose.services.impl.ProductEntryServiceImpl;
import com.rose.services.impl.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/")
public class ProductController {

    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private ProductEntryServiceImpl entryService;
    @Autowired private DiscountServiceImpl discountService;


    @Operation(summary = "Search product depend on name, code, description, if keyword is null then return all products available")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class))
            })
    })
    @GetMapping("/products/search")
    public ResponseEntity<ResponseObject> search(@RequestParam String keyword) {
        if (keyword != null && keyword.length() > 0) {
            List<ProductDto> results = productService.searchProductWithElasticsearch(keyword)
                    .stream().peek( product -> product.setRevenue(0.00))
                    .filter(ProductDto::getAvailable)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Success", results, results.size())
            );
        }

        List<ProductDto> productList = productService.getAll().stream().filter(Product::getAvailable)
                .map(ProductDto::new).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Success", productList, productList.size())
        );
    }

    @Operation(summary = "API for get all List [Product] not include [Product Entry]")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class))
            })
    })
    @GetMapping("/products")
    public ResponseEntity<ResponseObject> getAllProductsAvailable() {
        List<ProductDto> productList = productService.getAll()
                .stream().peek( product -> {
                    product.setRevenue(0.00);
                    if (product.getDiscount() != null && product.getDiscount().getActive()
                            && discountService.canUseDiscount(product.getDiscount())){
                        Discount discount = product.getDiscount();
                        discount.setTotalExpense(0.0);
                        discount.setTotalDiscountUsed(0);
                    } else {
                        product.setDiscount(null);
                    }
                })
                .filter(Product::getAvailable)
                .map(ProductDto::new).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", productList, productList.size())
        );
    }

    @Operation(summary = "API for get [Product] include [Product Entry] by ProductCode")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))
            }),
            @ApiResponse(responseCode = "404", description = "Properties not found", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @GetMapping("/products/{productCode}")
    public ResponseEntity<ResponseObject> getProductByCode(@PathVariable String productCode) {
        Product product = productService.getProductByProductCode(productCode).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Product not found!")
        );

        if (!product.getAvailable()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("NOT_FOUND", "Product is not available...", null, null)
            );
        }
        // hide properties secret of discount
        if (product.getDiscount() != null && product.getDiscount().getActive()
            && discountService.canUseDiscount(product.getDiscount())){
            Discount discount = product.getDiscount();
            discount.setTotalExpense(0.0);
            discount.setTotalDiscountUsed(0);
        } else {
            product.setDiscount(null);
        }

        product.setRevenue(0.00);
        product.setProductEntries(
                product.getProductEntries().stream()
                        .peek( p -> p.setRevenue(0.00))
                        .filter(ProductEntry::getAvailable)
                        .collect(Collectors.toSet())
        );

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", product, 1)
        );
    }

    @Operation(summary = "Search product depend on name, code, description, if keyword is null then return all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class))
            })
    })
    @GetMapping("/management/products/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ResponseObject> searchByAdmin(@RequestParam String keyword) {
        if (keyword != null && keyword.length() > 0) {
            List<ProductDto> results = productService.searchProductWithElasticsearch(keyword);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Success", results, results.size())
            );
        }
        List<ProductDto> productList = productService.getAll().stream()
                .map(ProductDto::new).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Success", productList, productList.size())
        );
    }

    @Operation(summary = "API for get List [Product] not include [Product Entry], include available true and false")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class))
            })
    })
    @GetMapping("/management/products")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ResponseObject> getAllProducts() {

        List<ProductDto> productList = productService.getAll().stream()
                .map(ProductDto::new).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", productList, productList.size())
        );
    }

    @Operation(summary = "API for get [Product] by Product Code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))
            }),
            @ApiResponse(responseCode = "404", description = "Properties not found", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @GetMapping("/management/products/{productCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ResponseObject> getAllProductsByCode(
            @PathVariable String productCode) {

        Product product = productService.getProductByProductCode(productCode).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_ACCEPTABLE, "Product not found!")
        );

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", product, 1)
        );
    }

    @Operation(summary = "API for create [Product] include [Product Entry]")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))
            }),
            @ApiResponse(responseCode = "400", description = "Product Code already taken...", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Properties not found", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @PostMapping("/management/products")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ResponseObject> createProduct(@RequestBody @Valid ProductRequest product) {
        if (productService.existProductByProductCode(product.getProductCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("BAD_REQUEST", "Product Code already taken...", null, null)
            );
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("CREATED", "Create product successfully...", productService.createProduct(product), 1)
        );
    }

    @Operation(summary = "API for update [Product] not include [Product Entry]")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))
            }),
            @ApiResponse(responseCode = "404", description = "Properties not found", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @PutMapping("/management/products")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ResponseObject> updateProduct(@RequestBody @Valid ProductDto productDto) {
        Product product = productService.getProductByProductCode(productDto.getProductCode()).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Product not found!")
        );

        ModelMapper mapper = new ModelMapper();
        mapper.addMappings(new PropertyMap<ProductDto, Product>() {
            @Override
            protected void configure() {
                skip(destination.getRevenue());
                skip(destination.getSold());
                skip(destination.getProductCode());
                skip(destination.getCreateDate());
                skip(destination.getProductEntries());
            }
        });
        mapper.map(productDto, product);
        entryService.updateStatusListProductEntry(new ArrayList<>(product.getProductEntries()), product.getAvailable());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("CREATED", "Update product successfully...", productService.updateProduct(product), 1)
        );
    }


    @Operation(summary = "Set status of [Product]")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @DeleteMapping("/management/products/{product_code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ResponseObject> deleteProduct(@PathVariable String product_code) {
        Product product = productService.getProductByProductCode(product_code).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Product not found!")
        );

        product.setAvailable(!product.getAvailable());
        entryService.updateStatusListProductEntry(new ArrayList<>(product.getProductEntries()), product.getAvailable());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("Ok", "Handle successfully...", productService.updateProduct(product), 1)
        );
    }

}
