$(document).ready(function(){

	// Register Button
	$('.btnReg').on('click',function(){
		cmnAjaxFn({
			url			: '/apage/mnger031.do'
			,data		: {}
			,dataType	: 'html'
			,success	: function(data){
				$('#defaultModalLabel').html('<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="8.5" cy="7" r="4"></circle><line x1="20" y1="8" x2="20" y2="14"></line><line x1="23" y1="11" x2="17" y2="11"></line></svg> 관리자 등록');

				const $modal	= $('#defaultModal').find('.modal-footer');
				$modal.find('.btnModal').remove();
				$('<button>').attr('type','button').addClass('cv-btn cv-btn-primary btnModal btnModalInsert').text('등록').prependTo($modal);

				const $viewDiv	= $('#viewDiv');
				$viewDiv.empty();
				$viewDiv.html(data);
				$viewDiv.find('script').each(function() {
				    $.globalEval(this.text || this.textContent || this.innerHTML || '');
				});

				$('#defaultModal').modal('show');

				$('.mobile-phone-number').inputmask('999-9999-9999', { placeholder: '___-____-____'});
			}
			,error		: function(xhr,status,error){
				swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
			}
		});
	});

	// Table Row Click - 상세 페이지로 이동
	$('#templateTable').on('click','.templateView',function(){
		const mngrSeq = $(this).closest('td').attr('data-seq') || $(this).attr('data-seq');
		location.href = '/apage/mnger020.do?mngrSeq=' + mngrSeq;
	});

	// 비밀번호 변경 버튼
	$('#templateTable').on('click','.btnPwUpdt',function(){
		const $this = $(this);
		if($this.attr('data-author') == 'false'){
			swal({title: "비밀번호 변경 권한이 없습니다!",type: "warning"});
			return false;
		}
		const mngrSeq = $this.closest('td').attr('data-seq');
		location.href = '/apage/mnger020.do?mngrSeq=' + mngrSeq;
	});

	// 삭제 버튼
	$('#templateTable').on('click','.btnDel',function(){
		const $this = $(this);
		if($this.attr('data-author') == 'false'){
			swal({title: "삭제 권한이 없습니다!",type: "warning"});
			return false;
		}
		const mngrSeq = $this.closest('td').attr('data-seq');

		swal({
			title: "정말 삭제하시겠습니까?",
			text: "삭제하면 되돌릴 수 없습니다!",
			type: "warning",
			showCancelButton: true,
			confirmButtonText: "삭제",
			cancelButtonText: "취소",
			closeOnConfirm: false
		}, function(isConfirm){
			if(!isConfirm) return;

			cmnAjaxFn({
				url: '/apage/mnger052.do',
				data: { mngrSeq: mngrSeq },
				dataType: 'json',
				success: function(data){
					swal({title: "삭제되었습니다.", type: "success"}, function(){
						listCall(currPage);
					});
				},
				error: function(){
					swal({title: "서버와의 통신이 원활하지 않습니다.", text: "잠시 후 다시 시도해 주세요.", type: "warning"});
				}
			});
		});
	});

	// Status Toggle
	$('#templateTable').on('click','.cv-status-toggle input[type=checkbox]',function(){
		var $input	= $(this);
		var _val	= $input.val();

		if($input.attr('data-author') == 'false'){
			swal({title: "상태 변경 권한이 없습니다!",type: "warning"});
			return false;
		}

		if(_val == '1'){
			swal({title: "슈퍼관리자는 변경할 수 없습니다!",type: "warning"});
			return false;
		}

		cmnAjaxFn({
			url			: '/apage/mnger042.do'
			,data		: {
				'mngrSeq' 	: _val,
				'mngrSttus'	: $input.is(':checked') ? 'A' : 'I'
			}
			,dataType	: 'json'
			,success	: function(data){
				swal({title: "상태가 변경되었습니다.",type: "success"});
				// Update stats
				updateStats();
			}
			,error		: function(xhr,status,error){
				swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
			}
		});

	});

	// Pagination
	$('#pagination').on('click','a',function(e){
		const $li	= $(this).closest('li');
		if($li.hasClass('active')) return false;
		if(!$li.hasClass('disabled')){
			currPage	= {'currPage':$(this).attr('data-key')};
			listCall(currPage);
		}
	});

	/**
	 * Modal Actions
	 */
	// Insert
	$('#defaultModal').on('click','.btnModalInsert',function(){

		const $from	= $('#form_validation');

		if(!validationCheck()) return false;

		cmnAjaxFn({
			url			: '/apage/mnger032.do'
			,data		: $from.serializeArray()
			,dataType	: 'json'
			,success	: function(data){
				swal({title: "등록되었습니다.",type: "success"},function(){
					$('#defaultModal').modal('hide');
					listCall(currPage);
				});
			}
			,error		: function(xhr,status,error){
				swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
			}
		});
	});

	// Update
	$('#defaultModal').on('click','.btnModalUpdate',function(){

		const $from	= $('#form_validation');

		if(!validationCheck()) return false;

		cmnAjaxFn({
			url			: '/apage/mnger041.do'
			,data		: $from.serializeArray()
			,dataType	: 'json'
			,success	: function(data){

				swal({title: "수정되었습니다.",type: "success"},function(){
					$('#defaultModal').modal('hide');
					listCall($('#searchFrom').serializeArray());
				});
			}
			,error		: function(xhr,status,error){
				swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});

			}
		});
	});

	// Password Update
	$('#defaultModal').on('click','.btnModalPwUpd',function(){

		const $from			= $('#form_validation');
		const _pwUpdt		= $.trim($from.find('input[name="mngrPwUpdt"]').val());
		const _pwUpdtChk	= $.trim($from.find('input[name="mngrPwUpdtChk"]').val());

		if(!validationCheck()) return false;

		if(_pwUpdt !== _pwUpdtChk){
			swal({title:'변경 비밀번호가 일치하지 않습니다.',type:'warning'});
			return false;
		}

		cmnAjaxFn({
			url			: '/apage/mnger044.do'
			,data		: $from.serializeArray()
			,dataType	: 'json'
			,success	: function(data){
				swal({title: "비밀번호가 변경되었습니다.",type: "success"},function(){
					$('#defaultModal').modal('hide');
				});
			}
			,error		: function(xhr,status,error){
				swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
			}
		});
	});

	// Delete
	$('#defaultModal').on('click','.btnModalDelete',function(){
		const $from			= $('#form_validation');
		if(!validationCheck()) return false;

		swal({
				title	: "정말 삭제하시겠습니까?",
				text	: "삭제하면 되돌릴 수 없습니다!",
				type	: "warning",
				showCancelButton	: true,
				confirmButtonText	: "삭제",
				cancelButtonText	: "취소",
				closeOnConfirm		: false,
				closeOnCancel		: true
			},function(isConfirm){
				if(!isConfirm) return false;

				cmnAjaxFn({
					url			: '/apage/mnger052.do'
					,data		: $from.serializeArray()
					,dataType	: 'json'
					,success	: function(data){
						swal({title: "삭제되었습니다.",type: "success"},function(){
							$('#defaultModal').modal('hide');
							listCall($('#searchFrom').serializeArray());
						});
					}
					,error		: function(xhr,status,error){
						swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
					}
				});

			}
		);
	});

	let currPage	= {'currPage':1};
	listCall(currPage);

});

function validationCheck(){
	const $from	= $('#form_validation');
	let boolTy	= true;
	let $this,_val,_nm,_txt;

	$from.find('input,select').each(function(){
		$this	= $(this);
		_nm		= this.name;
		_val	= $.trim($this.val());
		_txt	= $this.attr('data-name');

		if(_nm == undefined || this.type == 'hidden') return true;

		if(_nm == 'mngrId' && _val.length < 5){
			swal({title:_txt+'를 5자리 이상으로 작성해주세요.',type:'warning'});
			boolTy	= false;
			return false;

		}else if(_nm == 'mngrTel' && _val.replace(/[^0-9]/gi,'').length < 9){
			swal({title:_txt+'를 확인해 주세요.',type:'warning'});
			boolTy	= false;
			return false;

		}else if(_nm == 'mngrPwUpdt' && _val != ''){

			if(_val.length < 8){
				swal({title:_txt+'는 최소 8자 이상이어야 합니다.',type:'warning'});
				boolTy	= false;
				return false;
			}
			if(isCommonPassword(_val)){
				swal({title:'자주 사용되는 비밀번호 패턴은 사용할 수 없습니다.',type:'warning'});
				boolTy	= false;
				return false;
			}

		}else if(_val == ''){
			swal({title:_txt+'를(을) 입력해주세요.',type:'warning'});
			boolTy	= false;
			return false;
		}
	});

	return boolTy;
}

function updateStats() {
	// This will be called after list loads
}

function listCall(data){
	cmnAjaxFn({
		url			: '/apage/mnger011.do'
		,data		: data
		,dataType	: 'json'
		,success	: function(data){

			// Update stats
			var totalCount = 0;
			var superCount = 0;
			var activeCount = 0;
			var inactiveCount = 0;

			if(data.list && data.list.length > 0) {
				totalCount = data.pagination ? data.pagination.totalCnt : data.list.length;
				data.list.forEach(function(item) {
					if(item.mngrSeq == '1') superCount++;
					if(item.mngrSttus == 'A') activeCount++;
					else inactiveCount++;
				});
			}

			$('#totalCount').text(totalCount);
			$('#superCount').text(superCount);
			$('#activeCount').text(activeCount);
			$('#inactiveCount').text(inactiveCount);

			$('#templateTable').kTable({
				data		: data,
				pageing		: true,
				pageFnNm	: 'templateTable',
				header		: ['관리자 정보','역할','연락처','상태','관리'],
				cols		: [
					{
						col : 'mngrId'		,addClass : 'templateView',
						linkAdd : function(data){return 'javascript:void(0)'},
						setArrt : function(data){
							return {'data-seq':data.mngrSeq};
						},
						render : function(data){
							var isSuper = (data.mngrSeq == '1');
							var avatarClass = isSuper ? 'cv-manager-avatar super' : 'cv-manager-avatar';
							var initial = data.mngrNm ? data.mngrNm.substring(0, 1) : 'A';

							return '<div class="cv-manager-info">' +
								'<div class="' + avatarClass + '">' + initial + '</div>' +
								'<div class="cv-manager-details">' +
									'<span class="cv-manager-id templateView" data-seq="' + data.mngrSeq + '">' + data.mngrId + '</span>' +
									'<span class="cv-manager-nickname">' + (data.mngrNcnm || data.mngrNm) + '</span>' +
								'</div>' +
							'</div>';
						}
					},
					{
						col	: 'mngrSeq'		,addClass : ''
						,render : function(data){
							var isSuper = (data.mngrSeq == '1');
							if(isSuper) {
								return '<span class="cv-role-badge super"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2L2 7l10 5 10-5-10-5z"></path><path d="M2 17l10 5 10-5"></path><path d="M2 12l10 5 10-5"></path></svg> 슈퍼관리자</span>';
							}
							return '<span class="cv-role-badge admin"><svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg> 관리자</span>';
						}
					},
					{
						col	: 'mngrTel'		,addClass : ''
						,render : function(data){
							return '<div class="cv-contact"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path></svg> ' + (data.mngrTel || '-') + '</div>';
						}
					},
					{col	: 'mngrSttus'	,addClass : '' ,render : function(data){
						var checked		= (data.mngrSttus == 'A');
						var isSuper		= (data.mngrSeq == '1');
						var author		= (data.sesionMgnrSeq === '1');

						return '<label class="cv-status-toggle">' +
							'<input type="checkbox" ' + (checked ? 'checked' : '') + ' ' + (isSuper ? 'disabled' : '') + ' value="' + data.mngrSeq + '" data-author="' + author + '">' +
							'<span class="cv-status-slider"></span>' +
						'</label>';
					}},
					{
						col	: 'mngrSttus'	,addClass : ''
						,setArrt : function(data){
							return {'data-seq':data.mngrSeq};
						}
						,render : function(data){
							var author = (data.sesionMgnrSeq === '1' || data.sesionMgnrSeq == data.mngrSeq);
							var deleteAuthor = (data.sesionMgnrSeq === '1');

							return '<div class="cv-action-group">' +
								'<button type="button" class="cv-action-btn btnPwUpdt" data-author="' + author + '" title="비밀번호 변경">' +
									'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path></svg>' +
								'</button>' +
								'<button type="button" class="cv-action-btn danger btnDel" data-author="' + deleteAuthor + '" title="삭제">' +
									'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg>' +
								'</button>' +
							'</div>';
						}
					},
				]
			});

		}
		,error		: function(xhr,status,error){
			swal({title: "서버와의 통신이 원활하지 않습니다.",text: "잠시 후 다시 시도해 주세요.",type: "warning"});
		}
	});
}
