import { useEffect, useState } from 'react';
import { useAuthStore } from '../context/authStore';
import { useNguyenVongStore } from '../context/nguyenVongStore';
import { useNganhStore } from '../context/nganhStore';
import NguyenVongForm from '../components/NguyenVongForm';
import NguyenVongList from '../components/NguyenVongList';
import Header from '../components/Header';

export default function DashboardPage() {
    const { logout } = useAuthStore();
    const { nguyenVongs, fetchNguyenVongs, loading: nv_loading } = useNguyenVongStore();
    const { nganhList, fetchAllNganh, loading: nganh_loading } = useNganhStore();
    const [showForm, setShowForm] = useState(false);
    const [activeTab, setActiveTab] = useState('list');

    useEffect(() => {
        fetchNguyenVongs();
        fetchAllNganh();
    }, []);

    return (
        <div className="min-h-screen bg-gray-100">
            <Header onLogout={logout} />

            <div className="max-w-4xl mx-auto py-8 px-4">
                <div className="bg-white rounded-lg shadow-lg p-6 mb-8">
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">
                        Quản lý nguyện vọng xét tuyển
                    </h2>

                    <div className="flex gap-4 mb-6">
                        <button
                            onClick={() => setActiveTab('list')}
                            className={`px-6 py-2 rounded-lg font-medium transition ${activeTab === 'list'
                                    ? 'bg-blue-600 text-white'
                                    : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                                }`}
                        >
                            Danh sách nguyện vọng
                        </button>
                        <button
                            onClick={() => setActiveTab('add')}
                            className={`px-6 py-2 rounded-lg font-medium transition ${activeTab === 'add'
                                    ? 'bg-blue-600 text-white'
                                    : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                                }`}
                        >
                            Thêm nguyện vọng
                        </button>
                    </div>

                    {/* Tab: Danh sách */}
                    {activeTab === 'list' && (
                        <div>
                            {nv_loading ? (
                                <div className="text-center py-8">
                                    <p className="text-gray-600">Đang tải...</p>
                                </div>
                            ) : nguyenVongs && nguyenVongs.length > 0 ? (
                                <NguyenVongList nguyenVongs={nguyenVongs} nganhList={nganhList} />
                            ) : (
                                <div className="bg-blue-50 border border-blue-200 rounded-lg p-6 text-center">
                                    <p className="text-gray-700">Bạn chưa có nguyện vọng nào.</p>
                                    <button
                                        onClick={() => setActiveTab('add')}
                                        className="mt-4 bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition"
                                    >
                                        Tạo nguyện vọng ngay
                                    </button>
                                </div>
                            )}
                        </div>
                    )}

                    {/* Tab: Thêm nguyện vọng */}
                    {activeTab === 'add' && (
                        <NguyenVongForm
                            nganhList={nganhList}
                            onSuccess={() => {
                                setActiveTab('list');
                                fetchNguyenVongs();
                            }}
                            loading={nganh_loading}
                        />
                    )}
                </div>

                {/* Thống kê */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div className="bg-white rounded-lg shadow p-6 text-center">
                        <div className="text-3xl font-bold text-blue-600">
                            {nguyenVongs ? nguyenVongs.length : 0}
                        </div>
                        <p className="text-gray-600 mt-2">Tổng nguyện vọng</p>
                    </div>
                    <div className="bg-white rounded-lg shadow p-6 text-center">
                        <div className="text-3xl font-bold text-green-600">
                            {nganhList ? nganhList.length : 0}
                        </div>
                        <p className="text-gray-600 mt-2">Tổng ngành có sẵn</p>
                    </div>
                    <div className="bg-white rounded-lg shadow p-6 text-center">
                        <div className="text-3xl font-bold text-orange-600">
                            {nguyenVongs && nguyenVongs.filter(nv => nv.diemXettuyen).length}
                        </div>
                        <p className="text-gray-600 mt-2">Nguyện vọng có điểm</p>
                    </div>
                </div>
            </div>
        </div>
    );
}
