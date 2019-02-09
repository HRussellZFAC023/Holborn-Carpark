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

    $.ajax({
        url: '/register',
        type: 'POST',
        data: {
            username:           $('#fld_username').val(),
            email:              $('#fld_email').val(),
            password:           $('#fld_password').val(),
            confirm_password:   $('#fld_pwd_confirm').val()
        },
        success: function (res, status, xhr) {
            if (res && res.redirect) {
                console.log(res)
                //window.location.replace(window.location.protocol + '//' + window.location.host + res.redirect);
            }
        },
        error: function (xhr, status, err) {
            console.log(xhr.responseJSON)
            internalError(xhr.responseJSON.type, xhr.responseJSON.message);
            nameError(xhr.responseJSON.type, xhr.responseJSON.message);
            emailError(xhr.responseJSON.type, xhr.responseJSON.message);
            passwordError(xhr.responseJSON.type, xhr.responseJSON.message);
            pwdConfirmError(xhr.responseJSON.type, xhr.responseJSON.message)
        }
    });
}

function internalError(type, message) {
    if (type === 'internal') {
        if (!$('#internal_error').length) {
            $('#box').append("<p id='internal_error' class='help is-large is-danger'>" + message + "</p>");
        }
    }
}

function nameError(type, message) {
    if (type === 'invalid name' || type === 'space in name' || type === 'taken name' || type === 'long name') {
        if (!$('#name_error').length) {
            $('#fld_username').addClass('is-danger');
            $('#ctrl_username').append("<p id='name_error' class='help is-danger'>" + message + "</p>");
        }
    }
}

function emailError(type, message) {
    if (type === 'invalid email') {
        if (!$('#email_error').length) {
            $('#fld_email').addClass('is-danger');
            $('#ctrl_email').append("<p id='email_error' class='help is-danger'>" + message + "</p>");
        }
    }
}

function passwordError(type, message) {
    if (type === 'invalid pwd' || type === 'short pwd' || type === 'disallowed pwd' || type === 'weak pwd') {
        if (!$('#pwd_error').length) {
            $('#fld_password').addClass('is-danger');
            $('#ctrl_password').append("<p id='pwd_error' class='help is-danger'>" + message + "</p>");
        }
    }
}

function pwdConfirmError(type, message) {
    if (type === 'match pwd') {
        if (!$('#pwd_confirm_error').length) {
            $('#fld_pwd_confirm').addClass('is-danger');
            $('#ctrl_pwd_confirm').append("<p id='pwd_confirm_error' class='help is-danger'>" + message + "</p>");
        }
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