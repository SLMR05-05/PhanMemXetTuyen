import { useState } from 'react';
import { useNguyenVongStore } from '../context/nguyenVongStore';

export default function NguyenVongForm({ nganhList, onSuccess, loading }) {
    const [maNganh, setMaNganh] = useState('');
    const [thuTuNguyenVong, setThuTuNguyenVong] = useState(1);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [successMessage, setSuccessMessage] = useState('');
    const { addNguyenVong, error, clearError } = useNguyenVongStore();

    const handleSubmit = async (e) => {
        e.preventDefault();
        clearError();
        setSuccessMessage('');
        setIsSubmitting(true);

        try {
            await addNguyenVong(maNganh, thuTuNguyenVong);
            setSuccessMessage('Thêm nguyện vọng thành công!');
            setMaNganh('');
            setThuTuNguyenVong(1);
            setTimeout(() => {
                setSuccessMessage('');
                onSuccess();
            }, 1500);
        } catch (err) {
            console.error('Error:', err);
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-4">
            {error && (
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded-lg">
                    {error}
                </div>
            )}

            {successMessage && (
                <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded-lg">
                    {successMessage}
                </div>
            )}

            <div>
                <label className="block text-gray-700 font-medium mb-2">
                    Chọn ngành *
                </label>
                <select
                    value={maNganh}
                    onChange={(e) => setMaNganh(e.target.value)}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                    disabled={loading}
                >
                    <option value="">
                        {loading ? 'Đang tải...' : 'Chọn ngành'}
                    </option>
                    {nganhList &&
                        nganhList.map((nganh) => (
                            <option key={nganh.maNganh} value={nganh.maNganh}>
                                {nganh.tenNganh} ({nganh.maNganh})
                            </option>
                        ))}
                </select>
            </div>

            <div>
                <label className="block text-gray-700 font-medium mb-2">
                    Thứ tự nguyện vọng *
                </label>
                <select
                    value={thuTuNguyenVong}
                    onChange={(e) => setThuTuNguyenVong(parseInt(e.target.value))}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    required
                >
                    {[1, 2, 3, 4, 5].map((num) => (
                        <option key={num} value={num}>
                            Thứ tự {num}
                        </option>
                    ))}
                </select>
            </div>

            <button
                type="submit"
                disabled={isSubmitting || loading}
                className="w-full bg-green-600 text-white font-semibold py-2 rounded-lg hover:bg-green-700 transition disabled:bg-gray-400"
            >
                {isSubmitting ? 'Đang thêm...' : 'Thêm nguyện vọng'}
            </button>
        </form>
    );
}
