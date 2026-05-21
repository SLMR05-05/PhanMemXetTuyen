import { useEffect, useMemo, useState } from 'react';
import { useAuthStore } from '../context/authStore';
import { useNguyenVongStore } from '../context/nguyenVongStore';
import { useNganhStore } from '../context/nganhStore';
import DashboardLayout from '../components/DashboardLayout';
import OverviewPanel from './dashboard/panels/OverviewPanel';
import AdmissionPanel from './dashboard/panels/AdmissionPanel';
import SearchPanel from './dashboard/panels/SearchPanel';
import DiemThiPanel from './dashboard/panels/DiemThiPanel';
import TuyenThangPanel from './dashboard/panels/TuyenThangPanel';
import { dashboardPanelLabels } from '../components/dashboard/dashboardNavigation';

export default function DashboardPage() {
    const { logout } = useAuthStore();
    const { nguyenVongs, fetchNguyenVongs, loading: nv_loading } = useNguyenVongStore();
    const { nganhList, fetchAllNganh, loading: nganh_loading } = useNganhStore();
    const [activePanel, setActivePanel] = useState('overview');
    const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
    const [expandedGroups, setExpandedGroups] = useState({ search: true });

    useEffect(() => {
        fetchNguyenVongs();
        fetchAllNganh();
    }, [fetchAllNganh, fetchNguyenVongs]);

    const stats = useMemo(
        () => [
            {
                label: 'Tổng nguyện vọng',
                value: nguyenVongs ? nguyenVongs.length : 0,
                tone: 'text-blue-600',
                note: 'Dữ liệu đang theo dõi',
            },
            {
                label: 'Tổng ngành có sẵn',
                value: nganhList ? nganhList.length : 0,
                tone: 'text-emerald-600',
                note: 'Danh mục ngành tuyển sinh',
            },
            {
                label: 'Nguyện vọng có điểm',
                value: nguyenVongs ? nguyenVongs.filter((nv) => nv.diemXettuyen).length : 0,
                tone: 'text-amber-600',
                note: 'Đã có dữ liệu xét tuyển',
            },
        ],
        [nganhList, nguyenVongs],
    );

    const handleSelectPanel = (panelKey) => {
        setActivePanel(panelKey);
        if (panelKey.startsWith('search-')) {
            setExpandedGroups((current) => ({ ...current, search: true }));
        }
    };

    const handleToggleGroup = (groupKey) => {
        setExpandedGroups((current) => ({
            ...current,
            [groupKey]: !current[groupKey],
        }));
    };

    const renderPanel = () => {
        switch (activePanel) {
            case 'overview':
                return <OverviewPanel stats={stats} />;
            case 'admission':
                return (
                    <AdmissionPanel
                        nguyenVongs={nguyenVongs}
                        nganhList={nganhList}
                        listLoading={nv_loading}
                        formLoading={nganh_loading}
                        onRefresh={fetchNguyenVongs}
                    />
                );
            case 'search-exam':
                return (
                    <DiemThiPanel
                        title={dashboardPanelLabels['search-exam']}
                        description="Kết quả các kỳ thi được đồng bộ tự động từ cơ sở dữ liệu."
                    />
                );
            case 'search-direct':
                return (
                    <TuyenThangPanel
                        title={dashboardPanelLabels['search-direct']}
                        description="Khu vực tra cứu hồ sơ tuyển thẳng và ưu tiên xét tuyển."
                    />
                );
            case 'search-english':
                return (
                    <SearchPanel
                        title={dashboardPanelLabels['search-english']}
                        description="Khu vực tra cứu điểm quy đổi Tiếng Anh được tách riêng thành panel độc lập trong area main."
                    />
                );
            default:
                return <OverviewPanel stats={stats} />;
        }
    };

    return (
        <DashboardLayout
            onLogout={logout}
            userName="Cao Tuệ Anh"
            sidebarCollapsed={sidebarCollapsed}
            onToggleSidebar={() => setSidebarCollapsed((value) => !value)}
            activePanel={activePanel}
            expandedGroups={expandedGroups}
            onToggleGroup={handleToggleGroup}
            onSelectPanel={handleSelectPanel}
        >
            {renderPanel()}
        </DashboardLayout>
    );
}
