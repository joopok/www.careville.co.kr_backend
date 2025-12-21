$(document).ready(function(){
	
	$('.btnSearch').on('click',function(e){
		const $f	= $('#searchFrom');
		$f.find('input[name="currPage"]').val(1);
		formMake({
			'action'	: '/apage/case010.do'
			,'input'	: $f.serializeArray()
		});
	});

	// 테이블 행 클릭 시 상세 페이지 이동
	$('#caseTable').on('click', '.cv-case-row', function(e){
		// 버튼 클릭 시에는 행 클릭 이벤트 무시
		if($(e.target).closest('.btnView').length > 0) return;

		var inputArr = new Array();
		inputArr.push({'name':'caseSeq','value':$(this).attr('data-key')});
		$('#searchMap').find('input').each(function(){
			inputArr.push({'name':this.name,'value':this.value});
		});

		formMake({
			'action'	: '/apage/case020.do'
			,'input'	: inputArr
		});
	});

	// 보기 버튼 클릭 시 상세 페이지 이동
	$('#caseTable').on('click', '.btnView', function(e){
		e.stopPropagation();
		var inputArr = new Array();
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

    // View mode toggle (grid/table)
    function setViewMode(mode){
        if(mode === 'grid'){
            $('#caseListTableBlock').hide();
            $('#caseListGridBlock').show();
            $('#btnGridView').removeClass('cv-btn-ghost').addClass('cv-btn-primary');
            $('#btnTableView').removeClass('cv-btn-primary').addClass('cv-btn-ghost');
        }else{
            $('#caseListGridBlock').hide();
            $('#caseListTableBlock').show();
            $('#btnTableView').removeClass('cv-btn-ghost').addClass('cv-btn-primary');
            $('#btnGridView').removeClass('cv-btn-primary').addClass('cv-btn-ghost');
        }
        try{ localStorage.setItem('caseViewMode', mode); }catch(e){}
    }

    // init view mode
    var initMode = 'table';
    try{ initMode = localStorage.getItem('caseViewMode') || 'table'; }catch(e){}
    setViewMode(initMode);

    $('#btnGridView').on('click', function(){ setViewMode('grid'); });
    $('#btnTableView').on('click', function(){ setViewMode('table'); });

    // Grid card click to detail
    $('#caseListGridBlock').on('click', '.cv-case-card', function(e){
        if($(e.target).closest('.btnView').length > 0) return;
        var inputArr = new Array();
        inputArr.push({'name':'caseSeq','value':$(this).attr('data-key')});
        $('#searchMap').find('input').each(function(){
            inputArr.push({'name':this.name,'value':this.value});
        });
        formMake({ 'action': '/apage/case020.do', 'input': inputArr });
    });

    // Grid view button to detail
    $('#caseListGridBlock').on('click', '.btnView', function(e){
        e.stopPropagation();
        var inputArr = new Array();
        inputArr.push({'name':'caseSeq','value':$(this).attr('data-key')});
        $('#searchMap').find('input').each(function(){
            inputArr.push({'name':this.name,'value':this.value});
        });
        formMake({ 'action': '/apage/case020.do', 'input': inputArr });
    });

    // Click tag to filter by hashtag
    $('#caseListGridBlock').on('click', '.cv-case-tag', function(e){
        e.stopPropagation();
        var tag = $(this).text() || '';
        // normalize: remove starting '#', trim
        tag = tag.replace(/^#\s*/, '').trim();
        if(tag){
            $sf.find('input[name="hashtag"]').val('#' + tag);
            $('.btnSearch').click();
        }
    });

    // Service chips filtering
    function syncChips(){
        var val = $sf.find('select[name="serviceCd"]').val() || '';
        $('.svc-chip').removeClass('active');
        $('.svc-chip').each(function(){
            if($(this).attr('data-value') === val){ $(this).addClass('active'); }
        });
    }
    syncChips();

    $('.svc-chip').on('click', function(){
        var val = $(this).attr('data-value') || '';
        $sf.find('select[name="serviceCd"]').val(val).selectpicker('render');
        $('.btnSearch').click();
    });
});
