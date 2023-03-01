
function getOrderUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/orders";
}

function getOrderSearchUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/orders/search";
}

function getOrderItemUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/orders/items";
}

function getInvoiceUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/orders/invoice";
}

function openCreateOrderDialog(event){
console.log("openCreateOrderDialog called")
	$('#order-form-modal').modal('toggle');
	orderItems = new Map();
	refreshOrderForm();
}

var orderItems = new Map();

function addItem(){
    console.log("inside add item")
    var data = {};
	var $form = $("#order-item-form");
    if(!validateForm($form))
        return;

	data['barcode'] = $("#order-item-form input[name=barcode]").val();
	data['quantity'] = $("#order-item-form input[name=quantity]").val();
	data['sellingPrice'] = $("#order-item-form input[name=sellingPrice]").val();

    for(var i in data){
        if(data[i].trim()===""){
            makeToast(false,i+" cannot be blank",null)
            return;
        }
    }

	if(orderItems.has(data['barcode'])){
	    if(orderItems.get(data['barcode'])['sellingPrice']!=data['sellingPrice']){
	        makeToast(false, "Selling Price cannot be different for same Product", null);
	    }
	    else{
	        orderItems.get(data['barcode'])['quantity']=parseInt(data['quantity'])+parseInt(orderItems.get(data['barcode'])['quantity']);
	    }
	}else{
	    orderItems.set(data['barcode'], data);
	}

	console.log(orderItems.get(data['barcode']));
	refreshOrderForm();

	$("#order-item-form input[name=barcode]").val("");
	$("#order-item-form input[name=quantity]").val("");
	$("#order-item-form input[name=sellingPrice]").val("");
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
    if(orderItems.keys().length === 0){
        $('#create-order-item-table').hide()
        return;
    }else{
        $('#create-order-item-table').show()
    }
    console.log('Printing product data');
    var $tbody = $('#create-order-item-table').find('tbody');
    $tbody.empty();
    console.log(orderItems.keys())
    for (let [barcode, e] of orderItems) {

        console.log(e);
        var buttonHtml ='<button class="btn" onclick="removeProduct(\'' + barcode + '\')"><i class="fa fa-trash"></i></button>'
        var row = '<tr>'
        + '<td>' + e.barcode + '</td>'
        + '<td>' + e.quantity + '</td>'
        + '<td>' + e.sellingPrice + '</td>'
        + '<td>' + buttonHtml + '</td>'
        + '</tr>';
        $tbody.append(row);
    }
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
    if(document.getElementById('order-table').rows.length<pageSize){
        return;
    }
    pageNo++;
    document.getElementById("page-number").innerHTML = pageNo;
    displayOrders(false);
}
function previousPage(){
    pageNo = document.getElementById("page-number").innerHTML;
    if(pageNo>1){
        pageNo--;
        document.getElementById("page-number").innerHTML = pageNo;

        displayOrders(false);
    }
}

function displayOrders(searchWithId){

    url = getOrderSearchUrl();
    var data = {};
	setPageDetails(data);

    if(searchWithId){
        var $form = $("#order-id-search-form");
         if(!validateForm($form))
             return;

        if($("#input-order-id").val().trim() != ""){
            data["orderId"] = $("#input-order-id").val();
        }else{
             makeToast(false, "Order id cannot be blank", null);
             return;
        }
        document.getElementById("page-number").innerHTML = 1;

    }
    else{

    var $form = $("#order-search-form");
    if(!validateForm($form))
         return;

    data["startDate"] = new Date($("#input-start-date").val());
    data["endDate"] = new Date($("#input-end-date").val());

        if(getSelectedIndex("input-order-status")!=0){
            data["orderStatus"] = $('#input-order-status').val();
        }
    }

	var json = JSON.stringify(data);

    $.ajax({
       url: url,
       type: 'POST',
       data: json,
       headers: {
        'Content-Type': 'application/json'
       },
       success: function(data) {
       console.log(data);
            if(data.length==0 && pageNo>1){
                    previousPage();
               }
            var $tbody = $('#order-table').find('tbody');
            $tbody.empty();
            for(var i in data){
                var e = data[i];

                var row = '<tr>'
                + '<td onclick="getOrderDetails(' + e.id+',\''+getIstDate(e.createdAt) + '\')" style="color:blue;"> <u>' + e.id + '</u></td>'
                + '<td>'  + getIstDate(e.createdAt) + '</td>'
                + '<td>'  + e.orderStatus + '</td>'
	            + getRow(e)
                + '</tr>';
                $tbody.append(row);
            }
       },
       error: function(error){
	        message = error.responseJSON.message;
	   		makeToast(false, message, null);

       }
       
    });
}

function getIstDate(date){
    date = date.replace("T", " ").replace("Z", " UTC");
    console.log(date);
    date = new Date(date);
    return date.toString().substring(4,25)+" IST"
}

function getRow(e){
    if(e.orderStatus === "CREATED"){
        return '<td><button class="btn btn-primary" onclick="generateInvoice('+e.id+')" ><i class="fa fa-file-text"></i>&nbsp;&nbsp;Generate Invoice&nbsp;&nbsp;</button></td>'
    }else if(e.orderStatus === "INVOICED"){
        return '<td><button class="btn btn-primary" onclick="generateInvoice('+e.id+')" ><i class="fa fa-download"></i>&nbsp;&nbsp;Download Invoice</button></td>'
    }else{
        return '<td></td>'
    }
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
    orderItemForms = [];
    for (let [barcode, form] of orderItems) {
        orderItemForms.push(form);
    }

    order = {"orderItemForms":orderItemForms}
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
            orderItems = new Map()
            displayOrders(false);
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
function removeProduct(barcode){
    orderItems.delete(barcode);
    refreshOrderForm();
}

function getOrderDetails(orderId,date){
    var url = getOrderItemUrl()+"/"+orderId;

    //Make ajax call
    $.ajax({
       url: url,
       type: 'GET',
       headers: {
        'Content-Type': 'application/json'
       },
       success: function(data) {
             console.log(data);   
             displayOrderDetails(orderId, data, date);
       },
       error: function(error){
             console.log(error)
       }
    });
}
function displayOrderDetails(orderId, data, date){
     $("#order-details-modal").modal('toggle');
	console.log('Printing order details');
	var $tbody = $('#order-items-table').find('tbody');
	$tbody.empty();
	var total = 0;
    document.getElementById("order-id-details").innerHTML = "Order Id : "+orderId+" | Creation Date : "+date;

	for(var i in data){
	    console.log(i);
		var e = data[i];
        console.log(e.id);
        var sno = parseInt(i)+1;
		var row = '<tr>'
        + '<td>' + sno + '</td>'
        + '<td>' + e.productName + '</td>'
        + '<td>' + e.barcode + '</td>'
        + '<td>' + e.quantity + '</td>'
        + '<td>' + roundToTwo(e.sellingPrice) + '</td>'
		+ '</tr>';
        $tbody.append(row);
        total+=(e.sellingPrice*e.quantity);
	}

	var row = '<tr>'
          + '<td></td>'
          + '<td></td>'
          + '<td></td>'
          + '<td><b>Total</b></td>'
          + '<td><b>'+roundToTwo(total)+'</b></td>'
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
            displayOrders(false);
                   },
       error: function(error){
            console.log(error)
	   	   makeToast(false,"Cannot connect to invoice-server", null);
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
	$('#open-create-dialog').click(openCreateOrderDialog);
	$('#form-create-order').click(createOrder);
	$('#generate-invoice').click(generateInvoice);
    $('#add-item').click(addItem);
    $('#download-errors').click(downloadErrors);
    dateLimit(document, ["input-start-date", "input-end-date"]);
    formatDate();

}

$(document).ready(init);
