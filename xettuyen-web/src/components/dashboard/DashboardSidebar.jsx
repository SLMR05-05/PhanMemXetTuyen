import { AnimatePresence, motion } from 'framer-motion';
import { ChevronDown, ChevronRight, LogOut } from 'lucide-react';
import logoBGD from '../../img/Logo-BGD&DT.png';
import { dashboardNavigation } from './dashboardNavigation';

function SidebarItem({ icon: Icon, label, active = false, onClick, hasChildren = false, open = false, collapsed = false }) {
    return (
        <motion.button
            type="button"
            onClick={onClick}
            whileHover={{ x: collapsed ? 0 : 4 }}
            whileTap={{ scale: 0.98 }}
            className={`group flex w-full items-center justify-between rounded-xl px-3 py-3 text-left transition-colors ${active ? 'bg-blue-50 text-blue-600' : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                } ${collapsed ? 'justify-center px-2' : ''}`}
        >
            <span className={`flex items-center gap-3 ${collapsed ? 'justify-center' : ''}`}>
                <motion.span
                    animate={active ? { scale: [1, 1.08, 1] } : { scale: 1 }}
                    transition={{ duration: 0.35 }}
                    className="flex h-9 w-9 items-center justify-center rounded-lg bg-white shadow-sm ring-1 ring-slate-200"
                >
                    <Icon className={`h-5 w-5 ${active ? 'text-blue-600' : 'text-slate-500'}`} />
                </motion.span>
                {!collapsed ? <span className="text-sm font-medium">{label}</span> : null}
            </span>
            {!collapsed && hasChildren ? (
                <motion.span animate={{ rotate: open ? 180 : 0 }} transition={{ duration: 0.2 }}>
                    <ChevronDown className="h-4 w-4 text-slate-400" />
                </motion.span>
            ) : null}
        </motion.button>
    );
}

export default function DashboardSidebar({
    collapsed,
    activePanel,
    expandedGroups,
    onToggleGroup,
    onSelectPanel,
    onLogout,
}) {
    const searchGroup = dashboardNavigation.find((item) => item.key === 'search');
    const isSearchGroupOpen = expandedGroups.search || searchGroup.children.some((child) => child.key === activePanel);

    return (
        <aside className={`flex h-full flex-col border-slate-200 bg-white shadow-sm transition-all duration-300 ${collapsed ? 'w-20' : 'w-64'}`}>
            <div className={`flex h-16 items-center gap-3 border-b border-slate-200 ${collapsed ? 'justify-center px-2' : 'px-4'}`}>
                <div className="flex h-11 w-11 items-center justify-center overflow-hidden rounded-xl bg-slate-50 ring-1 ring-slate-200">
                    <img src={logoBGD} alt="Bộ Giáo dục & Đào tạo" className="h-10 w-10 object-contain" />
                </div>
                {!collapsed ? (
                    <div className="min-w-0">
                        <p className="truncate text-[11px] font-semibold uppercase tracking-[0.22em] text-slate-500">
                            Bộ Giáo dục & Đào tạo
                        </p>
                        <h1 className="truncate text-sm font-bold text-slate-900">Kỳ thi tốt nghiệp THPT</h1>
                    </div>
                ) : null}
            </div>

            <div className="flex-1 overflow-y-auto px-3 py-4">
                <nav className="space-y-1">
                    {dashboardNavigation.map((item) => {
                        if (item.type === 'group') {
                            return (
                                <div key={item.key} className="space-y-1">
                                    <SidebarItem
                                        icon={item.icon}
                                        label={item.label}
                                        active={false}
                                        hasChildren
                                        open={isSearchGroupOpen}
                                        collapsed={collapsed}
                                        onClick={() => onToggleGroup(item.key)}
                                    />

                                    <AnimatePresence initial={false}>
                                        {isSearchGroupOpen && !collapsed ? (
                                            <motion.div
                                                key={`${item.key}-submenu`}
                                                initial={{ height: 0, opacity: 0 }}
                                                animate={{ height: 'auto', opacity: 1 }}
                                                exit={{ height: 0, opacity: 0 }}
                                                transition={{ duration: 0.25, ease: 'easeOut' }}
                                                className="overflow-hidden"
                                            >
                                                <div className="ml-5 mt-1 space-y-1 border-l border-slate-200 pl-3">
                                                    {item.children.map((child) => {
                                                        const isActive = activePanel === child.key;
                                                        return (
                                                            <motion.button
                                                                key={child.key}
                                                                type="button"
                                                                whileHover={{ x: 4 }}
                                                                whileTap={{ scale: 0.98 }}
                                                                onClick={() => onSelectPanel(child.key)}
                                                                className={`flex w-full items-center gap-2 rounded-lg px-3 py-2 text-left text-sm transition-colors ${isActive
                                                                    ? 'bg-blue-50 text-blue-600'
                                                                    : 'text-slate-600 hover:bg-blue-50 hover:text-blue-600'
                                                                    }`}
                                                            >
                                                                <ChevronRight className={`h-4 w-4 ${isActive ? 'text-blue-500' : 'text-slate-400'}`} />
                                                                <span className="font-medium">{child.label}</span>
                                                            </motion.button>
                                                        );
                                                    })}
                                                </div>
                                            </motion.div>
                                        ) : null}
                                    </AnimatePresence>
                                </div>
                            );
                        }

                        const isActive = activePanel === item.key;
                        return (
                            <SidebarItem
                                key={item.key}
                                icon={item.icon}
                                label={item.label}
                                active={isActive}
                                collapsed={collapsed}
                                onClick={() => onSelectPanel(item.key)}
                            />
                        );
                    })}
                </nav>
            </div>

            <div className="border-t border-slate-200 p-3">
                <motion.button
                    type="button"
                    whileHover={{ x: collapsed ? 0 : 4 }}
                    whileTap={{ scale: 0.98 }}
                    onClick={onLogout}
                    className={`flex w-full items-center gap-3 rounded-xl px-3 py-3 text-left text-sm font-medium text-slate-600 transition-colors hover:bg-red-50 hover:text-red-600 ${collapsed ? 'justify-center px-2' : ''}`}
                >
                    <span className="flex h-9 w-9 items-center justify-center rounded-lg bg-slate-100 ring-1 ring-slate-200 transition group-hover:bg-red-100">
                        <LogOut className="h-5 w-5" />
                    </span>
                    {!collapsed ? <span>Đăng xuất</span> : null}
                </motion.button>
            </div>
        </aside>
    );
}
