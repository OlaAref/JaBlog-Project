var loadCountryButton;
var countrySelect;
var addCountryButton;
var updateCountryButton;
var deleteCountryButton;
var countryNameField, countryNameLabel;
var countryCodeField;
var countryIsoField;

$(document).ready(function() {
	loadCountryButton = $('#loadCountryButton');
	countrySelect = $('#countrySelect');
	
	addCountryButton = $("#addCountryButton");
	updateCountryButton = $("#updateCountryButton");
	deleteCountryButton = $("#deleteCountryButton");
	
	countryNameField = $("#countryNameField");
	countryNameLabel = $("#countryNameLabel");
	countryCodeField = $("#countryCodeField");
	countryIsoField = $("#countryIsoField");

	loadCountryButton.click(function() {
		loadCountries();
	});
	
	countrySelect.change(function() {
		changeFormToSelectedCountry();
	});
	
	addCountryButton.click(function() {
		if(addCountryButton.val() == 'Add'){
			addCountry();
		}
		else{
			changeFormToNew();
		}
	});
	
	updateCountryButton.click(function() {
		updateCountry();
	});
	
	deleteCountryButton.click(function() {
		deleteCountry();
	});
});

function showNotificationMessage(message){
	$('#notifyMessage').text(message);
	$('#notifyToast').toast('show');
}

function showWarnNotificationMessage(message){
	$('#notifyMessageWarn').text(message);
	$('#notifyToastWarn').toast('show');
}

function validateForm(){
	var countryForm = document.getElementById("countryForm");
	console.log(countryForm);
	if(!countryForm.checkValidity()){
		countryForm.reportValidity();
		return false;
	}
	return true;
}

function changeFormToSelectedCountry(){
	var selectedCountry = $("#countrySelect option:selected");
	var selecetedCountryName = selectedCountry.text();
	var selecetedCountryCode = selectedCountry.val().split("-")[1];
	var selecetedCountryIso = selectedCountry.val().split("-")[2];
	
	countryNameLabel.text("Selected Country");
	countryNameField.val(selecetedCountryName);
	countryCodeField.val(selecetedCountryCode);
	countryIsoField.val(selecetedCountryIso);
	
	addCountryButton.val("New");
	addCountryButton.text("New");
	updateCountryButton.attr("disabled", false);
	deleteCountryButton.attr("disabled", false);
}

function changeFormToNew(){
	addCountryButton.val("Add");
	addCountryButton.text("Add");
	updateCountryButton.attr("disabled", true);
	deleteCountryButton.attr("disabled", true);
	
	countryNameLabel.text("Name");
	countryNameField.val("").focus();
	countryCodeField.val("");
	countryIsoField.val("");

}

function selectNewlyAddedCountry(addedCountryId, countryName, countryCode, countryIso){
	var optionValue = addedCountryId + "-" + countryCode + "-" + countryIso;
	$("<option>").text(countryName).val(optionValue).appendTo($("#countrySelect"));
	$("#countrySelect option[value="+optionValue+"]").attr("selected", true);
	
	countryNameField.val("").focus();
	countryCodeField.val("");
	countryIsoField.val("");
}

function loadCountries() {
	var url = contextPath + 'countries/list';

	$.get(url, function(jsonResponse) {
		countrySelect.empty();

		$.each(jsonResponse, function(index, country) {
			var optionValue = country.id + "-" + country.code + "-" + country.iso3;
			$("<option>").val(optionValue).text(country.name).appendTo(countrySelect);
		});

	})
	.done(function() {
		loadCountryButton.text("Refresh Country List");
		showNotificationMessage("All countries have been loaded.");
	})
	.fail(function() {
		showWarnNotificationMessage("ERROR : Could not connect to server or server encountered an error.");
	});
}

function addCountry(){
	if(!validateForm()) return;
	
	var url = contextPath + "countries/save";
	countryName = countryNameField.val();
	countryCode = countryCodeField.val();
	countryIso = countryIsoField.val();
	
	var countryAsJson = {
		name: countryName,
		code: countryCode,
		iso3: countryIso
	};
	
	$.ajax({
		method: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfToken);
		},
		data: JSON.stringify(countryAsJson),
		contentType: 'application/json'
	})
	.done(function(addedCountryId) {
		selectNewlyAddedCountry(addedCountryId, countryName, countryCode, countryIso);
		showNotificationMessage("The new country has been added.");
	})
	.fail(function() {
		showWarnNotificationMessage("ERROR : Could not connect to server or server encountered an error.");
	});
}

function updateCountry(){
	if(!validateForm()) return;
	
	var url = contextPath + "countries/save";
	countryId = $("#countrySelect option:selected").val().split("-")[0];
	countryName = countryNameField.val();
	countryCode = countryCodeField.val();
	countryIso = countryIsoField.val();
	
	var countryAsJson = {
		id: countryId,
		name: countryName,
		code: countryCode,
		iso3: countryIso
	};
	
	$.ajax({
		method: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfToken);
		},
		data: JSON.stringify(countryAsJson),
		contentType: 'application/json'
	})
	.done(function(updatedCountryId) {
		var optionValue = updatedCountryId + "-" + countryCode + "-" + countryIso;
		$("#countrySelect option:selected").text(countryName);
		$("#countrySelect option:selected").val(optionValue);
		showNotificationMessage("The country has been updated.");
		changeFormToNew();
	})
	.fail(function() {
		showWarnNotificationMessage("ERROR : Could not connect to server or server encountered an error.");
	});
}


function deleteCountry(){
	var selectedCountry = $("#countrySelect option:selected");
	var optionValue = selectedCountry.val();
	var countryId = selectedCountry.val().split("-")[0];
	var url = contextPath + "countries/delete/" + countryId;
	
	$.ajax({
		method: "DELETE",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfToken);
		}
	})
	.done(function() {
		$("#countrySelect option[value="+optionValue+"]").remove();
		showNotificationMessage("The country has been deleted.");
		changeFormToNew();
	})
	.fail(function() {
		showWarnNotificationMessage("ERROR : Could not connect to server or server encountered an error.");
	});
}









