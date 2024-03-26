app.controller('color-ctrl', function ($scope, $http,$filter){
    const API_List = {
        API_LOAD_DATA_COLOR: 'http://localhost:8080/api/v2/management/colors',
        API_ADD_COLOR: 'http://localhost:8080/api/v2/management/colors',
        API_UPDATE_COLOR: 'http://localhost:8080/api/v2/management/colors',
        API_DELETE_COLOR: 'http://localhost:8080/api/v2/management/colors',
        API_LOAD_DATA_SIZE: 'http://localhost:8080/api/v2/management/sizes',
        API_ADD_SIZE: 'http://localhost:8080/api/v2/management/sizes',
        API_UPDATE_SIZE: 'http://localhost:8080/api/v2/management/sizes',
        API_DELETE_SIZE: 'http://localhost:8080/api/v2/management/sizes',
    };

    //config data before handle api
    $scope.colors = [];
    $scope.form = {};
    $scope.sizes = [];
    $scope.profiles=[];
    $scope.orders=[];

    //load page
    $scope.initialize = function () {
        //load all colors
        $scope.init();
        $scope.initOrder();
        $scope.reset();
        $scope.form.available =true;
    };

    //load all Size and Color
    $scope.init = function () {
            const myHeaders = new Headers();
            const token = 'Bearer ' + localStorage.getItem('token');
            myHeaders.append('Authorization', token);

            $http({
                method: 'GET',
                url: API_List.API_LOAD_DATA_SIZE,
                headers: {
                    Authorization: token,
                },
            }).then(resp => {

                if (resp.data.status === 'OK') {
                    $scope.sizes = resp.data.data;
                }
            }).catch(error => {
                if(error.data.status === 401){
                    $scope.refreshToken();
                    location.replace('http://localhost:8080/admin/color');
                }
            });

            $http({
                method: 'GET',
                url: API_List.API_LOAD_DATA_COLOR,
                headers: {
                    Authorization: token,
                },
            }).then(resp => {
                if (resp.data.status === 'OK') {
                    $scope.colors = resp.data.data;

                }
            }).catch(error=> {
                if(error.data.status === 401){
                    $scope.refreshToken();
                    location.replace('http://localhost:8080/admin/color');
                }
            });

            //load profiles
            $http({
                method: 'GET',
                url: 'http://localhost:8080/api/v2/me' ,
                headers: {
                    Authorization: token,
                },
            }).then(resp => {
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
            .catch(error => console.log(error));
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
                    $scope.initialize();
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
                        }
                    });
                }
            }).catch(error => {
                console.log(error);
                $scope.refreshToken();
            });
    };

    //logout when token expirations
    $scope.logout = function () {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
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
                    }, 3000);

                } else {
                    swal("Welcome back to ROSE ADMIN !");
                }
            });


    };

    //load value color
    $scope.style = function (value) {
        return { 'background-color': value };
    };

    //edit data color on form
    $scope.editColor = function (color) {
        $scope.form = angular.copy(color);
    };

    //edit data size on form
    $scope.editSize = function (size) {
        $scope.form = angular.copy(size);
    };

    //Create Color
    $scope.createColor = function () {
        const myHeaders = new Headers();
        const token = 'Bearer ' + localStorage.getItem('token');
        myHeaders.append('Authorization', token);
        myHeaders.append('Content-Type', 'application/json');
        const data = JSON.stringify(angular.copy($scope.form));

        const requestOptions = {
            method: 'POST',
            headers: myHeaders,
            body: data,
            redirect: 'follow',
        };
   
        
        swal({
            title: 'Do you want to create ?',
            icon: 'warning',
            buttons: true,
            dangerMode: true,
        }).then((isOke) => {
            if (isOke) {
                fetch(API_List.API_ADD_COLOR, requestOptions)
                .then(response => response.json())
                .then(result => {
                    if (result.status === 'CREATED') {
                        swal(result.message, "You clicked the button!", "success");
                        $scope.init();
                        $scope.reset();
                    } else if (result.status === 'CONFLICT') {
                        swal(result.message, "You clicked the button!", "warning");
                    } else if (result.status === 'BAD_REQUEST') {
                       swal(result.message, "You clicked the button!", "warning");
                    }else if (result.status === '400 BAD_REQUEST') {
                        swal(result.message, "You clicked the button!", "warning");
                    }
                }).catch(error => console.log('error', error));
            }
        });


      
    };

    //Update color
    $scope.updateColor = function () {
        const myHeaders = new Headers();
        const token = 'Bearer ' + localStorage.getItem('token');
        myHeaders.append('Authorization', token);
        myHeaders.append('Content-Type', 'application/json');
        const data = JSON.stringify({
            id: $scope.form.id,
            colorName: $scope.form.colorName,
            colorValue: $scope.form.colorValue,
            available: $scope.form.available,
            createDate: $scope.form.createDate,
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
        }).then((isOke) => {
            if (isOke) {
                fetch(API_List.API_UPDATE_COLOR, requestOptions)
                .then(response => response.json())
                .then(result => {
                    if (result.status === 'OK') {
                        swal(result.message, "You clicked the button!", "success");
                        $scope.init();
                        $scope.reset();
                    }else if (result.status === '400 BAD_REQUEST') {
                        swal({
                            title: result.message,
                        });
                    }else if(result.status === "BAD_REQUEST"){
                        swal(result.message, "You clicked the button!", "warning");
                    }
                    console.log(result);
                }).catch(error => console.log('error', error));
            }
        });

        
    };

    //Delete Color
    $scope.deleteColor = function () {
        const myHeaders = new Headers();
        const token = 'Bearer ' + localStorage.getItem('token');
        myHeaders.append('Authorization', token);

        var formdata = new FormData();
        formdata.append('colorValue', $scope.form.colorValue);
        var requestOptions = {
            method: 'DELETE',
            headers: myHeaders,
            body: formdata,
            redirect: 'follow',
        };

        fetch('http://localhost:8080/api/v2/management/colors' + $scope.form.colorValue, requestOptions)
            .then((response) => response.json())
            .then((result) => {
                if (result.status === 'OK') {
                    swal(result.message, "You clicked the button!", "success");
                    $scope.init();
                    $scope.reset();
                } else if (result.status === '404 NOT_FOUND') {
                    swal(result.message, "You clicked the button!", "warning");
                }
            })
            .catch((error) => console.log('error', error));
    };

    //reset form
    $scope.reset = function () {
        $scope.form = {
            colorName: '',
            colorValue: '',
            available: true,
            createDate: new Date(),
        };
    };

    //Create size
    $scope.createSize = function () {
        const myHeaders = new Headers();
        const token = 'Bearer ' + localStorage.getItem('token');
        myHeaders.append('Authorization', token);
        myHeaders.append('Content-Type', 'application/json');
        const data = JSON.stringify(angular.copy($scope.form));

        const requestOptions = {
            method: 'POST',
            headers: myHeaders,
            body: data,
            redirect: 'follow',
        };

        swal({
            title: 'Do you want to create ?',
            icon: 'warning',
            buttons: true,
            dangerMode: true,
        }).then((isOke) => {
            if (isOke) {
                fetch(API_List.API_ADD_SIZE, requestOptions)
                .then(response => response.json())
                .then((result) => {
                    debugger
                    if (result.status === 'CREATED') {
                        swal(result.message, "You clicked the button!", "success");
                        $scope.init();
                        $scope.reset();
                    }else if(result.status === 'CONFLICT') {
                        swal(result.message, "You clicked the button!", "warning");
                    }else{
                        swal(result.message, "You clicked the button!", "warning");
                    }
                }).catch(error => console.log('error', error));
            }
        });

        
    };

    //Update size
    $scope.updateSize = function () {
        const myHeaders = new Headers();
        const token = 'Bearer ' + localStorage.getItem('token');
        myHeaders.append('Authorization', token);
        myHeaders.append('Content-Type', 'application/json');
        const data = JSON.stringify({
            id: $scope.form.id,
            sizeValue: $scope.form.sizeValue,
            available: $scope.form.available,
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
        }).then((isOke) => {
            if (isOke) {
                fetch(API_List.API_UPDATE_SIZE, requestOptions)
                .then(response => response.json())
                .then(result => {
                    if (result.status === 'OK') {
                        swal(result.message, "You clicked the button!", "success");
                        $scope.init();
                        $scope.reset();
                    } else if (result.status === 'BAD_REQUEST') {
                        swal(result.message, "You clicked the button!", "warning");
                    }else if (result.status === '400 BAD_REQUEST') {
                        swal(result.message, "You clicked the button!", "warning");
                    }
                    console.log(result);
                }).catch(error => console.log('error', error));
            }
        });

       
    };

    //Delete Size
    $scope.deleteSize = function () {
        const myHeaders = new Headers();
        const token = 'Bearer ' + localStorage.getItem('token');
        myHeaders.append('Authorization', token);

        var formdata = new FormData();

        var requestOptions = {
            method: 'DELETE',
            headers: myHeaders,
            body: formdata,
            redirect: 'follow',
        };

        swal({
            title: 'Do you want to delete ?',
            icon: 'warning',
            buttons: true,
            dangerMode: true,
        }).then((isOke) => {
            if (isOke) {
                fetch('http://localhost:8080/api/v2/management/sizes?sizeValue=' + $scope.form.sizeValue, requestOptions)
                .then(response => response.json())
                .then(result => {
                    if (result.status === 'OK') {
                        swal(result.message, "You clicked the button!", "success");
                        $scope.init();
                        $scope.reset();
                    } else if (result.status === '400 BAD_REQUEST') {
                        swal(result.message, "You clicked the button!", "warning");
                    }
                }).catch(error => console.log('error', error));
            }
        });

        
    };

    //reset form size
    $scope.resetSize = function () {
        $scope.form = {
            sizeValue: '',
            available: true,
        };
    };

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

    //pagination color
    $scope.pagerColor = {
        page: 0,
        size: 4,
        get colors() {
            var start = this.page * this.size;
            return $scope.colors.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil((1.0 * $scope.colors.length) / this.size);
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

    //pagination size
    $scope.pagerSize = {
        page: 0,
        size: 4,
        get sizes() {
            var start = this.page * this.size;
            return $scope.sizes.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil((1.0 * $scope.sizes.length) / this.size);
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
