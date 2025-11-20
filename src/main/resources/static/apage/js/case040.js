var oEditors = [];
$(document).ready(function () {
	
	nhn.husky.EZCreator.createInIFrame({
		oAppRef			: oEditors,
		elPlaceHolder	: "caseCn",
		sSkinURI		: "/plugins/smartEditor/SmartEditor2Skin.html",
		fCreator		: "createSEditor2"
	});
	
	$('#fieInput').change(function(e){
		
		if (e.target.files.length === 0) return false;
		
		var $mainImg		= $('#mainImg'); 
		var $fileAttachView	= $('#fileAttachView'); 
		var $this			= $(this);
		var file 			= e.target.files[0];
		var formData		= new FormData();
		
		formData.append("files", file);

		fetch('/fileUpload.do',{method:"POST",body:formData}).then(res => res.json()).then(data => {
			$mainImg.val(data.fileSeq);
			$fileAttachView.find('input').val(file.name);
			$fileAttachView.show();
			$this.hide();
		});
	});
	
	$('#fileAttachDel').on('click',function(){
		
		if($('#fieInput')[0].files.length == 0){
			$('#mainImg').val(''); 
			$('#fieInput').val('').show();
			$('#fileAttachView').hide(); 
			return false;
		}
		
		cmnAjaxFn({
			url			: '/fileDel.do'
			,data		: {'viewFileSeq':$('#mainImg').val()}
			,dataType	: 'json'
			,success	: function(data){
				$('#mainImg').val(''); 
				$('#fieInput').val('').show();
				$('#fileAttachView').hide(); 
			}
			,error		: function(xhr,status,error){
			}
		});
	});
	
	$('#btnSubmit').on('click',function(){
		
		oEditors.getById["caseCn"].exec("UPDATE_CONTENTS_FIELD", []);
		
		if($.trim($('#caseSj').val()) == ''){
			swal({title:'제목을 입력해 주세요.',type:'warning'});
			return false;
		}
		if($.trim($('#serviceCd').val()) == ''){
			swal({title:'서비스 종류를 선택해 주세요.',type:'warning'});
			return false;
		}		
		if($.trim($('#hashtag').val()) != '' && $.trim($('#hashtag').val()).length > 24){
			swal({title:'해시태그 사이즈를 줄여주세요.(MaxSize:24)',type:'warning'});
			return false;
		}		
		if($.trim($('#mainImg').val()) == ''){
			swal({title:'대표 이미지를 등록해 주세요.',type:'warning'});
			return false;
		}
		if($.trim($('#caseCn').val()) == '' || $.trim($('#caseCn').val()) == '<p><br></p>'){
			swal({title:'내용 입력해 주세요.',type:'warning'});
			return false;
		}
		
		var dataArry	= $('#form_validation').serializeArray();
		$('#searchMap').find('input').each(function(){
			dataArry.push({'name':this.name,'value':this.value});
		});
		
		cmnAjaxFn({
			url			: '/apage/case041.do'
			,data		: dataArry
			,dataType	: 'json'
			,success	: function(data){
				swal({title: "수정 되었습니다.",type: "success"},function(){
					$('#btnList').trigger('click');
				});
			}
			,error		: function(xhr,status,error){
				swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
			}
		});
	});
	
	$('#btnList').on('click',function(){
		var inputArr	= new Array();

		$('#searchMap').find('input').each(function(){
			inputArr.push({'name':this.name,'value':this.value});
		});

		formMake({
			'action'	: '/apage/case010.do'
			,'input'	: inputArr
		});		
	});
	
});