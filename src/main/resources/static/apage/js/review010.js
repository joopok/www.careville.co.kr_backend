$(function(){

	$('.btnSearch').on('click',function(e){
		const $f	= $('#searchFrom');
		$f.find('input,select').each(function(){
			if(this.type == 'hidden') return true;
			$(this).addClass('srch');
		});
		listCall($f.serializeArray());
	});

	// 전체 노출 버튼
	$('.btnDispAll').on('click',function(){
		var dispYn = $(this).attr('data-disp');
		var msg = dispYn === 'Y' ? '모든 비노출 리뷰를 노출로 변경하시겠습니까?' : '모든 노출 리뷰를 비노출로 변경하시겠습니까?';

		swal({
			title: msg,
			type: 'warning',
			showCancelButton: true,
			confirmButtonText: '확인',
			cancelButtonText: '취소',
			closeOnConfirm: false
		}, function(isConfirm){
			if(!isConfirm) return false;

			cmnAjaxFn({
				url: '/apage/review043.do',
				data: { 'dispYn': dispYn },
				dataType: 'json',
				success: function(data){
					swal({
						title: data.message,
						type: 'success'
					}, function(){
						listCall($('#searchFrom').serializeArray());
					});
				},
				error: function(xhr,status,error){
					swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
				}
			});
		});
	});
	
	$('#templateTable').on('click','.templateView',function(e){
		var inputArr	= $('#searchFrom').serializeArray();
		inputArr.push({'name':'reviewSeq','value':$(this).attr('data-seq')});
		
		formMake({
			'action'	: '/apage/review020.do'
			,'input'	: inputArr
		});
	});
	
	$('#templateTable').on('click','.badge[data-seq]',function(){
		var $badge = $(this);
		var reviewSeq = $badge.attr('data-seq');
		var currentDisp = $badge.attr('data-disp');
		var newDisp = (currentDisp === 'Y') ? 'N' : 'Y';

		cmnAjaxFn({
			url			: '/apage/review042.do'
			,data		: {
				'reviewSeq' : reviewSeq,
				'dispYn'	: newDisp
			}
			,dataType	: 'json'
			,success	: function(data){
				// 상태 토글
				$badge.attr('data-disp', newDisp);
				if(newDisp === 'Y') {
					$badge.removeClass('bg-grey').addClass('bg-green').text('노출');
				} else {
					$badge.removeClass('bg-green').addClass('bg-grey').text('비노출');
				}
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
	
	
	var firstData	= $('#searchMap').serializeArray();
	listCall(firstData.length == 0 ? {'currPage':'1'} : firstData);
	
	var $sf			= $('#searchFrom');
	$.each(firstData,function(i,d){
		$sf.find('input[name="'+d.name+'"]').val(d.value);
		$sf.find('select[name="'+d.name+'"]').val(d.value).selectpicker('render');
	});

});

function listCall(data){
	cmnAjaxFn({
		url			: '/apage/review011.do'
		,data		: data
		,dataType	: 'json'
		,success	: function(data){
			
			$('#templateTable').kTable({
				data		: data,
				pageing		: true,
				pageFnNm	: 'templateTable',
				header		: ['작성자','서비스','별점','서비스일자','등록일','노출'],
				cols		: [
					{
						col : 'reviewNm',addClass : 'tdc'},
					{
						col			: 'serviceNm',
						addClass	: 'templateView tdc', 
						linkAdd 	: function(data){return 'javascript:void(0)'}, 
						setArrt 	: function(data){
							return {'data-seq':data.reviewSeq};
						}						
					},
					{col	: 'starRate'	,addClass : 'tdc' ,render : function(data){
						return getStarHtml(data.starRate);
					}},
					{col	: 'svcDate'		,addClass : 'tdc' ,render : function(data){
						return toDate(data.svcDate);
					}},
					{col	: 'rgsDt'		,addClass : 'tdc'},
					{col	: 'dispYn'		,addClass : 'tdc' ,render : function(data){
						var isChecked = (data.dispYn === 'Y');
						var $badge = $('<span>')
							.addClass('badge')
							.addClass(isChecked ? 'bg-green' : 'bg-grey')
							.css({'cursor': 'pointer', 'padding': '6px 12px'})
							.attr('data-seq', data.reviewSeq)
							.attr('data-disp', data.dispYn)
							.text(isChecked ? '노출' : '비노출');
						return $badge;
					}}
				]				
			});
		}
		,error		: function(xhr,status,error){
			swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
		}
	});	
}

function getStarHtml(rate) {
	var html = "<span style='color:#ffd700;'>";
	for(var i = 1; i <= 5; i++) {
		if(i <= rate) {
			html += "★";
		} else {
			html += "☆";
		}
	}
	html += "</span>";
	return html;
}

function truncateText(text, maxLength) {
	if(text && text.length > maxLength) {
		return text.substring(0, maxLength) + "...";
	}
	return text || "";
}
