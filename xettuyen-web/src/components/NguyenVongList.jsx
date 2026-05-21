import { useState } from 'react';
import { useNguyenVongStore } from '../context/nguyenVongStore';

export default function NguyenVongList({ nguyenVongs, nganhList }) {
    const [deletingId, setDeletingId] = useState(null);
    const { removeNguyenVong } = useNguyenVongStore();

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

    return (
        <div className="overflow-x-auto">
            <table className="w-full border-collapse">
                <thead>
                    <tr className="bg-gray-100">
                        <th className="border border-gray-300 px-4 py-2 text-left">Thứ tự</th>
                        <th className="border border-gray-300 px-4 py-2 text-left">Ngành</th>
                        <th className="border border-gray-300 px-4 py-2 text-center">Điểm xét tuyển</th>
                        <th className="border border-gray-300 px-4 py-2 text-center">Điểm ưu tiên</th>
                        <th className="border border-gray-300 px-4 py-2 text-center">Kết quả</th>
                        <th className="border border-gray-300 px-4 py-2 text-center">Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    {nguyenVongs.map((nv) => (
                        <tr key={nv.idNv} className="hover:bg-gray-50">
                            <td className="border border-gray-300 px-4 py-2 font-semibold text-center">
                                {nv.nvTt}
                            </td>
                            <td className="border border-gray-300 px-4 py-2">
                                <div>
                                    <p className="font-medium">{getNganhName(nv.nvMaNganh, nv.tenNganh)}</p>
                                    <p className="text-sm text-gray-500">{nv.nvMaNganh}</p>
                                </div>
                            </td>
                            <td className="border border-gray-300 px-4 py-2 text-center">
                                {nv.diemThxt ? nv.diemThxt.toFixed(2) : '-'}
                            </td>
                            <td className="border border-gray-300 px-4 py-2 text-center">
                                {nv.diemUtqd ? nv.diemUtqd.toFixed(2) : '-'}
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
                                    disabled={deletingId === nv.idNv}
                                    className="bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded transition disabled:bg-gray-400"
                                >
                                    {deletingId === nv.idNv ? 'Xóa...' : 'Xóa'}
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}
