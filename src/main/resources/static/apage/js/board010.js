$(document).ready(function(){

	var currPageData	= {'currPage':'1'};
	
	$('#pagination').on('click','a',function(e){
		if(!$(this).closest('li').hasClass('disabled')){
			currPageData	= {'currPage':$(this).attr('data-key')};
			listCall(currPageData);
		}
	});
	
	listCall(currPageData);
	
	$('#templateTable').on('click','.templateView',function(){
		cmnAjaxFn({
			url			: '/apage/board021.do'
			,data		: {'boardSeq':$(this).closest('td').attr('data-seq')}
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
	
	$('#defaultModal').on('click','.btnModalUpdate',function(){
		
		let defaultVal = $("#sttusCd option[selected]").val();
		let currentVal = $("#sttusCd").val();

		if(currentVal == defaultVal){
			swal({title:'상태 값이 변경 되지 않았습니다.',type:'warning'});
			return false;
		}
		
		cmnAjaxFn({
			url			: '/apage/board041.do'
			,data		: {'boardSeq':$('input[name="boardSeq"]').val(),'sttusCd':currentVal}
			,dataType	: 'json'
			,success	: function(data){
				$('#defaultModal').modal('hide');
				listCall(currPageData);
			}
			,error		: function(xhr,status,error){
				swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
			}
		});
	});
	
});

function listCall(data){
	cmnAjaxFn({
		url			: '/apage/board011.do'
		,data		: data
		,dataType	: 'json'
		,success	: function(data){
			
			$('#templateTable').kTable({
				data		: data,
				pageing		: true,
				pageFnNm	: 'templateTable',
				rowInput	: {type:'checkbox',name:'defultRadio',val:'sampleCol_1'},	
				header		: ['제목','공지여부','작성자','등록일'],
				cols		: [
					{
						col : 'boardSj',	
						addClass : 'templateView tdc', 
						linkAdd : function(data){return 'javascript:void(0)'}, 
						setArrt : function(data){
							return {'data-seq':data.boardSeq};	// ,'data-toggle':'modal' ,'data-target':'#defaultModal'
						}
					},
					{col	: 'noticeYn'	,addClass : 'tdc'},
					{col	: 'regNm'		,addClass : 'tdc'},
					{col	: 'rgsDt'		,addClass : 'tdc'},
				]				
			});			

		}
		,error		: function(xhr,status,error){
			swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
		}
	});	
}
