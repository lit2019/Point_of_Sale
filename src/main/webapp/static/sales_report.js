
function getSalesReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/report/sales";
}
function getBrandListUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brands/distinct";
}

function getBrandCategoryListUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brands/search";
}

//UI DISPLAY METHODS

function displaySalesReportList(data){
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
		+ '<td>'  + roundToTwo(e.revenue) + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

//INITIALIZATION CODE
function init(){

}
function downloadSalesReport(){
tableToCSV(document, "inventory-report-table", 4)
}

function openFilter(){
    $('#filter-modal').modal('toggle');

}

function filter(){
	var url = getSalesReportUrl();
	data = {};
	if($("#form-brand-select").val().trim()!=""){
	    data["brandName"] = $("#form-brand-select").val();
	}
	if($("#form-category-select").val().trim()!=""){
	    data["category"] = $("#form-category-select").val();
	}
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
	   		displaySalesReportList(data);     //...
	   },
	   error: function(error){
	   	    makeToast(false, error.responseJSON.message, null);
	   }
	});
}

function init(){
	$('#open-filter').click(openFilter);
	$('#filter').click(filter);
    dateLimit(document, ["input-start-date", "input-end-date"]);

    formatDate();
}


$(document).ready(init);