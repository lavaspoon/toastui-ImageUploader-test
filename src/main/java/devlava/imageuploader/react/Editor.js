// src/components/Editor.js
import React, { useRef } from 'react';
import { Editor as ToastEditor } from '@toast-ui/react-editor';
import '@toast-ui/editor/dist/toastui-editor.css';

const Editor = () => {
    const editorRef = useRef();

    // 이미지 업로드 hook
    const handleImageUpload = async (blob, callback) => {
        try {
            const formData = new FormData();
            formData.append('image', blob);

            const response = await fetch('http://localhost:8080/api/images', {
                method: 'POST',
                body: formData,
            });

            const result = await response.json();

            if (result.url) {
                callback(result.url, '업로드 이미지');
            } else {
                alert('이미지 업로드 실패');
            }
        } catch (err) {
            console.error('이미지 업로드 실패:', err);
            alert('서버 오류 발생');
        }
    };

    return (
        <ToastEditor
            ref={editorRef}
            height="600px"
            initialEditType="markdown"
            previewStyle="vertical"
            hooks={{
                addImageBlobHook: handleImageUpload,
            }}
        />
    );
};

export default Editor;
