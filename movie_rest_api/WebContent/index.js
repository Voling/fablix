/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
let pagenum = 1;
let lastused = "";
let lastbrowsed = "";
let pagesize = 20;
let sorttype = "ranking";
let sortorder = "DESC";
function handle(resultData) {
  handleMovieResult(transformdata(resultData));
}

function transformdata(resultdata) {
  let movie = "";
  let prevstar = "";
  let prevgenre = "";
  let array = [];
  let length = -1;

  for (let i = 0; i < resultdata.length; i++) {
    console.log(resultdata[i]);
    if (resultdata[i].id != movie) {
      array.push({
        title: resultdata[i].title,
        id: resultdata[i].id,
        year: resultdata[i].year,
        director: resultdata[i].director,
        rating: resultdata[i].rating,
        genres: [resultdata[i].genre],
        cast: [[resultdata[i].star, resultdata[i].starid]],
        price: resultdata[i].price,
      });
      movie = resultdata[i].id;
      prevgenre = resultdata[i].genre;
      prevstar = resultdata[i].star;
      length += 1;
    } else {
      if (resultdata[i].genre != prevgenre) {
        array[length].genres.push(resultdata[i].genre);
        prevgenre = resultdata[i].genre;
      }
      if (resultdata[i].star != prevstar) {
        array[length].cast.push([resultdata[i].star, resultdata[i].starid]);
        prevstar = resultdata[i].star;
        //console.log(resultdata[i].starid);
      }
    }
  }
  for (let b = 0; b < array.length; b++) {
    array[b].genres.sort();
  }
  console.log(`beforehandle: ${JSON.stringify(array)}`);
  return array;
}
function handleMovieResult(resultData) {
  console.log("handleStarResult: populating star table from resultData");

  // Populate the star table
  // Find the empty table body by id "star_table_body"
  let starTableBodyElement = jQuery("#movie_table_body");
  console.log(resultData);
  // Iterate through resultData, no more than 10 entries
  let rowHTML = "";
  for (let i = 0; i < resultData.length; i++) {
    // Concatenate the html tags with resultData jsonObject
    console.log(`index${i}`);
    rowHTML += "<tr>";
    rowHTML +=
      '<th class="rounded-th">' +
      // Add a link to single-star.html with id passed with GET url parameter
      '<a href="single-movie.html?id=' +
      resultData[i]["id"] +
      '">' +
      resultData[i]["title"] + // display star_name for the link text
      "</a>" +
      "</th>";
    rowHTML += '<th class="rounded-th">' + resultData[i]["year"] + "</th>";
    rowHTML += '<th class="rounded-th">' + resultData[i]["director"] + "</th>";
    rowHTML += '<th class="rounded-th">';
    for (let a = 0; a < resultData[i]["genres"].length; a++) {
      rowHTML += resultData[i]["genres"][a] + " ";
    }
    rowHTML += "</th>";
    //console.log(resultData[i]["cast"]);
    rowHTML += '<th class="rounded-th">';
    if (resultData[i]["cast"][0] != null) {
      rowHTML +=
        '<a href="single-star.html?id=' +
        resultData[i]["cast"][0][1] +
        '">' +
        resultData[i]["cast"][0][0] + // display star_name for the link text
        "</a>";
    }
    if (resultData[i]["cast"][1] != null) {
      rowHTML +=
        " " +
        '<a href="single-star.html?id=' +
        resultData[i]["cast"][1][1] +
        '">' +
        resultData[i]["cast"][1][0] + // display star_name for the link text
        "</a>";
    }
    if (resultData[i]["cast"][2] != null) {
      rowHTML +=
        " " +
        '<a href="single-star.html?id=' +
        resultData[i]["cast"][2][1] +
        '">' +
        resultData[i]["cast"][2][0] + // display star_name for the link text
        "</a>" +
        "</th>";
    }
    rowHTML += '<th class="rounded-th">' + resultData[i]["rating"] + "</th>";
    rowHTML += '<th class="rounded-th">' + resultData[i]["price"] + "</th>";
    rowHTML += "</tr>";

    // Append the row created to the table body, which will refresh the page
  }
  starTableBodyElement.html(rowHTML);
}
function submitsearch(event) {
  event.preventDefault();
  console.log($("#search"));
  let formData = $("#search").serializeArray();
  console.log("form" + formData);
  let data = "";
  let counter = 0;
  for (let field of formData) {
    //console.log(`type:${field.TYPE}`)
    if (field.value === "") {
    } else {
      if (counter != 0) {
        data += "&";
      }
      if (field.name === "director" || field.name === "star") {
        console.log("hey soul sister");
        //data[field.name] = "%" + field.value + "%"; // Wrap the title value with % characters
        data += field.name + "=%25" + field.value + "%25";
      } else {
        data += field.name + "=" + field.value;
      }
      counter += 1;
    }
  }
  data += `&page=${pagenum}`;
  data += `&pagesize=${pagesize}&sort=${sorttype}&order=${sortorder}`;
  console.log("here!hey");
  console.log(data);
  jQuery.ajax({
    dataType: "json",
    url: "search?" + data, // Your server-side script that processes the search
    type: "GET",
    success: (resultData) => {
      console.log(resultData);
      handle(resultData);
    },
  });
}
function submitbrowse(event) {
  console.log(pagenum);
  jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: `browse?page=${pagenum}&type=genre&term=${lastbrowsed}&pagesize=${pagesize}&sort=${sorttype}&order=${sortorder}`, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => {
      console.log(resultData);
      handle(resultData);
    }, // Setting callback function to handle data returned successfully by the StarsServlet
  });
}
function submitbrowsetitle(event) {
  console.log(pagenum);
  jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: `browse?page=${pagenum}&type=title&term=%25${lastbrowsed}%25&pagesize=${pagesize}&sort=${sorttype}&order=${sortorder}`, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => {
      console.log(resultData);
      handle(resultData);
    }, // Setting callback function to handle data returned successfully by the StarsServlet
  });
}
function submitbrowsetitlea(event) {
  console.log(pagenum);
  jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: `browse?page=${pagenum}&type=title&term=${lastbrowsed}%25&pagesize=${pagesize}&sort=${sorttype}&order=${sortorder}`, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => {
      console.log(resultData);
      handle(resultData);
    }, // Setting callback function to handle data returned successfully by the StarsServlet
  });
}
function updatepagesize() {
  const selectElement = document.getElementById("numberSelect");
  console.log("updating");
  pagesize = parseInt(selectElement.value);
}
function updatesorttype() {
  const selectElement = document.getElementById("typeselect");
  console.log(selectElement.value);
  sorttype = selectElement.value;
}
function updatesortorder() {
  const selectElement = document.getElementById("orderselect");

  console.log(selectElement.value);
  sortorder = selectElement.value;
}
$(document).ready(function () {
  //find button and attach logout fx to it
  $("#logoutButton").click(function () {
    $.ajax({
      url: "api/logout",
      type: "POST",
      success: function (response) {
        window.location.replace("login.html");
      },
    });
  });
  $("#searchSubmit").click(function (event) {
    event.preventDefault();
    console.log($("#search"));
    let formData = $("#search").serializeArray();
    console.log("form" + formData);
    let data = "";
    let counter = 0;
    for (let field of formData) {
      //console.log(`type:${field.TYPE}`)
      if (field.value === "") {
      } else {
        if (counter != 0) {
          data += "&";
        }
        if (field.name === "director" || field.name === "star") {
          console.log("hey soul sister");
          //data[field.name] = "%" + field.value + "%"; // Wrap the title value with % characters
          data += field.name + "=%25" + field.value + "%25";
        } else {
          data += field.name + "=" + field.value;
        }
        counter += 1;
      }
    }
    data += `&pagesize=${pagesize}&sort=${sorttype}&order=${sortorder}`;
    if (lastused != "search") {
      lastused = "search";
      pagenum = 1;
    }
    console.log("here!hey");
    console.log(data);
    jQuery.ajax({
      dataType: "json",
      url: "search?" + data, // Your server-side script that processes the search
      type: "GET",
      success: (resultData) => {
        console.log(resultData);
        handle(resultData);
      },
    });
  });

  $("#prev").click((event) => {
    event.preventDefault();
    if (pagenum != 1) {
      if (lastused == "") {
        pagenum -= 1;
        jQuery.ajax({
          dataType: "json", // Setting return data type
          method: "GET", // Setting request method
          url: `api/movies?page=${pagenum}&pagesize=${pagesize}&sort=${sorttype}&order=${sortorder}`, // Setting request url, which is mapped by StarsServlet in Stars.java
          success: (resultData) => {
            console.log(resultData);
            handle(resultData);
          }, // Setting callback function to handle data returned successfully by the StarsServlet
        });
      }
      if (lastused == "search") {
        pagenum -= 1;
        submitsearch(event);
      }
      if (lastused == "browse") {
        pagenum -= 1;
        submitbrowse(event);
      }
      if (lastused == "browsetitle") {
        pagenum -= 1;
        submitbrowsetitle(event);
      }
      if (lastused == "browsetitle-a") {
        pagenum -= 1;
        submitbrowsetitlea(event);
      }
    }
  });

  $("#next").click((event) => {
    event.preventDefault();
    if (lastused == "") {
      pagenum += 1;
      jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: `api/movies?page=${pagenum}&pagesize=${pagesize}&sort=${sorttype}&order=${sortorder}`, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => {
          console.log(resultData);
          handle(resultData);
        }, // Setting callback function to handle data returned successfully by the StarsServlet
      });
    }
    if (lastused == "search") {
      pagenum += 1;
      submitsearch(event);
    }
    if (lastused == "browse") {
      pagenum += 1;
      submitbrowse(event);
    }
    if (lastused == "browsetitle") {
      pagenum += 1;
      submitbrowsetitle(event);
    }
    if (lastused == "browsetitle-a") {
      pagenum += 1;
      submitbrowsetitlea(event);
    }
  });
  $(".agenre").click((event) => {
    let selectedItemText = $(event.target).text();

    //let decodedText = decodeURIComponent(selectedItemText);
    if (lastused != "browse" || lastbrowsed != selectedItemText) {
      lastused = "browse";
      lastbrowsed = selectedItemText;
      pagenum = 1;
    }
    jQuery.ajax({
      dataType: "json", // Setting return data type
      method: "GET", // Setting request method
      url: `browse?page=${pagenum}&type=genre&term=${selectedItemText}&pagesize=${pagesize}&sort=${sorttype}&order=${sortorder}`, // Setting request url, which is mapped by StarsServlet in Stars.java
      success: (resultData) => {
        console.log(resultData);
        handle(resultData);
      }, // Setting callback function to handle data returned successfully by the StarsServlet
    });
  });
  $(".title-n").click((event) => {
    let selectedItemText = $(event.target).text();
    console.log(selectedItemText);
    if (lastused != "browsetitle" || lastbrowsed != selectedItemText) {
      lastused = "browsetitle";
      lastbrowsed = selectedItemText;
      pagenum = 1;
    }
    jQuery.ajax({
      dataType: "json", // Setting return data type
      method: "GET", // Setting request method
      url: `browse?page=${pagenum}&type=title&term=%25${selectedItemText}%25&pagesize=${pagesize}&sort=${sorttype}&order=${sortorder}`, // Setting request url, which is mapped by StarsServlet in Stars.java
      success: (resultData) => {
        console.log(resultData);
        handle(resultData);
      }, // Setting callback function to handle data returned successfully by the StarsServlet
    });
  });
  $(".title-a").click((event) => {
    let selectedItemText = $(event.target).text();
    console.log(selectedItemText);
    if (lastused != "browsetitle-a" || lastbrowsed != selectedItemText) {
      lastused = "browsetitle-a";
      lastbrowsed = selectedItemText;
      pagenum = 1;
    }
    jQuery.ajax({
      dataType: "json", // Setting return data type
      method: "GET", // Setting request method
      url: `browse?page=${pagenum}&type=title&term=${selectedItemText}%25&pagesize=${pagesize}&sort=${sorttype}&order=${sortorder}`, // Setting request url, which is mapped by StarsServlet in Stars.java
      success: (resultData) => {
        console.log(resultData);
        handle(resultData);
      }, // Setting callback function to handle data returned successfully by the StarsServlet
    });
  });
  $("#checkout-button").click(function () {
    window.location.href = "cart.html";
  });
});
/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
  dataType: "json", // Setting return data type
  method: "GET", // Setting request method
  url: "api/movies?page=1&pagesize=20&sort=ranking&order=DESC", // Setting request url, which is mapped by StarsServlet in Stars.java
  success: (resultData) => {
    console.log(resultData);
    if (lastused != "") {
      pagenum = 1;
      lastused = "";
    }
    handle(resultData);
  }, // Setting callback function to handle data returned successfully by the StarsServlet
});
