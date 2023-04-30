/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */

/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
let thetitle = "";
let theyear = -1;
let thedirector = "";
//let the_id = "";
function getParameterByName(target) {
  // Get request URL
  let url = window.location.href;
  // Encode target parameter name to url encoding
  target = target.replace(/[\[\]]/g, "\\$&");

  // Ues regular expression to find matched parameter value
  let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
    results = regex.exec(url);
  if (!results) return null;
  if (!results[2]) return "";

  // Return the decoded parameter value
  return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handle(resultData) {
  console.log("handleResult: populating single movie info from resultData");

  // populate the star info h3
  // find the empty h3 body by id "star_info"
  let movieInfoElement = jQuery("#movie_info");
  let allGenres = "";
  thetitle = resultData[0]["movie_title"];
  theyear = resultData[0]["movie_year"];
  thedirector = resultData[0]["movie_director"];
  for (let i = 0; i < resultData[0]["movie_genres"].length; i++) {
    allGenres += resultData[0]["movie_genres"][i] + " ";
  }
  // append two html <p> created to the h3 body, which will refresh the page
  movieInfoElement.append(
    '<p style = "color:#ffc107" id = "thetitle">Title: ' +
      resultData[0]["movie_title"] +
      "</p>" +
      '<p style = "color:#ffc107">Genres: ' +
      allGenres +
      "</p>" +
      '<p style = "color:#ffc107" id = "theyear">Year: ' +
      resultData[0]["movie_year"] +
      "</p>" +
      '<p style = "color:#ffc107">Rating: ' +
      resultData[0]["movie_rating"] +
      "</p>" +
      '<p style = "color:#ffc107" id = "thedirector">Director: ' +
      resultData[0]["movie_director"] +
      "</p>"
  );

  console.log("handleResult: populating movie table from resultData");

  // Populate the star table
  // Find the empty table body by id "movie_table_body"
  let movieTableBodyElement = jQuery("#star_table_body");

  // Concatenate the html tags with resultData jsonObject to create table rows
  for (let i = 0; i < Math.min(10, resultData[0]["starsInMovie"].length); i++) {
    let rowHTML = "";
    rowHTML += "<tr>";
    rowHTML +=
      "<th>" +
      '<a href="single-star.html?id=' +
      resultData[0]["starsInMovie"][i]["starid"] +
      '">' +
      resultData[0]["starsInMovie"][i]["starname"] +
      "</a>" +
      "</th>";
    rowHTML += "</tr>";

    // Append the row created to the table body, which will refresh the page
    movieTableBodyElement.append(rowHTML);
  }
}
$(document).ready(() => {
  $("#purchase").click(() => {
    console.log("hey yo");
    const movieObject = {
      movieid: movieId,
      amount: 1,
      title: thetitle,
      director: thedirector,
      year: theyear,
    };
    // Stringify the object to prepare it for sending in the request
    const movieObjectString = JSON.stringify(movieObject);
    jQuery.ajax(
      "cart",
      {
        dataType: "json", // Setting return data type
        method: "POST",
        data: { item: movieObjectString },
        // Setting request method
        //url: "api/cart", // Setting request url, which is mapped by StarsServlet in Stars.java
        success: () => window.location.replace("cart.html"),
      } // Setting callback function to handle data returned successfully by the StarsServlet
    );
    $("#checkout-button").onclick(function(){
      window.location.replace("cart.html");
    });
  });
});
/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName("id");

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
  dataType: "json", // Setting return data type
  method: "GET", // Setting request method
  url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
  success: (resultData) => handle(resultData), // Setting callback function to handle data returned successfully by the SingleStarServlet
});
