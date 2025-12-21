$(function(){
	// 날짜 입력 마스크
	$("#svcDate").inputmask("99999999", {placeholder: "YYYYMMDD"});

	// 별점 클릭 이벤트 (SVG .star 사용)
	$("#starRating .star").on("click", function() {
		var rating = $(this).data("value");
		$("#starRate").val(rating);
		updateStarDisplay(rating);
	});

	// 현재 별점 표시
	var currentRating = $("#starRate").val() || 5;
	updateStarDisplay(currentRating);
});

function updateStarDisplay(rating) {
	// SVG 기반 별점 표시 업데이트
	$("#starRating .star").each(function() {
		var value = $(this).data("value");
		if(value <= rating) {
			$(this).addClass("active").css("color", "var(--cv-gold, #f59e0b)");
		} else {
			$(this).removeClass("active").css("color", "var(--cv-silver-500, #6b7280)");
		}
	});
}

function updateReview() {
	// 유효성 검사
	if(!$("#reviewNm").val()) {
		swal({title:'작성자명을 입력하세요.',type:'warning'});
		return;
	}
	
	if(!$("#serviceCd").val()) {
		swal({title:'서비스 종류를 선택하세요.',type:'warning'});
		return;
	}
	
	if(!$("#reviewCn").val()) {
		swal({title:'후기 내용을 입력하세요.',type:'warning'});
		return;
	}
	
	var data = {
		reviewSeq: $("#reviewSeq").val(),
		reviewNm: $("#reviewNm").val(),
		serviceCd: $("#serviceCd").val(),
		starRate: $("#starRate").val(),
		svcDate: $("#svcDate").val(),
		dispYn: $("#dispYn").val(),
		reviewCn: $("#reviewCn").val(),
		pw: $("#pw").val()
	};
	
	$.ajax({
		url: "/apage/review041.do",
		type: "POST",
		data: data,
		dataType: "json",
		success: function(result) {
			if(result.isUpd == 'Y') {
				swal({title: "수정 되었습니다.",type: "success"},function(){
					var reviewSeq	= $('#searchMap').find('input[name="reviewSeq"]').val();
					var inputArr	= pageMoveInputArr();
					inputArr.push({'name':'reviewSeq','value':reviewSeq});
					formMake({
						'action'	: '/apage/review020.do'
						,'input'	: inputArr
					});
				});				
				
			} else {
				swal({title:result.message || "수정에 실패했습니다.",type:'warning'});
			}
		},
		error: function() {
			swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
		}
	});
}


function cancelList(){
	formMake({
		'action'	: '/apage/review010.do'
		,'input'	: pageMoveInputArr()
	});
}


function pageMoveInputArr(){
	var inputArr	= new Array();

	$('#searchMap').find('input').each(function(){
		inputArr.push({'name':this.name,'value':this.value});
	});
	
	return inputArr;
}