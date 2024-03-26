app.controller('category-ctrl', function ($scope, $http) {
    //config data before handle api
    $scope.firstNumberPage = 1;
    $scope.allCategory = [];
    $scope.allProduct = [];
    $scope.orders = [];
    $scope.profiles = [];
    $scope.form = {};
    $scope.setProductCode = [];
    $scope.cateProduct = [];
    $scope.curCate = '';

    //init page
    $scope.initialize = function () {
        $scope.initProfiles();
        $scope.initOrder();
    };

    //load data category
    $scope.initCate = function () {
        var token = 'Bearer ' + localStorage.getItem('token');
        $http({
            method: 'GET',
            url: '/api/v2/management/collections',
            headers: {
                Authorization: token,
            },
        })
            .then((resp) => {
                if (resp.status === 200) {
                    $scope.allCategory = resp.data.data;
                }
            })
            .catch((error) => {
                if (error.data.status === 401) {
                    $scope.refreshToken();
                }
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

    //load data profile
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

    //load list product
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
                if($scope.profiles.roles.name==="ROLE_MODERATOR"){
                    $scope.pageSummary = false;
                }else{
                    $scope.pageSummary = true;
                }
                if (resp.status === 401) {
                    $scope.refreshToken();
                }
            })
            .catch((error) => console.log(error));
    };

    //load data category and product
    $scope.init = function () {
        $scope.initProfiles();
        $scope.initCate();
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
                    $scope.logout();
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
                localStorage.removeItem('token');
                localStorage.removeItem('refreshToken');
                swal('We will miss you so much !', {
                    icon: 'success',
                });
                setTimeout(function redirect() {
                    location.replace('/security/login/form');
                }, 3000);
            } else {
                swal('Welcome back to ROSE ADMIN !');
            }
        });
    };

    //logout when token expirations
    $scope.logout = function () {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        location.replace('/security/login/form');
    };

    //edit form cate when click on table
    $scope.editCate = function (cate) {
        if (cate.categoryCode) {
            $scope.form = angular.copy(cate);
            $scope.form.setProductCode = '';
            if ($scope.form.status === 'Active') {
                document.getElementById('activeTrue').checked = true;
            } else {
                document.getElementById('activeFalse').checked = true;
            }
            $scope.curCate = cate.categoryCode;
        } else {
            $scope.curCate = cate;
        }
        var token = 'Bearer ' + localStorage.getItem('token');
        var api = '/api/v2/management/collections/' + $scope.curCate;
        $http({
            method: 'GET',
            url: api,
            headers: {
                Authorization: token,
            },
        }).then((resp) => {
            $scope.cateProduct = resp.data.data.products;
        });
        $scope.editProductCate = true;
        $('#edit-cate').tab('show');
    };

    //reset form when click
    $scope.reset = function () {
        $scope.form = {};
    };

    //Create Category
    $scope.createCate = function () {
        var token = 'Bearer ' + localStorage.getItem('token');
        var ul = document.getElementById('tags-ul');
        var li = ul.getElementsByTagName('li');
        for (let i = 0; i < li.length; i++) {
            $scope.setProductCode.push(li[i].innerText);
        }
        $scope.form.setProductCode = $scope.setProductCode;

        swal({
            title: 'Do you want to create?',
            icon: 'warning',
            buttons: true,
            dangerMode: true,
        }).then((isOke) => {
            if (isOke) {
                $http({
                    url: '/api/v2/management/collections',
                    method: 'POST',
                    headers: {
                        Authorization: token,
                    },
                    data: JSON.stringify($scope.form),
                })
                    .then((resp) => {
                        if (resp.status == 201) {
                            $scope.form = {};
                            $scope.cateProduct = [];
                            $scope.editProductCate = false;
                            clearTags();
                            ul.innerHTML = '';
                            swal('Success!', resp.data.message, 'success');
                            $scope.initCate();
                        } else {
                            swal('Warning!', resp.data.message, 'warning');
                        }
                    })
                    .catch((error) => {
                        console.log(error);
                        swal('Error!', 'Something went wrong', 'error');
                    });
            }
        });
    };

    //Update Category
    $scope.updateCate = function () {
        var token = 'Bearer ' + localStorage.getItem('token');
        swal({
            title: 'Do you want to update ?',
            icon: 'warning',
            buttons: true,
            dangerMode: true,
        }).then((isOke) => {
            if (isOke) {
                $http({
                    url: '/api/v2/management/collections',
                    method: 'PUT',
                    headers: {
                        Authorization: token,
                    },
                    data: JSON.stringify($scope.form),
                })
                    .then((resp) => {
                        console.log(resp);
                        if (resp.status == 200) {
                            swal('Success!', resp.data.message, 'success');
                            var ul = document.getElementById('tags-ul');
                            var li = ul.getElementsByTagName('li');
                            for (let i = 0; i < li.length; i++) {
                                $scope.setProductCode.push(li[i].innerText);
                            }
                            var blank = [];
                            if ($scope.setProductCode[0] != blank[0]) {
                                $scope.form.setProductCode = $scope.setProductCode;
                                const updateProductCateApi =
                                    '/api/v2/management/collections/' + $scope.form.categoryCode;
                                $http({
                                    url: updateProductCateApi,
                                    method: 'PUT',
                                    headers: {
                                        Authorization: token,
                                    },
                                    data: JSON.stringify($scope.setProductCode),
                                })
                                    .then((resp) => {
                                        if (resp.status == 200) {
                                            swal('Success!', resp.data.message, 'success');
                                            $scope.initCate();
                                            $scope.editCate($scope.curCate);
                                            clearTags();
                                            ul.innerHTML = '';
                                        } else {
                                            swal('Warning!', resp.data.message, 'warning');
                                        }
                                    })
                                    .catch((error) => {
                                        console.log(error);
                                    });
                            }
                        }
                    })
                    .catch((error) => {
                        if (error.data.status === '400 BAD_REQUEST') {
                            swal('Error!', error.data.message, 'error');
                            console.log(error.data.message);
                        }
                        console.log(error);
                    });
            }
        });
    };

    //delete product to list when click btn edit to table
    $scope.deleteProduct = function (productCode) {
        swal({
            title: 'Are you sure?',
            text: 'You will not be able to recover this!',
            icon: 'warning',
            buttons: true,
            dangerMode: true,
        }).then((isOke) => {
            if (isOke) {
                var token = 'Bearer ' + localStorage.getItem('token');
                var api = '/api/v2/management/collections/' + $scope.curCate;
                var data = [];
                data.push(productCode);
                $http({
                    url: api,
                    method: 'DELETE',
                    headers: {
                        Authorization: token,
                        'Content-Type': 'application/json',
                    },
                    data: JSON.stringify(data),
                })
                    .then((resp) => {
                        if (resp.data.status === 'OK') {
                            swal('Success!', resp.data.message, 'success');
                            $scope.initCate();
                            $scope.editCate($scope.curCate);
                        } else {
                            swal('Error!', resp.data.message, 'error');
                        }
                    })
                    .catch((error) => {
                        console.log(error);
                        swal('Error!', 'Something went wrong', 'error');
                    });
            } else {
                swal('Your product is safe!');
            }
        });
    };

    //pagination product
    $scope.pager = {
        pageCate: 0,
        sizeCate: 7,

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

        get allCate() {
            var start = this.pageCate * this.sizeCate;
            return $scope.allCategory.slice(start, start + this.sizeCate);
        },

        get countCate() {
            return Math.ceil((1.0 * $scope.allCategory.length) / this.sizeCate) > 0
                ? Math.ceil((1.0 * $scope.allCategory.length) / this.sizeCate)
                : 0;
        },

        firstCate() {
            this.pageCate = 0;
        },

        prevCate() {
            this.pageCate--;
            if (this.pageCate < 0) {
                this.pageCate = 0;
                return;
            }
        },

        nextCate() {
            this.pageCate++;
            if (this.pageCate >= this.countCate) {
                this.pageCate = this.countCate - 1;
                return;
            }
        },

        lastCate() {
            this.pageCate = this.countCate - 1;
        },
    };

    //pagination cate-product
    $scope.pagercateProduct = {
        page: 0,
        size: 8,
        get cateProduct() {
            var start = this.page * this.size;
            return $scope.cateProduct.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil((1.0 * $scope.cateProduct.length) / this.size);
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
    $scope.initialize();
});
