package com.rose.controllers;

import com.rose.entities.ProductEntry;
import com.rose.exceptions.CustomException;
import com.rose.models.ResponseObject;
import com.rose.models.product.ProductEntryDto;
import com.rose.services.impl.ColorServiceImpl;
import com.rose.services.impl.ImageServiceImpl;
import com.rose.services.impl.ProductEntryServiceImpl;
import com.rose.services.impl.SizeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v2/")
public class ProductEntryController {

    @Autowired private ProductEntryServiceImpl entryService;
    @Autowired private ColorServiceImpl colorService;
    @Autowired private SizeServiceImpl sizeService;
    @Autowired private ImageServiceImpl imageService;

    @Operation(summary = "Get product entry by SKU use for client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntry.class))
            }),
            @ApiResponse(responseCode = "204", description = "Not available", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntry.class))
            }),
            @ApiResponse(responseCode = "400", description = "Not Found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntry.class))
            })
    })
    @GetMapping("/products/details/{sku}")
    public ResponseEntity<ResponseObject> getProductDetails(@PathVariable String sku){

        ProductEntry productEntry = entryService.findBySKU(sku).orElseThrow(
                () -> new CustomException(HttpStatus.BAD_REQUEST, "Can't find SKU: "+ sku)
        );

        if (productEntry.getAvailable()){
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Find product entry successfully...", productEntry, 1)
            );
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                new ResponseObject("NO_CONTENT", "Product entry is not available", null, 0)
        );
    }

    @Operation(summary = "Get product entry by SKU use for Admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntry.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntry.class))
            })
    })
    @GetMapping("/management/products/details/{sku}")
    public ResponseEntity<ResponseObject> getProductDetailsAdmin(@PathVariable String sku){

        ProductEntry productEntry = entryService.findBySKU(sku).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Can't find SKU: "+ sku)
        );
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Find product entry successfully...", productEntry, 1)
        );
    }

    @Operation(summary = "Update [Product Entry] by SKU use for Admin.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntryDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Properties Not Found ", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @PutMapping("/management/products/product-entry")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> updateProductEntry(@RequestBody @Valid ProductEntryDto entryDto){
        // select and find Product Entry
        ProductEntry productEntry = entryService.findBySKU(entryDto.getSku()).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "[SKU]: "+ entryDto.getSku() +" is not found!")
        );
        // set Color again
        productEntry.setColor(colorService.getColorByColorValue(entryDto.getColorValue()).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "[SKU] : "+ entryDto.getSku()+".Color "+entryDto.getColorValue()+" is not exists...")
        ));
        // set Size
        productEntry.setSizes(sizeService.getSizeBySetString(entryDto.getSizeValue()));
        // if image change, will update it
        boolean isChangeImage = entryDto.getImageUrl().equalsIgnoreCase(productEntry.getImage().getImageUrl());

        if (!isChangeImage){
            productEntry.setImage(imageService.saveImage(entryDto.getImageUrl()));
        }
        productEntry.setProductPrice(entryDto.getProductPrice());
        productEntry.setQuantity(entryDto.getQuantity());
        productEntry.setAvailable(entryDto.getAvailable());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Update product entry successfully...", entryService.updateProductEntry(productEntry), 1)
        );
    }

    @Operation(summary = "Set status of [Product Entry]")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntry.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductEntry.class))
            })
    })

    @DeleteMapping("/management/products/product-entry/{sku}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> deleteProductEntry(@PathVariable String sku){
        ProductEntry productEntry = entryService.findBySKU(sku).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "[SKU]: "+ sku +" is not found!")
        );
        boolean isParentProductAvailable= productEntry.getProduct().getAvailable();

        if (!productEntry.getAvailable() && !isParentProductAvailable){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    new ResponseObject("NOT_ACCEPTABLE", "You can't change status to available when parent product is not available", null, 0)
            );
        }
        productEntry.setAvailable(!productEntry.getAvailable());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "", entryService.updateProductEntry(productEntry), 1)
        );
    }

}
