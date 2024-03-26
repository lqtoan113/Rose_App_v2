package com.rose.controllers;

import com.rose.entities.Category;
import com.rose.entities.Discount;
import com.rose.entities.Product;
import com.rose.exceptions.CustomException;
import com.rose.models.ResponseObject;
import com.rose.models.category.CategoryDto;
import com.rose.models.category.CategoryRequest;
import com.rose.models.category.CategoryResponse;
import com.rose.models.product.ProductDto;
import com.rose.services.impl.CategoryServiceImpl;
import com.rose.services.impl.DiscountServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/")
public class CategoryController {
    @Autowired private CategoryServiceImpl categoryService;
    @Autowired private DiscountServiceImpl discountService;

    @Operation(summary = "API for get list name of category active")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
            })
    })
    @GetMapping("/collections")
    public ResponseEntity<ResponseObject> getAllCollections(){
        List<String> categoryNames = categoryService.findAll()
                .stream().filter(Category::isActive)
                .map(Category::getCategoryName)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", categoryNames, categoryNames.size())
        );
    }

    @Operation(summary = "API for get List [Product] not include [Product Entry] by CategoryName or Code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class))
            }),
            @ApiResponse(responseCode = "204", description = "Category is empty", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @GetMapping("/collections/{keyword}")
    public ResponseEntity<ResponseObject> getAllProductsWithinCategory(@PathVariable String keyword) {

        Category category = categoryService.findCategoryByKeyword(keyword).orElseThrow(
                () -> new CustomException(HttpStatus.BAD_REQUEST, "Category is not found...")
        );

        List<ProductDto> productList = category.getProducts()
                .stream().filter(Product::getAvailable)
                .peek( product -> {
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
                .map(ProductDto::new)
                .collect(Collectors.toList());

        if (productList.size() == 0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                    new ResponseObject("OK", "No content...",
                            null, 0)
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", productList, productList.size())
        );
    }

    @Operation(summary = "API for get all list [Category]")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))
            })
    })
    @GetMapping("/management/collections")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ResponseObject> getAllCollectionsByAdmin(){
        List<CategoryResponse> categoryList = categoryService.findAll()
                .stream().map(CategoryResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", categoryList, categoryList.size())
        );
    }

    @Operation(summary = "API for get one Category using Category Code or Name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Category is not found", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @GetMapping("/management/collections/{keyword}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ResponseObject> getAllProductsByCategory(@PathVariable String keyword) {

        Category category = categoryService.findCategoryByKeyword(keyword).orElseThrow(
                () -> new CustomException(HttpStatus.BAD_REQUEST, "Category is not found...")
        );

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", category, 1)
        );
    }

    @Operation(summary = "API for get create category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))
            }),
            @ApiResponse(responseCode = "400", description = "Some properties already taken", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @PostMapping("/management/collections")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ResponseObject> createCollection( @RequestBody @Valid CategoryRequest categoryRequest){

        if (categoryService.existByCategoryCode(categoryRequest.getCategoryCode())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("BAD_REQUEST", "Name of category already taken...", null, null)
            );
        }

        if (categoryService.existByCategoryName(categoryRequest.getCategoryName())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("BAD_REQUEST", "Name of category already taken...", null, null)
            );
        }

        Category category = new Category(categoryRequest.getCategoryCode(), categoryRequest.getCategoryName(), categoryRequest.isActive());
        if (!categoryRequest.getSetProductCode().isEmpty()){
            category.setProducts(categoryService.getSetProductFromSetString(categoryRequest.getSetProductCode()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("CREATED", "Create category successfully...", categoryService.createCategory(category), 1)
        );
    }

    @PutMapping("/management/collections")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ResponseObject> updateCategory(@RequestBody @Valid CategoryDto dto){
        Category category = categoryService.findByCategoryCode(dto.getCategoryCode()).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, dto.getCategoryCode()+ " is not found...")
        );
        if (!category.getCategoryName().equals(dto.getCategoryName())){
            if (categoryService.existByCategoryName(dto.getCategoryName())){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new ResponseObject("CONFLICT", "Category with "+ dto.getCategoryName() +" already taken...", null, null)
                );
            }else {
                category.setCategoryName(dto.getCategoryName());
            }
        }
        category.setActive(dto.isActive());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Update category successfully...", categoryService.updateCategory(category), 1)
        );
    }

    @Operation(summary = "API for add products to  category by List category code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Some properties is not found", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @PutMapping("/management/collections/{categoryCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Transactional(rollbackFor = CustomException.class)
    public ResponseEntity<ResponseObject> addProductToCategory(@PathVariable String categoryCode, @RequestBody List<String> productCodeList){
        Category category = categoryService.findByCategoryCode(categoryCode).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, categoryCode + " is not found...")
        );
        if (productCodeList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("NOT_FOUND", "Product list to be added in category is empty!", null, 0)
            );
        }
        categoryService.addProductsToCategory(category, productCodeList);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Add product in category successfully...", null, productCodeList.size())
        );
    }

    @Operation(summary = "API for delete completely category by category code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Category is not found", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @DeleteMapping("/management/collections")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ResponseObject> deleteCategory(@RequestParam String cate){
        Category category = categoryService.findByCategoryCode(cate).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, cate + " is not found...")
        );
        categoryService.deleteCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("DELETED", "Delete category "+ category.getCategoryName() + "successfully...", null, 1)
        );
    }

    @Operation(summary = "API for delete products in category by List category code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Some properties is not found", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @DeleteMapping("/management/collections/{categoryCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Transactional(rollbackFor = CustomException.class)
    public ResponseEntity<ResponseObject> deleteProductInCategory(@PathVariable String categoryCode, @RequestBody List<String> productCodeList){
        Category category = categoryService.findByCategoryCode(categoryCode).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, categoryCode + " is not found...")
        );
        if (productCodeList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("NOT_FOUND", "Product list to be deleted in category is empty!", null, 0)
            );
        }
        categoryService.deleteProductsInCategory(category, productCodeList);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Delete product in category successfully...", null, productCodeList.size())
        );
    }
}
