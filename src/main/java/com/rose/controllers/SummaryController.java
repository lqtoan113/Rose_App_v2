package com.rose.controllers;

import com.rose.models.ResponseObject;
import com.rose.services.impl.AccountServiceImpl;
import com.rose.services.impl.OrderDetailsServiceImpl;
import com.rose.services.impl.OrderServiceImpl;
import com.rose.services.impl.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/")
public class SummaryController {
    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private OrderServiceImpl orderService;
    @Autowired
    private OrderDetailsServiceImpl orderDetailService;
    @Autowired
    private AccountServiceImpl accountService;

    @GetMapping("/management/todayOrder")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getTodayOrder() {
        Long todayOrder = orderService.getToDayOrder();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", todayOrder, null));
    }

    @GetMapping("/management/todayOrderByStatus")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getTodayOrderByStatus() {
        Map<String,Object> map = new HashMap<>();
        map.put("todayPendingAcceptOrder", orderService.TodayPendingAcceptOrder());
        map.put("todayAcceptOrder", orderService.TodayAcceptOrder());
        map.put("todayShippingOrder", orderService.TodayShippingOrder());
        map.put("todayCompleteOrder", orderService.TodayCompleteOrder());
        map.put("todayCancelOrder", orderService.TodayCancelOrder());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", map, null)
        );
    }

    @GetMapping("/management/totalUserWasActive")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getTotalUserWasActive() {
        Long totalUser = accountService.getToTalAccountActive();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", totalUser, null)
        );
    }


    @GetMapping("/management/totalMaleUser")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getTotalMaleUser() {
        Long totalMaleUser = accountService.getTotalMaleUser() ;
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", totalMaleUser, null)
        );
    }

    @GetMapping("/totalFemaleUser")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getTotalFemaleUser() {
        Long totalFemaleUser = accountService.getTotalFemaleUser() ;
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", totalFemaleUser, null)
        );
    }


    @GetMapping("/management/available")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getAvailable() {
        Long available = productService.getAvailable();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", available, null)
        );
    }

    @GetMapping("/management/totalProduct")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getTotalProduct() {
        Long totalProduct = productService.getTotalProduct();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", totalProduct, null)
        );
    }

    @GetMapping("/management/todayIncome")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getTodayIncome() {
        Double todayIncome = orderDetailService.getTodayIncome();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", todayIncome, null)
        );
    }

    @GetMapping("/management/totalIncome")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getTotalIncome() {
        Double totalIncome = orderDetailService.getTotalIncome();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", totalIncome, null)
        );
    }

    @GetMapping("/management/totalAccount")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getToTalAccount() {
        Long totalAccount = accountService.getToTalAccount();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", totalAccount, null)
        );
    }
    @GetMapping("/management/totalOrder")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getTotalOrder() {
        Long totalOrder = orderService.totalOrder();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", totalOrder, null)
        );
    }

    @GetMapping("/management/revenueLast7Days")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getRevenueLast7Days() {
        List<Object[]> revenueLast7Days = orderService.getRevenueLast7Days();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", revenueLast7Days, null)
        );
    }

    @GetMapping("/management/numberOfProductSoldByType")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getNumberOfProductSoldByType() {
        List<Object[]> numberOfProductSoldByType =  productService.numberOfProductSoldByType();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", numberOfProductSoldByType, null)
        );
    }

    @GetMapping("/management/percentByCate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getPercentByCate() {
        List<Object[]> getPercentByCate =  productService.getPercentByCate();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", getPercentByCate, null)
        );
    }

    @GetMapping("/management/availableRate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getAvailableRate() {
        List<Object[]> availableRate =  productService.availableRate();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", availableRate, null)
        );
    }

    @GetMapping("/management/top10SoldProduct")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getTop10SoldProduct() {
        List<Object> top10Product = productService.top10Product();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", top10Product, null)
        );
    }

    @GetMapping("/management/top10Customer")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getTop10Customer() {
        List<Object[]> top10Customer = accountService.top10Customer();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", top10Customer, null)
        );
    }
    }



