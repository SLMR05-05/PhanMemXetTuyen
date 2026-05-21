import { motion } from 'framer-motion';

export default function OverviewPanel({ stats }) {
    const cards = [
        {
            title: 'Welcome',
            description: 'Chào mừng bạn đến với Hệ thống Tuyển sinh SGU. Chọn chức năng bên trái để bắt đầu.',
            accent: 'bg-gradient-to-br from-blue-500 to-blue-600 text-white',
            descriptionTone: 'text-blue-50',
        },
        {
            title: 'Stats',
            description: 'Tổng quan nguyện vọng, dữ liệu xét tuyển và các mục tra cứu đang hoạt động.',
            accent: 'bg-white text-slate-700 ring-1 ring-slate-200',
            descriptionTone: 'text-slate-500',
        },
        {
            title: 'Quick Action',
            description: 'Đăng ký nguyện vọng mới hoặc kiểm tra kết quả tra cứu ngay trong dashboard.',
            accent: 'bg-white text-slate-700 ring-1 ring-slate-200',
            descriptionTone: 'text-slate-500',
        },
    ];

    return (
        <div className="space-y-6">
            <div className="grid gap-4 md:grid-cols-3">
                {stats.map((item) => (
                    <motion.div
                        key={item.label}
                        initial={{ opacity: 0, y: 18 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ duration: 0.35 }}
                        className="rounded-2xl bg-white p-5 shadow-sm ring-1 ring-slate-200"
                    >
                        <p className="text-sm font-medium text-slate-500">{item.label}</p>
                        <div className={`mt-3 text-3xl font-bold ${item.tone}`}>{item.value}</div>
                        <p className="mt-2 text-sm text-slate-500">{item.note}</p>
                    </motion.div>
                ))}
            </div>

            <div className="grid gap-4 lg:grid-cols-3">
                {cards.map((card, index) => (
                    <motion.div
                        key={card.title}
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ duration: 0.35, delay: index * 0.08 }}
                        className={`rounded-2xl p-6 shadow-sm ${card.accent}`}
                    >
                        <h3 className="text-lg font-semibold">{card.title}</h3>
                        <p className={`mt-2 text-sm leading-6 ${card.descriptionTone}`}>{card.description}</p>
                    </motion.div>
                ))}
            </div>
        </div>
    );
}
