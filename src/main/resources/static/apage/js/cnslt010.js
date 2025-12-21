$(document).ready(function(){

	$('.btnSearch').on('click',function(e){
		const $f	= $('#searchFrom');
		$f.find('input,select').each(function(){
			if(this.type == 'hidden') return true;
			$(this).addClass('srch');
		});
		listCall($f.serializeArray());
	});

	// 테이블 행 클릭 - 상세 페이지로 이동
	$('#templateTable').on('click','.templateView',function(){
		var cnsltSeq = $(this).closest('td').attr('data-seq');
		var $form = $('<form>').attr({
			'method': 'POST',
			'action': '/apage/cnslt020.do'
		});

		// cnsltSeq 추가
		$form.append($('<input>').attr({
			'type': 'hidden',
			'name': 'cnsltSeq',
			'value': cnsltSeq
		}));

		// 검색 조건 유지를 위해 searchFrom의 값들 추가
		$('#searchFrom').find('input,select').each(function(){
			$form.append($('<input>').attr({
				'type': 'hidden',
				'name': this.name,
				'value': this.value
			}));
		});

		$('body').append($form);
		$form.submit();
	});

	// 상태 배지 클릭 시 상태 변경
	$('#templateTable').on('click','.badge-status[data-seq]',function(){
		var $badge = $(this);
		var cnsltSeq = $badge.attr('data-seq');
		var currentStatus = $badge.attr('data-status');
		var newStatus = (currentStatus === '001') ? '002' : '001';
		var statusText = (newStatus === '001') ? '대기' : '완료';

		swal({
			title: '상태를 "' + statusText + '"로 변경하시겠습니까?',
			type: 'warning',
			showCancelButton: true,
			confirmButtonText: '확인',
			cancelButtonText: '취소',
			closeOnConfirm: false
		}, function(isConfirm){
			if(!isConfirm) return false;

			cmnAjaxFn({
				url			: '/apage/cnslt041.do'
				,data		: {'cnsltSeq': cnsltSeq, 'sttusCd': newStatus}
				,dataType	: 'json'
				,success	: function(data){
					swal({title: "상태가 변경되었습니다.", type: "success"}, function(){
						// 배지 상태 업데이트
						$badge.attr('data-status', newStatus);
						if(newStatus === '002') {
							$badge.removeClass('bg-orange').addClass('bg-grey').text('완료');
						} else {
							$badge.removeClass('bg-grey').addClass('bg-orange').text('대기');
						}
					});
				}
				,error		: function(xhr,status,error){
					swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
				}
			});
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
				header		: ['고객명','서비스명','희망일','등록일','유형','상태'],
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
						var isReservation = (data.reqType === '002');
						var $badge = $('<span>')
							.addClass('badge')
							.addClass(isReservation ? 'bg-green' : 'bg-blue')
							.css({'padding': '6px 12px'})
							.text(data.reqTypeNm);
						return $badge;
					}},
					{col	: 'sttusNm'	,addClass : 'tdc' ,render : function(data){
						var isComplete = (data.sttusCd === '002');
						var $badge = $('<span>')
							.addClass('badge badge-status')
							.addClass(isComplete ? 'bg-grey' : 'bg-orange')
							.css({'cursor': 'pointer', 'padding': '6px 12px'})
							.attr('data-seq', data.cnsltSeq)
							.attr('data-status', data.sttusCd)
							.text(isComplete ? '완료' : '대기');
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
