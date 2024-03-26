package com.rose.controllers;

import com.rose.entities.Color;
import com.rose.entities.Size;
import com.rose.exceptions.CustomException;
import com.rose.models.ResponseObject;
import com.rose.models.color.ColorRequest;
import com.rose.models.size.SizeRequest;
import com.rose.services.ISizeService;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/")
public class SizeController {

    @Autowired
    private ISizeService sizeService;

    @GetMapping("/sizes")
    public ResponseEntity<ResponseObject> getAllColorForUser() {
        List<Size> sizeList = sizeService.getAll()
                .stream().
                filter(Size::isAvailable)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", sizeList, null)
        );
    }

    @GetMapping("/management/sizes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getAllColorForAdmin() {
        List<Size> sizeList = sizeService.getAll();

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", sizeList, null)
        );
    }

    @GetMapping("/management/sizes/{sizeValue}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ResponseObject> getAllSizeBySizeValue(
            @PathVariable String sizeValue) {

        Size size = sizeService.findSizeByValue(sizeValue).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Size not found!")
        );

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", size, null)
        );
    }


    @PostMapping("/management/sizes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> createNewSize(@RequestBody @Valid SizeRequest sizeRequest) {
        if (sizeService.existsSizeByValue(sizeRequest.getSizeValue())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ResponseObject("CONFLICT", "Size value is already taken!", null, 0)
            );
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("CREATED", "Create new size successfully!",
                        sizeService.createSize(sizeRequest), 1)
        );
    }

    @PutMapping("/management/sizes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> updateSize(@RequestBody @Valid SizeRequest sizeRequest) {
        Size size = sizeService.findSizeById(sizeRequest.getId()).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Size not found!")
        );

        if(!size.getSizeValue().equals(sizeRequest.getSizeValue())){
            if (sizeService.existsSizeByValue(sizeRequest.getSizeValue())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new ResponseObject("CONFLICT", "Size value is already taken!", null, 0)
                );
            }
        }
        ModelMapper mapper = new ModelMapper();
        mapper.addMappings(new PropertyMap<ColorRequest, Color>() {
            @Override
            protected void configure() {
                skip(destination.getId());
            }
        });
        mapper.map(sizeRequest, size);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Update successfully!",
                        sizeService.updateSize(size), 1)
        );
    }


    @DeleteMapping("/management/sizes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> deleteSize(@RequestParam String sizeValue) {
        Size size = sizeService.findSizeByValue(sizeValue).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Size not found!")
        );
        size.setAvailable(!size.isAvailable());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Handle successfully!", sizeService.updateSize(size), null)
        );
    }

}
