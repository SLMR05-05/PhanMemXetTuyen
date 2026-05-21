import React from 'react';

export default function TuyenThangPanel({ title, description }) {
    return (
        <div className="bg-white rounded-lg shadow p-6 animate-fade-in">
            <h2 className="text-xl font-bold text-gray-800 mb-2">{title}</h2>
            <p className="text-gray-500 mb-6">{description}</p>

            <div className="bg-yellow-50 border border-yellow-200 rounded-xl p-8 text-center">
                <div className="w-16 h-16 bg-yellow-100 rounded-full flex items-center justify-center mx-auto mb-4">
                    <svg className="w-8 h-8 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path>
                    </svg>
                </div>
                <h3 className="text-xl font-bold text-gray-800 mb-2">Chưa có thông tin</h3>
                <p className="text-gray-600">
                    Hệ thống chưa ghi nhận hồ sơ tuyển thẳng / ưu tiên xét tuyển nào liên kết với tài khoản của bạn.
                </p>
            </div>
        </div>
    );
}