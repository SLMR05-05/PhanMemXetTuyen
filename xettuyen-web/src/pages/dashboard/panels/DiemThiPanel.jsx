import React, { useEffect } from 'react';
import { useDiemThiStore } from '../../../context/diemThiStore';

export default function DiemThiPanel({ title, description }) {
    const { diemThiList, fetchMyDiemThi, loading, error } = useDiemThiStore();

    useEffect(() => {
        fetchMyDiemThi();
    }, []);

    return (
        <div className="bg-white rounded-lg shadow p-6 animate-fade-in">
            <h2 className="text-xl font-bold text-gray-800 mb-2">{title}</h2>
            <p className="text-gray-500 mb-6">{description}</p>

            {loading ? (
                <div className="text-center py-8 text-gray-500">Đang tải điểm thi...</div>
            ) : error ? (
                <div className="text-red-500 bg-red-50 p-4 rounded-lg">{error}</div>
            ) : diemThiList && diemThiList.length > 0 ? (
                <div className="overflow-x-auto rounded-lg border border-gray-200">
                    <table className="w-full text-left border-collapse">
                        <thead className="bg-gray-50 text-gray-700">
                            <tr>
                                <th className="px-4 py-3 border-b">Kỳ thi</th>
                                <th className="px-4 py-3 border-b">Môn thi / Bài thi</th>
                                <th className="px-4 py-3 border-b text-center">Điểm số</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-200">
                            {diemThiList.map((diem, index) => (
                                <tr key={index} className="hover:bg-gray-50">
                                    <td className="px-4 py-3 text-gray-900 font-medium">{diem.kyThi}</td>
                                    <td className="px-4 py-3 text-gray-700">{diem.monThi}</td>
                                    <td className="px-4 py-3 text-center font-bold text-blue-600">
                                        {diem.diem}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            ) : (
                <div className="text-center py-8 text-gray-500 bg-gray-50 rounded-lg">
                    Hệ thống chưa ghi nhận điểm thi của bạn.
                </div>
            )}
        </div>
    );
}