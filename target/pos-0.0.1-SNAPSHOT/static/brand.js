
function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brands";
}

function getAddBrandListUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/add-brands";
}

//BUTTON ACTIONS
function addBrand(event){
	//Set the values to update
	var $form = $("#brand-form");
	var json = "["+toJson($form)+"]";
	var url = getAddBrandListUrl();

	$.ajax({
	   url: url, 
	   type: 'POST', 
	   data: json, 
	   headers: {
       	'Content-Type': 'application/json'
       }, 
	   success: function(response) {
	        $("#brand-form-modal").hide();
	   		console.log("Brand created");
	   		getBrandList();
	   }, 
	   error: function(error){
	        alert(error.responseJSON.message);
	   }
	});

	return false;
}

function updateBrand(event){
	$('#edit-brand-modal').modal('toggle');
	//Get the ID
	var id = $("#brand-edit-form input[name=id]").val();	
	var url = getBrandUrl() + "/" + id;

	//Set the values to update
	var $form = $("#brand-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url, 
	   type: 'PUT', 
	   data: json, 
	   headers: {
       	'Content-Type': 'application/json'
       }, 	   
	   success: function(response) {
	   		console.log("Brand update");	
	   		getBrandList();     //...
	   }, 
	   error: function(){
	   	        alert(error.responseJSON.message);

	   }
	});

	return false;
}

function getBrandList(){
	var url = getBrandUrl();
	$.ajax({
	   url: url, 
	   type: 'GET', 
	   success: function(data) {
	   		console.log("Brand data fetched");
	   		console.log(data);	
	   		displayBrandList(data);     //...
	   }, 
	   error: function(){
	   	        alert(error.responseJSON.message);

	   }
	});
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = "";
var processCount = 0;


function processData(){
	var file = $('#brandFile')[0].files[0];
	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results){
	fileData = results.data;
	uploadRows();
}

function uploadRows(){
	var json = JSON.stringify(fileData);
	var url = getAddBrandListUrl();

	//Make ajax call
	$.ajax({
	   url: url, 
	   type: 'POST', 
	   data: json, 
	   headers: {
       	'Content-Type': 'application/json'
       }, 	   
	   success: function(response) {
	        $('#upload-brand-modal').modal('toggle');
	   		makeToast(true, "TSV uploaded", null);
            getBrandList();

	   }, 
	   error: function(error){
	        $('#upload-brand-modal').modal('toggle');
	        var message =  error.responseJSON.message;
	        errorData = message;
	        var pos1 = message.indexOf("\n");
            var pos2 = message.indexOf("\n", pos1 + 1);
            message = message.slice(0, pos2);
            message += "...."
	   		makeToast(false, message, downloadErrors);
	   }
	});
}

function downloadErrors(){
	writeFileData(errorData);
}
//UI DISPLAY METHODS

function displayBrandList(data){
	console.log('Printing brand data');
	var $tbody = $('#brand-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
	    var buttonHtml ='<button class="btn" onclick="displayEditBrand(' + e.id + ')"><i class="fa fa-edit"></i> edit</button>'
		var row = '<tr>'
		+ '<td>' + e.name + '</td>'
		+ '<td>'  + e.category + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

function displayEditBrand(id){
	var url = getBrandUrl() + "/" + id;
	$.ajax({
	   url: url, 
	   type: 'GET', 
	   success: function(data) {
	   		console.log("Brand data fetched");
	   		console.log(data);	
	   		displayBrand(data);     //...
	   }, 
	   error: function(){
	   		alert("An error has occurred");
	   }
	});	
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#brandFile');
	$file.val('');
	$('#brandFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = "";
	//Update counts	
	getBrandList();
}

function updateFileName(){
	var $file = $('#brandFile');
	var fileName = $file.val();
	$('#brandFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog(); 	
	$('#upload-brand-modal').modal('toggle');
}
function displayAddDialog(){
    $('#brand-form-modal').modal('toggle');
}

function displayBrand(data){
	$("#brand-edit-form input[name=name]").val(data.name);	
	$("#brand-edit-form input[name=category]").val(data.category);	
	$("#brand-edit-form input[name=id]").val(data.id);	
	$('#edit-brand-modal').modal('toggle');
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

function setSelectOptions(form, options, initialValue){
    form.empty();
    for(var value in options){
        selectValues[value] = value;
    }
    $.each(selectValues, function(key, value) {
         form.append($("<option></option>")
                        .attr("value", key)
                        .text(value));
    });
    form.val(initialValue);
}


//INITIALIZATION CODE
function init(){
	$('#add-brand').click(addBrand);
	$('#update-brand').click(updateBrand);
	$('#refresh-data').click(getBrandList);
	$('#upload-data').click(displayUploadData);
    $('#process-data').click(processData);
    $('#brandFile').on('change', updateFileName)
    $("#open-add-dialog").click(displayAddDialog)
}

$(document).ready(init);
$(document).ready(getBrandList);


