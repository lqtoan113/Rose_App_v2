package com.rose.controllers;

import com.rose.entities.Color;
import com.rose.exceptions.CustomException;
import com.rose.models.ResponseObject;
import com.rose.models.color.ColorRequest;
import com.rose.services.IColorService;
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
public class ColorController {

    @Autowired
    private IColorService colorService;

    @GetMapping("/colors")
    public ResponseEntity<ResponseObject> getAllColorForUser() {
        List<Color> colorList = colorService.getAll()
                .stream()
                .filter(Color::isAvailable)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", colorList, colorList.size())
        );
    }
    @GetMapping("/management/colors")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getAllColorForAdmin() {
        List<Color> colorList = colorService.getAll();

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", colorList, null)
        );
    }

    @GetMapping("/management/colors-available")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getAllColorAvailableForAdmin() {
        List<Color> colorList = colorService.getAll()
                .stream().filter( Color::isAvailable)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", colorList, colorList.size())
        );
    }

    @GetMapping("/management/colors/{colorValue}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ResponseObject> getAllColorByColorValue(
            @PathVariable String colorValue) {

        Color color = colorService.getColorByColorValue(colorValue).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Color not found!")
        );

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", color, null)
        );
    }


    @PostMapping("/management/colors")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> createNewColor(@RequestBody @Valid ColorRequest colorRequest) {
        if (colorService.existsByColorValue(colorRequest.getColorValue())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("BAD_REQUEST", "Color value is already taken!", null, 0)
            );
        }
        if (colorService.existsByColorName(colorRequest.getColorName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ResponseObject("CONFLICT", "Name is already taken!", null, 0)
            );
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("CREATED", "Create new color successfully!",
                        colorService.createColor(colorRequest), 1)
        );
    }

    @PutMapping("/management/colors")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> updateColor(@RequestBody @Valid ColorRequest colorRequest) {
        Color color = colorService.findColorById(colorRequest.getId()).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Color not found!")
        );
        if (!color.getColorName().equals(colorRequest.getColorName())) {
            if (colorService.existsByColorName(colorRequest.getColorName())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new ResponseObject("CONFLICT", "Name is already taken!", null, 0)
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
        mapper.map(colorRequest, color);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Update successfully!",
                        colorService.updateColor(color), 1)
        );
    }

    @DeleteMapping("/management/colors")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> deleteColor(@RequestParam String colorValue) {
        Color color = colorService.findColorByColorValue(colorValue).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Color not found!")
        );
        color.setAvailable(!color.isAvailable());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Handle successfully!", colorService.updateColor(color), null)
        );
    }
}
