import { useState } from 'react';
import { Search } from 'lucide-react';
import { nguyenVongService } from '../../../services/nguyenVongService';

export default function NguyenVongLookupPanel() {
    const [cccd, setCccd] = useState('');
    const [results, setResults] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [hasSearched, setHasSearched] = useState(false);

    const formatScore = (value) => (value === null || value === undefined ? '-' : value.toFixed(2));

    const handleSubmit = async (event) => {
        event.preventDefault();
        const normalized = cccd.trim();
        setError('');
        setHasSearched(true);

        if (!normalized) {
            setResults([]);
            setError('Vui lòng nhập CCCD để tra cứu.');
            return;
        }

        setLoading(true);
        try {
            const data = await nguyenVongService.lookupNguyenVongByCccd(normalized);
            setResults(data || []);
        } catch (err) {
            const message = typeof err === 'string' ? err : err?.message;
            setResults([]);
            setError(message || 'Không thể tra cứu nguyện vọng.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="space-y-6">
            <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
                <div className="flex items-start gap-4">
                    <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-blue-50 text-blue-600 ring-1 ring-blue-100">
                        <Search className="h-6 w-6" />
                    </div>
                    <div className="min-w-0">
                        <h3 className="text-xl font-bold text-slate-900">Tra cứu nguyện vọng xét tuyển</h3>
                        <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-600">
                            Nhập CCCD để xem chi tiết điểm xét tuyển theo phương thức và tổ hợp môn của từng nguyện vọng.
                        </p>
                    </div>
                </div>

                <form onSubmit={handleSubmit} className="mt-6 flex flex-col gap-3 sm:flex-row sm:items-end">
                    <label className="flex w-full flex-col gap-2 text-sm font-medium text-slate-700">
                        CCCD thí sinh
                        <input
                            type="text"
                            value={cccd}
                            onChange={(event) => setCccd(event.target.value)}
                            placeholder="Nhập CCCD (12 số)"
                            className="h-11 w-full rounded-xl border border-slate-200 px-4 text-sm focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-200"
                        />
                    </label>
                    <button
                        type="submit"
                        disabled={loading}
                        className="h-11 rounded-xl bg-blue-600 px-6 text-sm font-semibold text-white transition hover:bg-blue-700 disabled:bg-slate-300"
                    >
                        {loading ? 'Đang tra cứu...' : 'Tra cứu'}
                    </button>
                </form>

                {error && (
                    <div className="mt-4 rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                        {error}
                    </div>
                )}
            </div>

            <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
                <div className="flex flex-wrap items-center justify-between gap-3">
                    <h4 className="text-lg font-semibold text-slate-900">Danh sách nguyện vọng</h4>
                    <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-600">
                        {results.length} nguyện vọng
                    </span>
                </div>

                {!loading && hasSearched && results.length === 0 && !error && (
                    <div className="mt-4 rounded-2xl border border-blue-100 bg-blue-50 p-6 text-center text-sm text-slate-600">
                        Không tìm thấy nguyện vọng phù hợp với CCCD đã nhập.
                    </div>
                )}

                {results.length > 0 && (
                    <div className="mt-4 overflow-x-auto">
                        <table className="w-full border-collapse text-sm">
                            <thead>
                                <tr className="bg-slate-100 text-slate-700">
                                    <th className="border border-slate-200 px-3 py-2 text-center">Thứ tự</th>
                                    <th className="border border-slate-200 px-3 py-2 text-left">Ngành</th>
                                    <th className="border border-slate-200 px-3 py-2 text-center">Phương thức</th>
                                    <th className="border border-slate-200 px-3 py-2 text-center">Tổ hợp</th>
                                    <th className="border border-slate-200 px-3 py-2 text-center">Điểm THXT</th>
                                    <th className="border border-slate-200 px-3 py-2 text-center">Điểm UTQD</th>
                                    <th className="border border-slate-200 px-3 py-2 text-center">Điểm cộng</th>
                                    <th className="border border-slate-200 px-3 py-2 text-center">Điểm xét tuyển</th>
                                    <th className="border border-slate-200 px-3 py-2 text-center">Kết quả</th>
                                </tr>
                            </thead>
                            <tbody>
                                {results.map((nv) => (
                                    <tr key={nv.idNv || `${nv.nvMaNganh}-${nv.nvTt}`} className="hover:bg-slate-50">
                                        <td className="border border-slate-200 px-3 py-2 text-center font-semibold">
                                            {nv.nvTt}
                                        </td>
                                        <td className="border border-slate-200 px-3 py-2">
                                            <div>
                                                <p className="font-medium text-slate-900">
                                                    {nv.tenNganh || nv.nvMaNganh}
                                                </p>
                                                <p className="text-xs text-slate-500">{nv.nvMaNganh}</p>
                                            </div>
                                        </td>
                                        <td className="border border-slate-200 px-3 py-2 text-center">
                                            {nv.ttPhuongThuc || '-'}
                                        </td>
                                        <td className="border border-slate-200 px-3 py-2 text-center">
                                            {nv.ttThm || '-'}
                                        </td>
                                        <td className="border border-slate-200 px-3 py-2 text-center">
                                            {formatScore(nv.diemThxt)}
                                        </td>
                                        <td className="border border-slate-200 px-3 py-2 text-center">
                                            {formatScore(nv.diemUtqd)}
                                        </td>
                                        <td className="border border-slate-200 px-3 py-2 text-center">
                                            {formatScore(nv.diemCong)}
                                        </td>
                                        <td className="border border-slate-200 px-3 py-2 text-center">
                                            {formatScore(nv.diemXettuyen)}
                                        </td>
                                        <td className="border border-slate-200 px-3 py-2 text-center">
                                            {nv.nvKetqua || 'Chưa có'}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    );
}
