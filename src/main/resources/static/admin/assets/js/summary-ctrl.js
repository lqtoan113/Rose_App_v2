app.controller("summary-ctrl", function ($scope, $http, $location, $filter) {
    let cardColor, headingColor, axisColor, shadeColor, borderColor;

    cardColor = config.colors.white;
    headingColor = config.colors.headingColor;
    axisColor = config.colors.axisColor;
    borderColor = config.colors.borderColor;
    $scope.todayOrder = '';
    $scope.totalOrder = '';
    $scope.available = '';
    $scope.totalProduct = '';
    $scope.todayIncome = '';
    $scope.totalIncome = '';
    $scope.totalAccount = '';
    $scope.revenueLast7Days = [];
    $scope.numberOfProductSoldByType = [];
    $scope.top10SoldProduct = [];
    $scope.top10Customer = [];
    $scope.profiles = [];
    $scope.orders = [];

    //row content Revenue
    $scope.secondRowRevenueByDate = [];
    $scope.secondRowRevenueByRevenue = [];
    $scope.secondRowCateName = [];
    $scope.secondRowNumberOfProduct = [];


    //row content Cate
    $scope.thirdRowCateName = [];
    $scope.thirdRowPercentageByCate = [];
    $scope.thirdRowCateName2 = [];
    $scope.thirdRowAvailable = [];
    $scope.thirdRowUnAvailable = [];

    $scope.fourthRowProductName = [];
    $scope.fourthRowQuantity = [];
    $scope.fifRowContent = [];

	//row top10customer
	$scope.top10customerUsername =[];
	$scope.top10customerFullname =[];
	$scope.top10customerEmail =[];
	$scope.top10customerImage =[];
	$scope.top10customerMoney =[];

    //Refresh Token when expire
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
            }).catch(error => console.log('error', error));
    };

    //logout when token expire
    $scope.logout = function () {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
    };

    //Load data row
    $scope.initialize = function () {
        const token = 'Bearer ' + localStorage.getItem('token');
        $scope.initOrder();

        $http({
            method: 'GET',
            url: 'http://localhost:8080/api/v2/me',
            headers: {
                Authorization: token,
            },
        }).then((resp) => {
            $scope.profiles = resp.data.data;
            $scope.profiles.roles.name = resp.data.data.roles[0].name;
            if ($scope.profiles.roles.name === "ROLE_MODERATOR") {
                location.replace("http://localhost:8080/security/page/403")
                $scope.pageSummary = false;
            } else {
                $scope.pageSummary = true;
            }
        }).catch((error) => {
            if (error.data.status === 401) {
                $scope.refreshToken();

            }
        });

        //todayOrder Content
        $http({
            method: 'GET',
            url: 'http://localhost:8080/api/v2/management/todayOrder',
            headers: {
                Authorization: token,
            },
        }).then(resp => {
            $scope.todayOrder = resp.data;
            if (resp.status === 401) {
                $scope.refreshToken();
            }
        }).catch(error => console.log(error));

        //top10Customer
        $http({
            method: 'GET',
            url: 'http://localhost:8080/api/v2/management/top10Customer',
            headers: {
                Authorization: token,
            },
        }).then(resp => {
			debugger
            $scope.top10Customer = resp.data.data;
			$scope.top10Customer.forEach(e => {
				$scope.top10customerUsername.push(e[0]);
				$scope.top10customerFullname.push(e[1]);
				$scope.top10customerEmail.push(e[2]);
				$scope.top10customerImage.push(e[3]);
				$scope.top10customerMoney.push(e[4]);
			})
            if (resp.status === 401) {
                $scope.refreshToken();
            }
        }).catch(error => console.log(error));

        //totalOrder Content
        $http({
            method: 'GET',
            url: 'http://localhost:8080/api/v2/management/totalOrder',
            headers: {
                Authorization: token,
            },
        }).then(resp => {
            $scope.totalOrderToday = resp.data.data;
            if (resp.status === 401) {
                $scope.refreshToken();
            }
        }).catch(error => console.log(error));

        //available Content
        $http({
            method: 'GET',
            url: 'http://localhost:8080/api/v2/management/available',
            headers: {
                Authorization: token,
            },
        })
            .then(resp => {
                $scope.available = resp.data;
                if (resp.status === 401) {
                    $scope.refreshToken();
                }
            }).catch(error => console.log(error));

        //totalProduct Content
        $http({
            method: 'GET',
            url: 'http://localhost:8080/api/v2/management/totalProduct',
            headers: {
                Authorization: token,
            },
        }).then(resp => {
            $scope.totalProduct = resp.data;
            if (resp.status === 401) {
                $scope.refreshToken();
            }
        }).catch(error => console.log(error));

        //todayIncome Content
        $http({
            method: 'GET',
            url: 'http://localhost:8080/api/v2/management/todayIncome',
            headers: {
                Authorization: token,
            },
        })
            .then(resp => {
                $scope.todayIncome = resp.data;
                if ($scope.todayIncome.data === null) {
                    $scope.todayIncome.data = 0;
                }
                if (resp.status === 401) {
                    $scope.refreshToken();
                }
            })
            .catch(error => console.log(error));


        //totalIncome Content
        $http({
            method: 'GET',
            url: 'http://localhost:8080/api/v2/management/totalIncome',
            headers: {
                Authorization: token,
            },
        }).then(resp => {
            $scope.totalIncome = resp.data;
            if (resp.status === 401) {
                $scope.refreshToken();
            }
        }).catch(error => console.log(error));

        //totalAccount Content
        $http({
            method: 'GET',
            url: 'http://localhost:8080/api/v2/management/totalAccount',
            headers: {
                Authorization: token,
            },
        }).then(resp => {
            $scope.totalAccount = resp.data;
            if (resp.status === 401) {
                $scope.refreshToken();
            }
        }).catch(error => console.log(error));

        $http({
            method: 'GET',
            url: 'http://localhost:8080/api/v2/management/revenueLast7Days',
            headers: {
                Authorization: token,
            },
        }).then(resp => {
            //revenue last 7days
            $scope.revenueLast7Days = resp.data.data;
            $scope.revenueLast7Days.forEach(e => {
                $scope.secondRowRevenueByDate.push($filter('date')(e[0], 'dd-MM'));
                $scope.secondRowRevenueByRevenue.push(e[1]);
            })

            //line chart for Revenue last 7 days
            var dataListRevenue = $scope.secondRowRevenueByDate;
            var revenueListByDays = $scope.secondRowRevenueByRevenue;
            const totalRevenueChartEl = document.querySelector('#totalRevenueChart'),
                totalRevenueChartOptions = {
                    series: [
                        {
                            name: 'Last 7 days',
                            data: revenueListByDays
                        },
                    ],
                    chart: {
                        height: 300,
                        stacked: true,
                        type: 'bar',
                        toolbar: {show: false}
                    },
                    plotOptions: {
                        bar: {
                            horizontal: false,
                            columnWidth: '33%',
                            borderRadius: 12,
                            startingShape: 'rounded',
                            endingShape: 'rounded'
                        }
                    },
                    colors: [config.colors.primary, config.colors.info],
                    dataLabels: {
                        enabled: false
                    },
                    stroke: {
                        curve: 'smooth',
                        width: 6,
                        lineCap: 'round',
                        colors: [cardColor]
                    },
                    legend: {
                        show: true,
                        horizontalAlign: 'left',
                        position: 'top',
                        markers: {
                            height: 8,
                            width: 8,
                            radius: 12,
                            offsetX: -3
                        },
                        labels: {
                            colors: axisColor
                        },
                        itemMargin: {
                            horizontal: 10
                        }
                    },
                    grid: {
                        borderColor: borderColor,
                        padding: {
                            top: 0,
                            bottom: -8,
                            left: 20,
                            right: 20
                        }
                    },
                    xaxis: {
                        categories: dataListRevenue,
                        labels: {
                            style: {
                                fontSize: '13px',
                                colors: axisColor
                            }
                        },
                        axisTicks: {
                            show: false
                        },
                        axisBorder: {
                            show: false
                        }
                    },
                    yaxis: {
                        labels: {
                            style: {
                                fontSize: '13px',
                                colors: axisColor
                            }
                        }
                    },
                    responsive: [
                        {
                            breakpoint: 1700,
                            options: {
                                plotOptions: {
                                    bar: {
                                        borderRadius: 10,
                                        columnWidth: '32%'
                                    }
                                }
                            }
                        },
                        {
                            breakpoint: 1580,
                            options: {
                                plotOptions: {
                                    bar: {
                                        borderRadius: 10,
                                        columnWidth: '35%'
                                    }
                                }
                            }
                        },
                        {
                            breakpoint: 1440,
                            options: {
                                plotOptions: {
                                    bar: {
                                        borderRadius: 10,
                                        columnWidth: '42%'
                                    }
                                }
                            }
                        },
                        {
                            breakpoint: 1300,
                            options: {
                                plotOptions: {
                                    bar: {
                                        borderRadius: 10,
                                        columnWidth: '48%'
                                    }
                                }
                            }
                        },
                        {
                            breakpoint: 1200,
                            options: {
                                plotOptions: {
                                    bar: {
                                        borderRadius: 10,
                                        columnWidth: '40%'
                                    }
                                }
                            }
                        },
                        {
                            breakpoint: 1040,
                            options: {
                                plotOptions: {
                                    bar: {
                                        borderRadius: 11,
                                        columnWidth: '48%'
                                    }
                                }
                            }
                        },
                        {
                            breakpoint: 991,
                            options: {
                                plotOptions: {
                                    bar: {
                                        borderRadius: 10,
                                        columnWidth: '30%'
                                    }
                                }
                            }
                        },
                        {
                            breakpoint: 840,
                            options: {
                                plotOptions: {
                                    bar: {
                                        borderRadius: 10,
                                        columnWidth: '35%'
                                    }
                                }
                            }
                        },
                        {
                            breakpoint: 768,
                            options: {
                                plotOptions: {
                                    bar: {
                                        borderRadius: 10,
                                        columnWidth: '28%'
                                    }
                                }
                            }
                        },
                        {
                            breakpoint: 640,
                            options: {
                                plotOptions: {
                                    bar: {
                                        borderRadius: 10,
                                        columnWidth: '32%'
                                    }
                                }
                            }
                        },
                        {
                            breakpoint: 576,
                            options: {
                                plotOptions: {
                                    bar: {
                                        borderRadius: 10,
                                        columnWidth: '37%'
                                    }
                                }
                            }
                        },
                        {
                            breakpoint: 480,
                            options: {
                                plotOptions: {
                                    bar: {
                                        borderRadius: 10,
                                        columnWidth: '45%'
                                    }
                                }
                            }
                        },
                        {
                            breakpoint: 420,
                            options: {
                                plotOptions: {
                                    bar: {
                                        borderRadius: 10,
                                        columnWidth: '52%'
                                    }
                                }
                            }
                        },
                        {
                            breakpoint: 380,
                            options: {
                                plotOptions: {
                                    bar: {
                                        borderRadius: 10,
                                        columnWidth: '60%'
                                    }
                                }
                            }
                        }
                    ],
                    states: {
                        hover: {
                            filter: {
                                type: 'none'
                            }
                        },
                        active: {
                            filter: {
                                type: 'none'
                            }
                        }
                    }
                };
            if (typeof totalRevenueChartEl !== undefined && totalRevenueChartEl !== null) {
                const totalRevenueChart = new ApexCharts(totalRevenueChartEl, totalRevenueChartOptions);
                totalRevenueChart.render();
            }
            if (resp.status === 401) {
                $scope.refreshToken();
            }
        })

        // //product sold by categories
        // $http({
        //     method: 'GET',
        //     url: 'http://localhost:8080/api/v2/management/numberOfProductSoldByType',
        //     headers: {
        //         Authorization: token,
        //     },
        // }).then((resp) => {
        //     $scope.numberOfProductSoldByType = resp.data.data;
        //     $scope.numberOfProductSoldByType.forEach(e => {
        //         $scope.thirdRowCateName.push(e[1]);
        //         $scope.thirdRowPercentageByCate.push(e[0]);
        //     })
        //     var cateName = $scope.thirdRowCateName;
        //     var numberOfProductSold = $scope.thirdRowPercentageByCate;
        //     $scope.totalOrder = 0;
        //     for (let i = 0; i < numberOfProductSold.length; i++) {
        //         $scope.totalOrder += numberOfProductSold[i];
        //     }
        //     const chartOrderStatistics = document.querySelector('#orderStatisticsChart'),
        //         orderChartConfig = {
        //             chart: {
        //                 height: 165,
        //                 width: 130,
        //                 type: 'donut'
        //             },
        //             labels: cateName,
        //             series: numberOfProductSold,
        //             colors: [config.colors.primary, config.colors.success, config.colors.info, config.colors.secondary],
        //             stroke: {
        //                 width: 5,
        //                 colors: cardColor
        //             },
        //             dataLabels: {
        //                 enabled: true,
        //                 formatter: function (val, opt) {
        //                     return parseInt(val) + '%';
        //                 }
        //             },
        //             legend: {
        //                 show: false
        //             },
        //             grid: {
        //                 padding: {
        //                     top: 0,
        //                     bottom: 0,
        //                     right: 15
        //                 }
        //             },
        //             plotOptions: {
        //                 pie: {
        //                     donut: {
        //                         size: '75%',
        //                         labels: {
        //                             show: true,
        //                             value: {
        //                                 fontSize: '1.5rem',
        //                                 fontFamily: 'Public Sans',
        //                                 color: headingColor,
        //                                 offsetY: -15,
        //                                 formatter: function (val) {
        //                                     return parseInt(val);
        //                                 }
        //                             },
        //                             name: {
        //                                 offsetY: 20,
        //                                 fontFamily: 'Public Sans'
        //                             }
        //                         }
        //                     }
        //                 }
        //             }
        //         };
        //     if (typeof chartOrderStatistics !== undefined && chartOrderStatistics !== null) {
        //         const statisticsChart = new ApexCharts(chartOrderStatistics, orderChartConfig);
        //         statisticsChart.render();
        //     }
        //
        // }).catch(error => {
        //     if (error.status === 401) {
        //         $scope.refreshToken();
        //     }
        // });

        $http({
            method: 'GET',
            url: 'http://localhost:8080/api/v2/management/top10SoldProduct',
            headers: {
                Authorization: token,
            },
        })
            .then((resp) => {
                $scope.top10SoldProduct = resp.data.data;
                $scope.top10SoldProduct.forEach(e => {
                    $scope.fourthRowProductName.push(e[0]);
                    $scope.fourthRowQuantity.push(e[1]);

                    var top10ProductName = $scope.fourthRowProductName;
                    var quantitySold = $scope.fourthRowQuantity;
                    const totalRevenueChartEl = document.querySelector('#top10product'),
                        totalRevenueChartOptions = {
                            series: [
                                {
                                    name: 'Last 7 days',
                                    data: quantitySold
                                },
                            ],
                            chart: {
                                height: 300,
                                stacked: true,
                                type: 'bar',
                                toolbar: {show: false}
                            },
                            plotOptions: {
                                bar: {
                                    horizontal: false,
                                    columnWidth: '33%',
                                    borderRadius: 12,
                                    startingShape: 'rounded',
                                    endingShape: 'rounded'
                                }
                            },
                            colors: [config.colors.primary, config.colors.info],
                            dataLabels: {
                                enabled: false
                            },
                            stroke: {
                                curve: 'smooth',
                                width: 6,
                                lineCap: 'round',
                                colors: [cardColor]
                            },
                            legend: {
                                show: true,
                                horizontalAlign: 'left',
                                position: 'top',
                                markers: {
                                    height: 8,
                                    width: 8,
                                    radius: 12,
                                    offsetX: -3
                                },
                                labels: {
                                    colors: axisColor
                                },
                                itemMargin: {
                                    horizontal: 10
                                }
                            },
                            grid: {
                                borderColor: borderColor,
                                padding: {
                                    top: 0,
                                    bottom: -8,
                                    left: 20,
                                    right: 20
                                }
                            },
                            xaxis: {
                                categories: top10ProductName,
                                labels: {
                                    style: {
                                        fontSize: '13px',
                                        colors: axisColor
                                    }
                                },
                                axisTicks: {
                                    show: false
                                },
                                axisBorder: {
                                    show: false
                                }
                            },
                            yaxis: {
                                labels: {
                                    style: {
                                        fontSize: '13px',
                                        colors: axisColor
                                    }
                                }
                            },
                            responsive: [
                                {
                                    breakpoint: 1700,
                                    options: {
                                        plotOptions: {
                                            bar: {
                                                borderRadius: 10,
                                                columnWidth: '32%'
                                            }
                                        }
                                    }
                                },
                                {
                                    breakpoint: 1580,
                                    options: {
                                        plotOptions: {
                                            bar: {
                                                borderRadius: 10,
                                                columnWidth: '35%'
                                            }
                                        }
                                    }
                                },
                                {
                                    breakpoint: 1440,
                                    options: {
                                        plotOptions: {
                                            bar: {
                                                borderRadius: 10,
                                                columnWidth: '42%'
                                            }
                                        }
                                    }
                                },
                                {
                                    breakpoint: 1300,
                                    options: {
                                        plotOptions: {
                                            bar: {
                                                borderRadius: 10,
                                                columnWidth: '48%'
                                            }
                                        }
                                    }
                                },
                                {
                                    breakpoint: 1200,
                                    options: {
                                        plotOptions: {
                                            bar: {
                                                borderRadius: 10,
                                                columnWidth: '40%'
                                            }
                                        }
                                    }
                                },
                                {
                                    breakpoint: 1040,
                                    options: {
                                        plotOptions: {
                                            bar: {
                                                borderRadius: 11,
                                                columnWidth: '48%'
                                            }
                                        }
                                    }
                                },
                                {
                                    breakpoint: 991,
                                    options: {
                                        plotOptions: {
                                            bar: {
                                                borderRadius: 10,
                                                columnWidth: '30%'
                                            }
                                        }
                                    }
                                },
                                {
                                    breakpoint: 840,
                                    options: {
                                        plotOptions: {
                                            bar: {
                                                borderRadius: 10,
                                                columnWidth: '35%'
                                            }
                                        }
                                    }
                                },
                                {
                                    breakpoint: 768,
                                    options: {
                                        plotOptions: {
                                            bar: {
                                                borderRadius: 10,
                                                columnWidth: '28%'
                                            }
                                        }
                                    }
                                },
                                {
                                    breakpoint: 640,
                                    options: {
                                        plotOptions: {
                                            bar: {
                                                borderRadius: 10,
                                                columnWidth: '32%'
                                            }
                                        }
                                    }
                                },
                                {
                                    breakpoint: 576,
                                    options: {
                                        plotOptions: {
                                            bar: {
                                                borderRadius: 10,
                                                columnWidth: '37%'
                                            }
                                        }
                                    }
                                },
                                {
                                    breakpoint: 480,
                                    options: {
                                        plotOptions: {
                                            bar: {
                                                borderRadius: 10,
                                                columnWidth: '45%'
                                            }
                                        }
                                    }
                                },
                                {
                                    breakpoint: 420,
                                    options: {
                                        plotOptions: {
                                            bar: {
                                                borderRadius: 10,
                                                columnWidth: '52%'
                                            }
                                        }
                                    }
                                },
                                {
                                    breakpoint: 380,
                                    options: {
                                        plotOptions: {
                                            bar: {
                                                borderRadius: 10,
                                                columnWidth: '60%'
                                            }
                                        }
                                    }
                                }
                            ],
                            states: {
                                hover: {
                                    filter: {
                                        type: 'none'
                                    }
                                },
                                active: {
                                    filter: {
                                        type: 'none'
                                    }
                                }
                            }
                        };
                    if (typeof totalRevenueChartEl !== undefined && totalRevenueChartEl !== null) {
                        const totalRevenueChart = new ApexCharts(totalRevenueChartEl, totalRevenueChartOptions);
                        totalRevenueChart.render();
                    }
                })
                if (resp.status === 401) {
                    $scope.refreshToken();
                }
            })
            .catch((error) => console.log(error));

    }

    //load data status order
    $scope.initOrder = function () {
        const token = 'Bearer ' + localStorage.getItem('token');
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
            if (error.status == 403) {
                location.replace('/security/page/403');
            } else {
                console.log(error);
            }
        });
    };

    //load profile
    $scope.getProfiles = function () {
        const token = 'Bearer ' + localStorage.getItem('token');
        $http({
            method: 'GET',
            url: 'http://localhost:8080/api/v2/me',
            headers: {
                Authorization: token,
            },
        }).then((resp) => {
            $scope.profiles = resp.data.data;
            $scope.profiles.roles.name = resp.data.data.roles[0].name;
            if ($scope.profiles.roles.name === "ROLE_MODERATOR") {
                location.replace('http://localhost:8080/security/page/403');
            }
            if ($scope.profiles.roles.name === "ROLE_MODERATOR") {
                $scope.pageSummary = false;
            } else {
                $scope.pageSummary = true;
            }

            if (resp.status === 401) {
                $scope.refreshToken();
            }
        }).catch((error) => {
            if (error.data.status === 401) {
                $scope.refreshToken();
            }
        });
    }

    //logout in admin page
    $scope.logoutAdmin = function () {
        swal({
            title: "Are you sure?",
            text: "Do you want to sign out ?",
            icon: "warning",
            buttons: true,
            dangerMode: true,
        }).then((isOke) => {
            if (isOke) {
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


    //load time
    function currentTime() {
        let date = new Date();
        let hh = date.getHours();
        let mm = date.getMinutes();
        let ss = date.getSeconds();
        let session = "AM";

        if (hh == 0) {
            hh = 12;
        }
        if (hh > 12) {
            hh = hh - 12;
            session = "PM";
        }

        hh = (hh < 10) ? "0" + hh : hh;
        mm = (mm < 10) ? "0" + mm : mm;
        ss = (ss < 10) ? "0" + ss : ss;

        let time = hh + ":" + mm + ":" + ss + " " + session;

        document.getElementById("clock").innerText = time;
        let t = setTimeout(function () {
            currentTime()
        }, 1000);
    }

    currentTime();

    //load data summary
    $scope.initialize();
})
