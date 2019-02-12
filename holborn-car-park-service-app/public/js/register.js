$(document).ready(function () {
    $('#fld_pwd_confirm').on('keypress', function (e) {
        if (e.which === 13) {
            validate();
        }
    });

    $('#btn_register').click(validate);
});

function validate() {
    removeErrors();
    let username = $('#fld_username').val();

    $.ajax({
        url: '/register',
        type: 'POST',
        data: {
            username: username,
            email: $('#fld_email').val(),
            password: $('#fld_password').val(),
            confirm_password: $('#fld_pwd_confirm').val()
        },
        success: function (res, status, xhr) {
            let cookie_username = getCookie("username");

            if ($("#remember_me").prop("checked") === true) {
                if(!cookie_username || cookie_username !== username) {
                    setCookie("username", username, 365);
                }
            }

            if (res && res.redirect) {
                window.location.replace(window.location.protocol + '//' + window.location.host + res.redirect);
            }
        },
        error: function (xhr, status, err) {
            let type = xhr.responseJSON.type;
            let message = xhr.responseJSON.message;

            if (type === 'internal') {
                internalError(message);
            }
            else if (type === 'invalid name' || type === 'space in name' || type === 'taken name' || type === 'long name') {
                nameError(message);
            }
            else if (type === 'invalid email') {
                emailError(message);
            }
            else if (type === 'invalid pwd' || type === 'short pwd' || type === 'disallowed pwd' || type === 'weak pwd') {
                passwordError(message);
            }
            else if (type === 'match pwd') {
                pwdConfirmError(message)
            }
        }
    });
}

function internalError(message) {
    if (!$('#internal_error').length) {
        $('#box').append("<p id='internal_error' class='help is-large is-danger'>" + message + "</p>");
    }
}

function nameError(message) {
    if (!$('#name_error').length) {
        $('#fld_username').addClass('is-danger');
        $('#ctrl_username').append("<p id='name_error' class='help is-danger'>" + message + "</p>");
    }
}

function emailError(message) {
    if (!$('#email_error').length) {
        $('#fld_email').addClass('is-danger');
        $('#ctrl_email').append("<p id='email_error' class='help is-danger'>" + message + "</p>");
    }
}

function passwordError(message) {
    if (!$('#pwd_error').length) {
        $('#fld_password').addClass('is-danger');
        $('#ctrl_password').append("<p id='pwd_error' class='help is-danger'>" + message + "</p>");
    }
}

function pwdConfirmError(message) {
    if (!$('#pwd_confirm_error').length) {
        $('#fld_pwd_confirm').addClass('is-danger');
        $('#ctrl_pwd_confirm').append("<p id='pwd_confirm_error' class='help is-danger'>" + message + "</p>");
    }
}

function removeErrors() {
    if ($('#name_error').length) {
        $('#fld_username').removeClass('is-danger');
        $('#name_error').remove();
    }
    else if ($('#email_error').length) {
        $('#fld_email').removeClass('is-danger');
        $('#email_error').remove();
    }
    else if ($('#pwd_error').length) {
        $('#fld_password').removeClass('is-danger');
        $('#pwd_error').remove();
    }
    else if ($('#pwd_confirm_error').length) {
        $('#fld_pwd_confirm').removeClass('is-danger');
        $('#pwd_confirm_error').remove();
    }
    else if ($('#internal_error').length) {
        $('#internal_error').remove();
    }
}

function setCookie(cname, cvalue, exdays) {
    let d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));

    let expires = "expires="+ d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
    let name = cname + "=";
    let decodedCookie = decodeURIComponent(document.cookie);
    let ca = decodedCookie.split(';');
    for(let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) === 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}