app.controller('order-ctrl', function ($scope,$http) {
    //list api order
    const API_List = {
        API_LOAD_DATA_ORDER: 'http://localhost:8080/api/v2/management/orders',
    };

    //config data before handle api
    $scope.orders = [];
    $scope.profiles =[];
    $scope.orderstatus="";
    $scope.startDate= new Date('2022-12-01T00:00:00');
    $scope.endDate= new Date();

    //load page
    $scope.initialize = function () {
        //load all orders
        $scope.init();
    };

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
            .then(response => response.json())
            .then(result => {
                if (result.status === 'OK') {
                    localStorage.removeItem('token');
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
                    }).then((isOke) => {
                        if (isOke) {
                            location.replace('/security/login/form');
                        }
                    });
                }
            }).catch(error => console.log('error', error));
    };

    //load all orders
    $scope.init = function () {
        var token = 'Bearer ' + localStorage.getItem('token');
        $http({
            method: 'GET',
            url: API_List.API_LOAD_DATA_ORDER,
            headers: {
                Authorization: token,
            },
        })
            .then((resp) => {
                if (resp.data.status === 'OK') {
                    $scope.orders = resp.data.data;
                    if($scope.orders.length === 0){
                        $scope.message = "No result"
                    }else {
                        $scope.message = ""
                    }
                }

            })
            .catch((error) => {
                if(error.data.status === 401){
                    $scope.refreshToken();
                }
            });

        //load profiles
        $http({
            method: 'GET',
            url: 'http://localhost:8080/api/v2/me' ,
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

        //load order by status
            $http({
                method: 'GET',
                url: 'http://localhost:8080/api/v2/management/todayOrderByStatus',
                headers: {
                    Authorization: token,
                },
            }).then((resp) => {
                if (resp.data.status === 'OK') {
                    $scope.orderByStatus = resp.data.data;
                }

            }).catch((error) => {
                if(error.data.status === 401){
                    $scope.refreshToken();
                }
            });
    };


    //view order when click
    $scope.viewOrderDetail = function (id) {
        var token = 'Bearer ' + localStorage.getItem('token');
        const api = '/api/v2/management/orders/' + id;
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
                    $scope.orderDetailList = resp.data.data.orderDetailList
                    debugger
                    if ($scope.orderDetail.status === 'PENDING_ACCEPT' ) {
                        $scope.orderDetailCancelButton = true;
                        $scope.orderDetailReceivedButton = true;
                        $scope.orderDetailShippingdButton = false;
                    } else if($scope.orderDetail.status === 'ACCEPTED'){
                        $scope.orderDetailCancelButton = true;
                        $scope.orderDetailReceivedButton = false;
                        $scope.orderDetailShippingdButton = true;
                    }
                    if ($scope.orderDetail.status === 'SHIPPING') {
                        $scope.orderDetailCancelButton = false;
                        $scope.orderDetailReceivedButton = false;
                        $scope.orderDetailShippingdButton = false;
                    }
                    console.log(resp.data.data)
                } else {
                    console.log(resp.data);
                }
            })
            .catch((error) => console.log(error));
    };

    //logout in admin page
    $scope.logoutAdmin = function () {
        swal({
            title: "Are you sure?",
            text: "Do you want to sign out ?",
            icon: "warning",
            buttons: true,
            dangerMode: true,
        })
            .then((willDelete) => {
                if (willDelete) {
                    localStorage.removeItem('token');
                    localStorage.removeItem('refreshToken');
                    swal("We will miss you so much !", {
                        icon: "success",
                    });
                    setTimeout(function redirect() {
                        location.replace('/security/login/form');
                    }, 2000);

                } else {
                    swal("Welcome back to ROSE ADMIN !");
                }
            });


    };

    //Update status order
    $scope.updateOrder = function (orderDetail){
                const token = 'Bearer ' + localStorage.getItem('token');
                $http({
                    method: 'PUT',
                    url: `/api/v2/management/orders/${orderDetail.id}`,
                    headers: {
                        Authorization: token,
                    },
                }).then(resp => {
                if (resp.data.status === 'OK') {
                    swal({
                        title: resp.data.message,
                        icon: "success",
                    });
                    if(resp.data.data.status ==='ACCEPTED'){
                        $scope.orderDetailCancelButton = true;
                        $scope.orderDetailReceivedButton = false;
                        $scope.orderDetailShippingdButton = true;
                    }else if(resp.data.data.status ==='SHIPPING'){
                        $scope.orderDetailCancelButton = false;
                        $scope.orderDetailReceivedButton = false;
                        $scope.orderDetailShippingdButton = false;
                    }
                    $scope.viewOrderDetail(orderDetail.id);
                    $scope.initialize();
                }
            }).catch(error => {
                if (error.data.status === 'NOT_ACCEPTABLE') {
                    swal({
                        title: error.data.message,
                        icon: "warning",
                    });
                }
                console.log(error)
            });
    }

    //Cancel order
    $scope.cancelOrder = function (id) {
        swal({
            title: 'Are you sure?',
            text: 'You will not be able to recover this order!',
            icon: 'warning',
            buttons: true,
            dangerMode: true,
        }).then((isOke) => {
            if (isOke) {
                var token = 'Bearer ' + localStorage.getItem('token');
                var reasonId = document.querySelector('input[name="reason"]:checked').value;
                var reasonValue = document.getElementById(reasonId).innerText;
                var form = new FormData();
                form.append('reason', reasonValue);
                const api = '/api/v2/management/orders/' + id;
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
                }).then(resp => {
                    if (resp.data.status === 'OK') {
                        swal('Success!', resp.data.message, 'success');
                        if(resp.data.data.status ==='CANCELLED'){
                            $scope.orderDetailCancelButton = false;
                            $scope.orderDetailReceivedButton = false;
                            $scope.orderDetailShippingdButton = false;
                        }
                        $scope.viewOrderDetail(id);
                        $scope.init();
                    }


                    else {
                        swal('Error!', 'System is malfunctioning!', 'error');
                    }
                });
            }
        });
    };

    //logout when token expirations
    $scope.logoutTemp = function () {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        $scope.profile = false;
    };

    //reset input sort
    $scope.resetSortByDate = () =>{
        $scope.startDate= new Date('2022-12-01T00:00:00');
        $scope.endDate= new Date();
    }


    //Sort colum
    // column to sort
    $scope.column = 'id';

    // sort ordering (Ascending or Descending). Set true for desending
    $scope.reverse = false;

    // called on header click
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

    // remove and change class
    $scope.sortClass = function (col) {
        if ($scope.column == col) {
            if ($scope.reverse) {
                return 'arrow-down';
            } else {
                return 'arrow-up';
            }
        } else {
            return '';
        }
    };

    //Pagination order
    $scope.pagerOrder = {
        page: 0,
        size: 8,
        get orders() {
            var start = this.page * this.size;
            return $scope.orders.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil((1.0 * $scope.orders.length) / this.size);
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

app.filter("myfilter", function($filter) {
    return function(items, from, to, dateField) {
        startDate = moment(from);
        endDate = moment(to);
        return $filter('filter')(items, function(elem) {
            var date = moment(elem[dateField]);
            return date >= startDate && date <= endDate;
        });

    };
});
