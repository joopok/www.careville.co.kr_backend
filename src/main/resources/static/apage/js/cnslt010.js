$(document).ready(function(){

	$('.btnSearch').on('click',function(e){
		const $f	= $('#searchFrom');
		$f.find('input,select').each(function(){
			if(this.type == 'hidden') return true;
			$(this).addClass('srch');
		});
		listCall($f.serializeArray());
	});

	$('#templateTable').on('click','.templateView',function(){
		cmnAjaxFn({
			url			: '/apage/cnslt021.do'
			,data		: {'cnsltSeq':$(this).closest('td').attr('data-seq')}
			,dataType	: 'html'
			,success	: function(data){
				$('#viewDiv').empty();
				$('#viewDiv').html(data);
				$('#defaultModal').modal('show');
				
				$('#viewDiv').find('script').each(function() {
				    $.globalEval(this.text || this.textContent || this.innerHTML || '');
				});
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
			const inputArr	= new Array();
			const $srchFrom	= $('#searchFrom');
			 
			$srchFrom.find('input[name="currPage"]').val($(this).attr('data-key'));
			$srchFrom.find('input,select').each(function(){
				if(this.type != 'hidden' && !$(this).hasClass('srch')) return true;
				inputArr.push({'name':this.name,'value':this.value});
			});
			
			listCall(inputArr);
		}
	});
	
	$('#defaultModal').on('click','.btnModalUpdate',function(){
		const defaultVal = $("#sttusCd option[selected]").val();
		const currentVal = $("#sttusCd").val();

		if(currentVal == defaultVal){
			swal({title: "상태 값이 변경 되지 않았습니다.!",type: "warning"});
			return false;
		}
		
		cmnAjaxFn({
			url			: '/apage/cnslt041.do'
			,data		: {'cnsltSeq':$('input[name="cnsltSeq"]').val(),'sttusCd':currentVal}
			,dataType	: 'json'
			,success	: function(data){
				swal({title: "상태 값이 변경 되었습니다.",type: "success"},function(){
					$('#defaultModal').modal('hide');
					listCall($('#searchFrom').serializeArray());
				});
			}
			,error		: function(xhr,status,error){
				swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
			}
		});
	});
	
	listCall($('#searchFrom').serializeArray());
	
});

function listCall(data){
	cmnAjaxFn({
		url			: '/apage/cnslt011.do'
		,data		: data
		,dataType	: 'json'
		,success	: function(data){
			
			$('#templateTable').kTable({
				data		: data,
				pageing		: true,
				pageFnNm	: 'templateTable',
				header		: ['작성자','서비스명','희망일','등록일','예약/상담','상태'],
				cols		: [
					{
						col : 'nm',	
						addClass : 'templateView tdc', 
						linkAdd : function(data){return 'javascript:void(0)'}, 
						setArrt : function(data){
							return {'data-seq':data.cnsltSeq};
						}
					},
					{col	: 'serviceName'	,addClass : 'tdc'},
					{col	: 'hopeDay'		,addClass : 'tdc'},
					{col	: 'regDt'		,addClass : 'tdc'},
					{col	: 'reqTypeNm'	,addClass : 'tdc' ,render : function(data){
						return $('<span>').addClass('badge '+(data.reqType == '002'?'bg-light-green':'bg-blue')).html(data.reqTypeNm);
					}},
					{col	: 'sttusNm'	,addClass : 'tdc' ,render : function(data){
						return $('<span>').addClass('badge '+(data.sttusCd == '002'?'bg-grey':'bg-orange')).html(data.sttusNm);
					}},
				]				
			});			

		}
		,error		: function(xhr,status,error){
			swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
		}
	});	
}
