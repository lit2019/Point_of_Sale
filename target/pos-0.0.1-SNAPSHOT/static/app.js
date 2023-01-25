
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

function makeToast(isSuccessful, message, onClick){
    var toastHeading = document.getElementById('toast-heading');
    var toastMessage = document.getElementById('toast-message');

    if(isSuccessful){
        toastHeading.innerHTML = "Success";
        toastHeading.style.color = 'green';
        $("#download-errors").hide();

    }else{
        toastHeading.innerHTML = "Error";
        toastHeading.style.color = 'red';
        $("#download-errors").show();
        $("#download-errors").click(onClick);

    }
    toastMessage.innerHTML = message;

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
