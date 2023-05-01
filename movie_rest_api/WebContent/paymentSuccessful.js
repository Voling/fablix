
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "clear", // Setting request url, which is mapped by StarsServlet in Stars.java
            success: (resultData) => {
             console.log("success")
            }, // Setting callback function to handle data returned successfully by the StarsServlet
          });
          