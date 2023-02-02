
function getInventoryReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory/report";
}



function getInventoryList(){
	var url = getInventoryReportUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		console.log("Inventory data fetched");
	   		console.log(data);
	   		displayInventoryReportList(data);     //...
	   },
	   error: function(){
	   	        alert(error.responseJSON.message);

	   }
	});
}

//UI DISPLAY METHODS

function displayInventoryReportList(data){
	console.log('Printing inventory data');
	var $tbody = $('#inventory-report-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
	    console.log(e)
		var row = '<tr>'
		+ '<td>' + e.brandName + '</td>'
		+ '<td>' + e.category + '</td>'
		+ '<td>'  + e.quantity + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
	paginate();
}





//INITIALIZATION CODE
function init(){

}
function downloadInventoryReport(){
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

$(document).ready(init);
$(document).ready(getInventoryList);


