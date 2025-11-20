$(document).ready(function () {
	
	$('#btnList').on('click',function(){
		formMake({
			'action'	: '/apage/review010.do'
			,'input'	: pageMoveInputArr()
		});
	});
	
	$('#btnDel').on('click',function(){
		
		swal({
			title	: '정말 삭제하시겠습니까?',
			type	: 'warning',
			showCancelButton	: true,
			confirmButtonText	: '확인',
			cancelButtonText	: '취소',
			closeOnConfirm		: false,
			closeOnCancel		: true 
		}, function(isConfirm){
			
			if(!isConfirm) return false;

			var reviewSeq	= $('#searchMap').find('input[name="reviewSeq"]').val();
			
			cmnAjaxFn({
				url			: '/apage/review051.do'
				,data		: {'reviewSeq':reviewSeq}
				,dataType	: 'json'
				,success	: function(data){
					if(data.isDel == 'Y'){
						swal({title: "삭제 되었습니다.",type: "success"},function(){
							var inputArr	= pageMoveInputArr();
							inputArr.push({'name':'reviewSeq','value':reviewSeq});
							formMake({
								'action'	: '/apage/review010.do'
								,'input'	: inputArr
							});
						});
	
						
					}else{
						swal({title:result.message || "삭제에 실패했습니다.",type:'warning'});
					}
	
				}
				,error		: function(xhr,status,error){
					swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
				}
			});
		});		
		
		
	});
	
	$('#btnModify').on('click',function(){
		var reviewSeq	= $('#searchMap').find('input[name="reviewSeq"]').val();
		var inputArr	= pageMoveInputArr();
		
		inputArr.push({'name':'reviewSeq','value':reviewSeq});
		
		formMake({
			'action'	: '/apage/review040.do'
			,'input'	: inputArr
		});
	});
	
});

function pageMoveInputArr(){
	var inputArr	= new Array();

	$('#searchMap').find('input').each(function(){
		inputArr.push({'name':this.name,'value':this.value});
	});
	
	return inputArr;
}


function deleteReview() {
	if(!confirm("정말 삭제하시겠습니까?")) {
		return;
	}
	
	var pw = prompt("비밀번호를 입력하세요. (관리자는 입력 불필요)");
	
	var data = {
		reviewSeq: $("#reviewSeq").val(),
		pw: pw || ""
	};
	
	$.ajax({
		url: "/reviewDel.do",
		type: "POST",
		data: data,
		dataType: "json",
		success: function(result) {
			if(result.isDel == 'Y') {
				swal({title: "삭제 되었습니다.",type: "success"},function(){
					location.href = "/apage/review010.do";
				});				
			} else {
				swal({title:result.message || "삭제에 실패했습니다.",type:'warning'});
			}
		},
		error: function() {
			swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
		}
	});
}