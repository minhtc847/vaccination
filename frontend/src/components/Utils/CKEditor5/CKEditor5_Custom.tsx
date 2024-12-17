import React, {useEffect, useRef, useState} from 'react';
import {CKEditor} from '@ckeditor/ckeditor5-react';
import {Container, Row} from "react-bootstrap";
import 'ckeditor5/ckeditor5.css';
import './CKEditor5_Custom.css';
import type {EditorConfig} from '@ckeditor/ckeditor5-core';

import {
    AccessibilityHelp,
    Alignment,
    Autoformat,
    AutoImage,
    AutoLink,
    Autosave,
    BalloonToolbar,
    Base64UploadAdapter,
    Bold,
    DecoupledEditor,
    Essentials,
    FindAndReplace,
    FontBackgroundColor,
    FontColor,
    FontFamily,
    FontSize,
    GeneralHtmlSupport,
    Heading,
    Highlight,
    HtmlEmbed,
    ImageBlock,
    ImageCaption,
    ImageInline,
    ImageInsert,
    ImageInsertViaUrl,
    ImageResize,
    ImageStyle,
    ImageTextAlternative,
    ImageToolbar,
    ImageUpload,
    Indent,
    IndentBlock,
    Italic,
    Link,
    LinkImage,
    List,
    ListProperties,
    MediaEmbed,
    Paragraph,
    PasteFromOffice,
    SelectAll,
    SourceEditing,
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
    Underline,
    Undo
} from 'ckeditor5';

interface CKEditor5_CustomProps {
    onChange: (data: string) => void;
    value?: string;
}

const CKEditor5_Custom: React.FC<CKEditor5_CustomProps> = ({onChange, value}) => {
    const editorContainerRef = useRef<HTMLDivElement>(null);
    const editorMenuBarRef = useRef<HTMLDivElement>(null);
    const editorToolbarRef = useRef<HTMLDivElement>(null);
    const editorRef = useRef<HTMLDivElement>(null);
    const [isLayoutReady, setIsLayoutReady] = useState(false);


    useEffect(() => {
        setIsLayoutReady(true);
        return () => setIsLayoutReady(false);
    }, []);

    const editorConfig: EditorConfig = {
        toolbar: {
            items: [
                'undo', 'redo', '|', 'heading', '|', 'fontSize', 'fontFamily',
                'fontColor', 'fontBackgroundColor', '|', 'bold', 'italic',
                'underline', 'highlight', '|', 'alignment', 'indent', 'outdent',
                'bulletedList', 'numberedList', '|', 'insertImage', 'insertTable',
                'specialCharacters', 'findAndReplace'
            ],
            shouldNotGroupWhenFull: false
        },
        plugins: [
            AccessibilityHelp,
            Alignment,
            Autoformat,
            AutoImage,
            AutoLink,
            Autosave,
            BalloonToolbar,
            Base64UploadAdapter,
            Bold,
            Essentials,
            FindAndReplace,
            FontBackgroundColor,
            FontColor,
            FontFamily,
            FontSize,
            GeneralHtmlSupport,
            Heading,
            Highlight,
            HtmlEmbed,
            ImageBlock,
            ImageCaption,
            ImageInline,
            ImageInsert,
            ImageInsertViaUrl,
            ImageResize,
            ImageStyle,
            ImageTextAlternative,
            ImageToolbar,
            ImageUpload,
            Indent,
            IndentBlock,
            Italic,
            Link,
            LinkImage,
            List,
            ListProperties,
            MediaEmbed,
            Paragraph,
            PasteFromOffice,
            SelectAll,

            SourceEditing,
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
            Underline,
            Undo
        ],
        balloonToolbar: ['bold', 'italic', '|', 'link', 'insertImage', '|', 'bulletedList', 'numberedList'],
        fontFamily: {
            supportAllValues: true
        },
        fontSize: {
            options: [10, 12, 14, 'default', 18, 20, 22],
            supportAllValues: true
        },
        heading: {
            options: [
                {model: 'paragraph', title: 'Paragraph', class: 'ck-heading_paragraph'},
                {model: 'heading1', view: 'h1', title: 'Heading 1', class: 'ck-heading_heading1'},
                {model: 'heading2', view: 'h2', title: 'Heading 2', class: 'ck-heading_heading2'},
                {model: 'heading3', view: 'h3', title: 'Heading 3', class: 'ck-heading_heading3'},
                {model: 'heading4', view: 'h4', title: 'Heading 4', class: 'ck-heading_heading4'},
                {model: 'heading5', view: 'h5', title: 'Heading 5', class: 'ck-heading_heading5'},
                {model: 'heading6', view: 'h6', title: 'Heading 6', class: 'ck-heading_heading6'}
            ]
        },
        image: {
            toolbar: [
                'toggleImageCaption',
                'imageTextAlternative',
                '|',
                'imageStyle:alignBlockLeft',
                'imageStyle:block',
                'imageStyle:alignBlockRight',
                '|',
                'resizeImage'
            ],
            styles: {
                options: ['alignBlockLeft', 'block', 'alignBlockRight']
            }
        },

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
        menuBar: {
            isVisible: true
        },
        placeholder: 'Type or paste your content here!',
        table: {
            contentToolbar: ['tableColumn', 'tableRow', 'mergeTableCells', 'tableProperties', 'tableCellProperties']
        },
        htmlSupport: {
            allow: [
                {
                    name: /.*/,
                    attributes: true,
                    classes: true,
                    styles: true
                }
            ]
        },
    };

    return (
        <Container className="mb-5">
            <Row>
                <div className="main-container">
                    <div className="editor-container editor-container_document-editor" ref={editorContainerRef}>
                        <div className="editor-container__menu-bar" ref={editorMenuBarRef}></div>
                        <div className="editor-container__toolbar" ref={editorToolbarRef}></div>
                        <div className="editor-container__editor-wrapper">
                            <div className="editor-container__editor">
                                <div ref={editorRef}>
                                    {isLayoutReady && (
                                        <CKEditor
                                            onReady={(editor) => {
                                                if (editorToolbarRef.current && editor.ui.view.toolbar.element) {
                                                    editorToolbarRef.current.appendChild(editor.ui.view.toolbar.element);
                                                }
                                                if (editorMenuBarRef.current && editor.ui.view.menuBarView?.element) {
                                                    editorMenuBarRef.current.appendChild(editor.ui.view.menuBarView.element);
                                                }
                                            }}
                                            onAfterDestroy={() => {
                                                if (editorToolbarRef.current) {
                                                    Array.from(editorToolbarRef.current.children).forEach(child => child.remove());
                                                }
                                                if (editorMenuBarRef.current) {
                                                    Array.from(editorMenuBarRef.current.children).forEach(child => child.remove());
                                                }
                                            }}
                                            editor={DecoupledEditor}
                                            config={editorConfig}
                                            data={value}
                                            onChange={(event, editor) => {
                                                const data = editor.getData();
                                                onChange(data);
                                            }}
                                        />
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </Row>
        </Container>
    );
};

export default CKEditor5_Custom;
