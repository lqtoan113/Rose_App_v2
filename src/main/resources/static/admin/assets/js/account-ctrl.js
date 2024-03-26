app.controller('account-ctrl', function ($scope, $http) {
    //list api account
    const API_List = {
        API_LOAD_DATA_ACCOUNT: 'http://localhost:8080/api/v2/management/accounts',
        API_UPLOAD_IMAGE: 'http://localhost:8080/api/v2/me',
        API_ADD_ACCOUNT: 'http://localhost:8080/api/v2/management/accounts',
        API_UPDATE_ACCOUNT: 'http://localhost:8080/api/v2/management/accounts',
        API_DELETE_ACCOUNT: 'http://localhost:8080/api/v2/management/accounts',
    };

    //config data before handle api
    $scope.accounts = [];
    $scope.roles = ['ROLE_USER', 'ROLE_ADMIN', 'ROLE_MODERATOR'];
    $scope.role = ['ROLE_USER'];
    $scope.profiles = [];
    $scope.orderList = [];
    $scope.orders = [];
    $scope.payment = [];
    $scope.form = {};
    $scope.accountsActive = {};
    $scope.userActive = 0;
    $scope.totalMaleUser = 0;
    $scope.totalFemaleUser = 0;

    //init page
    $scope.initialize = function () {
        //load message table no result
        $scope.message = 'No result';
        //load all accounts
        $scope.init();
        //load all orders
        $scope.initOrder();
        $scope.form.photo =
            'https://storage.googleapis.com/agile-being-318113.appspot.com/d9d6c455-d16b-49fe-860e-fbf0c8c946bb';
    };

    //load all accounts
    $scope.init = function () {
        const token = 'Bearer ' + localStorage.getItem('token');
        //load data account
        $http({
            method: 'GET',
            url: API_List.API_LOAD_DATA_ACCOUNT,
            headers: {
                Authorization: token,
            },
        })
            .then((resp) => {
                $scope.accounts = resp.data.data;
                $scope.accountsActive = resp.data;
            })
            .catch((error) => {
                if (error.data.status === 401) {
                    $scope.refreshToken();
                }
            });

        //load summary userActive
        $http({
            method: 'GET',
            url: '/api/v2/management/totalUserWasActive',
            headers: {
                Authorization: token,
            },
        })
            .then((resp) => {
                $scope.userActive = resp.data;
                console.log(resp.data);
                if (resp.status === 401) {
                    $scope.refreshToken();
                }
            })
            .catch((error) => console.log(error));

        //load summary totalMaleUser
        $http({
            method: 'GET',
            url: '/api/v2/management/totalMaleUser',
            headers: {
                Authorization: token,
            },
        })
            .then((resp) => {
                $scope.totalMaleUser = resp.data;
                console.log(resp.data);
                if (resp.status === 401) {
                    $scope.refreshToken();
                }
            })
            .catch((error) => console.log(error));

        //load summary totalFemaleUser
        $http({
            method: 'GET',
            url: '/api/v2/totalFemaleUser',
            headers: {
                Authorization: token,
            },
        })
            .then((resp) => {
                $scope.totalFemaleUser = resp.data;
                console.log(resp.data);
                if (resp.status === 401) {
                    $scope.refreshToken();
                }
            })
            .catch((error) => console.log(error));
        //load profiles
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

    //load status order
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
    //refresh token
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
                    $scope.initialize();
                } else {
                    $scope.logout();
                    swal({
                        title: 'Do you want to login again?',
                        text: 'Login version has expired!',
                        icon: 'warning',
                        buttons: true,
                        dangerMode: true,
                    }).then((isOk) => {
                        if (isOk) {
                            location.replace('/security/login/form');
                        } else {
                            location.replace('/shop');
                        }
                    });
                }
            })
            .catch((error) => console.error('error', error));
    };

    //logout to form login
    $scope.logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
    };

    $scope.logoutAdmin = () => {
        swal({
            title: 'Are you sure?',
            text: 'Do you want to sign out ?',
            icon: 'warning',
            buttons: true,
            dangerMode: true,
        }).then((isOk) => {
            if (isOk) {
                $scope.logout();
                swal('We will miss you so much !', {
                    icon: 'success',
                });
                setTimeout(function redirect() {
                    location.replace('/security/login/form');
                }, 2000);
            }
        });
    };

    //show data form
    $scope.editAccount = (account) => {
        $scope.orderList = [];
        $scope.form = angular.copy(account);
        $scope.role = account.roles.map(({ name }) => name);
        $scope.username = account.username;

        const token = 'Bearer ' + localStorage.getItem('token');
        const uri = `/api/v2/management/accounts/${$scope.username}/history-payment`;
        $http({
            method: 'GET',
            url: uri,
            headers: {
                Authorization: token,
            },
        })
            .then((resp) => {
                if (resp.data.status === 'OK') {
                    if (resp.data.data === null) {
                        $scope.message = 'No result';
                        $scope.orderList = [];
                    } else {
                        $scope.orderList = resp.data.data;
                        $scope.message = '';
                    }
                }
            })
            .catch((error) => console.error(error));

        $http({
            method: 'GET',
            url: `/api/v2/management/accounts/${$scope.username}`,
            headers: {
                Authorization: token,
            },
        })
            .then((resp) => {
                if (resp.data.status === 'OK') {
                    $scope.payment = resp.data.data.payments;
                    if ($scope.payment.length === 0) {
                        $scope.message = 'No result';
                    } else {
                        $scope.message = '';
                    }
                }
                if (resp.status === 401) {
                    $scope.refreshToken();
                }
            })
            .catch((error) => console.log(error));

        $('#edit-account').tab('show');
        $('#username').prop('disabled', true);
    };

    //Create Account
    const input = document.getElementById('image');
    input.addEventListener('change', (event) => {
        const target = event.target;
        if (target.files && target.files[0]) {
            const maxAllowedSize = 5 * 1024 * 1024;
            $scope.createAccount = function () {
                const myHeaders = new Headers();
                const token = 'Bearer ' + localStorage.getItem('token');
                myHeaders.append('Authorization', token);
                myHeaders.append('Content-Type', 'application/json');
                const data = JSON.stringify({
                    username: $scope.form.username,
                    password: $scope.form.password,
                    email: $scope.form.email,
                    phone: $scope.form.phone,
                    fullName: $scope.form.fullName,
                    photo: $scope.form.photo,
                    gender: $scope.form.gender,
                    address: $scope.form.address,
                    active: $scope.form.active,
                    balance: 0,
                    roles: $scope.role,
                });
                const requestOptions = {
                    method: 'POST',
                    headers: myHeaders,
                    body: data,
                    redirect: 'follow',
                };
                swal({
                    title: 'Do you want to create this account ?',
                    icon: 'warning',
                    buttons: true,
                    dangerMode: true,
                }).then((isOk) => {
                    if (isOk) {
                        fetch(API_List.API_ADD_ACCOUNT, requestOptions)
                            .then((response) => response.json())
                            .then((result) => {
                                if (result.status === 'CREATED') {
                                    swal('Create account successfully !', 'You clicked the button!', 'success');
                                    $scope.init();
                                    $scope.reset();
                                } else if (result.status === '400 BAD_REQUEST') {
                                    swal({
                                        title: result.message,
                                    });
                                }
                            })
                            .catch((error) => console.error('error', error));
                    }
                });
            };
            if (target.files[0].size > maxAllowedSize) {
                target.value = '';
                alert('No selected image more 5mb');
            }
        }
    });

    //Update account
    $scope.updateAccount = function () {
        const myHeaders = new Headers();
        const token = 'Bearer ' + localStorage.getItem('token');
        myHeaders.append('Authorization', token);
        myHeaders.append('Content-Type', 'application/json');
        const data = JSON.stringify({
            username: $scope.form.username,
            password: $scope.form.password,
            email: $scope.form.email,
            phone: $scope.form.phone,
            fullName: $scope.form.fullName,
            photo: $scope.form.photo,
            gender: $scope.form.gender,
            address: $scope.form.address,
            active: $scope.form.active,
            balance: 0,
            roles: $scope.role,
        });
        const requestOptions = {
            method: 'PUT',
            headers: myHeaders,
            body: data,
            redirect: 'follow',
        };

        swal({
            title: 'Do you want to update ?',
            icon: 'warning',
            buttons: true,
            dangerMode: true,
        }).then((isOk) => {
            if (isOk) {
                if ($scope.role.length === 0) {
                    swal('Please choose role !');
                } else {
                    fetch(API_List.API_UPDATE_ACCOUNT, requestOptions)
                        .then((response) => response.json())
                        .then((result) => {
                            if (result.status === 'OK') {
                                swal(result.message, 'You clicked the button!', 'success');
                                $scope.reset();
                                $scope.init();
                            }
                            else if(result.status === "400 BAD_REQUEST"){
                                swal(result.message, "You clicked the button!", "warning");
                            }else if(result.status === "NOT_ACCEPTABLE"){
                                swal(result.message, "You clicked the button!", "warning");
                            }
                            console.log(result);
                        })
                        .catch((error) => console.log('error', error));
                }
            }
        });
    };

    //Banned Account
    $scope.deleteAccount = function (username) {
        const myHeaders = new Headers();
        const token = 'Bearer ' + localStorage.getItem('token');
        myHeaders.append('Authorization', token);

        var requestOptions = {
            method: 'DELETE',
            headers: myHeaders,
            redirect: 'follow',
        };

        swal({
            title: 'Do you want to block ?',
            icon: 'warning',
            buttons: true,
            dangerMode: true,
        }).then((willDelete) => {
            if (willDelete) {
                fetch(
                    `/api/v2/management/accounts?username=` + username + `&description=` + $scope.description,
                    requestOptions,
                )
                    .then((response) => response.json())
                    .then((result) => {
                        if (result.status === 'OK') {
                            swal(result.message, 'You clicked the button!', 'success');
                            $scope.init();
                            $scope.reset();
                        } else if (result.status === '400 BAD_REQUEST') {
                            swal(result.message, 'You clicked the button!', 'warning');
                        } else if (result.status === 'NOT_ACCEPTABLE') {
                            swal(result.message, 'You clicked the button!', 'warning');
                        }
                    })
                    .catch((error) => console.log('error', error));
            }
        });
    };

    //reset form
    $scope.reset = function () {
        $scope.form = {
            photo: 'https://storage.googleapis.com/agile-being-318113.appspot.com/d9d6c455-d16b-49fe-860e-fbf0c8c946bb',
            username: '',
            password: '',
            fullName: '',
            phone: '',
            address: '',
            email: '',
        };
        $scope.orderList = [];
        $scope.payment = [];
        $('#username').prop('disabled', false);
    };

    //Upload Image
    $scope.imageChanged = function (files) {
        const myHeaders = new Headers();
        const token = 'Bearer ' + localStorage.getItem('token');
        myHeaders.append('Authorization', token);
        const data = new FormData();
        data.append('file', files[0]);
        const requestOptions = {
            method: 'POST',
            headers: myHeaders,
            'Content-Type': 'application/x-www-form-urlencoded',
            Accept: 'application/json',
            body: data,
            redirect: 'follow',
        };

        fetch(API_List.API_UPLOAD_IMAGE, requestOptions)
            .then((response) => response.json())
            .then((result) => {
                $scope.form.photo = result.data;
                document.getElementById('uploadedAvatar').src = result.data;
            })
            .catch((error) => console.log('error', error));
    };
    //role configuration
    $scope.toggleSelection = function toggleSelection(role) {
        var idx = $scope.role.indexOf(role);
        // Is currently selected
        if (idx > -1) {
            $scope.role.splice(idx, 1);
        }
        // Is newly selected
        else {
            $scope.role.push(role);
        }
    };

    //Sort colum
    // column to sort
    $scope.column = 'username';

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

    //pagination table account
    $scope.pager = {
        page: 0,
        size: 8,
        get accounts() {
            var start = this.page * this.size;
            return $scope.accounts.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil((1.0 * $scope.accounts.length) / this.size);
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

    //pagination table history orders
    $scope.pagerHistoryOrder = {
        page: 0,
        size: 8,

        get orderList() {
            var start = this.page * this.size;
            return $scope.orderList.length == 0 ? [] : $scope.orderList.slice(start, start + this.size);
        },
        get count() {
            let a = Math.ceil((1.0 * $scope.orderList.length) / this.size);
            return a > 0 ? a : 0;
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

    //pagination table history payment
    $scope.pagerHistoryPayment = {
        page: 0,
        size: 8,
        get payment() {
            var start = this.page * this.size;
            return $scope.payment.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil((1.0 * $scope.payment.length) / this.size);
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

    //load all function
    $scope.initialize();
});
