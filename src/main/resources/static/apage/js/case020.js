$(document).ready(function () {
	
	$('#btnList').on('click',function(){
		formMake({
			'action'	: '/apage/case010.do'
			,'input'	: $('#searchMap').serializeArray()
		});
	});
	
	$('#btnModify').on('click',function(){
		formMake({
			'action'	: '/apage/case040.do'
			,'input'	: $('#searchMap').serializeArray()
		});
	});
	
	$('#btnDelete').on('click',function(){
		
		swal({
			title	: "정말 삭제하시겠습니까?",
			type	: "warning",
			showCancelButton	: true,
			confirmButtonText	: "확인",
			cancelButtonText	: "취소",
			closeOnConfirm		: false,
			closeOnCancel		: true 
		},function(isConfirm){
			
			if(!isConfirm) return false;
			
			var dataArr	= $('#searchMap').serializeArray();
			
			cmnAjaxFn({
				url			: '/apage/case051.do'
				,data		: dataArr
				,dataType	: 'json'
				,success	: function(data){
					swal({title: "삭제 되었습니다.",type: "success"},function(){
						formMake({
							'action'	: '/apage/case010.do'
							,'input'	: dataArr
						});
					});
				}
				,error		: function(xhr,status,error){
					swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
				}
			});		
		});			
		
	});
	
});