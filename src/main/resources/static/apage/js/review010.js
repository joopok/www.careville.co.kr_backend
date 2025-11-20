$(function(){

	$('.btnSearch').on('click',function(e){
		const $f	= $('#searchFrom');
		$f.find('input,select').each(function(){
			if(this.type == 'hidden') return true;
			$(this).addClass('srch');
		});
		listCall($f.serializeArray());
	});
	
	$('#templateTable').on('click','.templateView',function(e){
		var inputArr	= $('#searchFrom').serializeArray();
		inputArr.push({'name':'reviewSeq','value':$(this).attr('data-seq')});
		
		formMake({
			'action'	: '/apage/review020.do'
			,'input'	: inputArr
		});
	});
	
	$('#templateTable').on('click','.mngrSttusChk input[type=checkbox]',function(){
		var $input	= $(this);
		var _val	= $input.val();

		cmnAjaxFn({
			url			: '/apage/review042.do'
			,data		: {
				'reviewSeq' : _val,
				'dispYn'	: $input.is(':checked') ? 'Y' : 'N'
			}
			,dataType	: 'json'
			,success	: function(data){
//				console.log(data);
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
						var checked		= data.dispYn == 'Y' ? true : false;
						var $div		= $('<div>').addClass('switch');
						var $label		= $('<label>').addClass('mngrSttusChk').appendTo($div);
						$('<input>').attr('type','checkbox').prop('checked',checked).val(data.reviewSeq).appendTo($label);
						$('<span>').addClass('lever switch-col-light-blue').prop('checked',checked).appendTo($label);

						return $div;						
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
