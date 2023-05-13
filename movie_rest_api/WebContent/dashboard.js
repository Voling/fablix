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
  dataType: "json",
  success: function (response) {
    let metadataTable = $("#metadataTable");
    let rowHTML = "";
    console.log(response);
    for (const tablename in response) {
      rowHTML += "<tr>";
      rowHTML += '<th class="rounded-th">' + tablename + "</th>";
      attribute_array = response[tablename];
      for (let i = 0; i < attribute_array.length; i++) {
        if (i === 0) {
          rowHTML +=
            '<th class="rounded-th">' + attribute_array[i]["name"] + "</th>";
          rowHTML +=
            '<th class="rounded-th">' + attribute_array[i]["type"] + "</th>";
        } else {
          rowHTML += "</tr>";
          rowHTML += '<th class="rounded-th">' + " " + "</th>";
          rowHTML +=
            '<th class="rounded-th">' + attribute_array[i]["name"] + "</th>";
          rowHTML +=
            '<th class="rounded-th">' + attribute_array[i]["type"] + "</th>";
        }
        rowHTML += "</tr>";
      }
    }

    metadataTable.html(rowHTML);
  },
});
