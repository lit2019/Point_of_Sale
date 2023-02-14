
function getDailySalesReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/dailysales";
}

//UI DISPLAY METHODS

function displayDailySalesReportList(data){
	console.log('Printing inventory data');
	var $tbody = $('#inventory-report-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
	    console.log(e)
		var row = '<tr>'
		+ '<td>' + e.date + '</td>'
		+ '<td>' + e.invoicedOrdersCount + '</td>'
		+ '<td>'  + e.invoicedItemsCount + '</td>'
		+ '<td>'  + e.totalRevenue + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
	paginate();
}

//INITIALIZATION CODE
function init(){

}
function downloadDailySalesReport(){
tableToCSV("inventory-report-table")
}
function tableToCSV(tableId) {

// Variable to store the final csv data
var csv_data = [];

// Get each row data
var rows = document.getElementById(tableId);
for (var i = 0; i < rows.length; i++) {

    // Get each column data
    var cols = rows[i].querySelectorAll('td,th');

    // Stores each csv row data
    var csvrow = [];
    for (var j = 0; j < cols.length; j++) {

        // Get the text data of each cell
        // of a row and push it to csvrow
        csvrow.push(cols[j].innerHTML);
    }

    // Combine each column value with comma
    csv_data.push(csvrow.join(","));
}

// Combine each row data with new line character
csv_data = csv_data.join('\n');

// Call this function to download csv file
downloadCSVFile(csv_data);
}

function paginate(){
      $('#inventory-report-table').DataTable();
      $('.dataTables_length').addClass('bs-select');

}

function openFilter(){
    $('#filter-modal').modal('toggle');

}




function filter(){
	var url = getDailySalesReportUrl();
	data = {};
	data["startDate"] = new Date($("#input-start-date").val());
	data["endDate"] = new Date($("#input-end-date").val());

    json = JSON.stringify(data);
    console.log(json);
//    console.log(json);
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(data) {
	   		console.log("Sales data fetched");
	   		console.log(data);
	   		displayDailySalesReportList(data);     //...
	   },
	   error: function(error){

	   	        alert(error.responseJSON.message);
	   }
	});
}

function init(){
	$('#open-filter').click(openFilter);
	$('#filter').click(filter);

}


$(document).ready(init);


