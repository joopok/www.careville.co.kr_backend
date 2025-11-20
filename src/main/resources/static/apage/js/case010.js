$(document).ready(function(){
	
	$('.btnSearch').on('click',function(e){
		const $f	= $('#searchFrom');
		$f.find('input[name="currPage"]').val(1);
		formMake({
			'action'	: '/apage/case010.do'
			,'input'	: $f.serializeArray()
		});
	});

	$('.list-unstyled').on('click','.templateView',function(e){
		var inputArr	= new Array();
		inputArr.push({'name':'caseSeq','value':$(this).attr('data-key')});
		$('#searchMap').find('input').each(function(){
			inputArr.push({'name':this.name,'value':this.value});
		});
		
		formMake({
			'action'	: '/apage/case020.do'
			,'input'	: inputArr
		});
	});
	
	$('#btnWrite').on('click',function(e){
		formMake({'action'	: '/apage/case030.do'});
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
			
			formMake({
				'action'	: '/apage/case010.do'
				,'input'	: inputArr
			});
		}		
	});
	
	const firstData	= $('#searchMap').serializeArray();
	const $sf		= $('#searchFrom');

	$.each(firstData,function(i,d){
		$sf.find('input[name="'+d.name+'"]').val(d.value);
		$sf.find('select[name="'+d.name+'"]').val(d.value).selectpicker('render');
	});
});