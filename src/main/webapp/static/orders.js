
function getOrderUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/orders";
}
function getInvoiceUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/order-invoice";
}

function openCreateOrderDialog(event){
console.log("openCreateOrderDialog called")
	$('#order-form-modal').modal('toggle');
	orderItems = [];
	refreshOrderForm();
}

var orderItems = [];
var orderId = '';

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
    if(orderItems.length === 0){
        $('#create-order-table').hide()
        return;
    }else{
        $('#create-order-table').show()
    }
    console.log('Printing product data');
    var $tbody = $('#create-order-table').find('tbody');
    $tbody.empty();
    for(var i in orderItems){
        var e = orderItems[i];
        console.log(e);
        var buttonHtml ='<button class="btn" onclick="removeProduct(' + i + ')"><i class="fa fa-trash"></i></button>'
        var row = '<tr>'
        + '<td>' + e.barcode + '</td>'
        + '<td>' + e.quantity + '</td>'
        + '<td>' + buttonHtml + '</td>'
        + '</tr>';
        $tbody.append(row);
    }
}

dropDownOrder = $("#select-order");
function makeOrderDropdown(initialOrderId){
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

            dropDownOrder.change(function () {
                  orderId = dropDownOrder.val().split(' ')[0];
                  getOrderDetails();
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
                    .text("select order"));
     dropDown.val("select order");
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
            makeToast(true, "");
            makeOrderDropdown();
	        $('#order-form-modal').modal('toggle');
            orderItems = []
       },
       error: function(error){
            $('#upload-product-modal').modal('toggle');
            var message =  error.responseJSON.message;
            errorData = message;
            var pos = message.indexOf(",");
            message = message.slice(0, pos);
            message += "...."
            makeToast(false, message);
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
    var url = getOrderUrl()+"/"+orderId;

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
             displayOrderDetails([]);
       }
    });
}
function displayOrderDetails(data){
    if(data.length===0){
        $("#order-table").hide()
        return;
    }else{
        $("#order-table").show()
    }
	console.log('Printing order details');
	var $tbody = $('#order-table').find('tbody');
	$tbody.empty();
	var total = 0;
	for(var i in data){
		var e = data[i];
        console.log(e.id);
        var sno = parseInt(i)+1;
		var row = '<tr>'
        + '<td>' + sno + '</td>'
        + '<td>' + e.productName + '</td>'
        + '<td>' + e.barcode + '</td>'
        + '<td>' + e.quantity + '</td>'
        + '<td>' + e.mrp + '</td>'
        + '<td>' + (e.mrp*e.quantity) + '</td>'
		+ '</tr>';
        $tbody.append(row);
        total+=(e.mrp*e.quantity);
	}
	var buttonHtml ='<button class="btn btn-primary" onclick="generateInvoice()" ><i class="fa fa-file-text-o"></i>&nbsp;&nbsp;Download Invoice</button>'

	var row = '<tr>'
          + '<td>' + buttonHtml + '</td>'
          + '<td></td>'
          + '<td></td>'
          + '<td></td>'
          + '<td><b>Total</b></td>'
          + '<td><b>'+total+'</b></td>'
        + '</tr>';
        $tbody.append(row);

    paginate();
}

function displayEditOrder(orderId){

}

function generateInvoice(){
    emptyDropdown(dropDownOrder);
    url = getInvoiceUrl()+"/"+orderId;
    console.log(url);

    $.ajax({
       url: url,
       type: 'GET',
       headers: {
        'Content-Type': 'application/json'
       },
       success: function(data) {
            console.log(data)
            download("file:///"+data.invoiceLink, "invoice.pdf");
                   },
       error: function(error){
            console.log(error)
       }
    });
}
function download(dataurl, filename) {
  const link = document.createElement("a");
  link.href = dataurl;
  link.download = filename;
  link.click();
}

//INITIALIZATION CODE
function init(){
	$('#open-add-dialog').click(openCreateOrderDialog);
	$('#form-create-order').click(createOrder);
	$('#generate-invoice').click(generateInvoice);
    $('#add-item').click(addItem);
    $('#download-errors').click(downloadErrors);
}

function paginate(){
      $('#product-table').DataTable();
      $('.dataTables_length').addClass('bs-select');
}

$(document).ready(init);
$(document).ready(makeOrderDropdown);
$(document).ready(displayOrderDetails([]));