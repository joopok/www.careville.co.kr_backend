$(document).ready(function(){
	
	const rememberme	= localStorage.getItem("rememberme");
	
	if(rememberme != null && rememberme == 'Y'){
		$('#rememberme').prop('checked',true);
		$('input[name="mngrId"]').val(localStorage.getItem("mngrId"));
		$('input[name="mngrPw"]').val(localStorage.getItem("mngrPw"));
		
	}
			
	$('input').focusout(function(){
		var $t	= $(this);
		$t.val($.trim($t.val()));				
	});

	$('#submit').on('click',function(e){
		var $mngrId	= $('input[name="mngrId"]');
		var $mngrPw	= $('input[name="mngrPw"]');
		var mngrId	= $mngrId.val();
		var mngrPw	= $mngrPw.val();
		
		if(mngrId.length <= 0){
			swal({title:'아이디를 입력해 주세요.',type:'warning'});
			$mngrId.focus();
			return false;
		}
		if(mngrPw.length <= 0){
			swal({title:'비밀번호를 입력해 주세요.',type:'warning'});
			$mngrPw.focus();
			return false;
		}
		
		cmnAjaxFn({
			url			: '/apage/signInChk.do'
			,data		: {'mngrId':mngrId,'mngrPw':mngrPw}
			,dataType	: 'json'
			,success	: function(data){
				
				if($('#rememberme').is(':checked')){
					localStorage.setItem("mngrId", mngrId);
					localStorage.setItem("mngrPw", mngrPw);
					localStorage.setItem("rememberme", 'Y');
				}else{
					localStorage.removeItem("mngrId");
					localStorage.removeItem("mngrPw");
					localStorage.removeItem("rememberme");
				}
				
				window.location.replace("/apage/");
				
			}
			,error		: function(xhr,status,error){
				swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
			}
		});	
	});
	
});