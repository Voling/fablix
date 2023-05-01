$(document).ready(function () {
    //find button and attach logout fx to it
    $("#submitPaymentInfo").click(function (event) {
        event.preventDefault();
        $.ajax({
            url: "payment",
            type: "POST",
            data: $("#paysub").serialize(),
            success: function (response) {
                window.location.replace("paymentSuccess.html"); //dne
            },
        });
    });
    $('#checkout-button').click(function() {
        window.location.href = 'cart.html';
    });
});
