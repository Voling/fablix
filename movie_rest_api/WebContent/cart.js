//const { json } = require("express");
//bruh what why require express, what the hell man
let cart = $("#cart");

/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleSessionData(resultDataJson) {
  //let resultDataJson = JSON.parse(resultDataString);

  console.log("handle session response");
  console.log(resultDataJson);
  console.log(resultDataJson["sessionID"]);

  // show the session information
  $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
  $("#lastAccessTime").text(
    "Last access time: " + resultDataJson["lastAccessTime"]
  );
  //let fullmoviedata = {};
  handleCartArray(resultDataJson["previousItems"]);

  /*
  jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "/api/single-movie?id=" + resultDataJson["previousItems"][0].key,
    success: (data) => {
      handleCartArray(data);
    },
  });
  */
  // show cart information
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultArray) {
  console.log(resultArray);

  // "{\"movieid\":\"tt0395642\",\"amount\":1,\"title\":\"Loma Lynda: Episode II\",\"director\":\"Jason Bognacki\",\"year\":\"2004\"}"
  console.log(resultArray.length);
  let item_list = $("#item_list");
  // change it to html list
  let rowHTML = "";
  for (let i = 0; i < resultArray.length; i++) {
    let eacharray = JSON.parse(resultArray[i]);
    console.log(`index${i}`);
    //let eacharray = JSON.parse(resultArray[i]);
    console.log(eacharray);
    rowHTML += "<tr>";
    rowHTML +=
      '<th class="rounded-th">' +
      // Add a link to single-star.html with id passed with GET url parameter
      '<a href="single-movie.html?id=' +
      eacharray["movieid"] +
      '">' +
      eacharray["title"] + // display star_name for the link text
      "</a>" +
      "</th>";
    rowHTML += '<th class="rounded-th">' + eacharray["year"] + "</th>";
    rowHTML += '<th class="rounded-th">' + eacharray["director"] + "</th>";
    rowHTML += '<th class="rounded-th">' + eacharray["amount"] + "</th>";
    rowHTML += "<tr>";

    // each item will be in a bullet point
    //res += "<li>" + resultArray[i] + "</li>";
  }
  console.log(rowHTML);
  // clear the old array and show the new array in the frontend
  item_list.append(rowHTML);
}

/**
 * Submit form content with POST method
 * @param cartEvent
 */
function handleCartInfo(cartEvent) {
  console.log("submit cart form");
  /**
   * When users click the submit button, the browser will not direct
   * users to the url defined in HTML form. Instead, it will call this
   * event handler when the event is triggered.
   */
  cartEvent.preventDefault();

  $.ajax("cart", {
    method: "POST",
    data: cart.serialize(),
    success: (resultDataString) => {
      let resultDataJson = JSON.parse(resultDataString);
      handleCartArray(resultDataJson["previousItems"]);
    },
  });

  // clear input form
  cart[0].reset();
}

$.ajax("cart", {
  method: "GET",
  dataType: "json",
  success: handleSessionData,
});

// Bind the submit action of the form to a event handler function
cart.submit(handleCartInfo);
