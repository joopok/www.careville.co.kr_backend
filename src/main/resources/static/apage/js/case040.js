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

	// 기존 대표이미지1 로드
	if (typeof existingMainImg !== 'undefined' && existingMainImg && existingMainImgSrc) {
		mainUploader.loadExisting([{
			seq: existingMainImg,
			src: existingMainImgSrc
		}]);
	}

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

	// 기존 대표이미지2 로드
	if (typeof existingMainImg2 !== 'undefined' && existingMainImg2 && existingMainImg2Src) {
		mainUploader2.loadExisting([{
			seq: existingMainImg2,
			src: existingMainImg2Src
		}]);
	}

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

	// 기존 갤러리 이미지 로드 (대표이미지1, 대표이미지2 제외)
	if (typeof existingGallery !== 'undefined' && existingGallery && existingGallery.length > 0) {
		var galleryItems = existingGallery
			.filter(function(g) {
				return g.fileSeq !== mainFileSeq && g.fileSeq !== mainFileSeq2;
			})
			.map(function(g) {
				return {
					seq: g.viewFileSeq,
					src: '/fileView.do?viewFileSeq=' + g.viewFileSeq
				};
			});

		if (galleryItems.length > 0) {
			galleryUploader.loadExisting(galleryItems);
		}
	}

	// 수정 버튼
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

		var dataArry = $('#form_validation').serializeArray();
		$('#searchMap').find('input').each(function(){
			dataArry.push({'name': this.name, 'value': this.value});
		});

		cmnAjaxFn({
			url			: '/apage/case041.do',
			data		: dataArry,
			dataType	: 'json',
			success		: function(data){
				swal({title: "수정 되었습니다.", type: "success"}, function(){
					$('#btnList').trigger('click');
				});
			},
			error		: function(xhr, status, error){
				swal({title: "서버와의 통신이 원활하지 않습니다.", text: "잠시 후 다시 시도해 주세요.", type: "warning"});
			}
		});
	});

	// 목록 버튼
	$('#btnList, #btnListBottom').on('click', function(){
		var inputArr = [];

		$('#searchMap').find('input').each(function(){
			inputArr.push({'name': this.name, 'value': this.value});
		});

		formMake({
			'action'	: '/apage/case010.do',
			'input'		: inputArr
		});
	});

	// 삭제 버튼
	$('#btnDelete').on('click', function(){
		swal({
			title: "정말 삭제하시겠습니까?",
			text: "삭제된 데이터는 복구할 수 없습니다.",
			type: "warning",
			showCancelButton: true,
			confirmButtonColor: "#DD6B55",
			confirmButtonText: "삭제",
			cancelButtonText: "취소",
			closeOnConfirm: false
		}, function(){
			var dataArry = $('#searchMap').serializeArray();

			cmnAjaxFn({
				url			: '/apage/case051.do',
				data		: dataArry,
				dataType	: 'json',
				success		: function(data){
					swal({title: "삭제 되었습니다.", type: "success"}, function(){
						location.replace('/apage/case010.do');
					});
				},
				error		: function(xhr, status, error){
					swal({title: "서버와의 통신이 원활하지 않습니다.", text: "잠시 후 다시 시도해 주세요.", type: "warning"});
				}
			});
		});
	});

});
