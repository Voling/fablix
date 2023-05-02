
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "clear", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => {
        console.log("success")
        console.log(resultData)
        $(document).ready(function() {
            let tableBody = $("#tableBody");
            previousItems = resultData.previousItems; //has previous items key
            let allMovies = new Map();

            for (let i = 0; i < previousItems.length; i++) {
                let row = $("<tr>");
                row.append($("<td>").text(previousItems[i].title));
                row.append($("<td>").text(previousItems[i].amount));
                row.append($("<td>").text(previousItems[i].price));
                tableBody.append(row);
            }
            let row = $("<tr>");
            for (let i = 0; i < 3; i++) {
                row.append($("<td>"));
            }
            row.append($("<td>").text(resultData.total));
        })

    }, // Setting callback function to handle data returned successfully by the StarsServlet
});
