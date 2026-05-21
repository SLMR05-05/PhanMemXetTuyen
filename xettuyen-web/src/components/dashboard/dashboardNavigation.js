import { FileText, FolderSearch, Home, Search } from 'lucide-react';

export const dashboardNavigation = [
    {
        key: 'overview',
        label: 'Trang Chủ',
        icon: Home,
        type: 'panel',
    },
    {
        key: 'search',
        label: 'Tra Cứu',
        icon: FolderSearch,
        type: 'group',
        children: [
            {
                key: 'search-exam',
                label: 'Tra cứu điểm thi',
                icon: Search,
            },
            {
                key: 'search-direct',
                label: 'Tra cứu tuyển thẳng',
                icon: Search,
            },
            {
                key: 'search-english',
                label: 'Tra cứu điểm quy đổi Tiếng Anh',
                icon: Search,
            },
        ],
    },
    {
        key: 'admission',
        label: 'Đăng ký nguyện vọng',
        icon: FileText,
        type: 'panel',
    },
];

export const dashboardPanelLabels = {
    overview: 'Trang Chủ',
    'search-exam': 'Tra cứu điểm thi',
    'search-direct': 'Tra cứu tuyển thẳng',
    'search-english': 'Tra cứu điểm quy đổi Tiếng Anh',
    admission: 'Đăng ký nguyện vọng',
};
