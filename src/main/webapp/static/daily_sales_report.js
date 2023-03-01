
function getDailySalesReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/report/dailysales";
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
		+ '<td>' + e.date.split("T")[0] + '</td>'
		+ '<td>' + e.invoicedOrdersCount + '</td>'
		+ '<td>'  + e.invoicedItemsCount + '</td>'
		+ '<td>'  + roundToTwo(e.totalRevenue) + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}


function downloadDailySalesReport(){
tableToCSV(document, "inventory-report-table", 4);
}


function filter(){
	var url = getDailySalesReportUrl();
	data = {};
	data["startDate"] = new Date($("#input-start-date").val());
	data["endDate"] = new Date($("#input-end-date").val());

    var $form = $("#daily-report-form");
    if(!validateForm($form))
        return;

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
	   	        makeToast(false, error.responseJSON.message, null);
	   }
	});
}

function init(){
	$('#filter').click(filter);
    dateLimit(document, ["input-start-date", "input-end-date"]);
    formatDate();
}


$(document).ready(init);


