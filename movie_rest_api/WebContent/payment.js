$(document).ready(function () {
    //find button and attach logout fx to it
    $("#submitPaymentInfo").click(function (event) {
        event.preventDefault();
        $.ajax({
            url: "payment",
            type: "POST",
            dataType:"json",
            data: $("#paysub").serialize(),
            success: function (response) {
                console.log(response)
                if(response['status'] === "success"){
                window.location.replace("paymentSuccessful.html"); //dne
                }
                else{
                    $("#response").html("not correct");
                }
            },
            failure: ()=>{ $("#response").html("not correct");

            }
        });
    });
    $('#checkout-button').click(function() {
        window.location.href = 'cart.html';
    });
});
