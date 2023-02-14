
function getOrderUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/orders";
}

function getOrderSearchUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/orders/search";
}


function getInvoiceUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/orders/invoice";
}

function openCreateOrderDialog(event){
console.log("openCreateOrderDialog called")
	$('#order-form-modal').modal('toggle');
	orderItems = [];
	refreshOrderForm();
}

var orderItems = [];

function addItem(){
    console.log("inside add item")
    var data = {};
	data['barcode'] = $("#order-item-form input[name=barcode]").val();
	data['quantity'] = $("#order-item-form input[name=quantity]").val();
	data['sellingPrice'] = $("#order-item-form input[name=sellingPrice]").val();
	orderItems.push(data);
	console.log(orderItems);
	refreshOrderForm();
}

//HELPER METHOD
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

function refreshOrderForm(){
    if(orderItems.length === 0){
        $('#create-order-item-table').hide()
        return;
    }else{
        $('#create-order-item-table').show()
    }
    console.log('Printing product data');
    var $tbody = $('#create-order-item-table').find('tbody');
    $tbody.empty();
    for(var i in orderItems){
        var e = orderItems[i];
        console.log(e);
        var buttonHtml ='<button class="btn" onclick="removeProduct(' + i + ')"><i class="fa fa-trash"></i></button>'
        var row = '<tr>'
        + '<td>' + e.barcode + '</td>'
        + '<td>' + e.quantity + '</td>'
        + '<td>' + e.sellingPrice + '</td>'
        + '<td>' + buttonHtml + '</td>'
        + '</tr>';
        $tbody.append(row);
    }
}

function displayOrders(){

    url = getOrderSearchUrl();
    var data = {};
    data["startDate"] = new Date($("#input-start-date").val());
    data["endDate"] = new Date($("#input-end-date").val());

	var json = JSON.stringify(data);

    $.ajax({
       url: url,
       type: 'POST',
       data: json,
       headers: {
        'Content-Type': 'application/json'
       },
       success: function(data) {
            var $tbody = $('#order-table').find('tbody');
            $tbody.empty();
            for(var i in data){
                var e = data[i];
                var buttonHtml = '';
                if(userRole === 'supervisor'){
                    var buttonHtml ='<td><button type="button" class="btn btn-primary" onclick="getOrderDetails(' + e.id + ')">view</button></td>'
                }
                var row = '<tr>'
                + '<td>' + e.id + '</td>'
                + '<td>'  + e.createdAt + '</td>'
                 + buttonHtml +
                + '</tr>';
                $tbody.append(row);
            }
            paginate();

       },
       error: function(error){
	        message = error.responseJSON.message;
	   		makeToast(false, message, null);

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
       success: function(data) {
	        $('#order-form-modal').modal('toggle');
            makeToast(true, "", null);
            getOrderDetails(data.id);
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
            makeToast(false, message, null);
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

function getOrderDetails(orderId){
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
             displayOrderDetails(orderId,data);

       },
       error: function(error){
             console.log(error)
             displayOrderDetails(null,[]);
       }
    });
}
function displayOrderDetails(orderId,data){
     $("#order-details-modal").modal('toggle');
	console.log('Printing order details');
	var $tbody = $('#order-items-table').find('tbody');
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
        + '<td>' + (e.sellingPrice) + '</td>'
		+ '</tr>';
        $tbody.append(row);
        total+=(e.sellingPrice*e.quantity);
	}
	var buttonHtml ='<button class="btn btn-primary" onclick="generateInvoice('+orderId+')" ><i class="fa fa-file-text-o"></i>&nbsp;&nbsp;Generate Invoice</button>'

	var row = '<tr>'
          + '<td>' + buttonHtml + '</td>'
          + '<td></td>'
          + '<td></td>'
          + '<td><b>Total</b></td>'
          + '<td><b>'+total+'</b></td>'
            + '</tr>';
            $tbody.append(row);

}

function generateInvoice(orderId){
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
//            download("file:///"+data.invoiceLink, "invoice.pdf");
            downloadInvoice(data,orderId);
                   },
       error: function(error){
            console.log(error)
       }
    });
}
function downloadInvoice(data, id) {
    const linkSource = `data:application/pdf;base64,${data}`;
    const downloadLink = document.createElement("a");
    const fileName = "invoice-" + id + ".pdf";
    downloadLink.href = linkSource;
    downloadLink.download = fileName;
    downloadLink.click();
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
    $('#search-order').click(displayOrders);
}

function paginate(){
      $('#order-table').DataTable();
      $('.dataTables_length').addClass('bs-select');
}

$(document).ready(init);
