function readURL(input) {
	if (input.files && input.files[0]) {
		var reader = new FileReader();
		reader.onload = function(e) {
			$('#previewImage').attr('src', e.target.result);
		};
		reader.readAsDataURL(input.files[0]);
	}
}

function previewPic(input) {
	$('#previewImage').removeClass("d-none");
	readURL(input);
}

function checkFileSize(input, sizeInMB) {
	var limitSize = sizeInMB * 1048576;
	var fileSize = input.files[0].size;
	if(fileSize > limitSize){
		$('#photoNote').text("You must choose an image less than "+sizeInMB+" MB.");
		input.setCustomValidity("You must choose an image less than "+sizeInMB+" MB.");
		input.reportValidity();
		return false;
	}
	else{
		$('#photoNote').text("");
		return true;
	}
}

function showModal(type, title, message) {
	var modalType = '#' + type;
	$(modalType + ' #modalTitle').text(title);
	$(modalType + ' #modalMessage').text(message);
	$(modalType).modal('show');
}

function showToast(type, title, body) {
	$('#' + type + '-title').text(title);
	$('#' + type + '-body').text(body);
	$('#' + type + '-toast').toast({ delay: 7000 })
	$('#' + type + '-toast').toast('show')
}

function togglePassword(theEye, passField){
	$('body').on('click', theEye, function() {
		$(theEye).toggleClass("fa-eye fa-eye-slash");
		var type = $(theEye).hasClass("fa-eye-slash") ? "text" : "password";
		$(passField).attr("type", type);
	});
}

function emailCheck(form) {
	var url = contextPath + "users/check_email";
	var userEmail = $('#email').val();
	var userId = $('#id').val();

	var params = {
		id: userId,
		email: userEmail,
		_csrf: csrfValue
	};

	$.post(url, params, function(response) {

		if (response == "OK") {
			form.submit();
			//showModal("successModal","Success", "The email you entered is not registered.");
		}
		else if (response == "Duplicated") {
			showModal("warningModal", "Duplicated Email!!", "The email '" + userEmail + "' is registered.");
		}
		else {
			console.log(response);
			showModal("errorModal", "Error!!", "Unknown response from server.");
		}
	})
	.fail(function() {
		showModal("errorModal", "Error!!", "Could not connect to the server.");
	});

	return false;
}

function checkCategoryUnique(form){
	var url = contextPath + "categories/check_unique";
	var id = $("#id").val();
	var title = $("#title").val();
	var slug = $("#slug").val();
	
	var params ={
		id: id,
		title: title,
		slug: slug,
		_csrf: csrfValue
	};
	
	$.post(url, params, function(response){
		
		if(response == 'OK'){
			form.submit();
		}
		else if(response == 'DuplicateTitle'){
			showModal("warningModal", "Duplicated Title!!", "This title '" + title + "' is used before");
		}
		else if(response == 'DuplicateSlug'){
			showModal("warningModal", "Duplicated Slug!!", "This slug '" + slug + "' is used before");
		}
		else{
			console.log(response);
			showModal("errorModal", "Error!!", "Unknown response from server.");
		}
	})
	.fail(function(){
		showModal("errorModal", "Error!!", "Could not connect to the server.");
	});
	
	return false;
}

function checkTagUnique(form){
	var url = contextPath + "tags/check_unique";
	var id = $("#id").val();
	var title = $("#title").val();
	var slug = $("#slug").val();
	
	var params ={
		id: id,
		title: title,
		slug: slug,
		_csrf: csrfValue
	};
	
	$.post(url, params, function(response){
		
		if(response == 'OK'){
			form.submit();
		}
		else if(response == 'DuplicateTitle'){
			showModal("warningModal", "Duplicated Title!!", "This title '" + title + "' is used before");
		}
		else if(response == 'DuplicateSlug'){
			showModal("warningModal", "Duplicated Slug!!", "This slug '" + slug + "' is used before");
		}
		else{
			console.log(response);
			showModal("errorModal", "Error!!", "Unknown response from server.");
		}
	})
	.fail(function(){
		showModal("errorModal", "Error!!", "Could not connect to the server.");
	});
	
	return false;
}

function addTag(){
	var url = contextPath + "tags/save";
	var category = $("#categorySelect").val();
	var title = $("#titleField").val();
	var description = $("#descriptionField").val();
	var slug = $("#slugField").val();
	
	checkTag(title, slug);
	
	var tagAsJson ={
		title: title,
		description: description,
		slug: slug,
		category: category
	};
	
	$.ajax({
		method: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(tagAsJson),
		contentType: 'application/json'
	})
	.done(function(addedTagId) {
		$('#tagModel').modal('hide');
		showModal("successModal", "Tag Saved", "The tag saved successfully.");
		
		$(".tagSelect").append($("<option>").text(title).val(addedTagId));
		$(".tagSelect option[value="+addedTagId+"]").attr("selected", true);

		
	})
	.fail(function() {
		showModal("errorModal", "Error!!", "Could not connect to the server.");
	});
	
	return false;
}

function checkTag(title, slug){
	
	var url = contextPath + "tags/check_unique";
	var params ={
		id: 0,
		title: title,
		slug: slug,
		_csrf: csrfValue
	};
	
	$.post(url, params, function(response){
		
		if(response == 'OK'){
			//form.submit();
		}
		else if(response == 'DuplicateTitle'){
			showModal("warningModal", "Duplicated Title!!", "This title '" + title + "' is used before");
		}
		else if(response == 'DuplicateSlug'){
			showModal("warningModal", "Duplicated Slug!!", "This slug '" + slug + "' is used before");
		}
		else{
			console.log(response);
			showModal("errorModal", "Error!!", "Unknown response from server.");
		}
	})
	.fail(function(){
		showModal("errorModal", "Error!!", "Could not connect to the server.");
	});
}

function checkPostUnique(form){
	var url = contextPath + "posts/check_unique";
	var id = $("#id").val();
	var title = $("#title").val();
	var content = $("#content").val();
	var slug = $("#slug").val();
	
	var params ={
		id: id,
		title: title,
		content: content,
		slug: slug,
		_csrf: csrfValue
	};
	
	$.post(url, params, function(response){
		
		if(response == 'OK'){
			form.submit();
		}
		else if(response == 'DuplicateTitle'){
			showModal("warningModal", "Duplicated Title!!", "This title '" + title + "' is used before");
		}
		else if(response == 'DuplicateContent'){
			showModal("warningModal", "Duplicated Post!!", "This Post is already published.");
		}
		else if(response == 'DuplicateSlug'){
			showModal("warningModal", "Duplicated Slug!!", "This slug '" + slug + "' is used before");
		}
		else{
			console.log(response);
			showModal("errorModal", "Error!!", "Unknown response from server.");
		}
	})
	.fail(function(){
		showModal("errorModal", "Error!!", "Could not connect to the server.");
	});
	
	return false;
}


$("#anonymousLike").click(function() {
	var url = $(this).attr("href");

	$.ajax({
		method: "GET",
		url: url
	})
	.done(function(status) {
		if(status == 'OK'){
			$(".like-heart").removeClass('text-secondary').addClass('text-danger');
			var likesCount = parseInt($("#likesCount").text()) + 1;
			$("#likesCount").text(likesCount);
		}
		else{
			console.log(response);
			showModal("errorModal", "Error!!", "Unknown response from server.");
		}
	})
	.fail(function() {
		showModal("errorModal", "Error!!", "Could not connect to the server.");
	});
	
	return false;

});

function authenticatedLike(){
	
	$("#clickableDiv").click(function() {
		
		var url = $("#authenticatedLike").attr("href");
		
		var params = {
			email: email,
			_csrf: csrfValue
		};
		console.log(params);
		
		$.post(url, params, function(response){
			
			if(response == 'Like'){
				//$(".like-heart").removeClass('text-secondary').addClass('text-danger');
				//var likesCount = parseInt($("#likesCount").text()) + 1;
				//$("#likesCount").text(likesCount);
				location.reload();
			}
			else if(response == 'Dislike'){
				//$(".like-heart").removeClass('text-danger').addClass('text-secondary');
				//var likesCount = parseInt($("#likesCount").text()) - 1;
				//$("#likesCount").text(likesCount);
				location.reload();
			}
			else if(response == 'Duplicate'){
				
			}
			else{
				console.log(response);
				showModal("errorModal", "Error!!", "Unknown response from server.");
			}
		})
		.fail(function(){
			showModal("errorModal", "Error!!", "Could not connect to the server.");
		});
		
		return false;
	
	});
}









