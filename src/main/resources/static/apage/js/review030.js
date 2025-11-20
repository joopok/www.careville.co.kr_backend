$(function(){
	// 날짜 입력 마스크
	$("#svcDate").inputmask("99999999", {placeholder: "YYYYMMDD"});
	
	// 별점 클릭 이벤트
	$("#starRating .material-icons").on("click", function() {
		var rating = $(this).data("value");
		$("#starRate").val(rating);
		updateStarDisplay(rating);
	});
	
	// 초기 별점 5점 설정
	updateStarDisplay(5);
});

function updateStarDisplay(rating) {
	$("#starRating .material-icons").each(function() {
		var value = $(this).data("value");
		if(value <= rating) {
			$(this).text("star").addClass("active");
		} else {
			$(this).text("star_border").removeClass("active");
		}
	});
}

function saveReview() {
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
		reviewNm: $("#reviewNm").val(),
		serviceCd: $("#serviceCd").val(),
		starRate: $("#starRate").val(),
		svcDate: $("#svcDate").val(),
		dispYn: $("#dispYn").is(":checked") ? "Y" : "N",
		reviewCn: $("#reviewCn").val(),
		pw: $("#pw").val()
	};
	
	$.ajax({
		url: "/apage/review031.do",
		type: "POST",
		data: data,
		dataType: "json",
		success: function(result) {
			if(result.isReg == 'Y') {
				swal({title: "등록 되었습니다.",type: "success"},function(){
					location.href = "/apage/review010.do";
				});				
			} else {
				swal({title:'등록에 실패했습니다.',type:'warning'});
			}
		},
		error: function() {
			swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
		}
	});
}