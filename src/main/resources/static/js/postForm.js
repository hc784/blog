const {
	ClassicEditor,
	Autoformat,
	AutoImage,
	Autosave,
	BlockQuote,
	Bold,
	CloudServices,
	Essentials,
	FindAndReplace,
	Heading,
	ImageBlock,
	ImageCaption,
	ImageEditing,
	ImageInline,
	ImageInsert,
	ImageInsertViaUrl,
	ImageResize,
	ImageStyle,
	ImageTextAlternative,
	ImageToolbar,
	ImageUpload,
	ImageUtils,
	Indent,
	IndentBlock,
	Italic,
	Link,
	LinkImage,
	List,
	ListProperties,
	MediaEmbed,
	Mention,
	Paragraph,
	PasteFromOffice,
	SimpleUploadAdapter,
	SpecialCharacters,
	SpecialCharactersArrows,
	SpecialCharactersCurrency,
	SpecialCharactersEssentials,
	SpecialCharactersLatin,
	SpecialCharactersMathematical,
	SpecialCharactersText,
	Table,
	TableCaption,
	TableCellProperties,
	TableColumnResize,
	TableProperties,
	TableToolbar,
	TextTransformation,
	TodoList,
	Underline
} = window.CKEDITOR;

const LICENSE_KEY =
	'eyJhbGciOiJFUzI1NiJ9.eyJleHAiOjE3NDEzMDU1OTksImp0aSI6IjI0ZWQxOGY1LTQwYjctNGRiMi04Zjc5LTUwMTA1N2ZhM2IwOSIsInVzYWdlRW5kcG9pbnQiOiJodHRwczovL3Byb3h5LWV2ZW50LmNrZWRpdG9yLmNvbSIsImRpc3RyaWJ1dGlvbkNoYW5uZWwiOlsiY2xvdWQiLCJkcnVwYWwiLCJzaCJdLCJ3aGl0ZUxhYmVsIjp0cnVlLCJsaWNlbnNlVHlwZSI6InRyaWFsIiwiZmVhdHVyZXMiOlsiKiJdLCJ2YyI6IjJjYjc5MjQ4In0.5yDBITHjntBnyJxSFQfaZcVAyxQD79Xui1hSkWQWvVoBz5_CesZMyojGjj-ZkPNIVVmN6zYIrPyF1fbFKwtPcA';

const editorConfig = {
	toolbar: {
		items: [
			'findAndReplace',
			'|',
			'heading',
			'|',
			'bold',
			'italic',
			'underline',
			'|',
			'specialCharacters',
			'link',
			'insertImage',
			'mediaEmbed',
			'insertTable',
			'blockQuote',
			'|',
			'bulletedList',
			'numberedList',
			'todoList',
			'outdent',
			'indent'
		],
		shouldNotGroupWhenFull: false
	},
	plugins: [
		Autoformat,
		AutoImage,
		Autosave,
		BlockQuote,
		Bold,
		CloudServices,
		Essentials,
		FindAndReplace,
		Heading,
		ImageBlock,
		ImageCaption,
		ImageEditing,
		ImageInline,
		ImageInsert,
		ImageInsertViaUrl,
		ImageResize,
		ImageStyle,
		ImageTextAlternative,
		ImageToolbar,
		ImageUpload,
		ImageUtils,
		Indent,
		IndentBlock,
		Italic,
		Link,
		LinkImage,
		List,
		ListProperties,
		MediaEmbed,
		Mention,
		Paragraph,
		PasteFromOffice,
		SimpleUploadAdapter,
		SpecialCharacters,
		SpecialCharactersArrows,
		SpecialCharactersCurrency,
		SpecialCharactersEssentials,
		SpecialCharactersLatin,
		SpecialCharactersMathematical,
		SpecialCharactersText,
		Table,
		TableCaption,
		TableCellProperties,
		TableColumnResize,
		TableProperties,
		TableToolbar,
		TextTransformation,
		TodoList,
		Underline
	],
	heading: {
		options: [
			{
				model: 'paragraph',
				title: 'Paragraph',
				class: 'ck-heading_paragraph'
			},
			{
				model: 'heading1',
				view: 'h1',
				title: 'Heading 1',
				class: 'ck-heading_heading1'
			},
			{
				model: 'heading2',
				view: 'h2',
				title: 'Heading 2',
				class: 'ck-heading_heading2'
			},
			{
				model: 'heading3',
				view: 'h3',
				title: 'Heading 3',
				class: 'ck-heading_heading3'
			},
			{
				model: 'heading4',
				view: 'h4',
				title: 'Heading 4',
				class: 'ck-heading_heading4'
			},
			{
				model: 'heading5',
				view: 'h5',
				title: 'Heading 5',
				class: 'ck-heading_heading5'
			},
			{
				model: 'heading6',
				view: 'h6',
				title: 'Heading 6',
				class: 'ck-heading_heading6'
			}
		]
	},
	image: {
		toolbar: [
			'toggleImageCaption',
			'imageTextAlternative',
			'|',
			'imageStyle:inline',
			'imageStyle:wrapText',
			'imageStyle:breakText',
			'|',
			'resizeImage'
		]
	},
	initialData:
		'',
	language: 'ko',
	licenseKey: LICENSE_KEY,
	link: {
		addTargetToExternalLinks: true,
		defaultProtocol: 'https://',
		decorators: {
			toggleDownloadable: {
				mode: 'manual',
				label: 'Downloadable',
				attributes: {
					download: 'file'
				}
			}
		}
	},
	list: {
		properties: {
			styles: true,
			startIndex: true,
			reversed: true
		}
	},
	mention: {
		feeds: [
			{
				marker: '@',
				feed: [
					/* See: https://ckeditor.com/docs/ckeditor5/latest/features/mentions.html */
				]
			}
		]
	},
	placeholder: 'Type or paste your content here!',
	table: {
		contentToolbar: ['tableColumn', 'tableRow', 'mergeTableCells', 'tableProperties', 'tableCellProperties']
	}
};

/*ClassicEditor.create(document.querySelector('#editor'), editorConfig);*/

document.addEventListener("DOMContentLoaded", function () {
    const blogId = document.querySelector("#blogId").value;  // 블로그 ID 가져오기
    const postId = document.querySelector("#postId").value;  // 게시글 ID 가져오기
	let editorInstance;
	const contentInput = document.querySelector("#content"); // Hidden input
    // Simple Upload Adapter 설정 추가 (동적 URL 생성)
    editorConfig.simpleUpload = {
        uploadUrl: `/blogs/${blogId}/posts/${postId}/upload-image`,
		method : "POST",
        // 필요 시 헤더 추가 가능 (예: CSRF 토큰)
/*        headers: {
			withCredentials: false,
          //"X-CSRF-TOKEN": document.querySelector("meta[name='_csrf']").content
		  "Content-Type": "multipart/form-data; boundary=----WebKitFormBoundarylTMBUUyXqgLqmAdj"
        }*/
    };
	

    ClassicEditor.create(document.querySelector('#editor'), editorConfig)
        .then(editor => {
			editorInstance = editor;
            // 기존 글 수정 시 CKEditor에 기존 content 값 넣기
            if (contentInput.value) {
                editor.setData(contentInput.value);
            }
			// 폼 제출 시 CKEditor 내용 저장
	         document.querySelector("form").addEventListener("submit", function () {
	             contentInput.value = editorInstance.getData();
	         });
		  

        })
        .catch(error => {
            console.error("Error initializing editor:", error);
        });
});