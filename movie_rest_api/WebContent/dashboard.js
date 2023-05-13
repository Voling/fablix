$(document).ready(function () {
  //attach to insert star submit
  $("#insertStarButton").click(function (event) {
    event.preventDefault();
    let starName = $("#starName").val();
    let birthYear = $("#starBirthYear").val();
    console.log(starName);
    console.log(birthYear);
    // clear prev messages
    //$("#errorMsg").remove();

    //star insertion
    $.ajax("dashboard", {
      method: "POST",
      dataType: "json",
      // Serialize the login form to the data sent by POST request
      data: {
        name: starName,
        birthYear: birthYear,
      },

      success: function (resultDataJson) {
        console.log(resultDataJson);
        if (resultDataJson["status"] === "success") {
          $("#errorMsg").text("success");
        } else {
          $("#errorMsg").text("error");
        }
      },
    });
  });
});

//display metadata
$.ajax({
  url: "dashboard",
  method: "GET",
  success: function (response) {
    let metadataTable = $("#metadataTable tbody");
  },
});
