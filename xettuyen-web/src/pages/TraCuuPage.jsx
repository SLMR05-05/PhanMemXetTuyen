import NguyenVongLookupPanel from './dashboard/panels/NguyenVongLookupPanel';
import sguLogo from '../img/SGU-Logo.jpg';

export default function TraCuuPage() {
    return (
        <div className="min-h-screen bg-slate-50">
            <header className="border-b border-slate-200 bg-white shadow-sm">
                <div className="mx-auto flex w-full max-w-6xl items-center gap-4 px-4 py-4 sm:px-6">
                    <img
                        src={sguLogo}
                        alt="Logo SGU"
                        className="h-12 w-12 rounded-xl bg-white object-contain p-1 ring-1 ring-slate-200"
                    />
                    <div>
                        <h1 className="text-xl font-semibold text-slate-900">Tra cứu nguyện vọng xét tuyển</h1>
                        <p className="text-sm text-slate-600">Cổng tra cứu công khai theo CCCD</p>
                    </div>
                </div>
            </header>

            <main className="mx-auto w-full max-w-6xl px-4 py-6 sm:px-6">
                <NguyenVongLookupPanel />
            </main>
        </div>
    );
}
