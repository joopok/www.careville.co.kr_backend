var oEditors = [];
var mainUploader = null;
var mainUploader2 = null;
var galleryUploader = null;

$(document).ready(function () {

	// SmartEditor 초기화
	nhn.husky.EZCreator.createInIFrame({
		oAppRef			: oEditors,
		elPlaceHolder	: "caseCn",
		sSkinURI		: "/plugins/smartEditor/SmartEditor2Skin.html",
		fCreator		: "createSEditor2"
	});

	// 대표이미지1 업로더 초기화
	mainUploader = new CVImageUploader({
		dropzoneId: 'mainDropzone',
		previewId: 'mainPreview',
		hiddenInputId: 'mainImg',
		multiple: false,
		maxFiles: 1,
		sortable: false,
		onUploadComplete: function(seq, files) {
			console.log('대표이미지1 업로드 완료:', seq);
		},
		onDelete: function(seq, files) {
			console.log('대표이미지1 삭제:', seq);
		}
	});

	// 대표이미지2 업로더 초기화
	mainUploader2 = new CVImageUploader({
		dropzoneId: 'mainDropzone2',
		previewId: 'mainPreview2',
		hiddenInputId: 'mainImg2',
		multiple: false,
		maxFiles: 1,
		sortable: false,
		onUploadComplete: function(seq, files) {
			console.log('대표이미지2 업로드 완료:', seq);
		},
		onDelete: function(seq, files) {
			console.log('대표이미지2 삭제:', seq);
		}
	});

	// 갤러리 업로더 초기화
	galleryUploader = new CVImageUploader({
		dropzoneId: 'galleryDropzone',
		previewId: 'galleryPreview',
		hiddenInputId: 'galleryArr',
		multiple: true,
		maxFiles: 10,
		sortable: true,
		onUploadComplete: function(seq, files) {
			console.log('갤러리 이미지 업로드 완료:', seq, '총', files.length, '개');
		},
		onDelete: function(seq, files) {
			console.log('갤러리 이미지 삭제:', seq);
		},
		onOrderChange: function(seqs) {
			console.log('갤러리 순서 변경:', seqs);
		}
	});

	// 저장 버튼
	$('#btnSubmit').on('click', function(){

		oEditors.getById["caseCn"].exec("UPDATE_CONTENTS_FIELD", []);

		if($.trim($('#caseSj').val()) == ''){
			swal({title:'제목을 입력해 주세요.', type:'warning'});
			return false;
		}
		if($.trim($('#serviceCd').val()) == ''){
			swal({title:'서비스 종류를 선택해 주세요.', type:'warning'});
			return false;
		}
		if($.trim($('#hashtag').val()) != '' && $.trim($('#hashtag').val()).length > 24){
			swal({title:'해시태그 사이즈를 줄여주세요.(MaxSize:24)', type:'warning'});
			return false;
		}
		if($.trim($('#mainImg').val()) == ''){
			swal({title:'대표이미지1을 등록해 주세요.', type:'warning'});
			return false;
		}
		if($.trim($('#caseCn').val()) == '' || $.trim($('#caseCn').val()) == '<p><br></p>'){
			swal({title:'내용을 입력해 주세요.', type:'warning'});
			return false;
		}

		cmnAjaxFn({
			url			: '/apage/case031.do',
			data		: $('#form_validation').serialize(),
			dataType	: 'json',
			success		: function(data){
				swal({title: "저장 되었습니다.", type: "success"}, function(){
					location.replace('/apage/case010.do');
				});
			},
			error		: function(xhr, status, error){
				swal({title: "서버와의 통신이 원활하지 않습니다.", text: "잠시 후 다시 시도해 주세요.", type: "warning"});
			}
		});
	});

	// 목록 버튼
	$('#btnList, #btnListBottom').on('click', function(){
		location.replace('/apage/case010.do');
	});

});
