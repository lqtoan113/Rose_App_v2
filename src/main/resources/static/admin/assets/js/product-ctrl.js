app.controller('product-ctrl', function ($scope, $http) {
    //list api product
    const API_List = {
        API_LOAD_DATA_PRODUCT: '/api/v2/management/products?' + 'page=' + $scope.curPage + '&limit=12',
        API_LOAD_DATA_SIZE: 'http://localhost:8080/api/v2/management/sizes',
        API_LOAD_DATA_COLOR: 'http://localhost:8080/api/v2/management/colors-available',
        API_UPLOAD_PRODUCT: 'http://localhost:8080/api/v2/me',
        API_ADD_PRODUCT: 'http://localhost:8080/api/v2/management/products',
        API_UPDATE_PRODUCT: 'http://localhost:8080/api/v2/management/products',
        API_DELETE_PRODUCT: 'http://localhost:8080/api/v2/management/products',
        API_UPLOAD_IMAGE: 'http://localhost:8080/api/v2/firebase/upload',
    };

    //config data before handle api
    $scope.products = [];
    $scope.sizes = [];
    $scope.colors = [];
    $scope.orders=[];
    $scope.productEntries = [];
    $scope.form = {};
    $scope.sizesOfProductEntry = [];
    $scope.props = ['productCode', 'productName', 'commonPrice'];
    $scope.profiles = [];
    $scope.productsAvailable = '';
    $scope.firstNumberPage = 1;
    $scope.curPage;
    $scope.maxPage;

    //load data page
    $scope.initialize = function () {
        //load all products
        $scope.reset();
        $scope.initProducts();
        $scope.initOrder();
        $scope.initProfiles();
        //load value color
        $scope.form.available = true;
        $scope.form.imageUrl =
            'https://storage.googleapis.com/agile-being-318113.appspot.com/97285ce7-d935-4e00-94bc-731372f8f2fepng';
        $scope.loadProductAvailable();

    };

    //Search product
    $scope.searchElastic = function () {
        const token = 'Bearer ' + localStorage.getItem('token');
        const api = 'api/v2/management/products/search?keyword=' + $scope.search;
        $http({
            method: 'GET',
            url: api,
            headers: {
                Authorization: token,
            },
        }).then(resp => {
            $scope.products = resp.data.data;
            if ($scope.search.length === 0) {
                $scope.init();
            }
        });
    };

    //load data summary product available
    $scope.loadProductAvailable = function () {
        const token = 'Bearer ' + localStorage.getItem('token');
            //load Product Available
            $http({
                method: 'GET',
                url: '/api/v2/management/available',
                headers: {
                    Authorization: token,
                },
            }).then(resp => {
                $scope.productsAvailable = resp.data;
                if (resp.status === 401) {
                    $scope.refreshToken();
                }
            }).catch(error => console.log(error));
    };

    //load value color
    $scope.style = function (value) {
        return { 'background-color': value };
    };

    //load data product on table
    $scope.initProducts= function (){
            const token = 'Bearer ' + localStorage.getItem('token');
            $http({
                method: 'GET',
                url: '/api/v2/management/products',
                headers: {
                    Authorization: token,
                },
            }).then(resp => {
                if (resp.data.status === 'OK') {
                    $scope.products = resp.data.data;
                }
            }).catch(error => {
                if(error.data.status === 401){
                    $scope.refreshToken();
                }
            });
    }

    //Pagination, load data size and color
    $scope.init = function () {
            $scope.curPage = 1;
            const token = 'Bearer ' + localStorage.getItem('token');

            //load data product have paging
            $http({
                method: 'GET',
                url: '/api/v2/management/products?' + 'page=' + $scope.curPage + '&limit=12',
                headers: {
                    Authorization: token,
                },
            }).then(resp => {
                if (resp.data.status === 'OK') {
                    $scope.products = resp.data.data;
                    $scope.maxPage = resp.data.totalRecord;
                }
            }).catch(error => {
                if(error.data.status === 401){
                    $scope.refreshToken();
                }
            });

            //load data size in product entry
            $http({
                method: 'GET',
                url: API_List.API_LOAD_DATA_SIZE,
                headers: {
                    Authorization: token,
                },
            }).then(resp => {
                if (resp.data.status === 'OK') {
                    $scope.sizes = resp.data.data;
                    debugger
                }
            }).catch(error => {
                if(error.data.status === 401){
                    $scope.refreshToken();
                }
            });

            //load color in product entry
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
            }).catch(error => {
                if(error.data.status === 401){
                    $scope.refreshToken();
                }
            });
    };

    $scope.initProfiles = function () {
        const token = 'Bearer ' + localStorage.getItem('token');
        $http({
            method: 'GET',
            url: 'http://localhost:8080/api/v2/me',
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

    //Load data status order
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
                    }).then((isOke) => {
                        if (isOke) {
                            location.replace('/security/login/form');
                        }
                    });
                }
            }).catch(error => {
                console.log(error);
                $scope.refreshToken();
            });
    };

    //Logout when token expirations
    $scope.logout = function () {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
    };

    //Logout in admin page
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

    //Paging: firstPage
    $scope.firstPage = function () {
        $scope.init();
    };

    //Paging: prevPage
    $scope.prevPage = function () {
        if ($scope.curPage == 1) {
            return;
        } else {
                var token = 'Bearer ' + localStorage.getItem('token');
                $scope.curPage -= 1;
                var api = '/api/v2/management/products?' + 'page=' + $scope.curPage + '&limit=12';
                $http({
                    method: 'GET',
                    url: api,
                    headers: {
                        Authorization: token,
                    },
                }).then(resp => {
                    console.log(resp);
                    if (resp.data.status == 'OK') {
                        $scope.products = resp.data.data;
                    } else {
                        console.log('Token is refreshing');
                        $scope.refreshToken();
                    }
                }).catch(() => {
                    console.log('Token is refreshing');
                    $scope.refreshToken();
                });
        }
    };

    //Paging: nextPage
    $scope.nextPage = function () {
        if ($scope.curPage == $scope.maxPage) {
            return;
        } else {
            $scope.curPage += 1;
            var token = 'Bearer ' + localStorage.getItem('token');
            var api = '/api/v2/management/products?' + 'page=' + $scope.curPage + '&limit=12';
            $http({
                method: 'GET',
                url: api,
                headers: {
                    Authorization: token,
                },
            })
                .then((resp) => {
                    console.log(resp);
                    if (resp.data.status == 'OK') {
                        $scope.products = resp.data.data;
                    } else {
                        console.log('Token is refreshing');
                        $scope.refreshToken();
                    }
                })
                .catch(() => {
                    console.log('Token is refreshing');
                    $scope.refreshToken();
                });
        }
    };

    //Paging: lastPage
    $scope.lastPage = function () {
        $scope.curPage = $scope.maxPage;
        const api = '/api/v2/products' + '?' + 'page=' + $scope.curPage + '&' + 'limit=12';
        $http({
            method: 'GET',
            url: api,
        }).then(resp => {
            $scope.products = resp.data.data;
            $scope.allProductTemp = resp.data.data;
        })
    };

    //show data form
    $scope.editProduct = function (product) {
        $scope.reset();
        $('#productCode').prop('disabled', true);
        const productCode = product.productCode;
        const myHeaders = new Headers();
        const token = 'Bearer ' + localStorage.getItem('token');
        myHeaders.append('Authorization', token);
        var requestOptions = {
            method: 'GET',
            headers: myHeaders,
            redirect: 'follow',
        };

        fetch('http://localhost:8080/api/v2/management/products/' + productCode, requestOptions)
            .then(response => response.json())
            .then(result => {
                let { imageUrl, productCode, productName, available, createDate, description, commonPrice } =
                    result.data;
                $scope.form = {
                    imageUrl: imageUrl,
                    productCode: productCode,
                    productName: productName,
                    available: available,
                    createDate: createDate,
                    description: description,
                    commonPrice: commonPrice,
                };
                result.data.productEntries.forEach((i) => {
                    let sizes = i.sizes.map(({ sizeValue }) => sizeValue);
                    $scope.sizesOfProductEntry.push(sizes);
                    let { sku, quantity, productPrice, available,sold,revenue } = i;
                    $scope.productEntries.push({
                        sku: sku,
                        quantity: quantity,
                        productPrice: productPrice,
                        imageUrl: i.image.imageUrl,
                        colorValue: i.color.colorValue,
                        available: available,
                        sizeValue: sizes,
                        sold: sold,
                        revenue: revenue,
                    });
                });
                $('#edit-product').tab('show');
                $scope.init();
            }).catch(error => console.log('error', error));
    };

    // Toggle selection for a given fruit by name
    $scope.toggleSelection = function toggleSelection(sizeValue, indexOfProductEntry) {
        try {
            let idx = $scope.productEntries[indexOfProductEntry].sizeValue.indexOf(sizeValue);
            if (idx > -1) {
                $scope.productEntries[indexOfProductEntry].sizeValue.splice(idx, 1);
            } else {
                $scope.sizesOfProductEntry[indexOfProductEntry].push(sizeValue);
                $scope.productEntries[indexOfProductEntry].sizeValue = $scope.sizesOfProductEntry[indexOfProductEntry];
            }
        } catch (e) {
            $scope.sizesOfProductEntry[indexOfProductEntry].push(sizeValue);
            $scope.productEntries[indexOfProductEntry].sizeValue = $scope.sizesOfProductEntry[indexOfProductEntry];
        }
    };

    //Create products
    const input = document.getElementById('image');
    input.addEventListener('change', (event) => {
        const target = event.target;
        if (target.files && target.files[0]) {
            const maxAllowedSize = 5 * 1024 * 1024;
            $scope.createProduct = function () {
                const myHeaders = new Headers();
                const token = 'Bearer ' + localStorage.getItem('token');
                myHeaders.append('Authorization', token);
                myHeaders.append('Content-Type', 'application/json');
                const data = JSON.stringify({
                    productCode: $scope.form.productCode,
                    productName: $scope.form.productName,
                    available: $scope.form.available,
                    description: $scope.form.description,
                    imageUrl: $scope.form.imageUrl,
                    commonPrice: $scope.form.commonPrice,
                    productEntries: $scope.productEntries,
                });
                const requestOptions = {
                    method: 'POST',
                    headers: myHeaders,
                    body: data,
                    redirect: 'follow',
                };

                swal({
                    title: 'Do you want to create this product ?',
                    icon: 'success',
                    buttons: true,
                    dangerMode: true,
                }).then((isOke) => {
                    if (isOke) {
                        if($scope.productEntries.length === 0){
                            swal({
                                title: "Please enter more sub-products",
                            });
                        }else {
                            fetch(API_List.API_ADD_PRODUCT, requestOptions)
                                .then(response => response.json())
                                .then(result => {
                                    if (result.status === 'CREATED') {
                                        swal({
                                            title: result.message,
                                            icon: 'success',
                                        });
                                        $scope.initProducts();
                                        $scope.reset();
                                    } else if (result.status === 'BAD_REQUEST') {
                                        swal({
                                            title: result.message,
                                        });
                                    } else if (result.status === '400 BAD_REQUEST') {
                                        swal({
                                            title: result.message,
                                        });
                                    } else if (result.status === '404 NOT_FOUND') {
                                        swal({
                                            title: result.message,
                                        });
                                    }
                                }).catch(error => console.log('error', error));
                        }

                    }
                });
            };
            if (target.files[0].size > maxAllowedSize) {
                target.value = '';
                swal({
                    title: 'No selected image more 5mb',
                });
                $scope.reset();
            }
        }
    });

    //Update product
    $scope.updateProduct = function () {
        const myHeaders = new Headers();
        const token = 'Bearer ' + localStorage.getItem('token');
        myHeaders.append('Authorization', token);
        myHeaders.append('Content-Type', 'application/json');
        const data = JSON.stringify({
            productCode: $scope.form.productCode,
            productName: $scope.form.productName,
            available: $scope.form.available,
            description: $scope.form.description,
            imageUrl: $scope.form.imageUrl,
            commonPrice: $scope.form.commonPrice,
        });
        const requestOptions = {
            method: 'PUT',
            headers: myHeaders,
            body: data,
            redirect: 'follow',
        };

        swal({
            title: 'Do you want to update this product ?',
            buttons: true,
            dangerMode: true,
        }).then((isOke) => {
            if (isOke) {
                fetch(API_List.API_UPDATE_PRODUCT, requestOptions)
                    .then(response => response.json())
                    .then(result => {
                        if (result.status === 'CREATED') {
                            swal({
                                title: result.message,
                                icon: 'success',
                            });
                            $scope.initProducts();
                            $scope.reset();
                        } else if (result.status === '404 NOT_FOUND') {
                            swal({
                                title: result.message,
                            });
                        } else if (result.status === '400 BAD_REQUEST') {
                            swal({
                                title: result.message,
                            });
                        }
                    }).catch(error => console.log('error', error));
            }
        });
    };

    //Update ProductEntry
    $scope.updateProEntry = function (index) {
        const myHeaders = new Headers();
        const token = 'Bearer ' + localStorage.getItem('token');
        myHeaders.append('Authorization', token);
        myHeaders.append('Content-Type', 'application/json');
        var raw = JSON.stringify({
            sku: $scope.productEntries[index].sku,
            quantity: $scope.productEntries[index].quantity,
            productPrice: $scope.productEntries[index].productPrice,
            available: $scope.productEntries[index].available,
            imageUrl: $scope.productEntries[index].imageUrl,
            colorValue: $scope.productEntries[index].colorValue,
            sizeValue: $scope.sizesOfProductEntry[index],
        });
        const requestOptions = {
            method: 'PUT',
            headers: myHeaders,
            body: raw,
            redirect: 'follow',
        };

        swal({
            title: 'Do you want to update this product ?',
            icon: 'warning',
            buttons: true,
            dangerMode: true,
        }).then((isOke) => {
            if (isOke) {
                fetch('http://localhost:8080/api/v2/management/products/product-entry', requestOptions)
                    .then(response => response.json())
                    .then(result => {
                        if (result.status === 'OK') {
                            swal({
                                title: result.message,
                                icon: 'success',
                            });
                            $scope.init();
                        } else if (result.status === '400 BAD_REQUEST') {
                            swal({
                                title: result.message,
                            });
                        } else if (result.status === '404 NOT_FOUND') {
                            swal({
                                title: result.message,
                            });
                        }
                    }).catch(error => console.log('error', error));
            }
        });
    };

    //Delete Product
    $scope.deleteProduct = function (productCode) {
        const myHeaders = new Headers();
        const token = 'Bearer ' + localStorage.getItem('token');
        myHeaders.append('Authorization', token);

        const requestOptions = {
            method: 'DELETE',
            headers: myHeaders,
            redirect: 'follow',
        };

        swal({
            title: 'Do you want to unavailable product ?',
            icon: 'warning',
            buttons: true,
            dangerMode: true,
        }).then((isOke) => {
            if (isOke) {
                fetch(`http://localhost:8080/api/v2/management/products/${productCode}`, requestOptions)
                    .then(response => response.json())
                    .then(result => {
                        debugger
                        if (result.status === 'Ok') {
                            swal({
                                title: result.message,
                                icon: 'success',
                            });
                            $scope.initProducts();
                            $scope.reset();
                        } else if (result.status === '404 NOT_FOUND') {
                            swal({
                                title: result.message,
                            });
                        }
                    }).catch(error => console.log('error', error));
            }
        });
    };

    //Delete ProductEntry
    $scope.deleteProEntry = function (index) {
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
            title: 'Do you want to unavailable product?',
            icon: 'warning',
            buttons: true,
            dangerMode: true,
        }).then((isOke) => {
            if (isOke) {
                fetch(
                    'http://localhost:8080/api/v2/management/products/product-entry/' +
                        $scope.productEntries[index].sku,
                    requestOptions,
                )
                    .then(response => response.json())
                    .then(result => {
                        if (result.status === 'OK') {
                            swal({
                                title: "Successfully !",
                                icon: 'success',
                            });
                            $scope.initProducts();

                            if($scope.productEntries[index].available === true){
                                $scope.productEntries[index].available = false;
                                 debugger
                            }else if($scope.productEntries[index].available === false){
                                $scope.productEntries[index].available = true;
                            }

                        } else if (result.status === '404 NOT_FOUND') {
                            swal({
                                title: result.message,
                            });
                        } else if (result.status === 'NOT_ACCEPTABLE') {
                            swal({
                                title: result.message,
                            });
                        }
                    }).catch(error => console.log('error', error));
            }
        });
    };

    //reset form
    $scope.reset = function () {
        $scope.form = {
            productCode: '',
            productName: '',
            available: '',
            description: '',
            imageUrl:
                'https://storage.googleapis.com/agile-being-318113.appspot.com/97285ce7-d935-4e00-94bc-731372f8f2fepng',
            commonPrice: '',
        };
        $scope.productEntries = [];
        $('#productCode').prop('disabled', false);
        $('#sku').prop('disabled', false);
    };

    //Upload ImageProduct
    $scope.imageChanged = function (files) {
        const myHeaders = new Headers();
        const token = 'Bearer ' + localStorage.getItem('token');
        myHeaders.append('Authorization', token);
        var data = new FormData();
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
                $scope.form.imageUrl = result.data;
                document.getElementById('uploadedAvatar').src = result.data;
            }).catch((error) => console.log('error', error));
    };

    //Upload ImageProductEntry
    $scope.imageChangedEnTry = function (input) {
        const myHeaders = new Headers();
        const token = 'Bearer ' + localStorage.getItem('token');
        myHeaders.append('Authorization', token);

        var index = angular.element(input).scope().$index;
        var data = new FormData();
        data.append('file', input.files[0]);

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
                $scope.productEntries[index].imageUrl = result.data;
                document.getElementById($scope.productEntries[index].sku).src = $scope.productEntries[index].imageUrl;
                debugger
            }).catch((error) => console.log('error', error));
    };

    //Sort colum
    // column to sort
    $scope.column = 'productCode';

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

    //add tab product entry
    $scope.addFormProductEntry = function () {
        $scope.init();
        $scope.productEntries.push({});
        $scope.sizesOfProductEntry.push([]);
    };

    //delete tab product entry
    $scope.DeleteFormProductEntry = function ($index) {
        $scope.productEntries.splice($index, 1);
    };

    //pagination products
    $scope.pagerProducts = {
        page: 0,
        size: 8,
        get products() {
            var start = this.page * this.size;
            return $scope.products.slice(start, start + this.size);
        },
        get count() {
            return Math.ceil((1.0 * $scope.products.length) / this.size);
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
