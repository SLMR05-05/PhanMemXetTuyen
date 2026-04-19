export default function Header({ onLogout }) {
    return (
        <header className="bg-blue-600 text-white shadow-lg">
            <div className="max-w-4xl mx-auto px-4 py-4 flex justify-between items-center">
                <div>
                    <h1 className="text-2xl font-bold">Xét Tuyển 2026</h1>
                    <p className="text-blue-100 text-sm">Hệ thống quản lý nguyện vọng</p>
                </div>
                <button
                    onClick={onLogout}
                    className="bg-red-500 hover:bg-red-600 px-4 py-2 rounded-lg font-medium transition"
                >
                    Đăng xuất
                </button>
            </div>
        </header>
    );
}
