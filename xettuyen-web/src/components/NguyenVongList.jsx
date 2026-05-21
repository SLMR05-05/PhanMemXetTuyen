import { useState } from 'react';
import { useNguyenVongStore } from '../context/nguyenVongStore';

export default function NguyenVongList({ nguyenVongs, nganhList }) {
    const [deletingId, setDeletingId] = useState(null);
    const [swappingId, setSwappingId] = useState(null);
    const { removeNguyenVong, swapNguyenVong } = useNguyenVongStore();

    // Đảm bảo danh sách luôn được sắp xếp theo thứ tự nvTt trước khi render
    const sortedList = [...(nguyenVongs || [])].sort((a, b) => a.nvTt - b.nvTt);

    // const getNganhName = (maNganh) => {
    //     const nganh = nganhList?.find((n) => n.maNganh === maNganh);
    //     return nganh ? nganh.tenNganh : maNganh;
    // };
    const getNganhName = (maNganh, tenNganh) => {

        if (tenNganh) {

            return tenNganh;

        }

        const nganh = nganhList?.find((n) => n.maNganh === maNganh);

        return nganh ? nganh.tenNganh : maNganh;

    };
    const handleDelete = async (idNv) => {
        if (window.confirm('Bạn có chắc chắn muốn xóa nguyện vọng này?')) {
            setDeletingId(idNv);
            try {
                await removeNguyenVong(idNv);
            } finally {
                setDeletingId(null);
            }
        }
    };

    const handleSwap = async (id1, id2) => {
        setSwappingId(id1); // Hiển thị trạng thái loading cho dòng đang thao tác
        try {
            await swapNguyenVong(id1, id2);
        } finally {
            setSwappingId(null);
        }
    };

    if (!sortedList || sortedList.length === 0) {
        return <div className="text-gray-500 text-center py-4">Chưa có nguyện vọng nào được đăng ký.</div>;
    }

    return (
        <div className="overflow-x-auto">
            <table className="w-full border-collapse">
                <thead>
                    <tr className="bg-gray-100">
                        <th className="border border-gray-300 px-4 py-2 text-center w-32">Thứ tự</th>
                        <th className="border border-gray-300 px-4 py-2 text-left">Ngành</th>
                        <th className="border border-gray-300 px-4 py-2 text-center">Trạng thái</th>
                        <th className="border border-gray-300 px-4 py-2 text-center w-32">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    {sortedList.map((nv, index) => (
                        <tr key={nv.idNv} className="hover:bg-gray-50">
                            <td className="border border-gray-300 px-4 py-2 text-center">
                                <div className="flex items-center justify-center space-x-2">
                                    <span className="font-bold w-6">{nv.nvTt}</span>
                                    {/* Chỉ hiển thị nút điều hướng nếu có từ 2 NV trở lên */}
                                    {sortedList.length > 1 && (
                                        <div className="flex flex-col">
                                            <button
                                                onClick={() => handleSwap(nv.idNv, sortedList[index - 1].idNv)}
                                                disabled={index === 0 || swappingId !== null} // Vô hiệu hóa nút Lên ở dòng đầu tiên
                                                className="text-gray-500 hover:text-blue-600 disabled:text-gray-300 transition-colors px-1"
                                                title="Đẩy lên trên"
                                            >
                                                ▲
                                            </button>
                                            <button
                                                onClick={() => handleSwap(nv.idNv, sortedList[index + 1].idNv)}
                                                disabled={index === sortedList.length - 1 || swappingId !== null} // Vô hiệu hóa nút Xuống ở dòng cuối cùng
                                                className="text-gray-500 hover:text-blue-600 disabled:text-gray-300 transition-colors px-1"
                                                title="Đẩy xuống dưới"
                                            >
                                                ▼
                                            </button>
                                        </div>
                                    )}
                                </div>
                            </td>
                            <td className="border border-gray-300 px-4 py-2">
                                {getNganhName(nv.maNganh, nv.tenNganh)} <span className="text-sm text-gray-500">({nv.nvMaNganh})</span>
                            </td>
                            <td className="border border-gray-300 px-4 py-2 text-center">
                                <span
                                    className={`px-3 py-1 rounded-full text-sm font-medium ${nv.nvKetqua === 'Đậu'
                                            ? 'bg-green-100 text-green-800'
                                            : nv.nvKetqua === 'Rớt'
                                                ? 'bg-red-100 text-red-800'
                                                : 'bg-gray-100 text-gray-800'
                                        }`}
                                >
                                    {nv.nvKetqua || 'Chưa có'}
                                </span>
                            </td>
                            <td className="border border-gray-300 px-4 py-2 text-center">
                                <button
                                    onClick={() => handleDelete(nv.idNv)}
                                    disabled={deletingId === nv.idNv || swappingId !== null}
                                    className="bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded transition disabled:bg-gray-400 text-sm"
                                >
                                    {deletingId === nv.idNv ? 'Đang xóa...' : 'Xóa'}
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}