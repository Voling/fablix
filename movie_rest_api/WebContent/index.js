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

function handle(resultData){

    handlemovieResult(transformdata(resultData));

}

function transformdata(resultdata){
    let movie = ""
    let prevstar = ""
    let prevgenre = ""
    let array = []
    let length = -1
    for (let i = 0; i < resultdata.length; i++){
        if(resultdata[i].id != movie){
            array.push({ title: resultdata[i].title,id:resultdata[i].id ,year:resultdata[i].year, director:resultdata[i].director,rating:resultdata[i].rating,genres:resultdata[i].genre,cast:[[resultdata[i].star,resultdata[i].starid]]})
            movie = resultdata[i].id
            prevgenre = resultdata[i].genre;
            prevstar = resultdata[i].star;
            length += 1;
        }
        else{
            if(resultdata[i].genre != prevgenre){
                array[length].genres += " " + resultdata[i].genre
                prevgenre = resultdata[i].genre
            }
            if(resultdata[i].star != prevstar){
                array[length].cast.push([resultdata[i].star,resultdata[i].starid])
                prevstar = resultdata[i].star
                console.log(resultdata[i].starid)
            }
        }
    }
    console.log(array)
    return array

}
function handlemovieResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#movie_table_body");
    console.log(resultData)
    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th class=\"rounded-th\">" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['id'] + '">'
            + resultData[i]["title"] +     // display star_name for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th class=\"rounded-th\">" + resultData[i]["year"] + "</th>";
        rowHTML += "<th class=\"rounded-th\">" + resultData[i]["director"] + "</th>";
        rowHTML += "<th class=\"rounded-th\">" + resultData[i]["genres"]+ "</th>";
        rowHTML += "<th class=\"rounded-th\">" + '<a href="single-star.html?id=' + resultData[i]["cast"][0][1]+ '">'
            + resultData[i]["cast"][0][0] +     // display star_name for the link text
            '</a>'+  " " + '<a href="single-star.html?id=' + resultData[i]["cast"][1][1]+ '">'
            + resultData[i]["cast"][1][0] +     // display star_name for the link text
            '</a>' + " " + '<a href="single-star.html?id=' + resultData[i]["cast"][2][1]+ '">'
            + resultData[i]["cast"][2][0] +    // display star_name for the link text
            '</a>'+ "</th>";
        rowHTML += "<th class=\"rounded-th\">" + resultData[i]["rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handle(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});
