var commentContent;
var commentPost;
var addCommentBtn;
var replyCommentBtn;
var deleteCommentBtn;

var replyContent;
var replyPost;
var addReplyBtn;
var parentCommentId;


$(document).ready(function() {
	commentContent = $('#commentContent');
	commentPost = $('#commentPost');
	addCommentBtn = $('#addComment');
	replyCommentBtn = $("a[id^='replyLink']");
	deleteCommentBtn = $("a[id^='deleteLink']");
	var confirmDeleteBtn = $("#confirmModalAjax #yesBtn");

	addCommentBtn.click(function() {
		addComment();
	});
	
	replyCommentBtn.click(function(e){
		e.preventDefault();
		appendCommentForm(this);
	});
	
	var commentsLength = $(".randomColorBorder").length;
	const colors = [];
	for(let i=0; i<commentsLength; i++){
		var randomColor = Math.floor(Math.random()*16777215).toString(16);
		colors[i] = "#" + randomColor;
	}
	
	$(".randomColorBorder").each(function(index, element){
		this.style.cssText += "border:2px solid "+colors[index]+"!important";
		
	});
	
	$(".randomColorIcon").each(function(index, element){
		this.style.color = colors[index];
		
	});
	
	deleteCommentBtn.click(function(e) {
		e.preventDefault();
		var link = this;
		showModal("confirmModalAjax", "Delete Comment", "Are you sure you want to delete this comment?");
		confirmDeleteBtn.click(function(e){
			deleteComment(link);
		});
	});
	
	
});


function validateForm(formName){
	var form = document.getElementById(formName);
	
	if(!form.checkValidity()){
		form.reportValidity();
		return false;
	}
	return true;
}


function addComment(){
	if(!validateForm("commentForm")) return;
	
	var url = contextPath + "comments/save";
	var content = commentContent.val();
	var post = commentPost.val();
	
	var params = {
		content: content,
		post: post,
		email: email,
		_csrf: csrfValue
	};
	
	$.ajax({
		method: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: params
	})
	.done(function(response) {
		if(response == 'OK'){
			location.reload();
		}
		else{
			showWarnNotificationMessage("ERROR : Server response is unknown.");
		}
	})
	.fail(function() {
		showWarnNotificationMessage("ERROR : Could not connect to server or server encountered an error.");
	});
}

function appendCommentForm(replyBtn){
	var commentId = replyBtn.id.substr(9);
	var replyDiv = $("#replyDiv"+commentId);
	
	var form = `
		<form id="replyForm" class="mb-4 mt-3">
        	<input hidden="hidden" id="replyPost" value="${postId}">
        	<input hidden="hidden" id="parentCommentId" value="${commentId}">
        	<textarea class="form-control" id="replyContent" rows="3" placeholder="Add reply" required="required"></textarea>
        	<div class="d-flex justify-content-end link-padding"><button class="btn btn-primary" type="button" id="addReply">Add Reply</button></div>
        </form>
	`;
	replyDiv.append(form);
	var replyLink = $("#"+replyBtn.id);
	replyLink.css("visibility", "hidden");
	
	replyContent = $('#replyContent');
	replyPost = $('#replyPost');
	parentCommentId = $('#parentCommentId');
	addReplyBtn = $('#addReply');
	
	addReplyBtn.click(function(e) {
		e.preventDefault();
		addReply();
	});
}

function addReply(){
	if(!validateForm("replyForm")) return;

	var url = contextPath + "comments/saveReply";
	var content = replyContent.val();
	var post = replyPost.val();
	var parent = parentCommentId.val();
	
	var params = {
		content: content,
		post: post,
		parent: parent,
		email: email,
		_csrf: csrfValue
	};
	
	$.ajax({
		method: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: params
	})
	.done(function(response) {
		if(response == 'OK'){
			location.reload();
		}
		else{
			alert("ERROR : Server response is unknown.");
		}
	})
	.fail(function() {
		alert("ERROR : Could not connect to server or server encountered an error.");
	});
}


function deleteComment(deleteBtn){
	var commentId = deleteBtn.id.substr(10);
	var url = contextPath + "comments/delete/" + commentId;
	
	$.ajax({
		method: "DELETE",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	})
	.done(function() {
		location.reload();
	})
	.fail(function() {
		alert("ERROR : Could not connect to server or server encountered an error.");
	});
}








