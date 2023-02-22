
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
    return json;
}

function handleAjaxError(response){
	var response = JSON.parse(response.responseText);
	alert(response.message);
}
function validateForm(form){
    return form[0].reportValidity();
}

function readFileData(file, callback){
	var config = {
		header: true,
		delimiter: "\t",
		skipEmptyLines: "greedy",
		complete: function(results) {
			callback(results);
	  	}	
	}
	Papa.parse(file, config);
}

function isValidForm(form){

    return true;
}

function makeToast(isSuccessful, message, downloadFunction){
    var toastHeading = document.getElementById('toast-heading');
    var toastMessage = document.getElementById('toast-message');

    $("#download-errors").show();
    if(downloadFunction===null){
        $("#download-errors").hide();
    }

    if(isSuccessful){
        toastHeading.innerHTML = "Success";
        toastHeading.style.color = 'green';
        toastMessage.innerHTML = null;
    }else{
        toastHeading.innerHTML = "Error";
        toastHeading.style.color = 'red';
        toastMessage.innerHTML = message;
    }

    var options = {
        animation : true,
        delay : 3000
    };

    var toastHTMLElement = document.getElementById("toast");

    var toastElement = new bootstrap.Toast(toastHTMLElement, options)

    toastElement.show();
}

function writeFileData(arr){
	var config = {
		quoteChar: '',
		escapeChar: '',
		delimiter: "\t"
	};
	
    var blob = new Blob([arr], {type: 'text/plain'});
    var fileUrl =  null;

    if (navigator.msSaveBlob) {
        fileUrl = navigator.msSaveBlob(blob, 'download.txt');
    } else {
        fileUrl = window.URL.createObjectURL(blob);
    }
    var tempLink = document.createElement('a');
    tempLink.href = fileUrl;
    tempLink.setAttribute('download', 'download.txt');
    tempLink.click(); 
}

function tableToCSV(document, tableId, columns) {

// Variable to store the final csv data
var csv_data = [];

// Get each row data
var rows = document.getElementById(tableId).rows;
if(rows.length==1){
    return;
}
console.log(rows);
for (var i = 0; i < rows.length; i++) {

      cells = $(rows[i]).find('td,th');
      csv_row = [];
      for (j = 0; j < columns; j++) {
         txt = cells[j].innerText;
         csv_row.push(txt.replace(",", "-"));
      }
    // Combine each column value with comma
    csv_data.push(csv_row.join(","));
    console.log(csv_row);
}

// Combine each row data with new line character
csv_data = csv_data.join('\n');

// Call this function to download csv file
downloadCSVFile(csv_data);
}

function downloadCSVFile(csv_data) {

// Create CSV file object and feed
// our csv_data into it
CSVFile = new Blob([csv_data], {
    type: "text/csv"
});

// Create to temporary link to initiate
// download process
var temp_link = document.createElement('a');

// Download csv file
temp_link.download = "download.csv";
var url = window.URL.createObjectURL(CSVFile);
temp_link.href = url;

// This link should not be displayed
temp_link.style.display = "none";
document.body.appendChild(temp_link);

// Automatically click the link to
// trigger download
temp_link.click();
document.body.removeChild(temp_link);
}
