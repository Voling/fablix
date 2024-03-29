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
  console.log(resultDataJson["total"]);
  $("#totalprice").html(`Total price${resultDataJson["total"]}`);

  // show the session information
  $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
  $("#lastAccessTime").text(
    "Last access time: " + resultDataJson["lastAccessTime"]
  );
  //let fullmoviedata = {};
  handleCartArray(resultDataJson["previousItems"]);
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultArray) {
  console.log(resultArray);

  console.log(resultArray.length);
  let item_list = $("#item_list");
  // change it to html list
  let rowHTML = "";
  for (let i = 0; i < resultArray.length; i++) {
    let eacharray = resultArray[i];
    console.log(`index${i}`);
    //let eacharray = JSON.parse(resultArray[i]);
    console.log(eacharray);
    rowHTML += `<tr class= "row${i} entire">`;
    rowHTML +=
      `<th class="rounded-th movietitle row${i}">` +
      // Add a link to single-star.html with id passed with GET url parameter
      '<a href="single-movie.html?id=' +
      eacharray["movieid"] +
      '">' +
      eacharray["title"] + // display star_name for the link text
      "</a>" +
      "</th>";
    rowHTML +=
      `<th class="rounded-th movieyear row${i}">` + eacharray["year"] + "</th>";
    rowHTML +=
      `<th class="rounded-th moviedirector row${i}">` +
      eacharray["director"] +
      "</th>";
    rowHTML +=
      `<th class="rounded-th movieamount row${i}">` +
      eacharray["amount"] +
      "</th>";
    rowHTML +=
      `<th class="rounded-th movieid row${i}">` +
      eacharray["movieid"] +
      "</th>";
    rowHTML +=
      `<th class="rounded-th movieprice row${i}">` +
      eacharray["price"] +
      "</th>";
    rowHTML +=
      `<th class="rounded-th movieaction row${i}">` +
      `<button class="plus"  data-row="${i}">+</button>` +
      `<button class="minus"  data-row="${i}">-</button>` +
      `<button class="del" data-row="${i}">deleta all</button>` +
      "</th>";

    rowHTML += "<tr>";
  }
  console.log(rowHTML);
  // clear the old array and show the new array in the frontend
  item_list.append(rowHTML);
}

$(document).ready(function () {
  $("body").on("click", "#checkout-button", (event) => {
    window.location.replace("payment.html");
  });

  $("body").on("click", ".plus", (event) => {
    const i = event.target.getAttribute("data-row");
    let movieid = $(`.movieid.row${i}`).html();
    console.log(movieid);
    console.log(i);
    let title = $(`.movietitle.row${i}`).html();
    let year = $(`.movieyear.row${i}`).html();
    let director = $(`.moviedirector.row${i}`).html();
    let amount = $(`.movieamount.row${i}`).html();
    let price = $(`.movieprice.row${i}`).html();
    increment(movieid, title, year, director, amount, i, price);
  });

  $("body").on("click", ".minus", (event) => {
    const i = event.target.getAttribute("data-row");
    console.log(i);
    //console.log(movieid);
    //console.log(i);
    let movieid = $(`.movieid.row${i}`).html();
    console.log(movieid);
    let title = $(`.movietitle.row${i}`).html();
    let year = $(`.movieyear.row${i}`).html();
    let director = $(`.moviedirector.row${i}`).html();
    let amount = $(`.movieamount.row${i}`).html();
    let price = $(`.movieprice.row${i}`).html();
    decrement(movieid, title, year, director, amount, i, price);
  });

  $("body").on("click", ".del", (event) => {
    const i = event.target.getAttribute("data-row");
    console.log(i);
    let movieid = $(`.movieid.row${i}`).html();
    console.log(movieid);
    //console.log(i);
    let title = $(`.movietitle.row${i}`).html();
    let year = $(`.movieyear.row${i}`).html();
    let director = $(`.moviedirector.row${i}`).html();
    let amount = $(`.movieamount.row${i}`).html();
    let price = $(`.movieprice.row${i}`).html();
    remove(movieid, title, year, director, amount, i, price);
  });
});

function increment(movieid, title, year, director, amount, i, price) {
  jQuery.ajax("cart", {
    dataType: "json", // Setting return data type
    method: "POST",
    data: {
      movieid: movieid,
      title: title,
      director: director,
      year: year,
      price: price,
      operation: "add",
    },
    success: (data) => {
      console.log("success");
      amount = parseInt(amount);
      console.log(amount + 1);
      $(`.movieamount.row${i}`).html(`${amount + 1}`);
      $("#totalprice").html(`Total price${data["total"]}`);
    },
  });
}
function decrement(movieid, title, year, director, amount, i, price) {
  jQuery.ajax("cart", {
    dataType: "json", // Setting return data type
    method: "POST",
    data: {
      movieid: movieid,
      title: title,
      director: director,
      year: year,
      price: price,
      operation: "minus",
    },
    success: (data) => {
      amount = parseInt(amount);
      $(`.movieamount.row${i}`).html(`${amount - 1}`);
      $("#totalprice").html(`Total price${data["total"]}`);
    },
  });
}
function remove(movieid, title, year, director, amount, i, price) {
  console.log("deleted");
  jQuery.ajax("cart", {
    dataType: "json", // Setting return data type
    method: "POST",
    data: {
      movieid: movieid,
      title: title,
      director: director,
      year: year,
      price: price,
      operation: "remove",
    },
    success: (data) => {
      $(`.row${i}.entire`).html("");
      $("#totalprice").html(`Total price${data["total"]}`);
    },
  });
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
