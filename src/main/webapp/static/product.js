
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/products";
}

//BUTTON ACTIONS
function addProduct(event){
	//Set the values to update
	var $form = $("#product-form");
	var json = toJson($form);
	console.log(json);
	var url = getProductUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		console.log("Product created");	
	   		getProductList();     //...
	   },
	   error: function(){
	   		alert("An error has occurred");
	   }
	});

	return false;
}

function updateProduct(event){
	$('#edit-product-modal').modal('toggle');
	//Get the ID
	var id = $("#product-edit-form input[name=id]").val();	
	var url = getProductUrl() + "/" + id;

	//Set the values to update
	var $form = $("#product-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		console.log("Product update");	
	   		getProductList();     //...
	   },
	   error: function(){
	   		alert("An error has occurred");
	   }
	});

	return false;
}


function getProductList(){
	var url = getProductUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		console.log("Product data fetched");
	   		console.log(data);	
	   		displayProductList(data);     //...
	   },
	   error: function(){
	   		alert("An error has occurred");
	   }
	});
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){
	var file = $('#productFile')[0].files[0];
	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results){
	fileData = results.data;
	uploadRows();
}

function uploadRows(){
	//Update progress
	updateUploadDialog();
	//If everything processed then return
	if(processCount==fileData.length){
		return;
	}

	//Process next row
	var row = fileData[processCount];
	processCount++;

	var json = JSON.stringify(row);
	var url = getProductUrl();

	//Make ajax call
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		uploadRows();
	   },
	   error: function(response){
	   		row.error=response.responseText
	   		errorData.push(row);
	   		uploadRows();
	   }
	});

}

function downloadErrors(){
	writeFileData(errorData);
}

//UI DISPLAY METHODS

function displayProductList(data){
	console.log('Printing product data');
	var $tbody = $('#product-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var buttonHtml = ' <button onclick="displayEditProduct(' + e.id + ')">edit</button>'
		var row = '<tr>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>' + e.brandName + '</td>'
		+ '<td>'  + e.category + '</td>'
		+ '<td>'  + e.productName + '</td>'
		+ '<td>'  + e.mrp + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}
function displayEditProduct(id){
	var url = getProductUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		console.log("Product data fetched");
	   		console.log(data);
	   		displayProduct(data);     //...
	   },
	   error: function(){
	   		alert("An error has occurred");
	   }
	});
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#productFile');
	$file.val('');
	$('#productFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	//Update counts
	updateUploadDialog();
	getProductList();
}
function updateUploadDialog(){
	$('#rowCount').html("" + fileData.length);
	$('#processCount').html("" + processCount);
	$('#errorCount').html("" + errorData.length);
}

function updateFileName(){
	var $file = $('#productFile');
	var fileName = $file.val();
	$('#productFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog();
	$('#upload-product-modal').modal('toggle');
}
function displayEditProduct(id){
	var url = getProductUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		console.log("Product data fetched");
	   		console.log(data);	
	   		displayProduct(data);     //...
	   },
	   error: function(){
	   		alert("An error has occurred");
	   }
	});	
}

function displayProduct(data){
	$("#product-edit-form input[name=productName]").val(data.productName);
	$("#product-edit-form input[name=brandName]").val(data.brandName);
	$("#product-edit-form input[name=category]").val(data.category);
	$("#product-edit-form input[name=mrp]").val(data.mrp);
	$("#product-edit-form input[name=category]").val(data.category);
	$("#product-edit-form input[name=barcode]").val(data.barcode);
	$('#edit-product-modal').modal('toggle');
}


//HELPER METHOD
function toJson($form){
    var serialized = $form.serializeArray();
    console.log(serialized);
    var s = '';
    var data = {};
    for(s in serialized){
        data[serialized[s]['name']] = serialized[s]['value']
    }
    var json = JSON.stringify(data);
    console.log(json);
    return json;
}


//INITIALIZATION CODE
function init(){
	$('#add-product').click(addProduct);
	$('#update-product').click(updateProduct);
	$('#refresh-data').click(getProductList);
    $('#upload-data').click(displayUploadData);
    $('#process-data').click(processData);
    $('#download-errors').click(downloadErrors);
    $('#productFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(getProductList);

