
function getOrderUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/orders";
}

function openCreateOrderDialog(event){
console.log("openCreateOrderDialog called")
	$('#order-form-modal').modal('toggle');
	orderItems = [];
	$('#add-item').click(addItem);
	refreshOrderForm();
}

var orderItems = [];

function addItem(){
    console.log("inside add item")
    var data = {};
	data['barcode'] = $("#order-item-form input[name=barcode]").val();
	data['quantity'] = $("#order-item-form input[name=quantity]").val();
	orderItems.push(data);
	console.log(orderItems);
	refreshOrderForm();
}

//HELPER METHOD
function toJson($form){
    var serialized = $form.serializeArray();
    console.log(serialized);
    var s = '';
    for(s in serialized){
        data[serialized[s]['name']] = serialized[s]['value']
    }
    var json = JSON.stringify(data);

    console.log(json);
    return json;
}

function refreshOrderForm(){
    console.log('Printing product data');
    var $tbody = $('#brand-table').find('tbody');
    $tbody.empty();
    for(var i in orderItems){
        var e = orderItems[i];
        console.log(e);
        var buttonHtml ='<button class="btn" onclick="removeProduct(' + i + ')"><i class="fa fa-trash"></i></button>'
        console.log(e.id);
        var row = '<tr>'
        + '<td>' + e.barcode + '</td>'
        + '<td>' + e.quantity + '</td>'
        + '<td>' + buttonHtml + '</td>'
        + '</tr>';
        $tbody.append(row);
    }
}

dropDownOrder = $("#select-order");
function makeOrderDropdown(){
    emptyDropdown(dropDownOrder);

    url = getOrderUrl();

    $.ajax({
       url: url,
       type: 'GET',
       headers: {
        'Content-Type': 'application/json'
       },
       success: function(data) {
            var selectValues = {}
            console.log(data);
            for(var i in data){
                val = data[i];
                val = val.id+"  ["+val.createdAt+"]";
                selectValues[val] = val;
            }
            $.each(selectValues, function(key, value) {
                 dropDownOrder.append($("<option></option>")
                                .attr("value", key)
                                .text(value));
            });

       },
       error: function(error){

       }
    });
}

function emptyDropdown(dropDown){
     dropDown.empty();
     dropDown.append($("<option></option>")
                    .attr("value", null)
                    .text("select"));
     dropDown.val("select");
}

errorData = "";
function createOrder(){
    order = {"orderItemForms":orderItems}
    var json = JSON.stringify(order);
    var url = getOrderUrl();

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
            makeOrderDropdown();
	        $('#order-form-modal').modal('toggle');

       },
       error: function(error){
            $('#upload-product-modal').modal('toggle');
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
function removeProduct(i){

    orderItems.splice(i,1)
    refreshOrderForm();
}

function getOrderDetails(){
    var id = dropDownOrder.val().split(' ')[0];
    var url = getOrderUrl()+"/"+id;

    //Make ajax call
    $.ajax({
       url: url,
       type: 'GET',
       headers: {
        'Content-Type': 'application/json'
       },
       success: function(data) {
             console.log(data);   
             displayOrderDetails(data);

       },
       error: function(error){
             console.log(error)
       }
    });
}
function displayOrderDetails(data){
	console.log('Printing order details');
	var $tbody = $('#order-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
//	    var buttonHtml ='<button class="btn" onclick="displayEditOrder(' + e.id + ')"><i class="fa fa-edit"></i> edit</button>'
        console.log(e.id);
		var row = '<tr>'
		+ '<td>'  + e.productName + '</td>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>'  + e.quantity + '</td>'
//		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

//INITIALIZATION CODE
function init(){
	$('#open-add-dialog').click(openCreateOrderDialog);
	$('#form-create-order').click(createOrder);
	$('#get-order-details').click(getOrderDetails);


}
$(document).ready(init);
$(document).ready(makeOrderDropdown);
