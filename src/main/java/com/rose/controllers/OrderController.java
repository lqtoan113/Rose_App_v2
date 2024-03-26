package com.rose.controllers;

import com.rose.entities.Order;
import com.rose.entities.enums.EOrder;
import com.rose.entities.enums.EPaymentMethod;
import com.rose.entities.enums.EPaymentStatus;
import com.rose.exceptions.CustomException;
import com.rose.models.ResponseObject;
import com.rose.models.SmsObject;
import com.rose.models.order.OrderRequest;
import com.rose.models.order.OrderResponse;
import com.rose.models.order.OrderUserResponse;
import com.rose.services.impl.OrderServiceImpl;
import com.rose.services.impl.SmsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/")
public class OrderController {
    @Value("${my.app.twilio.defaultphone}")
    private String defaultNumberPhone;
    @Autowired private OrderServiceImpl orderService;
    @Autowired private SmsServiceImpl smsService;

    @Operation(summary = "Get all orders of Client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = OrderUserResponse.class))
            })
    })
    @GetMapping("/me/orders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseObject> getMyOrders(Authentication auth) {
        List<OrderUserResponse> orderList = orderService.getMyOrdersByUsername(auth.getName())
                .stream().map(OrderUserResponse::new)
                .collect(Collectors.toList());

        if (orderList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "You doesn't have any order...", null, 0)
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successful...", orderList, orderList.size())
        );
    }

    @Operation(summary = "Get single order by Order Id of Client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @GetMapping("/me/orders/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseObject> getMyOrderByOrderId(Authentication auth, @PathVariable Long id) {
        Order order = orderService.getOrderByUsernameAndId(auth.getName(), id).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Your order is not exists...")
        );
        order.setOrderDetailList(new ArrayList<>(new HashSet<>(order.getOrderDetailList())));
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successful...", order, 1)
        );
    }

    @Operation(summary = "Create new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))
            }),
            @ApiResponse(responseCode = "406", description = "Account don't have number phone", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @PostMapping("/me/order")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseObject> doCreateMyOrder(Authentication auth, HttpServletRequest req, @RequestBody @Valid OrderRequest orderRequest) throws ParseException {
        if (orderRequest.getOrderDetails().isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Please select product to order...", null, 0)
            );
        }

        return switch (orderRequest.getPaymentMethod()) {
            case "VNPay" -> ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK VNPay", "Create order successfully...", orderService.createOrderWithVNPay(auth, orderRequest, req), 1)
            );
            case "RosePay" -> ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK RosePay", "Create order successfully...", orderService.createOrderWithRosePay(auth, orderRequest, req), 1)
            );
            default -> ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK COD", "Create order successfully...", orderService.createOrderWithCOD(auth, orderRequest, req), 1)
            );
        };

    }

    @Operation(summary = "API for confirm received order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))
            }),
            @ApiResponse(responseCode = "406", description = "Order in status which not accept to confirm ", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Order not found", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @PutMapping("/me/order/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseObject> doCompleteMyOrder(Authentication auth, @PathVariable Long id) {
        Order order = orderService.getOrderByUsernameAndId(auth.getName(), id).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Can't find your order...")
        );
        order.setOrderDetailList(new ArrayList<>(new HashSet<>(order.getOrderDetailList())));
        return switch (order.getStatus()) {
            case PENDING_ACCEPT, COMPLETED, CANCELLED, ACCEPTED ->
                    ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                            new ResponseObject("NOT_ACCEPTABLE", "You cannot complete your order...", null, null)
                    );
            default -> ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Thanks a lot...", orderService.doCompleteOrder(order), 1)
            );
        };
    }

    @Operation(summary = "APi for Cancel order by Order ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))
            }),
            @ApiResponse(responseCode = "406", description = "Cannot cancel order...", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @DeleteMapping("/me/order/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseObject> doCancelMyOrder(Authentication auth, @PathVariable Long id, @RequestParam String reason) {
        Order order = orderService.getOrderByUsernameAndId(auth.getName(), id).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Can't find your order...")
        );
        order.setOrderDetailList(new ArrayList<>(new HashSet<>(order.getOrderDetailList())));
        switch (order.getStatus()) {
            case PENDING_ACCEPT:
            case ACCEPTED:
                order.setDescription(reason);
                order.setStatus(EOrder.CANCELLED);

                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("OK", "Your order has been cancelled...", orderService.rollbackOrder(order), 1)
                );
            case SHIPPING:
                order.setDescription(reason);
                order.setStatus(EOrder.REFUNDED);
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                        new ResponseObject("NOT_ACCEPTABLE", "Your order has been refunded...", orderService.rollbackOrder(order), 1)
                );
            default:
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                        new ResponseObject("NOT_ACCEPTABLE", "You cannot cancel your order...", null, null)
                );
        }
    }

    @Operation(summary = "Get all list orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponse.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @GetMapping("/management/orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ResponseObject> getAllOrders() {
        List<OrderResponse> orderList = orderService.getAllOrders().stream()
                .peek(order -> order.setOrderDetailList(new ArrayList<>(new HashSet<>(order.getOrderDetailList()))))
                .map(OrderResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successful...", orderList, orderList.size())
        );
    }

    @Operation(summary = "Get orders by orderId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = OrderResponse.class))
            }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @GetMapping("/management/orders/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ResponseObject> getOrderByOrderId(@PathVariable Long id) {
        Order order = orderService.getOrderById(id).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Order is not found:" + id)
        );
        order.setOrderDetailList(new ArrayList<>(new HashSet<>(order.getOrderDetailList())));
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successful...", order, 1)
        );
    }

    @Operation(summary = "API for [Admin] confirm order : when PENDING_ACCEPT -> ACCEPTED. When ACCEPTED-> SHIPPING")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))
            }),
            @ApiResponse(responseCode = "406", description = "Order in status which not accept to confirm ", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Order not found", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @PutMapping("/management/orders/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ResponseObject> doAcceptOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Can't find your order...")
        );
        order.setOrderDetailList(new ArrayList<>(new HashSet<>(order.getOrderDetailList())));
        switch (order.getStatus()) {
            case PENDING_ACCEPT -> {
                EPaymentMethod paymentMethod = order.getPayment().getPaymentMethod();
                if (paymentMethod.equals(EPaymentMethod.VN_PAY) || paymentMethod.equals(EPaymentMethod.ROSE_PAY)) {
                    if (!order.getPayment().getPaymentStatus().equals(EPaymentStatus.SUCCESS)) {
                        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                                new ResponseObject("NOT_ACCEPTABLE", "You cannot accept this order.", null, 1)
                        );
                    }
                }
                order.setStatus(EOrder.ACCEPTED);
                smsService.sendSms(new SmsObject(defaultNumberPhone, "Order: #" + order.getId() + "with "+ order.getTotalPriceOrder().toString() +"$ "
                        +"has been accepted!"));
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("OK", "Order accepted successfully!", orderService.updateOrder(order), 1)
                );
            }
            case ACCEPTED -> {
                order.setStatus(EOrder.SHIPPING);
                smsService.sendSms(new SmsObject(defaultNumberPhone, "Order: #" + order.getId() + " with "+ order.getTotalPriceOrder().toString() +"$"
                        +" is on its way to shipping!"));
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("OK", "Shipping order with ID:" + id, orderService.updateOrder(order), 1)
                );
            }
            default -> {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                        new ResponseObject("NOT_ACCEPTABLE", "You can't update order..." + id, orderService.updateOrder(order), 1)
                );
            }

        }
    }

    @Operation(summary = "API for [Admin] cancel order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Order.class))
            }),
            @ApiResponse(responseCode = "406", description = "Order in status which not accept to cancel ", content = {
                    @Content(mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Order not found", content = {
                    @Content(mediaType = "application/json")
            })
    })
    @DeleteMapping("/management/orders/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ResponseObject> doCancelOrder(@PathVariable Long id, @RequestParam String reason) {
        Order order = orderService.getOrderById(id).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Can't find your order...")
        );
        order.setOrderDetailList(new ArrayList<>(new HashSet<>(order.getOrderDetailList())));
        switch (order.getStatus()) {
            case PENDING_ACCEPT, ACCEPTED -> {
                order.setDescription(reason);
                order.setStatus(EOrder.CANCELLED);
                smsService.sendSms(new SmsObject(defaultNumberPhone, "Order: #" + order.getId() + " with "+ order.getTotalPriceOrder().toString() +"$"
                        +" has been cancelled by admin cause "+reason + "!"));
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("OK", "This order has been cancelled...", orderService.rollbackOrder(order), 1)
                );
            }
            default -> {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                        new ResponseObject("NOT_ACCEPTABLE", "You can't cancel order..." + id, orderService.updateOrder(order), 1)
                );
            }
        }
    }

}
