
function getSalesReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/orders/sales/report";
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
		+ '<td>'  + e.revenue + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
	paginate();
}

//INITIALIZATION CODE
function init(){

}
function downloadSalesReport(){
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
function emptyDropdown(dropDown){
     dropDown.empty();
     dropDown.append($("<option></option>")
                    .attr("value", null)
                    .text("select"));
     dropDown.val("select");
}

function makeDropdowns(dropDownBrand,initialBrand,dropDownCategory,initialCategory){
    emptyDropdown(dropDownBrand);
    emptyDropdown(dropDownCategory);
    url = getBrandListUrl();

    $.ajax({
       url: url,
       type: 'GET',
       headers: {
        'Content-Type': 'application/json'
       },
       success: function(data) {
            var selectValues = {}
            console.log(data);
            for(var value in data){
                selectValues[data[value]] = data[value];
            }
            $.each(selectValues, function(key, value) {
                 dropDownBrand.append($("<option></option>")
                                .attr("value", key)
                                .text(value));
            });
            if(initialBrand!=null){
                dropDownBrand.val(initialBrand);
            }

            makeCategoryDropDown(initialBrand,dropDownCategory,initialCategory);
       },
       error: function(error){

       }
    });
}
$("#form-brand-select").change(function () {
     makeCategoryDropDown($("#form-brand-select").val(),$("#form-category-select"),null);
});
function getSelectedIndex(id){
    return document.getElementById(id).selectedIndex
}

function makeCategoryDropDown(brandName,dropDownCategory,initialCategory){
        emptyDropdown(dropDownCategory);
        url = getBrandCategoryListUrl();

	data = {"name":brandName, "category":null};

	json = JSON.stringify(data);
    console.log(data);
	$.ajax({
	   url: url,
	   type: 'POST',
	   data:json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(data) {
            var selectValues = {}
            console.log(data);
            for(var i in data){
                var e = data[i];
                var value = e.category;
                selectValues[value] = value;
            }
            $.each(selectValues, function(key, value) {
                 dropDownCategory.append($("<option></option>")
                                .attr("value", key)
                                .text(value));
            });
            if(initialCategory!=null){
                dropDownCategory.val(initialCategory);
            }
	   },
	   error: function(error){
	   	    alert(error.responseJSON.message);
	   }
	});
}

function filter(){
	var url = getSalesReportUrl();
	data = {};
	if(getSelectedIndex("form-brand-select")!=0){
	    data["brandName"] = $("#form-brand-select").val();
	}
	if(getSelectedIndex("form-category-select")!=0){
	    data["category"] = $("#form-category-select").val();
	}
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
	   		displaySalesReportList(data);     //...
	   },
	   error: function(error){

	   	        alert(error.responseJSON.message);
	   }
	});
}

function init(){
	$('#open-filter').click(openFilter);
	$('#filter').click(filter);
    makeDropdowns($("#form-brand-select"),null,$("#form-category-select"),null);

}


$(document).ready(init);
$(document).ready(filter);


