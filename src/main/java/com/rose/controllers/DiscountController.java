package com.rose.controllers;

import com.rose.entities.Discount;
import com.rose.exceptions.CustomException;
import com.rose.models.ResponseObject;
import com.rose.models.discount.DiscountDto;
import com.rose.models.discount.DiscountRequest;
import com.rose.services.impl.DiscountServiceImpl;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2")
public class DiscountController {

    @Autowired
    private DiscountServiceImpl discountService;

    @Operation(summary = "API for get list discount for admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Discount.class))
            })
    })
    @GetMapping("/management/discounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getAllDiscountForAdmin() {
        List<Discount> discount = discountService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", discount, discount.size())
        );
    }

    @Operation(summary = "API for find a discount for admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Discount.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @GetMapping("/management/discounts/{discountCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getDiscountDetail(@PathVariable String discountCode) {
        Discount discount = discountService.findById(discountCode).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, discountCode+ " is not found!"));
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", new DiscountDto(discount), 1)
        );
    }


    @Operation(summary = "API for create new discount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Discount.class))
            }),
            @ApiResponse(responseCode = "400", description = "Discount Code already exist...", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Discount.class))
            })
    })
    @PostMapping("/management/discounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> createDiscount(@RequestBody @Valid DiscountRequest discountRequest) throws Exception {
        if (discountService.existByDiscountCode(discountRequest.getDiscountCode())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("BAD_REQUEST",
                            discountRequest.getDiscountCode() + " already exist please choose another code...", null, null)
            );
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("CREATED", "Create new discount successfully!", discountService.createDiscount(discountRequest), 1)
        );
    }

    @Operation(summary = "API for add discount for products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Add product to discount success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Discount.class))
            }),
            @ApiResponse(responseCode = "404", description = "Discount is not found", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @PutMapping("/management/discount/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(rollbackFor = CustomException.class)
    public ResponseEntity<ResponseObject> doDiscountProducts(@PathVariable String code, @RequestBody List<String> productCodeList){
        Discount discount = discountService.findById(code).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, code+ " can not be found!")
        );
        if (productCodeList.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("BAD_REQUEST", "Product list to be added to discount is empty!", null, 0)
            );
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Added products to discount successfully...",  discountService.doDisCountProducts(discount, productCodeList), productCodeList.size())
        );
    }

    @Operation(summary = "API for update information of discount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update a discount success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Discount.class))
            }),
            @ApiResponse(responseCode = "404", description = "Discount is not found", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @PutMapping("/management/discounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> updateDiscount(@RequestBody @Valid DiscountRequest discountRequest) throws ParseException {
        Discount discount = discountService.findById(discountRequest.getDiscountCode()).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND,discountRequest.getDiscountCode() +  "can not be found!")
        );
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Update successfully!", discountService.updateDiscount(discount, discountRequest), 1)
        );
    }

    @Operation(summary = "API for remove discount of products by Discount code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Remove discount of products successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Discount.class))
            }),
            @ApiResponse(responseCode = "404", description = "Discount is not found", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @DeleteMapping("/management/discount/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(rollbackFor = CustomException.class)
    public ResponseEntity<ResponseObject> doUnDiscountProducts(@PathVariable String code, @RequestBody String productCode){
        Discount discount = discountService.findById(code).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, code+ " can not be found!")
        );
        if (productCode == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("NOT_FOUND", "Product code to be deleted to discount is empty!",
                            null, 0)
            );
        }
        discountService.doUnDisCountProducts(discount, productCode.replaceAll("\"", ""));
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Remove products to discount successfully...",null  , 1)
        );
    }

}
