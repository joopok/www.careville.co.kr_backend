// Menu Controll
$(function(){
	// Top My Info Layer Toggle
	$(".dropdown").click(function(){
		$(this).toggleClass("show");
		$(this).find('.dropdown-menu').toggleClass("show");
	});
	// Left Menu Layer Toggle
	$("#sidebarToggle").click(function(){
		$("body").toggleClass("sb-sidenav-toggled");		
	});
	// Left Menu Toggle
	$('#layoutSidenav_nav').find('.title').click(function(){
		var $this	= $(this);
		if($this.siblings().length > 0){
			$this.next('div').slideToggle(300);
			$this.closest('li').siblings().find('div').slideUp(300);
		}
	});	
	// Left Menu Reset
	$(document).click(function(e){
		var $t	= $(e.target);
		if($t.closest('.menu').length < 1){
			loadingNav();
		}
	});
	// Left Menu Activation 
	loadingNav();
	// Left Menu Activation Function
	function loadingNav(){
		var path	= window.location.pathname;
		$('#layoutSidenav_nav').find('a').each(function(){
			var $this	= $(this);
			if($this.attr('href') == path){
				var $thisLi	= $this.closest('li'); 
				if($thisLi.find('div').css('display') == 'block') return false;
				$this.addClass('active');
				$('#layoutSidenav_nav').find('li').find('div').slideUp(300);
				if(!$this.hasClass('title')){
					$thisLi.find('.title').addClass("active");
					$thisLi.find('div').slideDown(300);
				}
				return false;
			}
		});
	}
});


// 정규식 선언
var regexInt	= /^[0-9]$/gi;
var regexDate	= /^((19|20)\d{2})-([0][1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])/;
var regexTel	= /^\d{3}-\d{3,4}-\d{4}$/;
var regexMail	= /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
var regPwd 		= /^(?=.*?[a-z])(?=.*?[0-9]).{6,10}$/; 

// json 파싱
var printObj = typeof JSON != "undefined" ? JSON.stringify : function(obj) {  
	var arr = [];  
	$.each(obj, function(key, val) {    
		var next = key + ": ";    
		next += $.isPlainObject(val) ? printObj(val) : val;    
		arr.push( next );  
	});  
	return "{ " +  arr.join(", ") + " }";
};

function jsonDomDrowStr(obj,space){
	try{
		if(!Array.isArray(obj) && !$.isPlainObject(obj)){
			obj	= jQuery.parseJSON(obj);
		}
	}catch(e){
		return obj;
	}
	return JSON.stringify(obj,null,space).replace(/\n/gi,"<br>").replace(/ /gi, "&nbsp;");
}

// 비밀번호 체크
function isCommonPassword(pw) {
	// 많이 쓰이는 비밀번호 리스트
	const commonList = [
		"123456", "123456789", "111111", "000000", "qwerty", "abc123",
		"password", "admin", "welcome", "iloveyou", "letmein", "monkey",
		"dragon", "sunshine"
	];

	// 소문자 비교
	if (commonList.includes(pw.toLowerCase())) {
		return true;
	}

	// 단순 키보드/숫자 패턴 정규식
	const weakPatterns = [
		/(.)\1{2,}/,       // 같은 문자 반복 (111, aaa)
		/1234|2345|3456/,  // 연속된 숫자
		/qwer|asdf|zxcv/i, // 키보드 패턴
	];

	return weakPatterns.some((regex) => regex.test(pw));
}
/*
function validatePassword(pw) {
	if (pw.length < 8) {
		return "❌ 비밀번호는 최소 8자 이상이어야 합니다.";
	}
	if (!/[A-Z]/.test(pw) || !/[a-z]/.test(pw) || !/[0-9]/.test(pw)) {
		return "❌ 비밀번호는 대문자, 소문자, 숫자를 포함해야 합니다.";
	}
	if (isCommonPassword(pw)) {
		return "❌ 자주 사용되는 비밀번호 패턴은 사용할 수 없습니다.";
	}
	
	return "✅ 사용 가능한 비밀번호입니다.";
}
*/

// int 변환.(문자는 삭제 하고 숫자만 남김) 
function charOnlyIntFn(param){
	if(param == null || param == undefined) return '';
	var num	= String(param).replace(/[^0-9]/gi,'');
	return num;
}

function charOnlyEnFn(param){
	if(param == null || param == undefined) return '';
	var num	= String(param).replace(/[^A-Z]/gi,'').toUpperCase();
	return num;
}

// int 변환.(문자는 삭제 하고 숫자만 남김) 
function numberOnlyFn(param){
	if(param == null || param == undefined) return '';
	var num	= String(param).replace(/[^0-9]/gi,'');
	num		= $.trim(num) == '' ? '' : parseInt(num);
	return num;
}

//int 숫자 컴마포함 변환 
function numberCommaFn(param){
	if(param == null || param == undefined) return '';
	var num	= String(param).replace(/[^\.0-9]/g,'');
	return $.trim(num);
}

// int 변환
function parseIntFn(param){
	var num;
	if($.isNumeric(param)){
		num	= parseInt(param);
	}else{
		num	= parseInt(numberOnlyFn(param));
		if(!$.isNumeric(num)) num = 0;
	}
	return num;
}

// 숫자에 컴마찍기
function comMaFn(param){
	return param.toString().replace(/\B(?=(\d{3})+(?!\d))/g,',');
}

// 숫자 8자리(Date)이면 YYYY-MM-DD 형태로 변경
function toDate(param){
	if (!/^\d{8}$/.test(param)) {
		return param;
	}
	return param.replace(/^(\d{4})(\d{2})(\d{2})$/, "$1-$2-$3");
}

// 스크립트의 replace 한번만 변경 함, 전체를 변경  
function replaceAll(str, searchStr, replaceStr) {
    return str.split(searchStr).join(replaceStr);
}

// 공백(띄어쓰기) 제거
function trimAllFn(param){
	var returnStr	= '';
	if(param != null && param != undefined && param != ''){
		returnStr	= $.trim(param.replace(/\s/gi, ''));
	}
	return returnStr;
}

// 공통 아작스콜
function cmnAjaxFn(pramData){
	$.ajax({
		url			: pramData.url
		,data 		: pramData.data
		,headers	: {"x-kframe-ajax-call" : "Y"}
		,type		: "POST"
		,async		: false
		,dataType	: pramData.dataType
		,success	: function(data){
			if(data != null){
				if(data.isError == 'true'){
					swal({title:data.excpMsg,type:'warning'});
					return false;
				}				
				pramData.success(data);
			}else{
				swal({title: "서버와의 통신이 원활하지 않습니다.",text: "관리자에게 문의 하세요.",type: "warning"});
			}
		}
		,error		: function(xhr, status, error){
			pramData.error(xhr, status, error);
		}
		,beforeSend : pramData.beforeSend
		,complete	: pramData.complete
	});
}

function convertJSON(data){
	var ret=null;
	if(typeof data =='object'){
		ret= data;
	}else{
		try{ret=jQuery.parseJSON(data);}catch(e){}
	}
	chkJSONNull(ret);
	return ret;
}

function chkJSONNull(json){
	try{
		$.each(json, function(key,v){
			if(v==null){
				json[key]="";
			}else if(typeof v=="object"){
				chkJSONNull(v);
				return;
			}
		});
	}catch(e){}
}

// 폼 생성 이벤트
function formMake(pramData){
	var defaultsOTP	= {
		'formId'	: 'tempForm'
		,'action'	: ''  
		,'target'	: ''
		,'parent'	: 'body'
		,'input'	: [{'name':'defaultInput','value':''}]
		,'submit'	: true	
	};
	pramData = $.extend({}, defaultsOTP, pramData);
	$('#'+pramData.formId).remove();
	var $form	= $('<form>').attr({'id':pramData.formId,'method':'post','action':pramData.action}).appendTo(pramData.parent);
	if(pramData.target != ''){
		$form.attr('target',pramData.target);
	}
	inputMake({'input':pramData.input,'form':$form});
	if(pramData.submit && pramData.action != ''){
		$form.submit();
		setTimeout(function(){$form.remove()}, 100);
	}
	
	/* 예시
	formMake({
		'formId'	: 'tempForm'				// default:'tempForm'
		,'action'	: ''						// default:''
		,'target'	: ''						// default:''
		,'parent'	: 'body'					// default:body
		,'input'	: [{'name':'','value':''}]	// default:{'name':'defaultInput','value':''}
		,'submit'	: false						// default:true
	});
	*/
}


// 인풋 생성 이벤트
function inputMake(pramData){
	var $input;
	$.each(pramData.input,function(k,v){
		if(pramData.form.find('input').is('[name="'+v.name+'"]')){
			pramData.form.find('input[name="'+v.name+'"]').val(v.value);
		}else{
			$input	= $('<input type="hidden">').appendTo(pramData.form);
			$input.attr('name',v.name);
			$input.val(v.value);
		}
	});
	
	/* 예시
	inputMake({
		'input'	: [
					{'name':'input1'	,'value':''}
					,{'name':'input2'	,'value':''}
					,{'name':'input3'	,'value':''}		       	   
		       	   ]
		,'form'	: $('#cForm')
	});	
	 */
}
var pagingUtil	= {
	_url			: ''
	,_form			: ''
	,pageSubmit		: function(currPage) {
		$("#currPage").remove();
		$('<input>').attr({type:'hidden',id:'currPage',name:'currPage'}).val(currPage).appendTo(this._form);
		this._form.attr({action:this._url, method:'post'}).submit();
	}
};

//jquery datepicker 기본 셋팅
try{
	if($.datepicker != null){
		$.datepicker.setDefaults({
			closeText		: '닫기',
			prevText		: '이전달',
			nextText		: '다음달',
			currentText		: '오늘',
			monthNamesShort	: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
			dayNames		: ['일','월','화','수','목','금','토'],
			dayNamesShort	: ['일','월','화','수','목','금','토'],
			dayNamesMin		: ['일','월','화','수','목','금','토'],
			weekHeader		: 'Wk',
			dateFormat		: 'yy-mm-dd',
			firstDay		: 0,
			isRTL			: false,
			yearSuffix		: '',
			changeMonth		: true,
			changeYear		: true,
			yearRange		: 'c-99:c+5',	
			showMonthAfterYear	: true
		});
	}
}catch(e){} 

function reLoadOffFn(){	// 세로고침 방지
	document.oncontextmenu	= function(){return false;}		// 오른쪽 마우스 방지
	document.onselectstart	= function(){return false;};	// IE 택스트 선택 방지
	document.onkeydown		= function(evt){
		var keyCode	= evt.which ? evt.which : event.keyCode;
		if(keyCode == '116'|| keyCode == '17'){
			return false;
		}
	}
}

String.prototype.replaceAll = function(substr,newSubstr){
	return this.split(substr).join(newSubstr);
}


;(function($){
	$('.btnClose,.btnCancel').click(function(){
		modalControllerFn(false);
	});
});

function modalControllerFn(type,modalSubject){
	if(type){
		$('#modalLayer').find('.modalId').html(modalSubject);
		$('#modalBg').show();
		$('#modalLayer').show();
		document.body.style='overflow:hidden';
	}else{
		$('#modalBg').hide();
		$('#modalLayer').hide();
		document.body.style='overflow:auto';
	}
}

;(function($){
	$.fn.kTable = function(OTP){
		OTP 				= $.extend({}, $.fn.kTableDefaults, OTP);
		var thisId			= $(this).attr('id');
		var $table			= $(this).empty();
		var $thead			= $('<thead>').appendTo($table);
		var $tbody			= $('<tbody>').appendTo($table);
		var $tr				= $('<tr>');
		var $th				= $('<th>').addClass('tdc');
		var $td				= $('<td>');
		var checkAllLen		= 0;
		var checkCheckedLen	= 0;
		var $trClone,$thClone,$tdClone,$input;
		
		$trClone	= $tr.clone().appendTo($thead);
		$.each(OTP.header,function(k,v){
			$thClone	= $th.clone().appendTo($trClone);

			if(OTP.rowInput != null && k == 0){
				if(OTP.rowInput.type == 'checkbox'){
					$input	= $('<input>').appendTo($thClone);
					$input.attr({'type':'checkbox','id':OTP.rowInput.name,'class':'filled-in'}).val(OTP.rowInput.val);
					$('<label>').attr('for',OTP.rowInput.name).appendTo($thClone);
					$thClone.css('width','42px');
					
					$input.click(function(){
						if($(this).is(':checked')){
							$table.find('tbody').find('input:checkbox').prop('checked',true);
						}else{
							$table.find('tbody').find('input:checkbox').prop('checked',false);
						}
					});
				}

				$thClone	= $th.clone().appendTo($trClone);
			}
			
			if(OTP.rowIndex && k == 0){
				$thClone.html('No.');
				$thClone	= $th.clone().appendTo($trClone);
			}
			$thClone.html(v);
		});

		if(OTP.data.list.length > 0 ){
			$.each(OTP.data.list,function(k,data){
				
				if(OTP.data.sesionMgnrSeq) data['sesionMgnrSeq'] = OTP.data.sesionMgnrSeq;
				
				$trClone	= $tr.clone().appendTo($tbody);
				
				$.each(OTP.cols,function(kk,vv){
					$tdClone	= $td.clone().appendTo($trClone);

					if(OTP.rowInput != null && kk == 0){
						$tdClone.addClass('tdc');
						$input	= $('<input>').appendTo($tdClone);
						$input.attr({'type':OTP.rowInput.type,'name':OTP.rowInput.name,'id':OTP.rowInput.name+'_'+k,'class':'filled-in'}).val(data[OTP.rowInput.val]);
						$('<label>').attr('for',OTP.rowInput.name+'_'+k).appendTo($tdClone);
						
						if(OTP.rowInput.type == 'checkbox'){
							$input.click(function(){
								checkAllLen		= $table.find('tbody').find('input:checkbox').length;
								checkCheckedLen	= $table.find('tbody').find('input:checkbox:checked').length;
								if(checkAllLen == checkCheckedLen){
									$(document.getElementById(OTP.rowInput.name)).prop('checked',true);
								}else{
									$(document.getElementById(OTP.rowInput.name)).prop('checked',false);
								}
							});
						}
						
						$tdClone	= $td.clone().appendTo($trClone);
					}
					
					if(OTP.rowIndex && kk == 0){
						$tdClone.addClass('tdc');
						$tdClone.html(OTP.data.rowNum-k);
						$tdClone	= $td.clone().appendTo($trClone);
					}
					
					if(vv.addClass != undefined && vv.addClass != ''){
						$tdClone.addClass(vv.addClass);
					}
					
					if(vv.setArrt != undefined && vv.setArrt != ''){
						$tdClone.attr(vv.setArrt(data));
					}
					
					if(vv.linkAdd != undefined && vv.linkAdd != ''){
						$tdClone.append($('<a>').attr('href',vv.linkAdd(data)).html(data[vv.col]));
						
					}else{
						if(vv.render != undefined && vv.render != ''){
							$tdClone.html(vv.render(data));								
						}else{
							$tdClone.html(data[vv.col]);
						}
					}
				});
			});
			
		}else{
			$trClone	= $tr.clone().appendTo($tbody);
			$trClone.append($td.clone().attr('colspan','99').html('데이터가 없습니다.'));
		}
		
		const pdata	= OTP.data.pagination;
		const $p	= $('#pagination').empty();
		if(OTP.paging && pdata != null && pdata != '' && pdata.order.length > 0){
			var $pul	= $('<ul>').addClass('pagination').appendTo($p);
			var $pli	= $('<li>').addClass('paginate_button');
			var $pa		= $('<a>').attr('href','javascript:void(0);');
			var $pliClone,$paClone;
			
			$pliClone	= $pli.clone().addClass('previous').appendTo($pul);
			$paClone	= $pa.clone().appendTo($pliClone);
			$paClone.attr('data-key',pdata.prev).html('‹<span class="sr-only">Previous</span>');
			if(pdata.prev <= 0) $pliClone.addClass('disabled');
			
			$.each(pdata.order,function(n,m){
				$pliClone	= $pli.clone().appendTo($pul);
				$paClone	= $pa.clone().appendTo($pliClone);
				$paClone.attr('data-key',m).html(m);
				if(m == pdata.currPage) $pliClone.addClass('active');
			});
			
			$pliClone	= $pli.clone().addClass('next').appendTo($pul);
			$paClone	= $pa.clone().appendTo($pliClone);
			$paClone.attr('data-key',pdata.next).html('›<span class="sr-only">Next</span>');
			if(pdata.next <= 0) $pliClone.addClass('disabled');		
		}
	};
	
	$.fn.kTableDefaults = {
		data		: {
			list:[
				{'sampleCol_1':'row_1_col_1','sampleCol_2':'row_1_col_2','sampleCol_3':'row_1_col_3'},
				{'sampleCol_1':'row_2_col_1','sampleCol_2':'row_2_col_2','sampleCol_3':'row_2_col_3'},
				{'sampleCol_1':'row_3_col_1','sampleCol_2':'row_3_col_2','sampleCol_3':'row_3_col_3'},
			]
			,rowNum:'3'
			,pagination:'페이징 TAG ADD....'
		},
		paging		: true,
		pageFnNm	: 'defultPageFn',
		rowIndex	: true,
		rowInput	: null,	//{type:'checkbox',name:'defultRadio',val:'sampleCol_1'},		
		header		: ['sampleCol_1','sampleCo2_1','sampleCo3_1'],
		cols		: [
			{col	: 'sampleCol_1'	,addClass : 'addClass'},
			{col	: 'sampleCol_2'	,linkAdd : function(data){return '#none'}	,setArrt : function(data){ return {'onclick':'alert("'+data.sampleCol_2+'");'};}},
			{col	: 'sampleCol_3'	,render : function(data){return '<button>'+data.sampleCol_3+'</button>';}}
		]
	};
})(jQuery);

function defultPageFn(seq){
	alert('['+seq+'] 페이징 처리 함수!!');
}

function kModal(OTP){
	var kModalfaults = {
		msg	: 'Modal Subject',
		con	: function(){}
	};
	
	OTP	= $.extend({}, kModalfaults, OTP);
	
	if($('#modalLayer').length > 0 && $('#modalBg').length > 0){
		$('#modalLayer').find('.modalId').html(OTP.msg);
		
		var $modalContainer	= $('#modalLayer').find('.modalContainer').empty();
		
		if(typeof(OTP.con) == 'string'){
			var $content		= $('<div>').addClass('modalContent').appendTo($modalContainer);
			$('<div>').addClass('msgBox').html(OTP.con).appendTo($content);
		}else{
			$modalContainer.append(OTP.con($modalContainer));
		}
		
	}else{
		$('#modalLayer,#modalBg').remove();
		var $modalLayer		= $('<div>').attr('id','modalLayer').addClass('modalLayer').appendTo('body');
		$('<div>').attr('id','modalBg').addClass('modalBg').appendTo('body');
		var $modalHeader	= $('<div>').addClass('modalHeader').appendTo($modalLayer);
		$('<span>').addClass('bullet').text('-').appendTo($modalHeader);
		$('<span>').addClass('modalId').html(OTP.msg).appendTo($modalHeader);
		$('<span>').addClass('btnClose').text('X').click(modalClose).appendTo($modalHeader);
		var $modalContainer	= $('<div>').addClass('modalContainer').appendTo($modalLayer);
		
		if(typeof(OTP.con) == 'string'){
			var $content		= $('<div>').addClass('modalContent').appendTo($modalContainer);
			$('<div>').addClass('msgBox').html(OTP.con).appendTo($content);
		}else{
			$modalContainer.append(OTP.con($modalContainer));
		}
		
		var $modalFooter	= $('<div>').addClass('modalFooter').appendTo($modalLayer);
		$('<button>').addClass('btnCancel').text('닫기').click(modalClose).appendTo($modalFooter);
		
		function modalClose(){
			$('#modalBg').hide();
			$('#modalLayer').hide();
			document.body.style='overflow:auto';
		}
	}
	
	$('#modalBg').show();
	$('#modalLayer').show();
	document.body.style='overflow:hidden';

}
