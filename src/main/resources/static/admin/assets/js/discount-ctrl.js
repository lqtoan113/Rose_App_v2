app.controller('discount-ctrl', function ($scope, $http, moment) {
    $scope.allDiscount = [];
    $scope.curDiscount = '';
    $scope.allProduct = [];
    $scope.setProductCode = [];
    $scope.editProductCate = false;

    //logout when token expirations
    $scope.logoutTemp = function () {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
    };

    $scope.initProfiles = function () {
        const token = 'Bearer ' + localStorage.getItem('token');
        $http({
            method: 'GET',
            url: '/api/v2/me',
            headers: {
                Authorization: token,
            },
        })
            .then((resp) => {
                $scope.profiles = resp.data.data;
                $scope.profiles.roles.name = resp.data.data.roles[0].name;
                if ($scope.profiles.roles.name === 'ROLE_MODERATOR') {
                    $scope.pageSummary = false;
                } else {
                    $scope.pageSummary = true;
                }
                if (resp.status === 401) {
                    $scope.refreshToken();
                }
            })
            .catch((error) => console.log(error));
    };

    $scope.initDiscount = function () {
        let token = 'Bearer ' + localStorage.getItem('token');
        $http({
            url: '/api/v2/management/discounts',
            method: 'GET',
            timeout: 0,
            headers: {
                Authorization: token,
            },
        })
            .then((resp) => {
                if (resp.data.status === '403 FORBIDDEN') {
                    location.replace('/security/page/403');
                } else {
                    if (resp.data.data) {
                        $scope.allDiscount = resp.data.data;
                    } else {
                        $scope.allDiscount = false;
                    }
                }
            })
            .catch((error) => {
                if (error.status == 403) {
                    location.replace('/security/page/403');
                } else {
                    console.log(error);
                }
            });
    };

    $scope.initProduct = function () {
        var token = 'Bearer ' + localStorage.getItem('token');
        var api = '/api/v2/management/products';
        $http({
            method: 'GET',
            url: api,
            headers: {
                Authorization: token,
            },
        })
            .then((resp) => {
                if (resp.status == 200) {
                    $scope.allProduct = resp.data.data;
                } else {
                    console.log('Token is refreshing');
                    $scope.refreshToken();
                }
            })
            .catch(() => {
                console.log('Token is refreshing');
                $scope.refreshToken();
            });
    };

    //load data status-order
    $scope.initOrder = function () {
        var token = 'Bearer ' + localStorage.getItem('token');
        $http({
            method: 'GET',
            url: 'http://localhost:8080/api/v2/management/todayOrderByStatus',
            headers: {
                Authorization: token,
            },
        }).then((resp) => {
            if (resp.data.status === 'OK') {
                $scope.orderByStatus = resp.data.data;
                console.log($scope.orderByStatus)
            }

        }).catch((error) => {
            if(error.data.status === 401){
                $scope.refreshToken();
            }
        });
    };

    //load data category and Discount
    $scope.init = function () {
        $scope.initProfiles();
        $scope.initOrder();
        $scope.initDiscount();
        $scope.initProduct();
    };

    $scope.init();

    //Refresh Token
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
                    localStorage.setItem('token', result.data);
                    $scope.init();
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
                        } else {
                            location.replace('/');
                        }
                    });
                }
            })
            .catch((error) => console.log('error', error));
    };

    //logout in admin page
    $scope.logoutAdmin = function () {
        swal({
            title: 'Are you sure?',
            text: 'Do you want to sign out ?',
            icon: 'warning',
            buttons: true,
            dangerMode: true,
        }).then((willDelete) => {
            if (willDelete) {
                $scope.logoutTemp();
                swal('We will miss you so much !', {
                    icon: 'success',
                });
                setTimeout(function redirect() {
                    location.replace('/security/login/form');
                }, 3000);
            }
        });
    };

    $scope.reset = function () {
        $scope.form = {};
        document.getElementById('discountCode').disabled = false;
        $('#addProduct').addClass('d-none');
        $scope.editProductCate = false
    };

    $scope.editDiscount = function (discount) {
        var ul = document.getElementById('tags-ul');
        ul.innerHTML = '';
        clearTags();
        $scope.curDiscount = discount ? discount.discountCode : $scope.curDiscount;
        var token = 'Bearer ' + localStorage.getItem('token');
        $http({
            url: `/api/v2/management/discounts/${$scope.curDiscount}`,
            method: 'GET',
            timeout: 0,
            headers: {
                Authorization: token,
            },
        }).then((resp) => {
            if (resp.status == 200) {
                $scope.form = resp.data.data;
                $scope.discountProduct = resp.data.data.products;
                $scope.editProductCate = true;
                document.getElementById('day-start').value = new moment(new Date($scope.form.startTime)).format("yyyy-MM-DD HH:mm")
                document.getElementById('day-end').value = new moment(new Date($scope.form.endTime)).format("yyyy-MM-DD HH:mm")
                document.getElementById('discountCode').disabled = true;
                $('#addProduct').removeClass('d-none');
                $('#edit-discount').tab('show');
            }
        });
    };

    $scope.createDiscount = function () {
        const token = 'Bearer ' + localStorage.getItem('token');
        if (document.getElementById('day-start').value >= document.getElementById('day-end').value) {
            swal('Warning!', 'Please choose day end greater than day start!', 'warning');
            return;
        }
        $scope.form.startTime = document.getElementById('day-start').value.replace('T', ' ');
        $scope.form.endTime = document.getElementById('day-end').value.replace('T', ' ');
        $http({
            url: '/api/v2/management/discounts',
            method: 'POST',
            timeout: 0,
            headers: {
                Authorization: token,
                'Content-Type': 'application/json',
            },
            data: JSON.stringify($scope.form),
        })
            .then((resp) => {
                if (resp.status == 201) {
                    swal('Success!', resp.data.message, 'success');
                    $scope.reset();
                    $scope.initDiscount();
                } else {
                    swal('Error!', resp.data.message, 'error');
                }
            })
            .catch((error) => {
                swal('Error!', error.data.message, 'error');
            });
    };

    $scope.updateDiscount = function () {
        const token = 'Bearer ' + localStorage.getItem('token');
        if (document.getElementById('day-start').value >= document.getElementById('day-end').value) {
            swal('Warning!', 'Please choose day end greater than day start!', 'warning');
            return;
        }
        $scope.form.startTime = document.getElementById('day-start').value.replace('T', ' ');
        $scope.form.endTime = document.getElementById('day-end').value.replace('T', ' ');
        $http({
            url: '/api/v2/management/discounts',
            method: 'PUT',
            timeout: 0,
            headers: {
                Authorization: token,
                'Content-Type': 'application/json',
            },
            data: JSON.stringify($scope.form),
        })
            .then((resp) => {
                if (resp.status == 200) {
                    swal('Success!', resp.data.message, 'success');
                    $scope.initDiscount();
                    clearTags();
                    $scope.editProductCate = false;
                    $scope.reset();
                    $scope.addProductToDiscount();
                } else {
                    swal('Error!', resp.data.message, 'error');
                }
            })
            .catch((error) => {
                swal('Error!', error.data.message, 'error');
            });
    };

    $scope.addProductToDiscount = function () {
        var token = 'Bearer ' + localStorage.getItem('token');
        var ul = document.getElementById('tags-ul');
        var li = ul.getElementsByTagName('li');
        for (let i = 0; i < li.length; i++) {
            $scope.setProductCode.push(li[i].innerText);
        }
        $http({
            url: `/api/v2/management/discount/${$scope.curDiscount}`,
            method: 'PUT',
            timeout: 0,
            headers: {
                Authorization: token,
                'Content-Type': 'application/json',
            },
            data: JSON.stringify($scope.setProductCode),
        })
            .then((resp) => {
                if (resp.status == 200) {
                    ul.innerHTML = '';
                    clearTags();
                    $scope.reset();
                    $('#addProduct').addClass('d-none');
                } else {
                    swal('Error!', resp.data.message, 'error');
                }
            })
            .catch((error) => {
                console.log(error);
            });
    };

    $scope.deleteProduct = function (p) {
        var token = 'Bearer ' + localStorage.getItem('token');
        $http({
            url: `/api/v2/management/discount/${$scope.curDiscount}`,
            method: 'DELETE',
            timeout: 0,
            headers: {
                Authorization: token,
                'Content-Type': 'application/json',
            },
            data: JSON.stringify(p),
        })
            .then((resp) => {
                if (resp.status == 200) {
                    swal('Success!', resp.data.message, 'success');
                    $scope.editDiscount(false);
                } else {
                    swal('Error!', resp.data.message, 'error');
                }
            })
            .catch((error) => {
                console.log(error);
                swal('Error!', error.data.message, 'success');
            });
    };

    //pagination Discount
    $scope.pager = {
        pageDiscount: 0,
        sizeDiscount: 7,

        pageProduct: 0,
        sizeProduct: 7,

        get allProduct() {
            var start = this.pageProduct * this.sizeProduct;
            return $scope.allProduct
                ? $scope.allProduct.slice(start, start + this.sizeProduct)
                : ($scope.allProduct = false);
        },

        get count() {
            return Math.ceil((1.0 * $scope.allProduct.length) / this.sizeProduct);
        },

        firstProduct() {
            this.pageProduct = 0;
        },

        prevProduct() {
            this.pageProduct--;
            if (this.pageProduct < 0) {
                this.pageProduct = 0;
                return;
            }
        },

        nextProduct() {
            this.pageProduct++;
            if (this.pageProduct >= this.count) {
                this.pageProduct = this.count - 1;
                return;
            }
        },

        lastProduct() {
            this.pageProduct = this.count - 1;
        },

        get allDiscount() {
            var start = this.pageDiscount * this.sizeDiscount;
            return $scope.allDiscount
                ? $scope.allDiscount.slice(start, start + this.sizeDiscount)
                : ($scope.allDiscount = false);
        },

        get countDiscount() {
            return $scope.allDiscount ? Math.ceil((1.0 * $scope.allDiscount.length) / this.sizeDiscount) : 0;
        },

        firstDiscount() {
            this.pageDiscount = 0;
        },

        prevDiscount() {
            this.pageDiscount--;
            if (this.pageDiscount < 0) {
                this.pageDiscount = 0;
                return;
            }
        },

        nextDiscount() {
            this.pageDiscount++;
            if (this.pageDiscount >= this.count) {
                this.pageDiscount = this.count - 1;
                return;
            }
        },

        lastDiscount() {
            this.pageDiscount = this.count - 1;
        },
    };

    $scope.pagerDiscountProduct = {
        page: 0,
        size: 8,
        get discountProduct() {
            var start = this.page * this.size;
            return $scope.discountProduct.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil((1.0 * $scope.discountProduct.length) / this.size);
        },
        first() {
            this.page = 0;
        },
        previous() {
            this.page--;
            if (this.page < 0) {
                this.last();
            }
        },
        next() {
            this.page++;
            if (this.page >= this.count) {
                this.first();
            }
        },
        last() {
            this.page = this.count - 1;
        },
    };
});
