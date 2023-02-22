
function getInventoryReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/report/inventory";
}
function getBrandListUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brands/distinct";
}
function getBrandCategoryListUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brands/search";
}

function checkNull(s){
      return (s.trim()==="" ? null : s);
}

function filter(){
	var url = getInventoryReportUrl();
	data = {};
    data["category"] = checkNull($("#form-category-select").val());
    data["brandName"] = checkNull($("#form-brand-select").val());
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
	   		displayInventoryReportList(data);     //...

	   },
	   error: function(error){

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
}
function getSelectedIndex(id){
    return document.getElementById(id).selectedIndex
}


//INITIALIZATION CODE
function init(){

}
function downloadInventoryReport(){
    tableToCSV(document, "inventory-report-table", 3);
}



function init(){
	$('#filter').click(filter);
	filter();
}

$(document).ready(init);



