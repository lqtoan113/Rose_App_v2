var app = angular.module('shop', []);
app.controller('shop', function ($scope, $http) {
    let pathName = window.location.pathname;
    let curProduct = pathName.split('/').pop();
    $scope.profile = false;
    $scope.cartItems = [];
    $scope.allCate = [];
    $scope.curProductDetail = {};
    $scope.allProduct = [];
    $scope.orders = [];
    $scope.curProductEntries = [];
    $scope.size = [];
    $scope.sku = '';
    $scope.inventory = '0';
    $scope.cartTemp = [];
    $scope.cartToCheckout = [];
    $scope.cartToCheckoutTemp = [];
    $scope.cartProductTemp = {};
    $scope.top10Product = [];
    $scope.comments = [];
    $scope.search = '';
    $scope.listImage = [];
    $scope.commentId;
    $scope.formComment = {};
    $scope.flagEditComment = false;
    $scope.formPurchase = {};
    $scope.historyPayment = [];
    $scope.allProductName = [];

    $scope.refreshToken = function () {
        var formData = new FormData();
        formData.append('refreshToken', localStorage.getItem('refreshToken'));
        var requestOptions = {
            method: 'POST',
            body: formData,
            redirect: 'follow',
        };

        fetch('/api/v2/auth/refresh-token', requestOptions)
            .then((response) => response.json())
            .then((result) => {
                if (result.status === 'OK') {
                    localStorage.removeItem('token');
                    localStorage.setItem('token', result.data);
                    $scope.initUser();
                    $scope.initOrders();
                    $scope.initHistoryPayment();
                } else {
                    $scope.logoutTemp();
                    swal({
                        title: 'Do you want to login again?',
                        text: 'Login version has expired!',
                        icon: 'warning',
                        buttons: true,
                        dangerMode: true,
                    }).then((willDelete) => {
                        if (willDelete) {
                            location.replace('/security/login/form');
                        }
                    });
                }
            })
            .catch((error) => console.log('error', error));
    };

    $scope.searchElastic = function () {
        const api = 'api/v2/products/search?keyword=' + $scope.search;
        $http({
            method: 'GET',
            url: api,
        }).then((resp) => {
            if (resp.data.data.length == 0) {
                $scope.allProduct = false;
            } else {
                $scope.allProduct = resp.data.data;
            }
            if ($scope.search.length == 0) {
                $scope.initProduct();
            }
        });
    };

    $scope.logout = function () {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        $scope.profile = false;
        location.replace('/');
    };

    $scope.logoutTemp = function () {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        $scope.profile = false;
    };

    $scope.initUser = function () {
        var token = 'Bearer ' + localStorage.getItem('token');

        $http({
            method: 'GET',
            url: '/api/v2/me',
            headers: {
                Authorization: token,
            },
        })
            .then((resp) => {
                if (resp.status == 200) {
                    $scope.profile = resp.data.data;
                    setTimeout(removeToast, 1500);
                    function removeToast() {
                        $('#toast').addClass('d-n');
                    }
                    if (pathName === '/cart/checkout') {
                        $scope.loadFromLocalStorage();
                    } else {
                        $scope.cart.saveToLocalStorage('cartToCheckout');
                        $scope.cart.saveToLocalStorage('cartToCheckoutTemp');
                        $scope.loadFromLocalStorage();
                    }
                    const profileGender = $scope.profile.gender;
                    var gender;
                    $scope.roles = $scope.profile.roles[0].name;
                    $scope.permission =
                        $scope.roles === 'ROLE_ADMIN' || $scope.roles === 'ROLE_MODERATOR' ? true : false;
                    if (profileGender == true) {
                        gender = 'male';
                    } else if (profileGender == false) {
                        gender = 'female';
                    } else {
                        gender = 'other';
                    }
                    if (pathName === '/order-list') {
                        document.getElementById(gender).checked = true;
                    }
                } else {
                    $scope.logoutTemp();
                    swal({
                        title: 'Do you want to login again?',
                        text: 'Login version has expired!',
                        icon: 'warning',
                        buttons: true,
                        dangerMode: true,
                    }).then((willDelete) => {
                        if (willDelete) {
                            location.replace('/security/login/form');
                        }
                    });
                }
            })
            .catch((error) => {
                console.log('Token is refreshing');
                $scope.refreshToken();
            });
    };

    $scope.initComment = function () {
        const api = 'api/v2/products/' + curProduct + '/comments';
        $http({
            method: 'GET',
            url: api,
        }).then((resp) => {
            $scope.comments = resp.data.data;
            $scope.countComment = resp.data.data.length;
        });
    };

    $scope.initHome = function () {
        $http({
            method: 'GET',
            url: '/api/v2/products/top-10-best-sale',
        }).then((resp) => {
            $scope.top10Product = resp.data.data;
        });
    };

    $scope.initProduct = function () {
        const api = '/api/v2/products';
        $http({
            method: 'GET',
            url: api,
        }).then((resp) => {
            $scope.allProduct = resp.data.data;
            $scope.allProduct.map((product) => {
                $scope.allProductName.push(product.productName);
            });
        });
    };

    $scope.initCate = function () {
        $http({
            method: 'GET',
            url: '/api/v2/collections',
        }).then((resp) => {
            $scope.allCate = resp.data.data;
        });
    };

    $scope.initDetail = function () {
        const detailApi = '/api/v2/products/' + curProduct;
        $scope.initComment();
        $http.get(detailApi).then((resp) => {
            $scope.curProductDetail = resp.data.data;
            $scope.curProductDetail.productEntries.forEach((value) =>
                $scope.listImage.push({
                    imageUrl: value.image.imageUrl,
                }),
            );
            $scope.curProductEntries = $scope.curProductDetail.productEntries;
            $scope.curProductEntriesOne = $scope.curProductDetail.productEntries[0];
            $scope.curProductEntriesOneSize = $scope.curProductEntriesOne.sizes;
        });
    };

    if (pathName === '/product-detail/' + curProduct) {
        $scope.initDetail();
    }

    $scope.initOrders = function () {
        var token = 'Bearer ' + localStorage.getItem('token');
        $http({
            method: 'GET',
            url: '/api/v2/me/orders',
            headers: {
                Authorization: token,
            },
        })
            .then((resp) => {
                if (resp.data.status === 'OK') {
                    if (resp.data.data == null) {
                        $scope.orders = false;
                    } else {
                        $scope.orders = resp.data.data.reverse();
                    }
                }
            })
            .catch((error) => console.log(error));
    };

    if (pathName === '/order-list') {
        $scope.initOrders();
    }

    $scope.initHistoryPayment = function () {
        var token = 'Bearer ' + localStorage.getItem('token');

        $http({
            method: 'GET',
            url: '/api/v2/me/history-payment',
            headers: {
                Authorization: token,
            },
        })
            .then((resp) => {
                if (resp.status == 200) {
                    if (resp.data.data == null) {
                        $scope.historyPayment = false;
                    } else {
                        $scope.historyPayment = resp.data.data;
                    }
                } else {
                    $scope.logoutTemp();
                    swal({
                        title: 'Do you want to login again?',
                        text: 'Login version has expired!',
                        icon: 'warning',
                        buttons: true,
                        dangerMode: true,
                    }).then((willDelete) => {
                        if (willDelete) {
                            location.replace('/security/login/form');
                        }
                    });
                }
            })
            .catch((error) => {
                console.log(error);
            });
    };

    $scope.init = function () {
        if (localStorage.getItem('token')) {
            $scope.initUser();
            $scope.initHistoryPayment();
        }
        $scope.initHome();
        $scope.initProduct();
        $scope.initCate();
    };

    $scope.init();

    if (window.location.pathname === '/') {
        let token = new URL(location.href).searchParams.get('token');
        let error = new URL(location.href).searchParams.get('error');
        if (token) {
            localStorage.setItem('token', token);
            location.replace('/');
        }
        if (error) {
            swal('Error!', error, 'error');
        }
    }

    $scope.loadFromLocalStorage = function () {
        cartJson = localStorage.getItem($scope.profile.username + 'cart');
        $scope.cartItems = cartJson ? JSON.parse(cartJson) : [];
        var blank = [];
        $scope.haveItem = $scope.cartItems[0] === blank[0] ? false : true;

        cartTempJson = localStorage.getItem($scope.profile.username + 'cartTemp');
        $scope.cartTemp = cartTempJson ? JSON.parse(cartTempJson) : [];

        cartToCheckoutJson = localStorage.getItem($scope.profile.username + 'cartToCheckout');
        $scope.cartToCheckout = cartToCheckoutJson ? JSON.parse(cartToCheckoutJson) : [];

        cartToCheckoutTempJson = localStorage.getItem($scope.profile.username + 'cartToCheckoutTemp');
        $scope.cartToCheckoutTemp = cartToCheckoutTempJson ? JSON.parse(cartToCheckoutTempJson) : [];
    };

    $scope.cart = {
        //Thêm sản phẩm vào giỏ hàng
        add(sku) {
            if ($scope.profile.username) {
                var item = $scope.cartItems.find((item) => item.sku === sku);
                var itemTemp = $scope.cartTemp.find((item) => item.sku === sku);

                var quantity = parseInt(document.getElementById('productQuantity').value);
                if (!document.querySelector('input[name="colorRadio"]:checked')) {
                    swal('Invalid color!', 'Please choose color!', 'error');
                    return;
                } else {
                    var color = document.querySelector('input[name="colorRadio"]:checked').id;
                }
                if (!document.querySelector('input[name="sizeRadio"]:checked')) {
                    swal('Invalid size!', 'Please choose size!', 'error');
                    return;
                } else {
                    var size = document.querySelector('input[name="sizeRadio"]:checked').id;
                }
                if (item) {
                    if (item.quantity + quantity > $scope.inventory) {
                        swal('Warning!', 'Out of stock!', 'warning');
                        return;
                    }
                    item.quantity += quantity;
                    this.saveToLocalStorage('cart');

                    itemTemp.quantity += quantity;
                    this.saveToLocalStorage('cartTemp');
                } else {
                    if (quantity > $scope.inventory) {
                        swal('Warning!', 'Out of stock!', 'warning');
                        return;
                    }
                    //Save to cart
                    $scope.curProductDetail.quantity = quantity;
                    $scope.curProductDetail.sizeValue = size;
                    $scope.curProductDetail.color = color;
                    $scope.curProductDetail.sku = sku;
                    debugger;
                    $scope.curProductDetail.discountPercent = $scope.curProductDetail.discount
                        ? $scope.curProductDetail.discount.discountPercent
                        : 0;
                    $scope.cartItems.push($scope.curProductDetail);
                    this.saveToLocalStorage('cart');

                    //Save to temp cart
                    $scope.cartProductTemp.quantity = quantity;
                    $scope.cartProductTemp.sizeValue = size;
                    $scope.cartProductTemp.color = color;
                    $scope.cartProductTemp.sku = sku;
                    $scope.cartTemp.push($scope.cartProductTemp);
                    this.saveToLocalStorage('cartTemp');
                }
                $scope.loadFromLocalStorage();
            } else {
                swal('Warning!', 'Login to continue!', 'warning');
            }
        },

        quantityPlus(id) {
            let item = $scope.cartItems.find((item) => item.sku === id);
            let _item = $scope.cartTemp.find((item) => item.sku === id);
            if (document.getElementById(id).value == $scope.inventory) {
                swal('Warning!', 'Out of stock!', 'warning');
                return;
            }
            if (item && _item) {
                item.quantity++;
                _item.quantity++;
                this.saveToLocalStorage('cart');
                this.saveToLocalStorage('cartTemp');
                this.saveToLocalStorage('cartToCheckout');
                this.saveToLocalStorage('cartToCheckoutTemp');
            } else {
                item.quantity++;
                _item.quantity++;
                this.saveToLocalStorage('cart');
                this.saveToLocalStorage('cartTemp');
            }
        },

        quantityMinus(id) {
            if (document.getElementById(id).value == 1) {
                return;
            }
            let item = $scope.cartItems.find((item) => item.sku === id);
            let _item = $scope.cartTemp.find((item) => item.sku === id);

            if (item && _item) {
                item.quantity--;
                _item.quantity--;
                this.saveToLocalStorage('cart');
                this.saveToLocalStorage('cartTemp');
                this.saveToLocalStorage('cartToCheckout');
                this.saveToLocalStorage('cartToCheckoutTemp');
            } else {
                item.quantity--;
                _item.quantity--;
                this.saveToLocalStorage('cart');
                this.saveToLocalStorage('cartTemp');
            }
        },

        //Xoá sản phẩm khỏi giỏ hàng
        remove(id) {
            debugger;
            var index = $scope.cartItems.findIndex((item) => item.productCode === id);
            $scope.cartItems.splice(index, 1);
            this.saveToLocalStorage('cart');

            var cartTempIndex = $scope.cartTemp.findIndex((item) => item.productCode === id);
            $scope.cartTemp.splice(cartTempIndex, 1);
            this.saveToLocalStorage('cartTemp');
            $scope.loadFromLocalStorage();
        },

        //Xoá sạch các mặt hàng trong giỏ
        clear() {
            $scope.cartItems = [];
            this.saveToLocalStorage('cart');
            $scope.cartTemp = [];
            this.saveToLocalStorage('cartTemp');
        },

        //Tính tổng số lượng các mặt hàng trong giỏ
        get count() {
            return $scope.cartItems.length;
        },

        //Tổng thành tiền các mặt hàng trong giỏ
        get amount() {
            return $scope.cartItems
                .map((item) => item.quantity * item.commonPrice)
                .reduce((total, qty) => (total += qty), 0);
        },

        get amountToPayment() {
            return $scope.cartToCheckoutTemp
                .map((item) => (item.quantity * item.commonPrice * (100 - item.discountPercent)) / 100)
                .reduce((total, qty) => (total += qty), 0);
        },

        //Lưu giỏ hàng vào local storage
        saveToLocalStorage(type) {
            if (type === 'cart') {
                //dùng angular để copy xong đổi các mặt hàng sang json
                var json = JSON.stringify(angular.copy($scope.cartItems));
                localStorage.setItem($scope.profile.username + 'cart', json);
            } else if (type === 'cartTemp') {
                //dùng angular để copy xong đổi các mặt hàng sang json
                var json = JSON.stringify(angular.copy($scope.cartTemp));
                localStorage.setItem($scope.profile.username + 'cartTemp', json);
            } else if (type === 'cartToCheckoutTemp') {
                //dùng angular để copy xong đổi các mặt hàng sang json
                var json = JSON.stringify(angular.copy($scope.cartToCheckoutTemp));
                localStorage.setItem($scope.profile.username + 'cartToCheckoutTemp', json);
            } else {
                //dùng angular để copy xong đổi các mặt hàng sang json
                var json = JSON.stringify(angular.copy($scope.cartToCheckout));
                localStorage.setItem($scope.profile.username + 'cartToCheckout', json);
            }
        },
    };

    $scope.viewOrderDetail = function (id) {
        var token = 'Bearer ' + localStorage.getItem('token');
        const api = '/api/v2/me/orders/' + id;
        $http({
            method: 'GET',
            url: api,
            headers: {
                Authorization: token,
            },
        })
            .then((resp) => {
                if (resp.data.status === 'OK') {
                    $scope.orderDetail = resp.data.data;
                    $scope.orderDetailPayment = resp.data.data.payment;
                    var timeNow = new Date();
                    var timeExpired = new Date($scope.orderDetailPayment.vnp_ExpireDate);
                    if (timeExpired > timeNow && $scope.orderDetailPayment.paymentStatus != 'SUCCESS') {
                        $scope.paymentAgainBtn = true;
                    } else {
                        $scope.paymentAgainBtn = false;
                    }
                    $scope.orderDetailList = resp.data.data.orderDetailList;
                } else {
                    console.log(resp.data);
                }
            })
            .catch((error) => console.log(error));
    };

    if (pathName === '/order-payment/' + curProduct || pathName === '/order-success/' + curProduct) {
        // const orderDetailId = new URL(location.href).searchParams.get('vnp_Amount');
        $scope.viewOrderDetail(curProduct);
    }

    $scope.style = function (value) {
        return { 'background-color': value };
    };

    $scope.changeSizeByColor = function (color) {
        for (let i = 0; i < $scope.curProductEntries.length; i++) {
            if ($scope.curProductEntries[i].color.colorName === color) {
                $scope.curProductEntriesOneSize = $scope.curProductEntries[i].sizes;
                $scope.sku = $scope.curProductEntries[i].sku;
                $scope.inventory = $scope.curProductEntries[i].quantity;
                $scope.curProductDetail.commonPrice = $scope.curProductEntries[i].productPrice;
                document.getElementById('detail-img').src = $scope.curProductEntries[i].image.imageUrl;
                document.getElementById('productQuantity').value = 1;
            }
        }
    };

    $scope.checkPlus = function () {
        if (document.getElementById('productQuantity').value >= $scope.inventory) {
            swal('Warning!', 'Out of stock!', 'warning');
            document.getElementById('productQuantity').value = $scope.inventory;
        }
    };

    $scope.purchase = function () {
        document.getElementById('paymentBtn').disabled = true;
        if (document.getElementById('phoneNumberCheckbox').checked == false) {
            $scope.phoneNumberDelivery = '0' + $scope.formPurchase.phoneNumber;
            var regex = /(84|0[3|5|7|8|9])+([0-9]{8})\b/g;
            var checkPhoneNumber = regex.test($scope.phoneNumberDelivery) ? false : true;
            if (checkPhoneNumber) {
                swal('Invalid phone number!', 'Please enter the correct phone number', 'warning');
                document.getElementById('paymentBtn').disabled = false;
                return;
            }
        } else {
            $scope.phoneNumberDelivery = $scope.profile.phone;
            if ($scope.phoneNumberDelivery == null) {
                swal('Have not phone number!', 'You do not have phone number', 'warning');
                document.getElementById('paymentBtn').disabled = false;
                return;
            }
        }
        if (document.getElementById('addressCheckbox').checked == true) {
            $scope.deliveryAddress = $scope.profile.address;
            if ($scope.deliveryAddress == undefined) {
                swal('Invalid address!', 'Please enter the correct address', 'warning');
                document.getElementById('paymentBtn').disabled = false;
                return;
            }
        } else {
            const address = document.getElementById('address').value;
            const district = document.getElementById('select2-district-container').innerText;
            const province = document.getElementById('select2-province-container').innerText;
            if (address == '' || district == '' || province == '') {
                swal('Invalid address!', 'Please enter the correct address', 'warning');
                document.getElementById('paymentBtn').disabled = false;
                return;
            } else {
                $scope.deliveryAddress = address.concat(', ', district, ', ', province);
            }
        }

        var token = 'Bearer ' + localStorage.getItem('token');
        var paymentMethod = document.querySelector('input[name="paymentRadio"]:checked').id;
        if (paymentMethod === 'RosePay') {
            if ($scope.cart.amountToPayment > $scope.profile.balance) {
                swal('Not enough money!', 'Please top up to get enough money', 'error');
                return;
            }
        }
        $http({
            method: 'POST',
            url: '/api/v2/me/order',
            headers: {
                Authorization: token,
            },
            data: JSON.stringify({
                numberPhone: $scope.phoneNumberDelivery,
                paymentMethod: paymentMethod,
                address: $scope.deliveryAddress,
                orderDetails: $scope.cartToCheckout,
            }),
            'Content-Type': 'application/json',
        })
            .then((resp) => {
                if (resp.status == 200) {
                    if (resp.data.status === 'OK VNPay') {
                        window.location.replace(resp.data.data.vnp_PayUrl);
                    } else if (resp.data.status === 'OK COD') {
                        const redirectUrl = '/order-success/' + resp.data.data.id;
                        window.location.replace(redirectUrl);
                    } else {
                        const redirectUrl = '/order-success/' + resp.data.data.id;
                        window.location.replace(redirectUrl);
                    }
                } else {
                    swal('An error occur!', resp.data.message, 'error');
                    document.getElementById('paymentBtn').disabled = false;
                }
            })
            .catch((error) => {
                if (error.status == 500) {
                    swal({
                        title: 'Do you want to login?',
                        text: 'Your login version is expired!',
                        icon: 'warning',
                        buttons: true,
                        dangerMode: true,
                    }).then((willDelete) => {
                        if (willDelete) {
                            location.replace('/security/login/form');
                        } else {
                            swal('Error!', 'You cannot purchase!', 'error');
                        }
                    });
                } else {
                    swal('An error occur!', error.data.message, 'error');
                }
            });
    };

    $scope.getPageByCate = function (cate) {
        const getPageByCateApi = '/api/v2/collections/' + cate;
        $http({
            method: 'GET',
            url: getPageByCateApi,
        }).then((resp) => {
            if (resp.data.data === []) {
                $scope.allProduct = false;
            } else {
                $scope.pager.pageProduct = 0;
                $scope.allProduct = resp.data.data;
            }
        });
    };

    $scope.topFunction = function () {
        document.body.scrollTop = 0;
        document.documentElement.scrollTop = 0;
    };

    $scope.cancelOrder = function (id) {
        swal({
            title: 'Are you sure?',
            text: 'You will not be able to recover this order!',
            icon: 'warning',
            buttons: true,
            dangerMode: true,
        }).then((willDelete) => {
            if (willDelete) {
                var token = 'Bearer ' + localStorage.getItem('token');
                var reasonId = document.querySelector('input[name="reason"]:checked').value;
                var reasonValue = document.getElementById(reasonId).innerText;
                var form = new FormData();
                form.append('reason', reasonValue);
                const api = '/api/v2/me/order/' + id;
                $http({
                    method: 'DELETE',
                    url: api,
                    headers: {
                        Authorization: token,
                    },
                    processData: false,
                    mimeType: 'multipart/form-data',
                    contentType: false,
                    data: form,
                }).then((resp) => {
                    if (resp.data.status === 'OK') {
                        swal('Success!', resp.data.message, 'success');
                        $scope.viewOrderDetail(id);
                        $scope.initOrders();
                        $scope.initUser();
                    } else {
                        swal('Error!', 'System is malfunctioning!', 'error');
                    }
                });
            }
        });
    };

    $scope.receivedOrder = function (id) {
        swal({
            title: 'You have received order?',
            text: 'Please confirm!',
            icon: 'warning',
            buttons: true,
            dangerMode: true,
        }).then((willDelete) => {
            if (willDelete) {
                var token = 'Bearer ' + localStorage.getItem('token');
                const api = '/api/v2/me/order/' + id;
                $http({
                    method: 'PUT',
                    url: api,
                    headers: {
                        Authorization: token,
                    },
                }).then((resp) => {
                    if (resp.data.status === 'OK') {
                        swal('Success!', resp.data.message, 'success');
                        $scope.viewOrderDetail(id);
                        $scope.initOrders();
                    } else {
                        swal('Error!', 'System is malfunctioning!', 'error');
                    }
                });
            }
        });
    };

    $scope.reverse = false;

    $scope.sortColumn = function (col) {
        $scope.column = col;
        if ($scope.reverse) {
            $scope.reverse = false;
            $scope.reverseclass = 'arrow-up';
        } else {
            $scope.reverse = true;
            $scope.reverseclass = 'arrow-down';
        }
    };

    $scope.pager = {
        pageTop10: 0,
        sizeTop10: 4,
        pageProduct: 0,
        sizeProduct: 12,

        get top10Product() {
            var start = this.pageTop10 * this.sizeTop10;
            return $scope.top10Product.slice(start, start + this.sizeTop10);
        },
        get countTop10() {
            return Math.ceil((1.0 * $scope.top10Product.length) / this.sizeTop10);
        },
        prevTop10() {
            this.pageTop10--;
            if (this.pageTop10 < 0) {
                this.pageTop10 = 0;
                return;
            }
        },
        nextTop10() {
            this.pageTop10++;
            if (this.pageTop10 >= this.countTop10) {
                this.pageTop10 = this.countTop10 - 1;
                return;
            }
        },
        get allProduct() {
            var start = this.pageProduct * this.sizeProduct;
            return $scope.allProduct
                ? $scope.allProduct.slice(start, start + this.sizeProduct)
                : ($scope.allProduct = false);
        },
        get count() {
            return Math.ceil((1.0 * $scope.allProduct.length) / this.sizeProduct) > 0
                ? Math.ceil((1.0 * $scope.allProduct.length) / this.sizeProduct)
                : 0;
        },
        firstProduct() {
            this.pageProduct = 0;
            $scope.topFunction();
        },
        prevProduct() {
            this.pageProduct--;
            if (this.pageProduct < 0) {
                this.pageProduct = 0;
                return;
            }
            $scope.topFunction();
        },
        nextProduct() {
            this.pageProduct++;
            if (this.pageProduct >= this.count) {
                this.pageProduct = this.count - 1;
                return;
            }
            $scope.topFunction();
        },
        lastProduct() {
            this.pageProduct = this.count - 1;
            $scope.topFunction();
        },
    };

    $scope.infoEdit = function () {
        document.querySelectorAll('input').forEach((element) => (element.disabled = false));
        document.getElementById('username').disabled = true;
        document.getElementById('editInfoBtn').disabled = false;
        $('#editInfoBtn').removeClass('d-n');
    };

    $scope.commentHandel = function (method, api) {
        $scope.formComment.rate = document.getElementById('rating').value;
        var token = 'Bearer ' + localStorage.getItem('token');
        $http({
            method: method,
            url: api,
            headers: {
                Authorization: token,
                'Content-Type': 'application/json',
            },
            data: JSON.stringify($scope.formComment),
        })
            .then((resp) => {
                if (resp.status == 201) {
                    swal('Success!', resp.data.message, 'success');
                    $scope.formComment = {};
                    $scope.initComment();
                    $scope.initDetail();
                } else if (resp.status == 200) {
                    swal('Success!', resp.data.message, 'success');
                    $scope.flagEditComment = false;
                    $scope.formComment = {};
                    $scope.initComment();
                    $scope.initDetail();
                } else {
                    swal('error!', resp.data.message, 'error');
                }
            })
            .catch((error) => {
                console.log(error);
                swal('error!', 'Some thing went wrong!', 'error');
            });
    };

    $scope.addComment = function () {
        var api = '/api/v2/products/' + curProduct + '/comments';
        $scope.commentHandel('POST', api);
    };

    $scope.deleteComment = function (id) {
        var api = '/api/v2/products/' + curProduct + '/comments/' + id;
        $scope.commentHandel('DELETE', api);
    };

    $scope.editComment = function (comment) {
        $scope.flagEditComment = true;
        $scope.formComment = comment;
    };

    $scope.editCommentHandel = function () {
        var api = '/api/v2/products/' + curProduct + '/comments';
        $scope.commentHandel('PUT', api);
    };

    $scope.cancelEditComment = function () {
        $scope.flagEditComment = false;
        $scope.formComment = {};
    };

    $scope.checkToCart = function (id) {
        var checkBox = document.getElementById('check' + id);
        if (checkBox.checked == true) {
            var item = $scope.cartTemp.find((item) => item.sku === id);
            if (item) {
                $scope.cartToCheckout.push(item);
                $scope.cart.saveToLocalStorage('cartToCheckout');
            }

            var itemFull = $scope.cartItems.find((item) => item.sku === id);
            if (itemFull) {
                $scope.cartToCheckoutTemp.push(itemFull);
                $scope.cart.saveToLocalStorage('cartToCheckoutTemp');
            }
        } else {
            var index = $scope.cartToCheckout.findIndex((item) => item.sku === id);
            $scope.cartToCheckout.splice(index, 1);
            $scope.cart.saveToLocalStorage('cartToCheckout');
            var index1 = $scope.cartToCheckoutTemp.findIndex((item) => item.sku === id);
            $scope.cartToCheckoutTemp.splice(index1, 1);
            $scope.cart.saveToLocalStorage('cartToCheckoutTemp');
        }
    };

    $scope.payAgain = function (url) {
        window.location.replace(url);
    };

    $scope.processToPayment = function () {
        var blank = [];
        if ($scope.cartToCheckout[0] != blank[0]) {
            window.location.replace('/cart/checkout');
        } else {
            swal('No products selected!', 'Please check product to payment', 'warning');
        }
    };

    $scope.recharge = function () {
        var rechargeAmount = document.getElementById('rechargeAmount').value;
        if (rechargeAmount <= 0 || rechargeAmount === '') {
            swal('Money amount have not entered!', 'Please enter amount of money', 'warning');
        } else if (rechargeAmount > 800) {
            swal('Too many money!', 'Please enter amount of money below $2000', 'warning');
        } else {
            var api = '/api/v2/me/recharge/?amount=' + rechargeAmount;
            var token = 'Bearer ' + localStorage.getItem('token');
            $http({
                method: 'GET',
                url: api,
                headers: {
                    Authorization: token,
                },
            })
                .then((resp) => {
                    if (resp.status == 200) {
                        if (resp.data.status === 'OK VNPay') {
                            window.location.replace(resp.data.data.vnp_PayUrl);
                        }
                    } else {
                        swal('An error occur!', resp.data.message, 'error');
                        document.getElementById('paymentBtn').disabled = false;
                    }
                })
                .catch((error) => {
                    if (error.status == 500) {
                        swal({
                            title: 'Do you want to login?',
                            text: 'Your login verson is expired!',
                            icon: 'warning',
                            buttons: true,
                            dangerMode: true,
                        }).then((willDelete) => {
                            if (willDelete) {
                                location.replace('/security/login/form');
                            } else {
                                swal('Error!', 'You cannot recharge!', 'error');
                            }
                        });
                    } else {
                        swal('An error occur!', error.data.message, 'error');
                    }
                });
        }
    };

    $scope.paymentStatusHandle = function () {
        const status = new URL(location.href).searchParams.get('vnp_TransactionStatus');
        if (status == 00) {
            $scope.paymentStatus = true;
        } else {
            $scope.paymentStatus = false;
        }
    };

    if (pathName === '/recharge/' + curProduct) {
        $scope.paymentStatusHandle();
    }

    $scope.checkTable = function () {};

    if (pathName === '/shop') {
        autocomplete(document.getElementById('myInput'), $scope.allProductName);
    }

    $scope.updateCart = function () {
        for (let i = 0; i < $scope.allProduct.length; i++) {
            for (let j = 0; j < $scope.cartItems.length; j++) {
                if ($scope.allProduct[i].productCode === $scope.cartItems[j].productCode) {
                    $scope.cartItems[j].discountPercent = $scope.allProduct[i].discountPercent;
                }
            }
        }
        $scope.cart.saveToLocalStorage('cart');
    };

    setInterval(() => {
        $scope.updateCart();
    }, 2000);
});

function file_changed(uploadImage) {
    var token = 'Bearer ' + localStorage.getItem('token');
    var myHeaders = new Headers();
    myHeaders.append('Authorization', token);

    var formData = new FormData();
    formData.append('file', uploadImage.files[0]);

    var requestOptions = {
        method: 'POST',
        headers: myHeaders,
        body: formData,
        redirect: 'follow',
    };

    fetch('/api/v2/me', requestOptions)
        .then((response) => response.json())
        .then((result) => (document.getElementById('uploadedAvatar').src = result.data))
        .catch((error) => console.log('error', error));
    console.log(uploadImage.files[0]);
}

function convertDate(d) {
    var p = d.split('/');
    return +(p[2] + p[1] + p[0]);
}

function sortByDate(direction) {
    var tbody = document.querySelector('#results tbody');
    // get trs as array for ease of use
    var rows = [].slice.call(tbody.querySelectorAll('tr'));

    if (direction === 'asc') {
        document.querySelector('.sort-season').classList.remove('sort-season--start');
        document.querySelector('.sort-season').classList.add('sort-season--end');

        rows.sort(function (a, b) {
            return convertDate(b.cells[4].innerHTML) - convertDate(a.cells[4].innerHTML);
        });
    } else {
        document.querySelector('.sort-season').classList.add('sort-season--start');
        document.querySelector('.sort-season').classList.remove('sort-season--end');

        rows.sort(function (a, b) {
            return convertDate(a.cells[4].innerHTML) - convertDate(b.cells[4].innerHTML);
        });
    }

    rows.forEach(function (v) {
        tbody.appendChild(v);
    });
}

if (window.location.pathname === '/order-list') {
    document.querySelector('.sort-season').addEventListener('click', () => {
        if (document.querySelector('.sort-season').classList.contains('sort-season--start')) {
            sortByDate('asc');
        } else {
            sortByDate('desc');
        }
    });
}

function defaultAddress() {
    var checkBox = document.getElementById('addressCheckbox');
    if (checkBox.checked == false) {
        $('#wrapAddress').removeClass('d-n');
    } else {
        $('#wrapAddress').addClass('d-n');
    }
}

function defaultPhoneNumber() {
    var checkBox = document.getElementById('phoneNumberCheckbox');
    if (checkBox.checked == false) {
        $('#wrapPhoneNumber').removeClass('d-n');
    } else {
        $('#wrapPhoneNumber').addClass('d-n');
    }
}

function setAmount(amount) {
    document.getElementById('rechargeAmount').value = amount.innerText;
}

function checkTable() {
    setTimeout(() => {
        if (document.getElementById('tableOrders').innerText == '') {
            $('#tableOrderBlank').removeClass('d-n');
        } else {
            $('#tableOrderBlank').addClass('d-n');
        }
    }, 50);
}

function showHistoryOrderDetail(a) {
    var id = a.id;
    if (document.querySelector(id).classList == '') {
        $(id).addClass('d-n');
    } else {
        $(id).removeClass('d-n');
    }
}
