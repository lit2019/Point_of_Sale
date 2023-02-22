
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/products";
}
function getProductFilterUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/products/filter";
}

function getAddProductsUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/products/add";
}

function getBrandListUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brands/distinct";
}

function getBrandCategoryListUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brands/search";
}


//BUTTON ACTIONS
function addProductDialog(event){
	$('#product-form-modal').modal('toggle');
	resetProductForm();

}
function getSelectedIndex(id){
    return document.getElementById(id).selectedIndex
}
function filter(searchByBarcode){
	var url = getProductFilterUrl();
	data = {};
	if(searchByBarcode){
	    data["barcode"] = $("#input-barcode").val();
	}
	else{
        if($("#filter-brand-select").val().trim()!=""){
            data["brandName"] = $("#filter-brand-select").val();
        }
        if($("#filter-category-select").val().trim()!=""){
            data["category"] = $("#filter-category-select").val();
        }
	}

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
	   		console.log(data);
	   		displayProductList(data);     //...
	   },
	   error: function(error){
            alert(error.responseJSON.message);
	   }
	});
}

function uploadProduct(event){
	//Set the values to upload
	var $form = $("#product-form");
    if(!validateForm($form))
        return;
	data = toMap($form)

	if(getSelectedIndex("form-brand-select")!=0){
	    data["brandName"] = $("#form-brand-select").val();
	}
	if(getSelectedIndex("form-category-select")!=0){
	    data["category"] = $("#form-category-select").val();
	}
	var json = "["+JSON.stringify(data)+"]";
	var url = getAddProductsUrl();

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
	   error: function(error){
	   	   makeToast(false, error.responseJSON.message, null);


	   }
	});

	return false;
}

function updateProduct(event){
	$('#edit-product-modal').modal('toggle');
	//Get the ID
	var id = $("#product-edit-form input[name=id]").val();
	var url = getProductUrl() +"/"+id;

	//Set the values to update
	var $form = $("#product-edit-form");

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
            console.log("Product update");
            $("#product-form").hide();
            getProductList();     //...
	   },
	   error: function(error){
	        makeToast(false, error.responseJSON.message, null);

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
	   error: function(error){
	   		alert(error.responseJSON.message);

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
	var json = JSON.stringify(fileData);
	var url = getAddProductsUrl();

	//Make ajax call
	console.log(json);
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	        $('#upload-product-modal').modal('toggle');
	   		makeToast(true, "", null);
            getProductList();

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
    console.log("download errors called")
	writeFileData(errorData);
}

//UI DISPLAY METHODS

function displayProductList(data){
	console.log('Printing product data');
	var $tbody = $('#product-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
        console.log("userRole:"+userRole);
        var buttonHtml = '';
		if(userRole === 'supervisor'){
    	    var buttonHtml ='<td><button class="btn"  onclick="displayEditProduct(' + e.id + ')"><i class="fa fa-edit"></i> edit</button></td>'
		}
        console.log(e.id);
		var row = '<tr>'
		+ '<td>'  + e.productName + '</td>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>' + e.brandName + '</td>'
		+ '<td>'  + e.category + '</td>'
		+  '<td>' + e.mrp + '</td>'
		 + buttonHtml +
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
	   		displayProduct(data);
	   },
	   error: function(error){
	        makeToast(false, error.responseJSON.message, null);

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

function displayUploadData(event){
 	resetUploadDialog();
	$('#upload-product-modal').modal('toggle');
}

function emptyDropdown(dropDown){
     dropDown.empty();
     dropDown.append($("<option selected=\"selected\"></option>")
                    .attr("value", null)
                    .text("select"));
//     dropDown.val("select");
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



function makeCategoryDropDown(brandName,dropDownCategory,initialCategory){
        emptyDropdown(dropDownCategory);

        url = getBrandCategoryListUrl();
	data = {"name":brandName, "category":null};

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

function displayProduct(data){
    makeDropdowns($("#edit-brand-select"),data.brandName,$("#edit-category-select"),data.category)
	$("#product-edit-form input[name=productName]").val(data.productName);
	$("#product-edit-form input[name=mrp]").val(data.mrp);
	$("#product-edit-form input[name=category]").val(data.category);
	$("#product-edit-form input[name=barcode]").val(data.barcode);
	$("#product-edit-form input[name=id]").val(data.id);
	$('#edit-product-modal').modal('toggle');
}

function resetProductForm(){
    makeDropdowns($("#form-brand-select"),null,$("#form-category-select"),null)
	$("#product-form input[name=productName]").val("");
	$("#product-form input[name=brandName]").val("");
	$("#product-form input[name=category]").val("");
	$("#product-form input[name=mrp]").val("");
	$("#product-form input[name=category]").val("");
	$("#product-form input[name=barcode]").val("");
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
function toMap($form){
    var serialized = $form.serializeArray();
    console.log(serialized);
    var s = '';
    var data = {};
    for(s in serialized){
        data[serialized[s]['name']] = serialized[s]['value']
    }
    console.log(data);
    return data;
}


function downloadProductTable(){
tableToCSV(document, "product-table", 5);
}



//INITIALIZATION CODE
function init(){
	$('#add-product').click(addProductDialog);
	$('#refresh-data').click(getProductList);
    $('#upload-data').click(displayUploadData);
    $('#process-data').click(processData);
    $('#download-errors').click(downloadErrors);
	$('#form-add-product').click(uploadProduct);
	$('#update-product').click(updateProduct);
    $('#productFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(filter(false));

