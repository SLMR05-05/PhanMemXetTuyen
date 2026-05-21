import { Search } from 'lucide-react';

export default function SearchPanel({ title, description }) {
    return (
        <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
            <div className="flex items-start gap-4">
                <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-blue-50 text-blue-600 ring-1 ring-blue-100">
                    <Search className="h-6 w-6" />
                </div>
                <div className="min-w-0">
                    <h3 className="text-xl font-bold text-slate-900">{title}</h3>
                    <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-600">{description}</p>
                </div>
            </div>

            <div className="mt-6 rounded-2xl bg-slate-50 p-5 text-sm text-slate-600 ring-1 ring-slate-200">
                Khu vực này đã được tách thành panel riêng để bạn có thể mở rộng dữ liệu tra cứu sau này mà không cần sửa chung trong một file lớn.
            </div>
        </div>
    );
}
