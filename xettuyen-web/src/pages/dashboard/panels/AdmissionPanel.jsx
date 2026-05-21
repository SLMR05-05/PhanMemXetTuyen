import { useState } from 'react';
import NguyenVongForm from '../../../components/NguyenVongForm';
import NguyenVongList from '../../../components/NguyenVongList';

export default function AdmissionPanel({ nguyenVongs, nganhList, listLoading, formLoading, onRefresh }) {
    const [activeTab, setActiveTab] = useState('list');

    return (
        <div className="space-y-6">
            <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
                <h3 className="text-xl font-bold text-slate-900">Quản lý nguyện vọng xét tuyển</h3>

                <div className="mt-5 flex flex-wrap gap-3">
                    <button
                        onClick={() => setActiveTab('list')}
                        className={`rounded-xl px-5 py-2.5 text-sm font-semibold transition ${activeTab === 'list'
                            ? 'bg-blue-600 text-white shadow-sm'
                            : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
                            }`}
                    >
                        Danh sách nguyện vọng
                    </button>
                    <button
                        onClick={() => setActiveTab('add')}
                        className={`rounded-xl px-5 py-2.5 text-sm font-semibold transition ${activeTab === 'add'
                            ? 'bg-blue-600 text-white shadow-sm'
                            : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
                            }`}
                    >
                        Thêm nguyện vọng
                    </button>
                </div>

                <div className="mt-6">
                    {activeTab === 'list' ? (
                        listLoading ? (
                            <div className="rounded-2xl bg-slate-50 p-8 text-center text-slate-500">
                                Đang tải...
                            </div>
                        ) : nguyenVongs && nguyenVongs.length > 0 ? (
                            <NguyenVongList nguyenVongs={nguyenVongs} nganhList={nganhList} />
                        ) : (
                            <div className="rounded-2xl border border-blue-100 bg-blue-50 p-6 text-center">
                                <p className="text-slate-700">Bạn chưa có nguyện vọng nào.</p>
                                <button
                                    onClick={() => setActiveTab('add')}
                                    className="mt-4 rounded-xl bg-blue-600 px-6 py-2.5 text-sm font-semibold text-white transition hover:bg-blue-700"
                                >
                                    Tạo nguyện vọng ngay
                                </button>
                            </div>
                        )
                    ) : (
                        <NguyenVongForm
                            nganhList={nganhList}
                            onSuccess={() => {
                                setActiveTab('list');
                                onRefresh();
                            }}
                            loading={formLoading}
                        />
                    )}
                </div>
            </div>
        </div>
    );
}
