document.addEventListener('DOMContentLoaded', function () {
    // Mong muốn của chúng ta
    Validator({
        form: '#form-1',
        formGroupSelector: '.form-group',
        errorSelector: '.form-message',
        rules: [
            Validator.isRequired('#fullName', 'Please enter your full name'),
            Validator.isNumber('#phoneNumber'),
            Validator.isEmail('#email'),
            Validator.isRequired('#username'),
            Validator.minLength('#password', 6),
            Validator.isRequired('#password_confirmation'),
            Validator.isConfirmed(
                '#password_confirmation',
                function () {
                    return document.querySelector('#form-1 #password').value;
                },
                'Confirm password does not match',
            ),
        ],
        onSubmit: function (data) {
            grecaptcha.ready(function () {
                grecaptcha
                    .execute('6Ldp6jUjAAAAABdndzKPagfV1IPkOebJVzzpVDgB', { action: 'register' })
                    .then(function (token) {
                        // Add your logic to submit to your backend server here.

                        var myHeaders = new Headers();
                        myHeaders.append('Content-Type', 'application/json');

                        var raw = JSON.stringify(data);

                        var requestOptions = {
                            method: 'POST',
                            headers: myHeaders,
                            body: raw,
                            redirect: 'follow',
                        };

                        fetch(`/api/v2/auth/register?g-recaptcha-response=${token}`, requestOptions)
                            .then((response) => response.json())
                            .then((result) => {
                                if (result.status === 'CREATED') {
                                    swal('Success!', result.message, 'success');
                                    //Clear form
                                    const form = document.getElementById('form-1');
                                    Array.from(form.elements).forEach((element) => (element.value = ''));
                                } else {
                                    swal('Error!', result.message, 'error');
                                }
                            })
                            .catch((error) => console.log('error', error));
                    });
            });
        },
    });

    Validator({
        form: '#form-2',
        formGroupSelector: '.form-group',
        errorSelector: '.form-message',
        rules: [Validator.isRequired('#username'), Validator.minLength('#password', 6)],
        onSubmit: function (data) {
            grecaptcha.ready(function () {
                grecaptcha
                    .execute('6Ldp6jUjAAAAABdndzKPagfV1IPkOebJVzzpVDgB', { action: 'login' })
                    .then(function (token) {
                        // Add your logic to submit to your backend server here.
                        var myHeaders = new Headers();
                        myHeaders.append('Content-Type', 'application/json');
                        var raw = JSON.stringify(data);
                        var requestOptions = {
                            method: 'POST',
                            headers: myHeaders,
                            body: raw,
                            redirect: 'follow',
                        };
                        fetch(`/api/v2/auth/sign-in?g-recaptcha-response=${token}`, requestOptions)
                            .then((response) => response.json())
                            .then((result) => {
                                if (result.status === 'OK') {
                                    localStorage.setItem('token', result.data.token);
                                    localStorage.setItem('refreshToken', result.data.refreshToken);
                                    swal('Success!', result.message, 'success');
                                    setTimeout(function redirect() {
                                        window.location.replace('/');
                                    }, 500);
                                } else {
                                    swal('Error!', result.message, 'error');
                                }
                            })
                            .catch((error) => {
                                swal('error!', error.message, 'error');
                            });
                    });
            });
        },
    });

    Validator({
        form: '#form-3',
        formGroupSelector: '.form-group',
        errorSelector: '.form-message',
        rules: [Validator.isRequired('#username')],
        onSubmit: function (data) {
            // Call API
            document.getElementById('forget-message').innerHTML = 'System is handling, please wait';
            document.getElementById('btn-forget').disabled = true;
            var formData = new FormData();
            formData.append('keyword', data.username);

            var requestOptions = {
                method: 'POST',
                body: formData,
                redirect: 'follow',
            };

            fetch('/api/v2/auth/forgot-password', requestOptions)
                .then((response) => response.json())
                .then((result) => {
                    if (result.status === 'OK') {
                        swal('Success!', result.message, 'success');
                    } else {
                        swal('Error!', result.message, 'error');
                        document.getElementById('btn-forget').disabled = false;
                    }
                })
                .catch((error) => console.log('error', error));
        },
    });

    Validator({
        form: '#form-4',
        formGroupSelector: '.form-group',
        errorSelector: '.form-message',
        rules: [
            Validator.minLength('#password', 6),
            Validator.isRequired('#password_confirmation'),
            Validator.isConfirmed(
                '#password_confirmation',
                function () {
                    return document.querySelector('#form-4 #password').value;
                },
                'Confirm password does not match',
            ),
        ],
        onSubmit: function (data) {
            // Call API
            const token = new URL(location.href).searchParams.get('token');
            const newPass = data.password;
            var formData = new FormData();
            formData.append('token', token);
            formData.append('newPassword', newPass);

            var requestOptions = {
                method: 'POST',
                body: formData,
                redirect: 'follow',
            };

            fetch('/api/v2/auth/reset-password', requestOptions)
                .then((response) => response.json())
                .then((result) => {
                    if (result.status === 'CREATED') {
                        swal('Success!', result.message, 'success');
                        setTimeout(function redirect() {
                            window.location.replace('/security/login/form');
                        }, 1000);
                    }
                })
                .catch((error) => console.log('error', error));
        },
    });

    Validator({
        form: '#formAccountSettings',
        formGroupSelector: '.form-group',
        errorSelector: '.form-message',
        rules: [
            Validator.isRequired('#fullName', 'Please enter your full name'),
            Validator.isEmail('#email'),
            Validator.isRequired('#username'),
            Validator.isNumber('#phone'),
            Validator.isRequired('#address'),
            Validator.isRequired('input[name="gender"]'),
        ],
        onSubmit: function (data) {
            // Call API
            var token = 'Bearer ' + localStorage.getItem('token');
            var myHeaders = new Headers();
            myHeaders.append('Authorization', token);
            myHeaders.append('Content-Type', 'application/json');

            var raw = JSON.stringify(data);

            var requestOptions = {
                method: 'PUT',
                headers: myHeaders,
                body: raw,
                redirect: 'follow',
            };

            fetch('/api/v2/me', requestOptions)
                .then((response) => response.json())
                .then((result) => {
                    if (result.status === 'OK') {
                        swal('Success!', 'Edited information successfully!', 'success');
                        document.querySelectorAll('input').forEach((element) => (element.disabled = true));
                        document.getElementById('username').disabled = true;
                        document.getElementById('editInfoBtn').disabled = true;
                        $('#editInfoBtn').addClass('d-n');
                    } else {
                        swal('Error!', result.message, 'error');
                    }
                })
                .catch((error) => console.log('error', error));
        },
    });

    Validator({
        form: '#formChangePassword',
        formGroupSelector: '.form-group',
        errorSelector: '.form-message',
        rules: [
            Validator.minLength('#oldPassword', 6),
            Validator.minLength('#newPassword', 6),
            Validator.isNotConfirmed(
                '#newPassword',
                function () {
                    return document.querySelector('#formChangePassword #oldPassword').value;
                },
                'Old and new password can not match',
            ),
            Validator.isRequired('#password_confirmation'),
            Validator.isConfirmed(
                '#password_confirmation',
                function () {
                    return document.querySelector('#formChangePassword #newPassword').value;
                },
                'Confirm password does not match',
            ),
        ],
        onSubmit: function (data) {
            // Call API
            var token = 'Bearer ' + localStorage.getItem('token');
            var myHeaders = new Headers();
            myHeaders.append('Authorization', token);

            var formData = new FormData();
            formData.append('oldPassword', data.oldPassword);
            formData.append('newPassword', data.newPassword);

            var requestOptions = {
                method: 'POST',
                headers: myHeaders,
                body: formData,
                redirect: 'follow',
            };

            fetch('/api/v2/me/change-password', requestOptions)
                .then((response) => response.json())
                .then((result) => {
                    if (result.status === 'OK') {
                        swal('Success!', result.message, 'success');
                        location.replace('/order-list');
                    } else {
                        swal('Error!', result.message, 'error');
                    }
                })
                .catch((error) => {
                    swal('Error!', error.message, 'error');
                });
        },
    });
});
