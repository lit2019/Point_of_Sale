
function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brands";
}
function getBrandSearchUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brands/search";
}
//BUTTON ACTIONS
function addBrand(event){
	//Set the values to update
	var $form = $("#brand-form");
	var json = "["+toJson($form)+"]";
	var url = getBrandUrl();
    if(!validateForm($form))
        return;
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	        $("#brand-form-modal").modal('toggle');
	   		console.log("Brand created");
	   		getBrandList();
	   },
	   error: function(error){
	        console.log(error);
	        makeToast(false, error.responseJSON.message, null);
	   }
	});

	return false;
}

function updateBrand(event){
	//Get the ID
	var id = $("#brand-edit-form input[name=id]").val();
	var url = getBrandUrl()+"/"+id;

	//Set the values to update
	var $form = $("#brand-edit-form");
	if(!validateForm($form))
            return;
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	$('#edit-brand-modal').modal('toggle');
	   		console.log("Brand update");
	   		getBrandList();     //...
	   },
	   error: function(error){
	   	        message = (error.responseJSON.message);
	   	        makeToast(false, message, null);

	   }
	});

	return false;
}

function getBrandList(){
    resetBrandForm();
	var url = getBrandSearchUrl();
	data = {"name":null,"category":null};
	data = JSON.stringify(data);
    console.log(data);
	$.ajax({
	   url: url,
	   type: 'POST',
	   data:data,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(data) {
	   		console.log("Brand data fetched");
	   		console.log(data);
	   		displayBrandList(data);     //...
	   },
	   error: function(error){
	   	    message = error.responseJSON.message;
	   		makeToast(false, message, downloadErrors);

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
	var url = getBrandUrl();

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
	        var message =  error.responseJSON.message;
	        errorData = message;
	        var pos = message.indexOf(",");
            message = message.slice(0, pos);
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
        var buttonHtml = '';
        if(userRole === 'supervisor'){
            var buttonHtml ='<td><button class="btn" onclick="displayEditBrand(' + e.id + ')"><i class="fa fa-edit"></i> edit</button></td>'
        }
		var row = '<tr>'
		+ '<td>' + e.name + '</td>'
		+ '<td>'  + e.category + '</td>'
		 + buttonHtml +
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
function resetBrandForm(data){
	$("#brand-form input[name=name]").val("");
	$("#brand-form input[name=category]").val("");
	$("#brand-form input[name=id]").val();
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

function downloadBrandTable(){
tableToCSV(document, "brand-table", 2);
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
    $('#download-errors').click(downloadErrors);
}

$(document).ready(init);
$(document).ready(getBrandList);

