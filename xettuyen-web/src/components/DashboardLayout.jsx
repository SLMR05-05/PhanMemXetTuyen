import DashboardHeader from './dashboard/DashboardHeader';
import DashboardSidebar from './dashboard/DashboardSidebar';

export default function DashboardLayout({
    children,
    userName = 'Thí sinh',
    onLogout,
    sidebarCollapsed = false,
    onToggleSidebar,
    activePanel,
    expandedGroups,
    onToggleGroup,
    onSelectPanel,
}) {
    return (
        <div className="flex h-screen w-full overflow-hidden bg-slate-50 antialiased">
            <DashboardSidebar
                collapsed={sidebarCollapsed}
                activePanel={activePanel}
                expandedGroups={expandedGroups}
                onToggleGroup={onToggleGroup}
                onSelectPanel={onSelectPanel}
                onLogout={onLogout}
            />

            <main className="flex flex-1 flex-col overflow-hidden">
                <DashboardHeader
                    userName={userName}
                    onToggleSidebar={onToggleSidebar}
                    sidebarCollapsed={sidebarCollapsed}
                />

                <section className="flex-1 overflow-y-auto p-4 sm:p-6">
                    <div className="mx-auto flex w-full max-w-7xl flex-col gap-6 rounded-3xl bg-white p-4 shadow-sm ring-1 ring-slate-200 sm:p-6">
                        {children}
                    </div>
                </section>
            </main>
        </div>
    );
}