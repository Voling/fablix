$(document).ready(function() {
    //attach to insert star submit
    $('#insertStarBtn').click(function() {
        let starName = $('#starName').val();
        let birthYear = $('#birthYear').val();

        // clear prev messages
        $('#errorMsg').remove();

        //star insertion
        $.ajax({
            url: '/api/_dashboard',
            type: 'POST',
            data: {
                action: 'insertStar',
                name: starName,
                birthYear: birthYear
            },
            success: function(response) {
                if (response.error) {
                    //display error
                    let errorContainer = $('#errorMsg')
                    errorContainer.innerHTML = response.error;
                } else {
                    //clear fields
                    $('#starName').val('');
                    $('#birthYear').val('');
                }
            },
        });
    });

    //display metadata
    $.ajax({
        url: '/api/_dashboard',
        type: 'POST',
        data: {
            action: 'getMetadata'
        },
        success: function(response) {
            let metadataTable = $('#metadataTable tbody');

            $.each(response, function(tableName, columns) {
                let tableRow = $('<tr>');
                let tableNameCell = $('<td>').text(tableName);
                tableRow.append(tableNameCell);

                $.each(columns, function(index, column) {
                    let attributeCell = $('<td>').text(column.attribute);
                    let typeCell = $('<td>').text(column.type);
                    tableRow.append(attributeCell, typeCell);
                }); //get each column's data

                //append to table
                metadataTable.append(tableRow);
            });
        },
    });
});