import { Bell, Menu, UserCircle2 } from 'lucide-react';

export default function DashboardHeader({ userName = 'Thí sinh', onToggleSidebar, sidebarCollapsed = false }) {
    return (
        <header className="flex h-16 items-center justify-between border-b border-slate-200 bg-white px-4 shadow-sm sm:px-6">
            <div className="flex items-center gap-3 sm:gap-4">
                <button
                    type="button"
                    onClick={onToggleSidebar}
                    className="rounded-lg p-2 text-slate-500 transition hover:bg-slate-100 hover:text-slate-900"
                    aria-label={sidebarCollapsed ? 'Mở sidebar' : 'Thu gọn sidebar'}
                >
                    <Menu className="h-5 w-5" />
                </button>
                <div className="leading-tight">
                    <p className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-500">Dashboard</p>
                    <h2 className="text-sm font-semibold text-slate-900">Khu vực quản trị nguyện vọng</h2>
                </div>
            </div>

            <div className="flex items-center gap-3 sm:gap-4">
                <button type="button" className="relative rounded-full p-2 text-slate-500 transition hover:bg-slate-100 hover:text-slate-900">
                    <Bell className="h-5 w-5" />
                    <span className="absolute right-2 top-2 h-2 w-2 rounded-full bg-blue-600" />
                </button>
                <div className="flex items-center gap-3 rounded-full border border-slate-200 bg-slate-50 px-3 py-2">
                    <div className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-600 text-white">
                        <UserCircle2 className="h-5 w-5" />
                    </div>
                    <div className="hidden leading-tight sm:block">
                        <p className="text-sm font-semibold text-slate-900">{userName}</p>
                        <p className="text-xs text-slate-500">Thí sinh</p>
                    </div>
                </div>
            </div>
        </header>
    );
}
