
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
var pageNo = 1;
var pageSize = 10;

function setPageDetails(data){
    data["pageNo"] = pageNo;
    data["pageSize"] = pageSize;
}

function nextPage(){
    if(document.getElementById('product-table').rows.length<pageSize){
        return;
    }
    pageNo++;
    document.getElementById("page-number").innerHTML = pageNo;
    filter(false);
}
function previousPage(){
    pageNo = document.getElementById("page-number").innerHTML;
    if(pageNo>1){
        pageNo--;
        document.getElementById("page-number").innerHTML = pageNo;

        filter(false);
    }
}

function filter(searchByBarcode){
	var url = getProductFilterUrl();
	data = {};
	setPageDetails(data);
	if(searchByBarcode){

        var $form = $("#barcode-search-form");
         if(!validateForm($form))
             return;

	    if($("#input-barcode").val().trim()===""){
	        makeToast(false,"barcode cannot be blank",null);
	        return;
	    }
	    data["barcode"] = $("#input-barcode").val().trim();
        document.getElementById("page-number").innerHTML = 1;

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
	   		if(data.length==0 && pageNo>1){
       		    previousPage();
       		}

	   },
	   error: function(error){
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
	}else{
        data["brandName"] = null;
	}
	if(getSelectedIndex("form-category-select")!=0){
	    data["category"] = $("#form-category-select").val();
	}else{
	    data["category"] = null;
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
	   		$('#product-form-modal').modal('toggle');
	   		makeToast(true, "", null);
            filter(false);

	   },
	   error: function(error){
	   	   makeToast(false, error.responseJSON.message, null);


	   }
	});

	return false;
}

function updateProduct(event){
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
	   type: 'PATCH',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
            console.log("Product update");
	$('#edit-product-modal').modal('toggle');

            $("#product-form").hide();
            makeToast(true, "", null);
	   		filter(false);
	   },
	   error: function(error){
	        makeToast(false, error.responseJSON.message, null);

	   }
	});

	return false;
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
	if(fileData.length === 0){
       errorMessage("File is empty!")
       return;
    }
    var row = fileData[0];
    var title = Object.keys(row);
    if(title.length!=5 || title[0]!='productName' || title[1]!='brandName' || title[2]!='category' || title[3]!='mrp' || title[4]!='barcode'){
        var message = "Incorrect tsv format please check the sample file!";
        makeToast(false, message, null);
        errorData = message;
        return;
    }
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
            filter(false);

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
	            filter(false);

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

        if(brandName.trim().length==0){
            brandName = null;
        }

        if(brandName==null){
            return;
        }
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
	$('#refresh-data').click(filter(false));
    $('#upload-data').click(displayUploadData);
    $('#process-data').click(processData);
    $('#download-errors').click(downloadErrors);
	$('#form-add-product').click(uploadProduct);
	$('#update-product').click(updateProduct);
    $('#productFile').on('change', updateFileName)
    restrictDates(document);
}

$(document).ready(init);
$(document).ready(filter(false));

