$(document).ready(function () {
    //find button and attach logout fx to it
    $("#submitPaymentInfo").click(function () {
        $.ajax({
            url: "api/payment",
            type: "POST",
            success: function (response) {
                window.location.replace("paymentSuccess.html"); //dne
            },
        });
    });
});