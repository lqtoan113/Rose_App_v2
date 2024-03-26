app.controller('size-ctrl', function ($scope, $http) {
    const API_List = {
        API_LOAD_DATA_SIZE: 'http://localhost:8080/api/v2/management/sizes',
        API_ADD_SIZE: 'http://localhost:8080/api/v2/management/sizes',
        API_UPDATE_SIZE: 'http://localhost:8080/api/v2/management/sizes',
        API_DELETE_SIZE: 'http://localhost:8080/api/v2/management/sizes',
    };

    $scope.sizes = [];
    $scope.form = {};

    $scope.initialize = function () {
        //load all accounts
        $scope.init();
        $scope.reset();
        $scope.form.available = true;
    };

    //load all accounts
    $scope.init = function () {
        const myHeaders = new Headers();
        const token = 'Bearer ' + localStorage.getItem('token');
        myHeaders.append('Authorization', token);
        const requestOptions = {
            method: 'GET',
            headers: myHeaders,
            redirect: 'follow',
        };

        fetch(API_List.API_LOAD_DATA_SIZE, requestOptions)
            .then((response) => response.json())
            .then((result) => {
                $scope.sizes = result.data;
                console.log(result.data);
            })
            .catch((error) => {
                console.log('error', error);
                $scope.refreshToken();
            });
    };

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
            })
            .catch((error) => console.log('error', error));
    };

    $scope.logout = function () {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
    };

    //show data form
    $scope.edit = function (size) {
        $scope.form = angular.copy(size);
        $('#edit-size').tab('show');
    };

    //Create size
    $scope.create = function () {
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

        fetch(API_List.API_ADD_SIZE, requestOptions)
            .then((response) => response.json())
            .then((result) => {
                if (result.status === 'CREATED') {
                    swal("Create Size successfully!", "You clicked the button!", "success");
                    setTimeout(function redirect() {
                        window.location.replace('http://localhost:8080/admin/size');
                    }, 1000);
                } else if (result.status === '400 BAD_REQUEST') {
                    swal("Please enter complete information !", "You clicked the button!", "warning");
                }
            })
            .catch((error) => console.log('error', error));
    };

    //Update size
    $scope.update = function () {
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

        fetch(API_List.API_UPDATE_SIZE, requestOptions)
            .then((response) => response.json())
            .then((result) => {
                if (result.status === 'OK') {
                    swal("Update Size successfully!", "You clicked the button!", "success");
                    setTimeout(function redirect() {
                        window.location.replace('http://localhost:8080/admin/size');
                    }, 1000);
                } else if (result.status === 'BAD_REQUEST') {
                    swal("Size value already used!", "You clicked the button!", "warning");
                }
                console.log(result);
            })
            .catch((error) => console.log('error', error));
    };

    //Delete Size
    $scope.delete = function () {
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

        fetch('http://localhost:8080/api/v2/management/sizes?sizeValue=' + $scope.form.sizeValue, requestOptions)
            .then((response) => response.json())
            .then((result) => {
                if (result.status === 'OK') {
                    swal("Delete Size successfully!", "You clicked the button!", "success");
                    setTimeout(function redirect() {
                        window.location.replace('http://localhost:8080/admin/size');
                    }, 1000);
                } else if (result.status === '400 BAD_REQUEST') {
                    swal("Your account is not found!", "You clicked the button!", "warning");
                }
            })
            .catch((error) => console.log('error', error));
    };

    //reset form
    $scope.reset = function () {
        $scope.showButtons = [2, 4, 1, 0];
        $scope.form = {
            photo: 'user.png',
            username: '',
            password: '',
            fullName: '',
            phone: '',
            address: '',
            email: '',
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

    //Pagination
    $scope.pager = {
        page: 0,
        size: 8,
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
