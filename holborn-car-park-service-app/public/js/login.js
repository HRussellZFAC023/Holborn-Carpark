$(document).ready(function() {
    $('#fld_password').on('keypress',function(e) {
        if (e.which === 13) {
            validate();
        }
    });

    $('#btn_login').click(validate);
});

function validate() {
    removeErrors();

    $.ajax({
        url: '/login',
        type: 'POST',
        data: {
            username: $('#fld_username').val(),
            password: $('#fld_password').val()
        },
        success: function (res, status, xhr) {
            if(res && res.redirect) {
                window.location.replace(window.location.protocol + '//' + window.location.host + res.redirect);
            }
        },
        error: function (xhr, status, err) {
            let type = xhr.responseJSON.type;
            let message = xhr.responseJSON.message;

            if(type === 'internal'){
                internalError(message)
            }
            else if(type === 'user'){
                noSuchUser(message)
            }
            else if(type === 'pwd'){
                wrongPwd(message)
            }
        }
    });
}

function noSuchUser(message) {
    if(!$('#name_error').length) {
        $('#fld_username').addClass('is-danger');
        $('#ctrl_username').append("<p id='name_error' class='help is-danger'>" + message + "</p>");
    }
}

function wrongPwd(message) {
    if(!$('#pwd_error').length) {
        $('#fld_password').addClass('is-danger');
        $('#ctrl_password').append("<p id='pwd_error' class='help is-danger'>" + message + "</p>");
    }
}

function internalError(message){
    if(!$('#internal_error').length) {
        $('#box').append("<p id='internal_error' class='help is-large is-danger'>" + message + "</p>");
    }
}

function removeErrors(){
    if($('#name_error').length) {
        $('#fld_username').removeClass('is-danger');
        $('#name_error').remove();
    }
    else if($('#pwd_error').length) {
        $('#fld_password').removeClass('is-danger');
        $('#pwd_error').remove();
    }
    else if($('#internal_error').length) {
        $('#internal_error').remove();
    }
}