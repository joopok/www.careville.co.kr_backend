/**
 * CV Image Uploader - 드래그앤드롭 이미지 업로더 컴포넌트
 *
 * 기능:
 * - 드래그앤드롭 업로드
 * - 이미지 미리보기
 * - 다중 파일 업로드
 * - 순서 변경 (SortableJS 사용)
 */
class CVImageUploader {
    constructor(options) {
        this.dropzoneId = options.dropzoneId;           // 드롭존 컨테이너 ID
        this.previewId = options.previewId;             // 미리보기 컨테이너 ID
        this.hiddenInputId = options.hiddenInputId;     // 값 저장 hidden input ID
        this.multiple = options.multiple || false;       // 다중 파일 허용 여부
        this.maxFiles = options.maxFiles || 10;         // 최대 파일 수
        this.sortable = options.sortable || false;      // 순서 변경 가능 여부
        this.onUploadComplete = options.onUploadComplete || null;  // 업로드 완료 콜백
        this.onDelete = options.onDelete || null;       // 삭제 콜백
        this.onOrderChange = options.onOrderChange || null; // 순서 변경 콜백

        this.uploadedFiles = [];  // { seq: '암호화된seq', preview: 'base64' }
        this.sortableInstance = null;

        this.init();
    }

    init() {
        this.dropzone = document.getElementById(this.dropzoneId);
        this.preview = document.getElementById(this.previewId);
        this.hiddenInput = document.getElementById(this.hiddenInputId);

        if (!this.dropzone || !this.preview) {
            console.error('CVImageUploader: 필수 요소를 찾을 수 없습니다.');
            return;
        }

        // 파일 input 생성 (숨김)
        this.fileInput = document.createElement('input');
        this.fileInput.type = 'file';
        this.fileInput.accept = 'image/*';
        this.fileInput.multiple = this.multiple;
        this.fileInput.style.display = 'none';
        this.dropzone.appendChild(this.fileInput);

        this.bindEvents();

        // 순서 변경 초기화
        if (this.sortable && typeof Sortable !== 'undefined') {
            this.initSortable();
        }
    }

    bindEvents() {
        // 드롭존 클릭 시 파일 선택
        this.dropzone.addEventListener('click', (e) => {
            if (e.target.closest('.cv-uploader-item-del')) return;
            this.fileInput.click();
        });

        // 파일 선택 시
        this.fileInput.addEventListener('change', (e) => {
            this.handleFiles(e.target.files);
            this.fileInput.value = ''; // 리셋
        });

        // 드래그 이벤트
        this.dropzone.addEventListener('dragover', (e) => {
            e.preventDefault();
            e.stopPropagation();
            this.dropzone.classList.add('dragover');
        });

        this.dropzone.addEventListener('dragleave', (e) => {
            e.preventDefault();
            e.stopPropagation();
            this.dropzone.classList.remove('dragover');
        });

        this.dropzone.addEventListener('drop', (e) => {
            e.preventDefault();
            e.stopPropagation();
            this.dropzone.classList.remove('dragover');
            this.handleFiles(e.dataTransfer.files);
        });
    }

    initSortable() {
        this.sortableInstance = new Sortable(this.preview, {
            animation: 150,
            ghostClass: 'cv-uploader-ghost',
            dragClass: 'cv-uploader-drag',
            handle: '.cv-uploader-item',
            onEnd: () => {
                this.updateOrder();
            }
        });
    }

    handleFiles(files) {
        const fileArray = Array.from(files).filter(f => f.type.startsWith('image/'));

        if (!this.multiple) {
            // 단일 파일: 기존 파일 제거
            this.uploadedFiles = [];
            this.preview.innerHTML = '';
        }

        // 최대 파일 수 체크
        const remaining = this.maxFiles - this.uploadedFiles.length;
        const toProcess = fileArray.slice(0, remaining);

        if (toProcess.length < fileArray.length) {
            alert(`최대 ${this.maxFiles}개까지 업로드 가능합니다.`);
        }

        toProcess.forEach(file => this.processFile(file));
    }

    processFile(file) {
        // 미리보기 생성
        const reader = new FileReader();
        reader.onload = (e) => {
            const tempId = 'temp_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
            const previewUrl = e.target.result;

            // 미리보기 UI 추가 (로딩 상태)
            this.addPreviewItem(tempId, previewUrl, true);

            // 서버 업로드
            this.uploadToServer(file, tempId, previewUrl);
        };
        reader.readAsDataURL(file);
    }

    addPreviewItem(id, previewUrl, isLoading = false) {
        const item = document.createElement('div');
        item.className = 'cv-uploader-item' + (isLoading ? ' loading' : '');
        item.dataset.id = id;
        item.innerHTML = `
            <img src="${previewUrl}" alt="preview">
            <div class="cv-uploader-item-overlay">
                ${isLoading ? '<div class="cv-uploader-spinner"></div>' : ''}
                <button type="button" class="cv-uploader-item-del" title="삭제">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="18" y1="6" x2="6" y2="18"></line>
                        <line x1="6" y1="6" x2="18" y2="18"></line>
                    </svg>
                </button>
            </div>
            ${this.sortable ? '<div class="cv-uploader-order"></div>' : ''}
        `;

        // 삭제 버튼 이벤트
        item.querySelector('.cv-uploader-item-del').addEventListener('click', (e) => {
            e.stopPropagation();
            this.removeItem(id);
        });

        this.preview.appendChild(item);
        this.updateOrderBadges();
    }

    uploadToServer(file, tempId, previewUrl) {
        const formData = new FormData();
        formData.append('files', file);  // 서버에서 'files'로 받음
        formData.append('fileTrgetSe', 'CASE');

        fetch('/fileUpload.do', {
            method: 'POST',
            headers: {
                'Accept': 'application/json'
            },
            body: formData
        })
        .then(res => {
            if (!res.ok) {
                // HTTP 에러 상태 처리
                return res.json().then(data => {
                    throw new Error(data.excpMsg || data.excpCdMsg || `서버 오류 (${res.status})`);
                }).catch(parseErr => {
                    // JSON 파싱 실패 시
                    if (parseErr.message && !parseErr.message.includes('서버 오류')) {
                        throw parseErr;
                    }
                    throw new Error(`서버 오류 (${res.status})`);
                });
            }
            return res.json();
        })
        .then(data => {
            // isError 필드 체크 (KFException 발생 시)
            if (data.isError === 'true' || data.isError === true) {
                this.removeItem(tempId);
                var errorMsg = data.excpMsg || data.excpCdMsg || '파일 업로드에 실패했습니다.';
                alert(errorMsg);
                return;
            }

            if (data.fileSeq) {
                // 업로드 성공
                const seq = Array.isArray(data.fileSeq) ? data.fileSeq[0] : data.fileSeq;

                // 임시 ID를 실제 seq로 교체
                const item = this.preview.querySelector(`[data-id="${tempId}"]`);
                if (item) {
                    item.dataset.id = seq;
                    item.dataset.seq = seq;
                    item.classList.remove('loading');
                    item.querySelector('.cv-uploader-spinner')?.remove();
                }

                // 업로드 파일 목록에 추가
                this.uploadedFiles.push({ seq: seq, preview: previewUrl });
                this.updateHiddenInput();

                if (this.onUploadComplete) {
                    this.onUploadComplete(seq, this.uploadedFiles);
                }
            } else {
                // 업로드 실패 - 서버 오류 메시지 표시
                this.removeItem(tempId);
                var errorMsg = data.excpMsg || data.excpCdMsg || '파일 업로드에 실패했습니다.';
                alert(errorMsg);
            }
        })
        .catch(err => {
            console.error('Upload error:', err);
            this.removeItem(tempId);
            alert(err.message || '파일 업로드 중 오류가 발생했습니다. 네트워크 연결을 확인해주세요.');
        });
    }

    removeItem(id) {
        const item = this.preview.querySelector(`[data-id="${id}"]`);
        if (item) {
            const seq = item.dataset.seq;

            // 서버에서 삭제 (이미 업로드된 경우)
            if (seq && !seq.startsWith('temp_')) {
                fetch('/fileDel.do', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                        'Accept': 'application/json'
                    },
                    body: `viewFileSeq=${encodeURIComponent(seq)}`
                }).catch(err => console.error('Delete error:', err));

                // 목록에서 제거
                this.uploadedFiles = this.uploadedFiles.filter(f => f.seq !== seq);
            }

            item.remove();
            this.updateHiddenInput();
            this.updateOrderBadges();

            if (this.onDelete) {
                this.onDelete(seq, this.uploadedFiles);
            }
        }
    }

    updateOrder() {
        const items = this.preview.querySelectorAll('.cv-uploader-item');
        const newOrder = [];

        items.forEach(item => {
            const seq = item.dataset.seq;
            if (seq) {
                const file = this.uploadedFiles.find(f => f.seq === seq);
                if (file) newOrder.push(file);
            }
        });

        this.uploadedFiles = newOrder;
        this.updateHiddenInput();
        this.updateOrderBadges();

        if (this.onOrderChange) {
            this.onOrderChange(this.uploadedFiles.map(f => f.seq));
        }
    }

    updateOrderBadges() {
        if (!this.sortable) return;

        const items = this.preview.querySelectorAll('.cv-uploader-item');
        items.forEach((item, idx) => {
            const badge = item.querySelector('.cv-uploader-order');
            if (badge) {
                badge.textContent = idx + 1;
            }
        });
    }

    updateHiddenInput() {
        if (this.hiddenInput) {
            const seqs = this.uploadedFiles.map(f => f.seq);
            this.hiddenInput.value = this.multiple ? seqs.join(',') : (seqs[0] || '');
        }
    }

    // 기존 이미지 로드 (수정 화면용)
    loadExisting(items) {
        // items: [{ seq: '암호화seq', src: '/fileView.do?viewFileSeq=...' }, ...]
        items.forEach(item => {
            const previewItem = document.createElement('div');
            previewItem.className = 'cv-uploader-item';
            previewItem.dataset.id = item.seq;
            previewItem.dataset.seq = item.seq;
            previewItem.innerHTML = `
                <img src="${item.src}" alt="preview">
                <div class="cv-uploader-item-overlay">
                    <button type="button" class="cv-uploader-item-del" title="삭제">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="18" y1="6" x2="6" y2="18"></line>
                            <line x1="6" y1="6" x2="18" y2="18"></line>
                        </svg>
                    </button>
                </div>
                ${this.sortable ? '<div class="cv-uploader-order"></div>' : ''}
            `;

            previewItem.querySelector('.cv-uploader-item-del').addEventListener('click', (e) => {
                e.stopPropagation();
                this.removeItem(item.seq);
            });

            this.preview.appendChild(previewItem);
            this.uploadedFiles.push({ seq: item.seq, preview: item.src });
        });

        this.updateHiddenInput();
        this.updateOrderBadges();
    }

    // 값 가져오기
    getValue() {
        return this.multiple
            ? this.uploadedFiles.map(f => f.seq)
            : (this.uploadedFiles[0]?.seq || '');
    }

    // 값 설정 (hidden input과 동기화)
    setValue(seqs) {
        // 외부에서 직접 seq를 설정할 때 사용
        if (!Array.isArray(seqs)) seqs = seqs ? [seqs] : [];
        this.uploadedFiles = seqs.map(seq => ({ seq, preview: '' }));
        this.updateHiddenInput();
    }

    // 초기화
    clear() {
        this.uploadedFiles = [];
        this.preview.innerHTML = '';
        this.updateHiddenInput();
    }
}

// 전역으로 노출
window.CVImageUploader = CVImageUploader;
