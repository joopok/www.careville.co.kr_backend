$(document).ready(function(){
	
	$('.btnReg').on('click',function(){
		cmnAjaxFn({
			url			: '/apage/mnger031.do'
			,data		: {}
			,dataType	: 'html'
			,success	: function(data){
				$('#defaultModalLabel').html('관리자 등록');
				
				const $modal	= $('#defaultModal').find('.modal-footer');
				$modal.find('.btnModal').remove();
				$('<button>').attr('type','button').addClass('btn btn-link waves-effect btnModal btnModalInsert').text('INSERT').prependTo($modal);
				
				const $viewDiv	= $('#viewDiv');			
				$viewDiv.empty();
				$viewDiv.html(data);
				$viewDiv.find('script').each(function() {
				    $.globalEval(this.text || this.textContent || this.innerHTML || '');
				});
				
				$('#defaultModal').modal('show');
				
				$('.mobile-phone-number').inputmask('999-9999-9999', { placeholder: '___-____-____'});
			}
			,error		: function(xhr,status,error){
				swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
			}
		});
	});
	
	$('#templateTable').on('click','.templateView,.btnPwUpdt,.btnDel',function(){
		const $this	= $(this);
		let _url	= '/apage/mnger021.do';
		let _tit	= '관리자 상세'
		
		if($this.hasClass('btnPwUpdt')){
			_url	= '/apage/mnger043.do';
			_tit	= '관리자 비밀번호 변경'
			
			if($this.attr('data-author') == 'false'){
				swal({title: "비밀번호 변경 권한이 없습니다.!",type: "warning"});
				return false;
			}
			
		}else if($this.hasClass('btnDel')){
			_url	= '/apage/mnger051.do';
			_tit	= '관리자 삭제'
			
			if($this.attr('data-author') == 'false'){
				swal({title: "삭제 권한이 없습니다.!",type: "warning"});
				return false;
			}
		}
		
		cmnAjaxFn({
			url			: _url
			,data		: {'mngrSeq':$this.closest('td').attr('data-seq')}
			,dataType	: 'html'
			,success	: function(data){
				$('#defaultModalLabel').html(_tit);
				
				const $modal	= $('#defaultModal').find('.modal-footer');
				const $btn		= $('<button>').attr('type','button').addClass('btn btn-link waves-effect').prependTo($modal);
				$modal.find('.btnModal').remove();
				
				if($this.hasClass('btnPwUpdt')){
					$btn.addClass('btnModal btnModalPwUpd').text('UPDATE').prependTo($modal);
					
				}else if($this.hasClass('btnDel')){
					$btn.addClass('btnModal btnModalDelete').text('DELETE').prependTo($modal);
					
				}else{
					$btn.addClass('btnModal btnModalUpdate').text('UPDATE').prependTo($modal);
				}
				
				const $viewDiv	= $('#viewDiv');			
				$viewDiv.empty();
				$viewDiv.html(data);
				$viewDiv.find('script').each(function() {
				    $.globalEval(this.text || this.textContent || this.innerHTML || '');
				});

				$('#defaultModal').modal('show');

				$('.mobile-phone-number').inputmask('999-9999-9999', { placeholder: '___-____-____'});	
			}
			,error		: function(xhr,status,error){
				swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
			}
		});
	});
	
	$('#templateTable').on('click','.mngrSttusChk input[type=checkbox]',function(){
		var $input	= $(this);
		var _val	= $input.val();
		
		if($input.attr('data-author') == 'false'){
			swal({title: "상태 변경 권한이 없습니다.!",type: "warning"});
			return false;
		}
		
		if(_val == '1'){
			swal({title: "슈퍼관리자는 변경 할 수 없습니다.!",type: "warning"});
			return false;
		}

		cmnAjaxFn({
			url			: '/apage/mnger042.do'
			,data		: {
				'mngrSeq' 	: _val,
				'mngrSttus'	: $input.is(':checked') ? 'Y' : 'N'
			}
			,dataType	: 'json'
			,success	: function(data){
				swal({title: "상태 값이 변경 되었습니다.",type: "success"});
			}
			,error		: function(xhr,status,error){
				swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
			}
		});

	});
	
	
	$('#pagination').on('click','a',function(e){
		const $li	= $(this).closest('li');
		if($li.hasClass('active')) return false;
		if(!$li.hasClass('disabled')){
			currPage	= {'currPage':$(this).attr('data-key')}; 
			listCall(currPage);
		}
	});
	
	/**
	 * Modal Action
	 * */ 
	$('#defaultModal').on('click','.btnModalInsert',function(){
		
		const $from	= $('#form_validation');
		
		if(!validationCheck()) return false;
		
		cmnAjaxFn({
			url			: '/apage/mnger032.do'
			,data		: $from.serializeArray()
			,dataType	: 'json'
			,success	: function(data){
				swal({title: "등록되었습니다",type: "success"},function(){
					$('#defaultModal').modal('hide');
					listCall(currPage);
				});
			}
			,error		: function(xhr,status,error){
				swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
			}
		});
	});
	
	$('#defaultModal').on('click','.btnModalUpdate',function(){
		
		const $from	= $('#form_validation');

		if(!validationCheck()) return false;
		
		cmnAjaxFn({
			url			: '/apage/mnger041.do'
			,data		: $from.serializeArray()
			,dataType	: 'json'
			,success	: function(data){

				swal({title: "수정 되었습니다.",type: "success"},function(){
					$('#defaultModal').modal('hide');
					listCall($('#searchFrom').serializeArray());
				});
			}
			,error		: function(xhr,status,error){
				swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
				
			}
		});
	});
	
	$('#defaultModal').on('click','.btnModalPwUpd',function(){
		
		const $from			= $('#form_validation');
		const _pwUpdt		= $.trim($from.find('input[name="mngrPwUpdt"]').val());
		const _pwUpdtChk	= $.trim($from.find('input[name="mngrPwUpdtChk"]').val());

		if(!validationCheck()) return false;
		
		if(_pwUpdt !== _pwUpdtChk){
			swal({title:'변경비밀번호와 변경비밀번호확인이 틀립니다.',type:'warning'});
			return false;
		}
		
		cmnAjaxFn({
			url			: '/apage/mnger044.do'
			,data		: $from.serializeArray()
			,dataType	: 'json'
			,success	: function(data){
				swal({title: "수정되었습니다.",type: "success"},function(){
					$('#defaultModal').modal('hide');
				});
			}
			,error		: function(xhr,status,error){
				swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
			}
		});
	});
	
	$('#defaultModal').on('click','.btnModalDelete',function(){
		const $from			= $('#form_validation');
		if(!validationCheck()) return false;
		
		swal({
				title	: "정말 삭제하시겠습니까?",
				text	: "삭제하면 되돌릴 수 없습니다!",
				type	: "warning",
				showCancelButton	: true,
				confirmButtonText	: "확인",
				cancelButtonText	: "취소",
				closeOnConfirm		: false,
				closeOnCancel		: true
			},function(isConfirm){
				if(!isConfirm) return false;
					
				cmnAjaxFn({
					url			: '/apage/mnger052.do'
					,data		: $from.serializeArray()
					,dataType	: 'json'
					,success	: function(data){
						swal({title: "삭제되었습니다",type: "success"},function(){
							$('#defaultModal').modal('hide');
							listCall($('#searchFrom').serializeArray());
						});				
					}
					,error		: function(xhr,status,error){
						swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
					}
				});
					
			}
		);	
	});
	
	let currPage	= {'currPage':1};
	listCall(currPage);
	
});

function validationCheck(){
	const $from	= $('#form_validation');
	let boolTy	= true;
	let $this,_val,_nm,_txt;

	$from.find('input,select').each(function(){
		$this	= $(this);
		_nm		= this.name;
		_val	= $.trim($this.val()); 
		_txt	= $this.attr('data-name');
		
		if(_nm == undefined || this.type == 'hidden') return true;
		
		if(_nm == 'mngrId' && _val.length < 5){
			swal({title:_txt+'를 5자리 이상으로 작성해주세요.',type:'warning'});
			boolTy	= false;
			return false;
			
		}else if(_nm == 'mngrTel' && _val.replace(/[^0-9]/gi,'').length < 9){
			swal({title:_txt+'를 확인해 주세요.',type:'warning'});
			boolTy	= false;
			return false;
			
		}else if(_nm == 'mngrPwUpdt' && _val != ''){

			if(_val.length < 8){
				swal({title:_txt+'를 최소 8자 이상이어야 합니다.',type:'warning'});
				boolTy	= false;
				return false;
			}
			if(isCommonPassword(_val)){
				swal({title:_txt+'를 자주 사용되는 비밀번호 패턴으로 사용할 수 없습니다.',type:'warning'});
				boolTy	= false;
				return false;
			}

		}else if(_val == ''){
			swal({title:_txt+'를(을) 확인해주세요.',type:'warning'});
			boolTy	= false;
			return false;
		}
	});
	
	return boolTy;
}

function listCall(data){
	cmnAjaxFn({
		url			: '/apage/mnger011.do'
		,data		: data
		,dataType	: 'json'
		,success	: function(data){
			
			$('#templateTable').kTable({
				data		: data,
				pageing		: true,
				pageFnNm	: 'templateTable',
				header		: ['아이디','이름','별칭','연락처','상태','관리'],
				cols		: [
					{
						col : 'mngrId'		,addClass : 'templateView tdc', 
						linkAdd : function(data){return 'javascript:void(0)'}, 
						setArrt : function(data){
							return {'data-seq':data.mngrSeq};
						}
					},
					{col	: 'mngrNm'		,addClass : 'tdc'},
					{col	: 'mngrNcnm'	,addClass : 'tdc'},
					{col	: 'mngrTel'		,addClass : 'tdc'},
					{col	: 'mngrSttus'	,addClass : 'tdc' ,render : function(data){
						var checked		= (data.mngrSttus == 'Y');
						var author		= (data.sesionMgnrSeq === '1');
						var $div		= $('<div>').addClass('switch');
						var $label		= $('<label>').addClass('mngrSttusChk').appendTo($div);
						$('<input>').attr({'type':'checkbox','data-author':author}).prop('checked',checked).val(data.mngrSeq).appendTo($label);
						$('<span>').addClass('lever switch-col-light-blue').prop('checked',checked).appendTo($label);
						
						return $div;
					}},
					{
						col	: 'mngrSttus'	,addClass : 'tdc'
						,setArrt : function(data){
							return {'data-seq':data.mngrSeq};
						}
						,render : function(data){
							var $rwa		= $('<div>');
							var $btn1		= $('<button>').addClass('btn btn-default waves-effect btnPwUpdt').appendTo($rwa);
							$btn1.attr('data-author',(data.sesionMgnrSeq === '1' || data.sesionMgnrSeq == data.mngrSeq));
							$('<i>').addClass('material-icons').html('vpn_key').appendTo($btn1);
							var $btn2		= $('<button>').addClass('btn btn-default waves-effect btnDel').appendTo($rwa);
							$btn2.attr('data-author',(data.sesionMgnrSeq === '1'));
							$('<i>').addClass('material-icons').html('delete_sweep').appendTo($btn2);
							return $rwa[0].innerHTML;
						}
					},
				]				
			});			

		}
		,error		: function(xhr,status,error){
			swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
		}
	});	
}
